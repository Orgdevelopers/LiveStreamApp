package com.kulvinder.livestream.domain.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;

public interface LivestreamServices {

    LiveStreamEntity create(LiveStreamEntity liveStream);

    Optional<LiveStreamEntity> findById(Long id);

    Page<LiveStreamEntity> findAll(Integer sp, Integer lm);

    LiveStreamEntity update(LiveStreamEntity liveStream);

    void delete(Long id);
    
}