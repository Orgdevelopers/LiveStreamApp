package com.kulvinder.livestream.domain.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kulvinder.livestream.config.Constants;
import com.kulvinder.livestream.domain.models.dtos.UserDto;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.repositories.UserRepository;
import com.kulvinder.livestream.domain.services.UserServices;
import com.kulvinder.livestream.mappers.Mapper;

@Service
public class UserServicesImpl implements UserServices{

    private UserRepository userRepository;

    public UserServicesImpl(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserEntity createUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Page<UserEntity> findAll(Integer starting_point, Integer limit) {
        Pageable pageable = PageRequest.of(starting_point, limit,Constants.USERSORTING);
        return userRepository.findAll(pageable);
    }

    @Override
    public UserEntity updateUser(UserEntity user) {
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}
