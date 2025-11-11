package com.kulvinder.livestream.domain.services.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.kulvinder.livestream.domain.models.entities.GiftEntity;
import com.kulvinder.livestream.domain.repositories.GiftRepository;
import com.kulvinder.livestream.domain.services.GiftServices;

@Service
public class GiftServicesImpl implements GiftServices {

    private GiftRepository giftRepository;

    public GiftServicesImpl(GiftRepository giftRepository){
        this.giftRepository = giftRepository;
    }

    @Override
    public GiftEntity create(GiftEntity gift) {
        return giftRepository.save(gift);
    }

    @Override
    public Optional<GiftEntity> findById(Long id) {
        return giftRepository.findById(id);
    }

    @Override
    public Page<GiftEntity> findAll(Integer sp, Integer lm) {
        Pageable pageable = PageRequest.of(sp, lm);
        return giftRepository.findAll(pageable);
    }

    @Override
    public GiftEntity update(GiftEntity gift) {
        return giftRepository.save(gift);
    }

    @Override
    public void delete(Long id) {
        giftRepository.deleteById(id);
    }
}
