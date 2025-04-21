package com.example.socio_app.repository;

import com.example.socio_app.entity.Post;
import com.example.socio_app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post,Integer> {
    List<Post> findByUserId(int userId);

}
