package com.kulvinder.livestream.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kulvinder.livestream.beans.ApiResponse;
import com.kulvinder.livestream.beans.CustomPageResponse;
import com.kulvinder.livestream.beans.ResponseFactory;
import com.kulvinder.livestream.config.Constants;
import com.kulvinder.livestream.domain.models.dtos.LiveStreamDto;
import com.kulvinder.livestream.domain.models.dtos.UserDto;
import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.domain.services.LivestreamServices;
import com.kulvinder.livestream.domain.services.StreamBroadcastService;
import com.kulvinder.livestream.domain.services.UserServices;
import com.kulvinder.livestream.mappers.Mapper;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(path = "/livestreams")
@CrossOrigin(origins = "*")
public class LiveStreamsController {

    @Autowired
    private LivestreamServices streamServices;

    @Autowired
    private UserServices userServices;

    @Autowired
    private Mapper<LiveStreamEntity, LiveStreamDto> mapper;

    // @Autowired
    // private StreamSocketController socketController;
    @Autowired
    private StreamBroadcastService streamBroadcastService;

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<CustomPageResponse<LiveStreamDto>>> showActiveStreams(
            @RequestParam(defaultValue = Constants.STARTINGPOINT) Integer starting_point,
            @RequestParam(defaultValue = Constants.LIMIT) Integer limit) {

        Page<LiveStreamEntity> page = streamServices.findAllActiveStreams(starting_point, limit);
        Page<LiveStreamDto> dtoPage = page.map(mapper::MapTo);
        CustomPageResponse<LiveStreamDto> cdtoPage = CustomPageResponse.fromPage(dtoPage);

        return ResponseFactory.from(cdtoPage, "success");

    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LiveStreamDto>> findStream(@PathVariable Long id) {
        Optional<LiveStreamEntity> streamEntity = streamServices.findById(id);
        if (streamEntity.isPresent()) {
            return ResponseFactory.from(mapper.MapTo(streamEntity.get()), "success");
        }

        return ResponseFactory.notFound("stream not found");
    }


    @PostMapping("/start_stream")
    public ResponseEntity<ApiResponse<LiveStreamDto>> startStream(@RequestBody UserDto userDto) {
        Long user_id = userDto.getId();
        Optional<UserEntity> user = userServices.findById(user_id);
        if (!user.isPresent()) {
            return ResponseFactory.notFound("user not found");
        }

        LiveStreamEntity entity = streamServices.create(LiveStreamEntity.builder().user(user.get()).active(true).banner("https://picsum.photos/400/600?id="+new Random().nextInt(10000)).build());
        //send stream update to socket
        streamBroadcastService.broadcastStreamUpdate(entity);

        return ResponseFactory.from(mapper.MapTo(entity), "Stream started");
        
    }
    
    

    @PostMapping("/{id}/end_stream")
    public ResponseEntity<ApiResponse<LiveStreamDto>> endStream(@PathVariable Long id) {
        Optional<LiveStreamEntity> optional = streamServices.findById(id);
        if (!optional.isPresent()) {
            return ResponseFactory.notFound("stream not found");
        }
        LiveStreamEntity stream = streamServices.endStream(id);
        streamBroadcastService.broadcastStreamUpdate(stream);
        return ResponseFactory.from(mapper.MapTo(stream), "success");

    }
    

}
