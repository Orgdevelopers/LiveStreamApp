package com.kulvinder.livestream.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kulvinder.livestream.beans.ApiResponse;
import com.kulvinder.livestream.beans.ResponseFactory;
import com.kulvinder.livestream.domain.models.dtos.UserAuthDto;
import com.kulvinder.livestream.domain.models.dtos.UserDto;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.services.UserServices;
import com.kulvinder.livestream.mappers.Mapper;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/auth")
public class AuthController {

    @Autowired
    private UserServices userServices;

    @Autowired
    private Mapper<UserEntity, UserDto> mapper;

    @Autowired
    private PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserDto>> login(@RequestBody UserAuthDto userDto) {

        if (userDto == null || userDto.getUsername() == null || userDto.getPassword() == null) {
            return ResponseFactory.invalidRequest("Invalid request parameters");
        }

        Optional<UserEntity> userOpt = userServices.findByUsername(userDto.getUsername());

        if (userOpt.isEmpty()) {
            return ResponseFactory.notFound("No user found with username: " + userDto.getUsername());
        }

        UserEntity user = userOpt.get();

        if (!encoder.matches(userDto.getPassword(), user.getPassword())) {
            return ResponseFactory.conflict("Incorrect password");
        }

        return ResponseFactory.from(mapper.MapTo(user), "Login successful");
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDto>> signup(@RequestBody UserAuthDto userDto) {
        if (userDto == null) {
            return ResponseFactory.invalidRequest("invalid request parameters");
        }

        if (userServices.existsByUsername(userDto.getUsername())) {
            return ResponseFactory.conflict("user already exists : "+userDto.getUsername());
        }

        //encrypt password
        userDto.setPassword(encoder.encode(userDto.getPassword()));

        UserEntity user = userServices.createUser(UserEntity.builder().username(userDto.getUsername()).password(userDto.getPassword()).build());

        return ResponseFactory.from(mapper.MapTo(user), "registration successful");

    }

}
