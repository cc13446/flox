package com.cc.flox.utils.template.placeholder;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * 占位符解析
 *
 * @author cc
 * @date 2024/4/29
 */
@AllArgsConstructor
public class PlaceHolderParser {
    /**
     * 占位符开始字符
     */
    private final String openToken;
    /**
     * 占位符结束字符
     */
    private final String closeToken;
    /**
     * 占位符处理器
     */
    private final PlaceHolderHandler handler;

    /**
     * 解析
     *
     * @param text 模板
     * @return 结果
     */
    public String parse(String text) {
        StringBuilder res = new StringBuilder();
        if (StringUtils.isNoneBlank(text)) {
            char[] src = text.toCharArray();
            int offset = 0;
            int start = text.indexOf(openToken, offset);
            while (start > -1) {
                if (start > 0 && src[start - 1] == '\\') {
                    // 占位符转义
                    res.append(src, offset, start - 1).append(openToken);
                    offset = start + openToken.length();
                } else {
                    int end = text.indexOf(closeToken, start);
                    if (end == -1) {
                        // 没有结束符
                        res.append(src, offset, src.length - offset);
                        offset = src.length;
                    } else {
                        // 有结束符，执行handler
                        res.append(src, offset, start - offset);
                        offset = start + openToken.length();
                        String content = new String(src, offset, end - offset);
                        res.append(handler.handleToken(content));
                        offset = end + closeToken.length();
                    }
                }
                start = text.indexOf(openToken, offset);
            }
            if (offset < src.length) {
                res.append(src, offset, src.length - offset);
            }
        }
        return res.toString();
    }
}
