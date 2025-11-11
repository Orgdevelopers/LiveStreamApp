package com.kulvinder.livestream.domain.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kulvinder.livestream.domain.models.entities.GiftEntity;

public interface GiftServices {

    GiftEntity create(GiftEntity gift);

    Optional<GiftEntity> findById(Long id);

    Page<GiftEntity> findAll(Integer sp, Integer lm);

    GiftEntity update(GiftEntity gift);

    void delete(Long id);

}