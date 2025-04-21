package com.example.socio_app.services;

import com.example.socio_app.dto.LoginRequest;
import com.example.socio_app.dto.RegisterRequest;
import com.example.socio_app.entity.User;
import com.example.socio_app.repository.UserRepository;
import com.example.socio_app.securityConfig.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }


    public String register(@Valid RegisterRequest registerRequest) {
        if(userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return "User already exits";
        }

        User newuser = new User();

        newuser.setUsername(registerRequest.getUsername());
        newuser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newuser.setEmail(registerRequest.getEmail());


        userRepository.save(newuser);
        return "user Register Successfully";

    }

    public Map<String, String> Login(LoginRequest loginRequest, HttpServletResponse response) {
        try{
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
            );

            String token = jwtUtil.generateToken(loginRequest.getUsername());

            Cookie jwtCookie = new Cookie("jwt", token);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge(24 * 60 * 60);  // Set expiry to 1 day
            jwtCookie.setAttribute("SameSite", "None");
            jwtCookie.setSecure(true);

            response.addCookie(jwtCookie);



            Map<String,String> res = new HashMap<>();
            res.put("message","login successfully");


            return res;

        }catch(BadCredentialsException e){
            return Collections.singletonMap("error","invalid Username or password");
        }catch (Exception e){
            return Collections.singletonMap("error","something Went Wrong");
        }
    }

    public Map<String, String> Logout(HttpServletResponse response) {
        Cookie jwtCookie = new Cookie("jwt",null);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setSecure(false);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(0);
        response.addCookie(jwtCookie);
        Map<String,String> res = new HashMap<>();
        res.put("message","Logout Successfully");

        return res;
    }

    public User getAuthenticatedUser(){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username).orElseThrow(()->new UsernameNotFoundException("user not found"));
    }

}
