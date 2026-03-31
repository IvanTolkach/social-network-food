package com.foodsocial.social_media_food.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/upload")
@Tag(name = "File Upload", description = "API for uploading and retrieving files")
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    private String fixedFilename; // Поле для использования фиксированного имени в тестах

    // Метод для задания фиксированного имени файла (используется в тестах)
    public void setFixedFilename(String fixedFilename) {
        this.fixedFilename = fixedFilename;
    }

    @Operation(summary = "Upload a photo", description = "Upload a photo to the server and return its download URI")
    @PostMapping("/photo")
    public ResponseEntity<String> uploadPhoto(
            @Parameter(description = "The photo file to be uploaded", required = true) @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected for upload.");
        }

        try {
            // Создаем директорию для загрузки файлов, если ее нет
            Files.createDirectories(Paths.get(uploadPath));

            // Используем фиксированное имя файла в тестах, иначе генерируем стандартное имя
            String filename = (fixedFilename != null) ? fixedFilename : System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadPath + File.separator + filename);
            Files.write(path, file.getBytes());

            // Возвращаем путь к загруженному файлу
            String fileDownloadUri = "/uploads/" + filename;
            return ResponseEntity.status(HttpStatus.CREATED).body(fileDownloadUri);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload file: " + e.getMessage());
        }
    }

    @Operation(summary = "Retrieve a photo", description = "Retrieve a photo from the server by its filename")
    @GetMapping("/photo/{filename:.+}")
    public ResponseEntity<byte[]> getPhoto(
            @Parameter(description = "Filename of the photo to be retrieved", example = "photo.jpg") @PathVariable String filename) {
        try {
            Path path = Paths.get(uploadPath).resolve(filename);
            byte[] fileBytes = Files.readAllBytes(path);
            return ResponseEntity.ok().body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
