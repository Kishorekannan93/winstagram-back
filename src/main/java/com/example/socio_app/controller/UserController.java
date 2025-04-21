package com.example.socio_app.controller;

import com.example.socio_app.dto.UserResponse;
import com.example.socio_app.dto.UserResponse.SimpleUser;
import com.example.socio_app.entity.User;
import com.example.socio_app.securityConfig.JwtUtil;
import com.example.socio_app.services.CloudinaryService;
import com.example.socio_app.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private CloudinaryService cloudinaryService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/upload-profile-pic")
    public ResponseEntity<?> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            @CookieValue(name = "jwt", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            String imageUrl = cloudinaryService.uploadImage(file);
            userService.updateProfilePicture(username, imageUrl);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Image upload failed");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> updatePicture(
            @RequestParam("file") MultipartFile file,
            @CookieValue(name = "jwt", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);

            User user = userService.getUserByUsername(username);
            String existingUrl = user.getProfilePictureUrl();
            if(existingUrl != null && !existingUrl.isEmpty()){
                cloudinaryService.deleteImageByUrl(existingUrl);
            }
            String imageUrl = cloudinaryService.uploadImage(file);
            userService.updateProfilePicture(username, imageUrl);
            return ResponseEntity.ok(imageUrl);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Image upload failed");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }





    @PostMapping("/follow/{targetUserId}")
    public ResponseEntity<?> followUser(
            @PathVariable int targetUserId,
            @CookieValue(name = "jwt", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            userService.followUser(username, targetUserId);
            return ResponseEntity.ok("Successfully followed user");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unfollow/{targetUserId}")
    public ResponseEntity<?> unfollowUser(
            @PathVariable int targetUserId,
            @CookieValue(name = "jwt", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            userService.unfollowUser(username, targetUserId);
            return ResponseEntity.ok("Successfully unfollowed user");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/delete-profile-pic")
    public ResponseEntity<?> deleteProfilePicture(
            @CookieValue(name = "jwt", required = false) String token) {

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);

            // Get user and profile picture URL
            User user = userService.getUserByUsername(username);
            String existingUrl = user.getProfilePictureUrl();

            // If there's an image URL, delete it from Cloudinary
            if (existingUrl != null && !existingUrl.isEmpty()) {
                cloudinaryService.deleteImageByUrl(existingUrl);
            }

            // Call service to update profile picture URL to null
            userService.deleteProfilePicture(username);

            return ResponseEntity.ok("Profile picture deleted successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete image");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @GetMapping("/{userId}/followers")
    public ResponseEntity<?> getFollowers(@PathVariable int userId) {
        try {
            Set<SimpleUser> followers = userService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}/following")
    public ResponseEntity<?> getFollowing(@PathVariable int userId) {
        try {
            Set<SimpleUser> following = userService.getFollowing(userId);
            return ResponseEntity.ok(following);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/suggested-users")
    public ResponseEntity<?> getSuggestedUsers(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            List<UserResponse.SimpleUser> suggestedUsers = userService.getSuggestedUsers(username);

            // Return the suggested users as a response
            return ResponseEntity.ok(suggestedUsers);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }


    @GetMapping("/me")
    public ResponseEntity<?> getLoggedInUserDetails(@CookieValue(name = "jwt", required = false) String token) {
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No authentication token found");
        }

        try {
            String username = jwtUtil.extractUsername(token);
            UserResponse response = userService.getFullUserDetails(username);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }



}
