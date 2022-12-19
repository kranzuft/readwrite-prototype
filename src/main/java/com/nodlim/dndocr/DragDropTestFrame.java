package com.nodlim.dndocr;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serial;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static java.awt.BorderLayout.CENTER;
import static javax.swing.SpringLayout.*;

public class DragDropTestFrame extends JFrame {

    private static final int WIDTH = 430;
    private static final int HEIGHT = 550;

    @Serial
    private static final long serialVersionUID = 1L;

    private final KeyStroker keyStroker = new KeyStroker();

    private final Executor executor = Executors.newSingleThreadExecutor();

    public DragDropTestFrame(String tesseractLocation) {
        // Set the frame title
        super("Drag and drop OCR");

        JTextArea jTextArea = new JTextArea(20, 35);
        JScrollPane jScrollPane = new JScrollPane(jTextArea);

        // Create the label
        JLabel myLabel = new JLabel("", SwingConstants.CENTER);
        String imageFileNameDnD = System.getProperty("user.dir").replace("\\", "/") + "/tessocr/Tesseract-OCR/DragNDrop.png";
        unscaledImage(myLabel, imageFileNameDnD);

        // Create the drag and drop listener
        MyDragDropListener myDragDropListener = new MyDragDropListener(tesseractLocation) {
            @Override
            public void updateTextArea(String textAreaString) {
                jTextArea.setText(textAreaString);
            }
        };

        // Connect the label with a drag and drop listener
        new DropTarget(myLabel, myDragDropListener);
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new BorderLayout());
        SpinnerModel spinnerModel = new SpinnerNumberModel(5, 0, Integer.MAX_VALUE, 1);
        JSpinner jSpinner = new JSpinner(spinnerModel);
        JButton jButton = new JButton("Apply Keystrokes");
        jButton.addActionListener(ev -> {
            try {
                if (((Integer) jSpinner.getValue()) < 0) {
                    jSpinner.setValue(0);
                }
                TimeUnit.SECONDS.sleep((Integer) jSpinner.getValue());
                executor.execute(() -> keyStroker.type(jTextArea.getText()));
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        });
        bottomPanel.add(CENTER, jButton);
        bottomPanel.add(EAST, jSpinner);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());

        JButton jButton1 = new JButton("Cancel");
        jButton1.addActionListener(s -> {

        });

        // Add the label to the content

        jPanel.add(NORTH, myLabel);
        jPanel.add(CENTER, jScrollPane);
        jPanel.add(SOUTH, bottomPanel);
        Color backgroundColor = new Color(213, 227, 249);
        jPanel.setBackground(backgroundColor);
        this.getContentPane().add(CENTER, jPanel);
        this.getContentPane().setBackground(backgroundColor);

        // Show the frame
        this.setMinimumSize(new Dimension(WIDTH, HEIGHT));
        this.setMaximumSize(new Dimension(1024, 1024));
        this.setVisible(true);

        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void unscaledImage(JLabel imageLabel, String imageFileName) {
        try {
            BufferedImage image = ImageIO.read(new File(imageFileName));
            imageLabel.setIcon(new ImageIcon(image));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Keeping around for later reference
    @SuppressWarnings("unused")
    private void scaleImage(JLabel imageLabel, String imageFileName) {
        try {
            BufferedImage image = ImageIO.read(new File(imageFileName));
            double scaleX = ((double) WIDTH) / image.getWidth();
            double scaleY = 300.0 / image.getHeight();
            double scale = Math.min(scaleX, scaleY);
            int scaledWidth = (int) (image.getWidth() * scale);
            int scaledHeight = (int) (image.getHeight() * scale);
            BufferedImage bAWBImage = blackAndWhiten(image.getScaledInstance(scaledWidth, scaledHeight, Image.SCALE_DEFAULT));
            imageLabel.setIcon(new ImageIcon(bAWBImage));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage blackAndWhiten(Image input) {
        BufferedImage im = new BufferedImage(input.getWidth(null), input.getHeight(null), BufferedImage.TYPE_BYTE_GRAY);
        // Get the graphics context for the black-and-white image.
        Graphics2D g2d = im.createGraphics();
        // Render the input image on it.
        g2d.drawImage(input, 0, 0, null);

        return im;
    }
}