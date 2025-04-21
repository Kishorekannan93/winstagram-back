package com.example.socio_app.dto;

import com.example.socio_app.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {

    private int id;
    private int userId;
    private String content;
    private String imageUrl;
    private String username;




}
