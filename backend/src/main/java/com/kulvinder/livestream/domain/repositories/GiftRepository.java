package com.kulvinder.livestream.domain.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kulvinder.livestream.domain.models.entities.GiftEntity;

public interface GiftRepository extends JpaRepository<GiftEntity, Long> {

}
