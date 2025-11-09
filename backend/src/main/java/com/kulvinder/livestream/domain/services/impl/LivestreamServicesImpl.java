package com.kulvinder.livestream.domain.services.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.repositories.LiveStreamRepository;
import com.kulvinder.livestream.domain.services.LivestreamServices;
import com.kulvinder.livestream.domain.services.StreamBroadcastService;

@Service
public class LivestreamServicesImpl implements LivestreamServices {

    @Autowired
    private LiveStreamRepository repository;

    // @Autowired
    // private StreamSocketController socketController;
    @Autowired
    private StreamBroadcastService streamBroadcastService;

    @Override
    public LiveStreamEntity create(LiveStreamEntity liveStream) {
        return repository.save(liveStream);
    }

    @Override
    public Optional<LiveStreamEntity> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<LiveStreamEntity> findAll(Integer sp, Integer lm) {
        Pageable pageable = PageRequest.of(sp, lm);
        return repository.findAll(pageable);
    }

    @Override
    public Page<LiveStreamEntity> findAllActiveStreams(Integer sp, Integer lm) {
        Pageable pageable = PageRequest.of(sp, lm);
        return repository.findByActiveTrue(pageable);
    }

    @Override
    public LiveStreamEntity update(LiveStreamEntity liveStream) {
        return repository.save(liveStream);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public LiveStreamEntity startStream(UserEntity user) {
        LiveStreamEntity stream = new LiveStreamEntity();
        stream.setUser(user);
        stream.setActive(true);
        stream.setCreated(LocalDateTime.now());
        repository.save(stream);

        // Send update
        streamBroadcastService.broadcastStreamUpdate(stream);
        return stream;
    }

    //
    @Override
    public LiveStreamEntity endStream(Long id) {
        Optional<LiveStreamEntity> stream = findById(id);
        if (!stream.isPresent()) {
            return null;
        }

        LiveStreamEntity entity = stream.get();
        entity.setActive(false);
        entity.setEnded(LocalDateTime.now());

        streamBroadcastService.broadcastStreamUpdate(entity);
        return repository.save(entity);
    }

}
