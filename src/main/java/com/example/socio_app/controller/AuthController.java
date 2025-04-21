package com.example.socio_app.controller;
import com.example.socio_app.dto.LoginRequest;
import com.example.socio_app.dto.RegisterRequest;
import com.example.socio_app.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;
    @PostMapping("/register")
    public ResponseEntity<String>  registry(@Valid @RequestBody RegisterRequest registerRequest){
      String message = authService.register(registerRequest);
      if(message.equalsIgnoreCase("user already exits")){
          return ResponseEntity.status(400).body(message);
      }
      return ResponseEntity.ok().body(message);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response){
       Map<String,String> res = authService.Login(loginRequest,response);
       if(res.containsKey("error")){
           return ResponseEntity.status(403).body(res);
       }
       return ResponseEntity.ok().body(res);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String,String>> logout(HttpServletResponse response){
        Map<String,String> res = authService.Logout(response);
        return ResponseEntity.ok().body(res);
    }



}
