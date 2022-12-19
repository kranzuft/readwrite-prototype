package com.nodlim.dndocr;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;

// MyDragDropListener
// Gets images dropped on window for image-to-text conversion.
// Pretty much boilerplate.
abstract class MyDragDropListener implements DropTargetListener {
    private final String tesseractLocation;

    public MyDragDropListener(String tesseractLocation) {
        this.tesseractLocation = tesseractLocation;
    }

    public abstract void updateTextArea(String textAreaString);

    public void updateImage(String ignoredImageFileName) {
        // nothing to do
    }

    @Override
    public void drop(DropTargetDropEvent event) {

        // Accept copy drops
        event.acceptDrop(DnDConstants.ACTION_COPY);

        // Get the transfer which can provide the dropped item data
        Transferable transferable = event.getTransferable();

        // Get the data formats of the dropped item
        DataFlavor[] flavors = transferable.getTransferDataFlavors();

        StringBuilder stringBuilder = new StringBuilder();
        // Loop through the flavors
        for (DataFlavor flavor : flavors) {
            try {
                // If the drop items are files
                if (flavor.isFlavorJavaFileListType()) {

                    // Get the dropped files
                    var transferData = transferable.getTransferData(flavor);
                    if (transferData instanceof List<?> transferList) {
                        if (transferList.isEmpty() || !(transferList.get(0) instanceof File)) {
                            // The list is not a list of File objects, so do not attempt the cast
                            return;
                        }

                        // Can't get rid of warning other than suppression, because the compiler does not generate a
                        // full run-time check at cast time
                        // See: https://stackoverflow.com/questions/509076/how-do-i-address-unchecked-cast-warnings
                        @SuppressWarnings("unchecked")
                        List<File> files = (List<File>) transferList;

                        // The file(s) contents is converted one by one and appended to StringBuilder to be added
                        // to main text area
                        // Should probably create multiple tabs but will handle in next version
                        for (File file : files) {
                            String newFileName = file.getAbsolutePath();
                            updateImage(newFileName);
                            ProcessBuilder builder = new ProcessBuilder(
                                    "cmd.exe", "/c", tesseractLocation.replace("/", File.separator) + File.separator + "tesseract", newFileName, "stdout", "nobatch", "keys");
                            builder.redirectErrorStream(true);
                            builder.directory(new File(tesseractLocation));
                            Process p = builder.start();
                            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));

                            for (String line = ""; line != null; line = r.readLine()) {
                                stringBuilder.append(line).append("\n");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // Print out the error stack
                e.printStackTrace();
            }
        }

        updateTextArea(stringBuilder.toString());

        // Inform that the drop is complete
        event.dropComplete(true);

    }

    @Override
    public void dragEnter(DropTargetDragEvent event) {
    }

    @Override
    public void dragExit(DropTargetEvent event) {
    }

    @Override
    public void dragOver(DropTargetDragEvent event) {
    }

    @Override
    public void dropActionChanged(DropTargetDragEvent event) {
    }
}