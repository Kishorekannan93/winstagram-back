package com.example.socio_app.dto;

import com.example.socio_app.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private int id;
    private String username;
    private String profilePictureUrl;
    private Set<SimpleUser> followers;
    private Set<SimpleUser> following;
    private List<PostRequest> posts;



    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SimpleUser {
        private int id;
        private String username;
        private String profilePictureUrl;


    }



}
