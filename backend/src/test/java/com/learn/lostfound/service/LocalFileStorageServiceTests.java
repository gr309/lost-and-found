package com.learn.lostfound.service;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.server.ResponseStatusException;
import static org.junit.jupiter.api.Assertions.*;

class LocalFileStorageServiceTests {
    @TempDir Path directory;

    private LocalFileStorageService storage() throws Exception {
        return new LocalFileStorageService(directory.toString(), new ImageProcessor());
    }

    public static MockMultipartFile png() throws Exception {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ImageIO.write(new BufferedImage(32, 16, BufferedImage.TYPE_INT_ARGB), "png", output);
        return new MockMultipartFile("image", "../../unsafe.png", "image/png", output.toByteArray());
    }

    @Test void storesProcessedImageWithGeneratedNameAndSurvivesNewServiceInstance() throws Exception {
        String url = storage().save(png());
        assertTrue(url.matches("/uploads/items/[0-9a-f-]+\\.jpg"));
        Path file = directory.resolve(url.substring("/uploads/".length()));
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(Files.readAllBytes(file)));
        assertEquals(800, image.getWidth());
        assertEquals(800, image.getHeight());
        storage().delete(url);
        assertFalse(Files.exists(file));
    }

    @Test void rejectsFakeImageWithoutLeavingFiles() throws Exception {
        var storage = storage();
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> storage.save(
                new MockMultipartFile("image", "fake.jpg", "image/jpeg", "not an image".getBytes())))
                .getStatusCode().value());
        try (var files = Files.list(directory.resolve("items"))) { assertEquals(0, files.count()); }
    }

    @Test void rejectsUnsupportedAndMissingContentType() throws Exception {
        var storage = storage();
        for (String type : new String[]{"image/svg+xml", "image/webp", null}) {
            assertThrows(ResponseStatusException.class, () -> storage.save(
                    new MockMultipartFile("image", "image", type, png().getBytes())));
        }
    }

    @Test void rejectsTooLargeAndEmptyFiles() throws Exception {
        var storage = storage();
        assertEquals(413, assertThrows(ResponseStatusException.class, () -> storage.save(
                new MockMultipartFile("image", "large.jpg", "image/jpeg", new byte[5 * 1024 * 1024 + 1])))
                .getStatusCode().value());
        assertThrows(ResponseStatusException.class, () -> storage.save(
                new MockMultipartFile("image", new byte[0])));
    }

    @Test void refusesOversizedDecodedDimensions() throws Exception {
        // Valid PNG signature/IHDR, with dimensions changed before decoding pixel data.
        byte[] bytes = png().getBytes();
        java.nio.ByteBuffer.wrap(bytes, 16, 8).putInt(100_000).putInt(100_000);
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> storage().save(
                new MockMultipartFile("image", "large.png", "image/png", bytes))).getStatusCode().value());
    }

    @Test void deletionIgnoresTraversalAndExternalUrls() throws Exception {
        Path protectedFile = directory.resolve("keep.jpg");
        Files.writeString(protectedFile, "keep");
        var storage = storage();
        storage.delete("/uploads/items/../keep.jpg");
        storage.delete("https://example.com/uploads/items/keep.jpg");
        storage.delete(null);
        assertEquals("keep", Files.readString(protectedFile));
    }
}
