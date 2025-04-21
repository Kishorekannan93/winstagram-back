package com.example.socio_app.services;

import com.example.socio_app.dto.PostRequest;
import com.example.socio_app.dto.UserResponse;
import com.example.socio_app.entity.Post;
import com.example.socio_app.entity.User;
import com.example.socio_app.repository.PostRepository;
import com.example.socio_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private  CloudinaryService cloudinaryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    public String createPost(PostRequest postRequest, MultipartFile file) throws IOException {

        User currentUser = authService.getAuthenticatedUser();

        if(currentUser == null){
            throw new IllegalArgumentException("Unauthenticated error..");
        }

        String imgUrl = null;

        // Handle image upload only if the image is provided
        if (file != null && !file.isEmpty()) {
            imgUrl = cloudinaryService.uploadImage(file);
        } else {
            System.out.println("No image received!");
        }


        // If both content and imageUrl are null, throw an exception
        if ((postRequest == null || postRequest.getContent() == null || postRequest.getContent().trim().isEmpty()) &&
                (imgUrl == null || imgUrl.trim().isEmpty())) {
            throw new IllegalArgumentException("Either content or image is needed");
        }

        // Create and save the post
        Post newPost = new Post();
        newPost.setContent(postRequest != null ? postRequest.getContent() : "");
        newPost.setImageUrl(imgUrl);  // Make sure imgUrl is not null if image is not uploaded
        newPost.setUser(currentUser);


        postRepository.save(newPost);
        return "post created successfully";

    }


    public List<Post> getPost() {
        return postRepository.findAll();

    };



    public Optional<Post> getpostbyid(int id) {
        return postRepository.findById(id);

    }




    public String updatePost(int id, PostRequest postRequest, MultipartFile file) throws IOException {
        User currentuser = authService.getAuthenticatedUser();

        Post existingPost = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("post not found"));

        String imgUrl = null;

        if (!existingPost.getUser().equals(currentuser)) {
            throw new RuntimeException("user is not authorized update a post");
        }

        if(file != null && !file.isEmpty()){

            if(existingPost.getImageUrl()!=null && !existingPost.getImageUrl().isEmpty()){
                cloudinaryService.deleteImageByUrl(existingPost.getImageUrl());
            }
            imgUrl = cloudinaryService.uploadImage(file);
            existingPost.setImageUrl(imgUrl);
        }

        if ((postRequest == null || postRequest.getContent() == null || postRequest.getContent().trim().isEmpty()) &&
                (imgUrl == null || imgUrl.trim().isEmpty())) {
            throw new IllegalArgumentException("Either content or image is needed");
        }


        existingPost.setContent(postRequest.getContent());
        existingPost.setUser(currentuser);


     postRepository.save(existingPost);
     return "updated sucessfully..";


    }


    public String delete(int id) throws IOException {
        User currentuser = authService.getAuthenticatedUser();

        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getUser().equals(currentuser)) {
            throw new RuntimeException("You are not authorized to delete this post");
        }

        // ✅ Delete image from Cloudinary if exists
        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            cloudinaryService.deleteImageByUrl(post.getImageUrl());
        }

        postRepository.delete(post);
        return "Post deleted successfully";
    }

    public List<PostRequest> getPostsByUserId(int userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream().map(post -> {
            PostRequest pr = new PostRequest();
            pr.setId(post.getId());
            pr.setUserId(post.getUser().getId());
            pr.setUsername(post.getUser().getUsername());
            pr.setContent(post.getContent());
            pr.setImageUrl(post.getImageUrl());
            return pr;
        }).collect(Collectors.toList());
    }







}

