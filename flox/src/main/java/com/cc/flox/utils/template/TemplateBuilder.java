package com.cc.flox.utils.template;

import com.cc.flox.utils.XXHashUtils;
import com.cc.flox.utils.template.fragment.Fragment;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileReader;
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
    static class Builder {

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
            return null;
//            try {
//                Document document = parseXml(content);
//                List<Fragment> contents = buildDynamicTag(document.getElementsByTagName("script").item(0));
//                return new Template(new MixedSqlFragment(contents));
//            } catch (Exception e) {
//                throw new RuntimeException("Error constructing the XML template", e);
//            }
        }

        /**
         * @param content 模板
         * @return 解析好的xml对象
         */
        private Document parseXml(String content) throws ParserConfigurationException {
            DocumentBuilder builder = FACTORY.newDocumentBuilder();
            builder.setEntityResolver((publicId, systemId) -> new InputSource(new FileReader("/Users/cc/exam/SqlTemplate/src/main/resources/script-1.0.dtd")));
            return null;
        }

    }
}
