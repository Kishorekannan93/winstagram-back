package com.example.socio_app.controller;

import com.example.socio_app.services.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class CloudinaryController {

    @Autowired
    private CloudinaryService cloudinaryService;

    @PostMapping("/upload")
    public ResponseEntity<String> UploadImage(@RequestParam("file")MultipartFile file){
        try {
            String imgUrl = cloudinaryService.uploadImage(file);
            return   ResponseEntity.ok().body(imgUrl);
        } catch (IOException e) {
            return  ResponseEntity.badRequest().body("image uploaded failed");
        }

    }
    // ✅ New: Delete image by URL
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            cloudinaryService.deleteImageByUrl(imageUrl);
            return ResponseEntity.ok("Image deleted successfully from Cloudinary");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to delete image: " + e.getMessage());
        }
    }





}
