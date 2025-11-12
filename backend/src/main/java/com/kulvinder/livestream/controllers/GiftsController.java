package com.kulvinder.livestream.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.kulvinder.livestream.beans.ApiResponse;
import com.kulvinder.livestream.beans.CustomPageResponse;
import com.kulvinder.livestream.beans.ResponseFactory;
import com.kulvinder.livestream.config.Constants;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;
import com.kulvinder.livestream.domain.models.dtos.GiftDto;
import com.kulvinder.livestream.domain.models.entities.GiftEntity;
import com.kulvinder.livestream.domain.services.GiftServices;
import com.kulvinder.livestream.mappers.Mapper;

@RestController
@RequestMapping("/gifts")
public class GiftsController {

    @Autowired
    private GiftServices giftServices;

    @Autowired
    private Mapper<GiftEntity, GiftDto> mapper;

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftDto>> findOne(@PathVariable Long id) {
        Optional<GiftEntity> optional = giftServices.findById(id);
        if (optional.isPresent()) {
            return ResponseFactory.from(mapper.MapTo(optional.get()), "success");
        }
        return ResponseFactory.notFound("gift not found");
    }

    @GetMapping("")
    public ResponseEntity<ApiResponse<CustomPageResponse<GiftDto>>> findAll(
        @RequestParam(defaultValue = Constants.STARTINGPOINT) Integer starting_point,
        @RequestParam(defaultValue = Constants.LIMIT) Integer limit) {
        Page<GiftEntity> page = giftServices.findAll(starting_point, limit);
        Page<GiftDto> dtoPage = page.map(mapper::MapTo);
        CustomPageResponse<GiftDto> response = CustomPageResponse.fromPage(dtoPage);
        return ResponseFactory.from(response, "success");
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<GiftDto>> create(@RequestBody GiftDto giftDto) {
        GiftEntity giftEntity = mapper.MapFrom(giftDto);
        GiftEntity createdGift = giftServices.create(giftEntity);
        return ResponseFactory.from(mapper.MapTo(createdGift), "success");
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftDto>> update(@PathVariable Long id, @RequestBody GiftDto giftDto) {
        GiftEntity giftEntity = mapper.MapFrom(giftDto);
        GiftEntity updatedGift = giftServices.update(giftEntity);
        return ResponseFactory.from(mapper.MapTo(updatedGift), "success");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftDto>> delete(@PathVariable Long id) {
        giftServices.delete(id);
        return ResponseFactory.from(new GiftDto(), "success");
    }
}
