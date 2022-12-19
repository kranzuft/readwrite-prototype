package com.nodlim.dndocr;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class App {

    public static void main(String[] args) throws IOException {
        FileUtils fileUtils = new FileUtils();
        Path path = Paths.get(System.getProperty("user.dir")+ File.separator+"tessocr");

        Optional<String> copiedTo;
        if (!Files.exists(path)) {
            Files.createDirectory(path);
            copiedTo = fileUtils.exportResource("/Tesseract-OCR.zip");
        } else {
            copiedTo = Optional.of(System.getProperty("user.dir").replace("\\", "/") + "/tessocr/Tesseract-OCR.zip");
        }

        if (copiedTo.isEmpty()) {
            Files.delete(path);
            System.err.println("Couldn't copy file out from jar");
            return;
        }

        String unzipLocation = copiedTo.get().substring(0, copiedTo.get().lastIndexOf("."));
        Path unzipLocationPath = Paths.get(unzipLocation);
        if (!Files.exists(unzipLocationPath)) {
            fileUtils.unzip(Files.newInputStream(Paths.get(copiedTo.get())), Paths.get(copiedTo.get()).getParent());
        }

        new DragDropTestFrame(unzipLocation);

    }
}
