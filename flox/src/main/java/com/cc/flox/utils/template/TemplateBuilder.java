package com.cc.flox.utils.template;

import com.cc.flox.utils.XXHashUtils;
import com.cc.flox.utils.template.fragment.Fragment;
import com.cc.flox.utils.template.fragment.impl.MixedFragment;
import com.cc.flox.utils.template.fragment.impl.TextFragment;
import com.cc.flox.utils.template.tag.TagHandler;
import com.cc.flox.utils.template.tag.TagHandlerManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.FutureTask;

/**
 * Mybatis 模板配置
 *
 * @author cc
 * @date 2024/4/28
 */
@Slf4j
public class TemplateBuilder {

    /**
     * 模板缓存
     */
    private final ConcurrentHashMap<Long, FutureTask<Template>> templateCache;

    /**
     * 是否开启模板缓存
     */
    private final boolean cacheTemplate;

    /**
     * 资源解析器
     */
    private final ResourceLoader resourceLoader;

    public TemplateBuilder(ResourceLoader resourceLoader) {
        this(true, resourceLoader);
    }

    public TemplateBuilder(boolean cacheTemplate, ResourceLoader resourceLoader) {
        this.cacheTemplate = cacheTemplate;
        this.templateCache = new ConcurrentHashMap<>();
        this.resourceLoader = resourceLoader;
    }

    /**
     * @param content 模板内容
     * @return 模板
     */
    public Template getTemplate(final String content) {
        long hash = XXHashUtils.hash(content);
        if (cacheTemplate) {
            FutureTask<Template> f = templateCache.get(hash);
            if (Objects.isNull(f)) {
                FutureTask<Template> ft = new FutureTask<>(() -> createTemplate(content));
                f = templateCache.putIfAbsent(hash, ft);
                if (Objects.isNull(f)) {
                    ft.run();
                    f = ft;
                }
            }
            try {
                return f.get();
            } catch (Exception e) {
                templateCache.remove(hash);
                throw new RuntimeException(e);
            }
        }
        return createTemplate(content);
    }

    /**
     * @param content 模板内容
     * @return 模板
     */
    private Template createTemplate(String content) {
        return new Builder(content).build();
    }

    /**
     * 真正的构造器
     */
    @AllArgsConstructor
    public class Builder {

        private final static DocumentBuilderFactory FACTORY = DocumentBuilderFactory.newInstance();

        static {
            FACTORY.setValidating(true);
            FACTORY.setNamespaceAware(false);
            FACTORY.setIgnoringComments(true);
            FACTORY.setIgnoringElementContentWhitespace(false);
            FACTORY.setCoalescing(false);
            FACTORY.setExpandEntityReferences(true);
        }

        /**
         * 模板内容
         */
        private final String content;

        /**
         * @return 构造好的模板
         */
        public Template build() {
            try {
                Document document = parseXml(content);
                List<Fragment> contents = buildDynamicTag(document.getElementsByTagName("script").item(0));
                return new Template(new MixedFragment(contents));
            } catch (Exception e) {
                throw new RuntimeException("Error constructing the XML template", e);
            }
        }

        /**
         * @param content 模板
         * @return 解析好的xml对象
         */
        private Document parseXml(String content) throws ParserConfigurationException, IOException, SAXException {
            DocumentBuilder builder = FACTORY.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> new InputSource(resourceLoader.getResource(resourceLoader.CLASSPATH_URL_PREFIX + "meta/template/script.dtd").getInputStream()));
            builder.setErrorHandler(new ErrorHandler() {
                @Override
                public void warning(SAXParseException exception) {
                    log.warn("Parse [{}]", content, exception);
                }

                @Override
                public void error(SAXParseException exception) {
                    log.error("Parse [{}]", content, exception);
                }

                @Override
                public void fatalError(SAXParseException exception) {
                    log.error("Parse [{}]", content, exception);
                }
            });
            InputSource source = new InputSource(new StringReader(String.format("<?xml version = \"1.0\" ?>\r\n<!DOCTYPE script SYSTEM \"script-1.0.dtd\">\r\n%s", content)));
            return builder.parse(source);
        }

        /**
         * @param node node
         * @return 解析结果
         */
        public static List<Fragment> buildDynamicTag(Node node) {
            List<Fragment> contents = new ArrayList<>();
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                Node child = children.item(i);
                String nodeName = child.getNodeName();
                if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE) {
                    String s = child.getTextContent();
                    TextFragment textFragment = new TextFragment(s);
                    contents.add(textFragment);
                } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                    TagHandler tagHandler = TagHandlerManager.TAG_HANDLER_MAP.get(nodeName.toLowerCase());
                    if (Objects.nonNull(tagHandler)) {
                        tagHandler.handleNode(child, contents);
                    }
                }
            }
            return contents;
        }
    }
}
