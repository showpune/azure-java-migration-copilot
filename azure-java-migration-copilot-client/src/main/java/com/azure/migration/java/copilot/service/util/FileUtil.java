package com.azure.migration.java.copilot.service.util;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class FileUtil {

    public static void copyFiles(String sourcePath, String targetPath) throws IOException {
        Path sourceDirectory = Path.of(sourcePath);
        Path targetDirectory = Path.of(targetPath);

        Files.walkFileTree(sourceDirectory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path targetFile = targetDirectory.resolve(sourceDirectory.relativize(file));
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                Path targetDir = targetDirectory.resolve(sourceDirectory.relativize(dir));
                Files.createDirectories(targetDir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
