package com.cc.flox.utils;

import javax.tools.*;
import java.io.*;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * java 代码编译工具
 *
 * @author cc
 * @date 2024/5/1
 */
public class JavaCodeUtils {

    /**
     * 编译器
     */
    private static final JavaCompiler COMPILER = ToolProvider.getSystemJavaCompiler();

    /**
     * 类名正则匹配
     */
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("public\\s+class\\s+([a-zA-Z_$][a-zA-Z1-9]*)\\s+\\{");

    /**
     * 包名正则匹配
     */
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile("package\\s+([a-zA-Z1-9_$.]*)\\s*;");

    public static class JavaSourceCodeFromString extends SimpleJavaFileObject {
        private final String content;

        public JavaSourceCodeFromString(String className, String contents) {
            super(URI.create(className + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = contents;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }

    /**
     * java字符串代码，转为class字节码
     *
     * @param code code
     * @return class 字节码
     */
    public static byte[] codeToClass(String code) {
        String className = getClassNameFromCode(code);
        JavaFileObject stringObject = new JavaSourceCodeFromString(className, code);
        try (StandardJavaFileManager fileManager = COMPILER.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {
            JavaCompiler.CompilationTask task = COMPILER.getTask(null, fileManager, null, List.of("-Xlint:none"), null, Collections.singletonList(stringObject));
            if (task.call()) {
                JavaFileObject file = fileManager.getJavaFileForOutput(StandardLocation.CLASS_OUTPUT, className, JavaFileObject.Kind.CLASS, null);
                try (InputStream inputStream = file.openInputStream()) {
                    return inputStream.readAllBytes();
                } finally {
                    file.delete();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Compile java code error", e);
        }
        return null;
    }

    /**
     * 从java code字符串中获取类名
     */
    public static String getClassNameFromCode(String code) {
        Matcher matcher = CLASS_NAME_PATTERN.matcher(code);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }

    /**
     * 从java code字符串中获取包名
     */
    public static String getPackageNameFromCode(String code) {
        Matcher matcher = PACKAGE_NAME_PATTERN.matcher(code);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
}

