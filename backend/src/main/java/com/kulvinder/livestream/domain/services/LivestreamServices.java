package com.kulvinder.livestream.domain.services;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.models.entities.UserEntity;

public interface LivestreamServices {

    LiveStreamEntity create(LiveStreamEntity liveStream);

    Optional<LiveStreamEntity> findById(Long id);

    Page<LiveStreamEntity> findAll(Integer sp, Integer lm);

    Page<LiveStreamEntity> findAllActiveStreams(Integer sp, Integer lm);

    LiveStreamEntity update(LiveStreamEntity liveStream);

    LiveStreamEntity startStream(UserEntity user);

    LiveStreamEntity endStream(Long id);

    void delete(Long id);
    
}