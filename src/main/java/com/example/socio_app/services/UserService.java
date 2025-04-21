package com.example.socio_app.services;

import com.example.socio_app.dto.PostRequest;
import com.example.socio_app.dto.UserResponse;
import com.example.socio_app.dto.UserResponse.SimpleUser;
import com.example.socio_app.entity.User;
import com.example.socio_app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostService postService;

    public void updateProfilePicture(String username, String imageUrl) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setProfilePictureUrl(imageUrl);

        userRepository.save(user);
    }




    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }


    @Transactional
    public void followUser(String username, int targetUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        if (user.getId() == targetUserId) {
            throw new IllegalArgumentException("You cannot follow yourself!");
        }
        user.getFollowing().add(targetUser);
        targetUser.getFollowers().add(user);
        userRepository.saveAll(List.of(user, targetUser));
    }

    @Transactional
    public void unfollowUser(String username, int targetUserId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("Target user not found"));

        user.getFollowing().remove(targetUser);
        targetUser.getFollowers().remove(user);
        userRepository.saveAll(List.of(user, targetUser));
    }

    public Set<SimpleUser> getFollowers(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getFollowers().stream()
                .map(this::convertToSimpleUser)
                .collect(Collectors.toSet());
    }

    public Set<SimpleUser> getFollowing(int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return user.getFollowing().stream()
                .map(this::convertToSimpleUser)
                .collect(Collectors.toSet());
    }

    public List<SimpleUser> getSuggestedUsers(String username) {
        // Fetch the current user
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Get users who are not already followed by the current user and not the current user
        List<User> allUsers = userRepository.findAll();
        List<User> suggestedUsers = allUsers.stream()
                .filter(user -> !user.getUsername().equals(username) &&
                        !currentUser.getFollowing().contains(user))
                .collect(Collectors.toList());

        // Pick a random subset of users (e.g., 4 users)
        suggestedUsers = getRandomUsers(suggestedUsers, 4);

        // Convert to SimpleUser DTOs
        return suggestedUsers.stream()
                .map(this::convertToSimpleUser)
                .collect(Collectors.toList());
    }

    private List<User> getRandomUsers(List<User> users, int count) {
        Collections.shuffle(users);
        return users.stream().limit(count).collect(Collectors.toList());
    }


    private SimpleUser convertToSimpleUser(User user) {
        SimpleUser dto = new SimpleUser();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        return dto;
    }

    public void deleteProfilePicture(String username) {
        // Fetch user by username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Set the profile picture URL to null (or you can choose to set an empty string)
        user.setProfilePictureUrl(null);

        // Save the updated user to the database
        userRepository.save(user);
    }
    public UserResponse getFullUserDetails(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Set<SimpleUser> followers = user.getFollowers().stream()
                .map(this::convertToSimpleUser)
                .collect(Collectors.toSet());

        Set<SimpleUser> following = user.getFollowing().stream()
                .map(this::convertToSimpleUser)
                .collect(Collectors.toSet());

        List<PostRequest> posts = postService.getPostsByUserId(user.getId());

        UserResponse response = new UserResponse();
        response.setId(user.getId());
        response.setUsername(user.getUsername());
        response.setProfilePictureUrl(user.getProfilePictureUrl());
        response.setFollowers(followers);
        response.setFollowing(following);
        response.setPosts(posts); // 👈 include posts

        return response;
    }


}
