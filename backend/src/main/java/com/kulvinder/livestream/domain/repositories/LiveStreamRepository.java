package com.kulvinder.livestream.domain.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;

@Repository
public interface LiveStreamRepository extends JpaRepository<LiveStreamEntity,Long>{

    Page<LiveStreamEntity> findByActiveTrue(Pageable pageable);

}