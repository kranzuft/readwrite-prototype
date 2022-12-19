package com.nodlim.dndocr;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class FileUtils {
    public Optional<String> exportResource(String resourceName) {
        try (InputStream stream = FileUtils.class.getResourceAsStream(resourceName)) {
            if (stream == null) {
                return Optional.empty();
            }

            int readBytes;
            byte[] buffer = new byte[4096];

            String outFile = System.getProperty("user.dir").replace("\\", "/") + "/tessocr" + resourceName;
            System.out.println(outFile);
            try (FileOutputStream resStreamOut = new FileOutputStream(outFile)) {
                while ((readBytes = stream.read(buffer)) > 0) {
                    resStreamOut.write(buffer, 0, readBytes);
                }
            }

            return Optional.of(outFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Optional.empty();
    }

    public void unzip(InputStream is, Path targetDir) throws IOException {
        targetDir = targetDir.toAbsolutePath();
        try (ZipInputStream zipIn = new ZipInputStream(is)) {
            for (ZipEntry ze; (ze = zipIn.getNextEntry()) != null; ) {
                Path resolvedPath = targetDir.resolve(ze.getName()).normalize();
                if (!resolvedPath.startsWith(targetDir)) {
                    // see: https://snyk.io/research/zip-slip-vulnerability
                    throw new RuntimeException("Entry with an illegal path: "
                            + ze.getName());
                }
                if (ze.isDirectory()) {
                    Files.createDirectories(resolvedPath);
                } else {
                    Files.createDirectories(resolvedPath.getParent());
                    Files.copy(zipIn, resolvedPath);
                }
            }
        }
    }
}