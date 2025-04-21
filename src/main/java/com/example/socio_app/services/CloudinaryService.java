package com.example.socio_app.services;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    @Autowired
    private Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) throws IOException {
        Map uploaderResults = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
        return uploaderResults.get("url").toString();

    }



    // ✅ New: delete image by URL
    public void deleteImageByUrl(String imageUrl) throws IOException {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        String publicId = extractPublicIdFromUrl(imageUrl);
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    // ✅ Helper: Extract Cloudinary public ID from image URL
    private String extractPublicIdFromUrl(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("/")) return null;

        String[] parts = imageUrl.split("/");
        String filename = parts[parts.length - 1]; // last segment after /
        return filename.contains(".") ? filename.substring(0, filename.lastIndexOf('.')) : null;
    }



}
