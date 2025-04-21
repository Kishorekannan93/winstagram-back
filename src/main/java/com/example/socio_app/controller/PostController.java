package com.example.socio_app.controller;

import com.example.socio_app.dto.PostRequest;
import com.example.socio_app.entity.Post;
import com.example.socio_app.entity.User;
import com.example.socio_app.securityConfig.JwtUtil;
import com.example.socio_app.services.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPost(
            @RequestPart(value = "postRequest", required = false) String postRequestJson,
            @RequestParam("file") MultipartFile file) throws IOException {

        try {
            // Manually deserialize JSON string to PostRequest object
            ObjectMapper objectMapper = new ObjectMapper();
            PostRequest postRequest = objectMapper.readValue(postRequestJson, PostRequest.class);
            String savedPost = postService.createPost(postRequest, file);
            return ResponseEntity.ok(savedPost);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }


    @GetMapping("/posts")
    public ResponseEntity<?> getPost() {
        try {
            List<Post> posts = postService.getPost();
            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<?> getPostById(@PathVariable int id) {
        try {
            Optional<Post> post = postService.getpostbyid(id);
            return post.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePost(@PathVariable int id,
                                        @RequestPart(value = "postRequest", required = false) String postRequestJson,
                                        @RequestParam("file") MultipartFile file) throws IOException{
        try{
            ObjectMapper objectMapper = new ObjectMapper();
            PostRequest postRequest = objectMapper.readValue(postRequestJson,PostRequest.class);
            String UpdatePost = postService.updatePost(id,postRequest,file);
            return ResponseEntity.ok().body(UpdatePost);

        }catch(IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePost(@PathVariable int id){
        try{
            String delete = postService.delete(id);
            return ResponseEntity.ok().body(delete);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());

        }

    }




}
