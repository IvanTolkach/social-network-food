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

@RestController
@RequestMapping("/upload")
public class FileUploadController {

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/photo")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No file selected for upload.");
        }

        try {
            // Создаем директорию для загрузки файлов, если ее нет
            Files.createDirectories(Paths.get(uploadPath));

            // Сохраняем файл
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadPath + File.separator + filename);
            Files.write(path, file.getBytes());

            // Возвращаем путь к загруженному файлу
            String fileDownloadUri = "/uploads/" + filename;
            return ResponseEntity.status(HttpStatus.CREATED).body(fileDownloadUri);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not upload file: " + e.getMessage());
        }
    }

    @GetMapping("/photo/{filename:.+}")
    public ResponseEntity<byte[]> getPhoto(@PathVariable String filename) {
        try {
            Path path = Paths.get(uploadPath).resolve(filename);
            byte[] fileBytes = Files.readAllBytes(path);
            return ResponseEntity.ok().body(fileBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
