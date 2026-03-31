package com.foodsocial.social_media_food;

import com.foodsocial.social_media_food.controller.FileUploadController;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileUploadControllerTests {

    @InjectMocks
    private FileUploadController fileUploadController;

    private final String uploadPath = "src/main/resources/static/uploads";

    private MockedStatic<Files> mockedFiles;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        ReflectionTestUtils.setField(fileUploadController, "uploadPath", uploadPath);
        mockedFiles = mockStatic(Files.class);
    }

    @AfterEach
    void tearDown() {
        mockedFiles.close();
    }

    @Test
    void testUploadPhoto() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        // Устанавливаем фиксированное имя файла для теста
        ReflectionTestUtils.invokeMethod(fileUploadController, "setFixedFilename", "fixed_timestamp_test.jpg");

        mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(mock(Path.class));
        mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class))).thenReturn(mock(Path.class));

        ResponseEntity<String> response = fileUploadController.uploadPhoto(file);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("/uploads/fixed_timestamp_test.jpg", response.getBody());
    }

    @Test
    void testUploadPhotoEmptyFile() {
        MockMultipartFile file = new MockMultipartFile("file", "", "image/jpeg", new byte[0]);

        ResponseEntity<String> response = fileUploadController.uploadPhoto(file);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("No file selected for upload.", response.getBody());
    }

    @Test
    void testUploadPhotoIOException() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image content".getBytes());

        mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(mock(Path.class));
        mockedFiles.when(() -> Files.write(any(Path.class), any(byte[].class))).thenThrow(new IOException("Mock IOException"));

        ResponseEntity<String> response = fileUploadController.uploadPhoto(file);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Could not upload file: Mock IOException", response.getBody());
    }

    @Test
    void testGetPhoto() throws Exception {
        String filename = "test.jpg";
        byte[] fileContent = "test image content".getBytes();

        mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenReturn(fileContent);

        ResponseEntity<byte[]> response = fileUploadController.getPhoto(filename);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(fileContent, response.getBody());
    }

    @Test
    void testGetPhotoNotFound() throws Exception {
        String filename = "test.jpg";

        mockedFiles.when(() -> Files.readAllBytes(any(Path.class))).thenThrow(new IOException("Mock IOException"));

        ResponseEntity<byte[]> response = fileUploadController.getPhoto(filename);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals(null, response.getBody());
    }
}
