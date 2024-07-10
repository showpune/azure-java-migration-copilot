package com.azure.migration.java.copilot.service.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;

public class FileUtil {

    public static void createFiles(String sourcePath, String targetPath, boolean isDir) throws IOException {
        String classPath = "classpath:" + sourcePath + "**/*";
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(classPath);
        Path targetDirectory = Path.of(targetPath);
        for(Resource resource : resources) {
            Path targetDir = targetDirectory.resolve(getRelativePath(resource, sourcePath));
            if(resource.isFile()) {
                if (isDir && resource.getFile().isDirectory()) {
                    Files.createDirectories(targetDir);
                } else if (resource.getFile().isFile()) {
                    Files.copy(new ByteArrayInputStream(resource.getContentAsByteArray()), targetDir, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                Path path = Path.of(getRelativePath(resource, sourcePath));
                Path relative = path.getParent();
                if(isDir && relative != null) {
                    String fileName = path.getFileName().toString();
                    String relativeStr = relative.toString().replace(fileName, "");
                    if(!StringUtils.equals(fileName, relativeStr) ) {
                        targetDir = targetDirectory.resolve(relativeStr);
                        Files.createDirectories(targetDir);
                    }
                } else {
                    Files.copy(new ByteArrayInputStream(resource.getContentAsByteArray()), targetDir, StandardCopyOption.REPLACE_EXISTING);
                }
            }
        }
    }


    public static String getRelativePath(Resource resource, String sourcePath) throws IOException {
        String urlPath = resource.getURL().getPath();

        String classpath = FileUtil.class.getResource(sourcePath).getPath();
        return urlPath.substring(classpath.length());
    }
}
