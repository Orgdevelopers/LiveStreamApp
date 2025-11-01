package com.kulvinder.livestream.domain.services.impl;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.repositories.LiveStreamRepository;
import com.kulvinder.livestream.domain.services.LivestreamServices;

@Service
public class LivestreamServicesImpl implements LivestreamServices{

    @Autowired
    private LiveStreamRepository repository;

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
    public LiveStreamEntity update(LiveStreamEntity liveStream) {
        return repository.save(liveStream);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
    
}
