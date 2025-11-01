package com.kulvinder.livestream.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kulvinder.livestream.beans.ApiResponse;
import com.kulvinder.livestream.beans.CustomPageResponse;
import com.kulvinder.livestream.beans.ResponseFactory;
import com.kulvinder.livestream.config.Constants;
import com.kulvinder.livestream.domain.models.dtos.LiveStreamDto;
import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.services.LivestreamServices;
import com.kulvinder.livestream.mappers.Mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping(path = "/livestreams")
public class LiveStreamsController {

    @Autowired
    private LivestreamServices services;

    @Autowired
    private Mapper<LiveStreamEntity, LiveStreamDto> mapper;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<CustomPageResponse<LiveStreamDto>>> showActiveStreams(
            @RequestParam(defaultValue = Constants.STARTINGPOINT) Integer starting_point,
            @RequestParam(defaultValue = Constants.LIMIT) Integer limit) {

        Page<LiveStreamEntity> page = services.findAll(starting_point, limit);
        Page<LiveStreamDto> dtoPage = page.map(mapper::MapTo);
        CustomPageResponse<LiveStreamDto> cdtoPage = CustomPageResponse.fromPage(dtoPage);

        return ResponseFactory.from(cdtoPage, "success");

    }

}
