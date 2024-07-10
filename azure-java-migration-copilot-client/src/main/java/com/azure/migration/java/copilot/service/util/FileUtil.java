package com.azure.migration.java.copilot.service.util;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.*;

public class FileUtil {

    public static void copyFiles(String sourcePath, String targetPath) throws IOException {
    }

    public static void createFiles(String sourcePath, String targetPath, boolean isDir) throws IOException {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(sourcePath);
        Path targetDirectory = Path.of(targetPath);
        for(Resource resource : resources) {
            Path targetDir = targetDirectory.resolve(getRelativePath(resource));
            if(resource.isFile()) {
                if (isDir && resource.getFile().isDirectory()) {
                    Files.createDirectories(targetDir);
                } else if (resource.getFile().isFile()) {
                    Files.copy(new ByteArrayInputStream(resource.getContentAsByteArray()), targetDir, StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                Path path = Path.of(getRelativePath(resource));
                Path relative = path.getParent();
                if(isDir && relative != null) {
                    String fileName = path.getFileName().toString();
                    String relativeStr = relative.toString().replace(fileName, "");
                    System.out.println("relativeStr:" + relativeStr);
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


    public static String getRelativePath(Resource resource) throws IOException {
        String urlPath = resource.getURL().getPath();
        String classpath = FileUtil.class.getResource("/azd-template-files/").getPath();
        return urlPath.substring(classpath.length());
    }
}
