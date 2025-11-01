package com.kulvinder.livestream.controllers;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kulvinder.livestream.beans.ApiResponse;
import com.kulvinder.livestream.beans.CustomPageResponse;
import com.kulvinder.livestream.beans.ResponseFactory;
import com.kulvinder.livestream.config.Constants;
import com.kulvinder.livestream.domain.models.dtos.UserDto;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.services.UserServices;
import com.kulvinder.livestream.mappers.Mapper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(path = "/users")
public class UsersController {

    private UserServices userServices;
    private Mapper<UserEntity, UserDto> mapper;

    public UsersController(UserServices userServices, Mapper<UserEntity, UserDto> mapper) {
        this.userServices = userServices;
        this.mapper = mapper;
    }

    // read single user : /users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> findOne(@PathVariable Long id) {
        Optional<UserEntity> optional = userServices.findById(id);
        if (optional.isPresent()) {
            return ResponseFactory.from(mapper.MapTo(optional.get()), "success");
        }

        return ResponseFactory.notFound("user not found");
    }

    // read multiple users : /users
    @GetMapping("")
    public ResponseEntity<ApiResponse<CustomPageResponse<UserDto>>> findAll(
            @RequestParam(defaultValue = Constants.STARTINGPOINT) Integer starting_point,
            @RequestParam(defaultValue = Constants.LIMIT) Integer limit) {
        Page<UserEntity> users = userServices.findAll(starting_point, limit);
        Page<UserDto> usersDto = users.map(mapper::MapTo);
        CustomPageResponse<UserDto> response = CustomPageResponse.fromPage(usersDto);
        return ResponseFactory.from(response, "success");
    }

}
