package com.kulvinder.livestream.domain.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kulvinder.livestream.domain.models.entities.UserEntity;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long>{

    Optional<UserEntity> findByUsername(String username);

    Boolean existsByUsername(String username);
    
}