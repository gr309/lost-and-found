package com.learn.lostfound.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.ImageWriteParam;
import javax.imageio.stream.ImageOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

@Service
public class ImageProcessor {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 800;

    private static final float QUALITY = 0.75f;

    public byte[] process(MultipartFile file) throws IOException {

        // 1. Validate input size
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image is required");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IllegalArgumentException("Maximum image size is 10MB");
        }

        // 2. Read original image
        BufferedImage original;

        try (InputStream input = file.getInputStream()) {
            original = ImageIO.read(input);
        }

        if (original == null) {
            throw new IllegalArgumentException("Invalid image");
        }

        int originalWidth = original.getWidth();
        int originalHeight = original.getHeight();

        // 3. Calculate resize ratio
        double ratio = Math.min(
                (double) WIDTH / originalWidth,
                (double) HEIGHT / originalHeight
        );

        // Do not upscale smaller images
        ratio = Math.min(ratio, 1.0);

        int newWidth = Math.max(1, (int) Math.round(originalWidth * ratio));
        int newHeight = Math.max(1, (int) Math.round(originalHeight * ratio));

        // 4. Create an 800x800 image
        BufferedImage resized = new BufferedImage(
                WIDTH,
                HEIGHT,
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D graphics = resized.createGraphics();

        try {
            // White background
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, WIDTH, HEIGHT);

            // High-quality resizing
            graphics.setRenderingHint(
                    RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BICUBIC
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY
            );

            graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            // Center image
            int x = (WIDTH - newWidth) / 2;
            int y = (HEIGHT - newHeight) / 2;

            graphics.drawImage(
                    original,
                    x,
                    y,
                    newWidth,
                    newHeight,
                    null
            );

        } finally {
            graphics.dispose();
        }

        // 5. Compress as JPEG
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        Iterator<ImageWriter> writers =
                ImageIO.getImageWritersByFormatName("jpeg");

        if (!writers.hasNext()) {
            throw new IOException("JPEG writer not available");
        }

        ImageWriter writer = writers.next();

        try (ImageOutputStream imageOutput =
                     ImageIO.createImageOutputStream(output)) {

            writer.setOutput(imageOutput);

            ImageWriteParam params = writer.getDefaultWriteParam();

            params.setCompressionMode(
                    ImageWriteParam.MODE_EXPLICIT
            );

            params.setCompressionQuality(QUALITY);

            writer.write(
                    null,
                    new javax.imageio.IIOImage(resized, null, null),
                    params
            );

        } finally {
            writer.dispose();
        }

        return output.toByteArray();
    }
}