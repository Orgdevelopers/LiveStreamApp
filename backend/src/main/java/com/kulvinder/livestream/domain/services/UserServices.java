package com.kulvinder.livestream.domain.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kulvinder.livestream.domain.models.entities.UserEntity;

public interface UserServices {

    // create
    UserEntity createUser(UserEntity user);

    // read one
    Optional<UserEntity> findById(Long id);

    // read by username
    Optional<UserEntity> findByUsername(String username);

    // read many
    Page<UserEntity> findAll(Integer starting_point, Integer limit);

    // update full
    UserEntity updateUser(UserEntity user);

    // delete
    void deleteUser(Long id);


    //extra methods
    Boolean existsByUsername(String username);

}
