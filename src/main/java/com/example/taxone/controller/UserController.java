package com.example.taxone.controller;

import com.example.taxone.dto.request.UserRequest;
import com.example.taxone.dto.response.UserResponse;
import com.example.taxone.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getOwnProfile() {
        UserResponse response = userService.getOwnProfile();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me")
    public ResponseEntity<UserResponse> updateOwnProfile(@Valid @RequestBody UserRequest userRequest) {
        UserResponse response = userService.updateOwnProfile(userRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnProfile() {
        userService.deleteOwnProfile();
        return ResponseEntity.noContent().build();
    }


}
