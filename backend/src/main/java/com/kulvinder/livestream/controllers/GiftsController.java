package com.kulvinder.livestream.controllers;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.kulvinder.livestream.beans.ApiResponse;
import com.kulvinder.livestream.beans.CustomPageResponse;
import com.kulvinder.livestream.beans.ResponseFactory;
import com.kulvinder.livestream.config.Constants;
import org.springframework.data.domain.Page;
import com.kulvinder.livestream.domain.models.dtos.GiftDto;
import com.kulvinder.livestream.domain.models.entities.GiftEntity;
import com.kulvinder.livestream.domain.services.FileStorageService;
import com.kulvinder.livestream.domain.services.GiftServices;
import com.kulvinder.livestream.mappers.Mapper;

@RestController
@RequestMapping("/gifts")
@CrossOrigin(origins = "*")
public class GiftsController {

    @Autowired
    private GiftServices giftServices;

    @Autowired
    private Mapper<GiftEntity, GiftDto> mapper;

    @Autowired
    private FileStorageService fileStorageService;

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
    public ResponseEntity<ApiResponse<GiftDto>> create(
            @RequestParam(value = "name", required = true) String name,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "price", required = true) Double price,
            @RequestParam(value = "isAnimated", required = false, defaultValue = "false") Boolean isAnimated,
            @RequestParam(value = "animationFile", required = false) MultipartFile animationFile) {
        
        try {
            GiftEntity giftEntity = GiftEntity.builder()
                    .name(name)
                    .price(price)
                    .isAnimated(isAnimated != null && isAnimated)
                    .build();

            // Handle image file upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                String imageUrl = fileStorageService.storeGiftImageFile(imageFile, name);
                giftEntity.setImage(imageUrl);
            }

            // Handle animation file upload if provided
            if (animationFile != null && !animationFile.isEmpty()) {
                String animationUrl = fileStorageService.storeGiftFile(animationFile, name);
                giftEntity.setAnimation(animationUrl);
                giftEntity.setIsAnimated(true);
            }

            GiftEntity createdGift = giftServices.create(giftEntity);
            return ResponseFactory.from(mapper.MapTo(createdGift), "success");
        } catch (IOException e) {
            return ResponseFactory.error("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseFactory.error("Failed to create gift: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftDto>> update(
            @PathVariable Long id,
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "isAnimated", required = false) Boolean isAnimated,
            @RequestParam(value = "animationFile", required = false) MultipartFile animationFile) {
        
        Optional<GiftEntity> existingGiftOpt = giftServices.findById(id);
        if (existingGiftOpt.isEmpty()) {
            return ResponseFactory.notFound("Gift not found");
        }

        try {
            GiftEntity existingGift = existingGiftOpt.get();
            
            // Update fields if provided
            if (name != null) {
                existingGift.setName(name);
            }
            if (price != null) {
                existingGift.setPrice(price);
            }
            if (isAnimated != null) {
                existingGift.setIsAnimated(isAnimated);
            }

            // Handle image file upload if provided
            if (imageFile != null && !imageFile.isEmpty()) {
                // Delete old file if exists
                if (existingGift.getImage() != null) {
                    fileStorageService.deleteGiftFile(existingGift.getImage());
                }
                // Store new file
                String imageUrl = fileStorageService.storeGiftImageFile(imageFile, 
                        name != null ? name : existingGift.getName());
                existingGift.setImage(imageUrl);
            }

            // Handle animation file upload if provided
            if (animationFile != null && !animationFile.isEmpty()) {
                // Delete old file if exists
                if (existingGift.getAnimation() != null) {
                    fileStorageService.deleteGiftFile(existingGift.getAnimation());
                }
                // Store new file
                String animationUrl = fileStorageService.storeGiftFile(animationFile, 
                        name != null ? name : existingGift.getName());
                existingGift.setAnimation(animationUrl);
                existingGift.setIsAnimated(true);
            }

            GiftEntity updatedGift = giftServices.update(existingGift);
            return ResponseFactory.from(mapper.MapTo(updatedGift), "success");
        } catch (IOException e) {
            return ResponseFactory.error("Failed to upload file: " + e.getMessage());
        } catch (Exception e) {
            return ResponseFactory.error("Failed to update gift: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<GiftDto>> delete(@PathVariable Long id) {
        Optional<GiftEntity> giftOpt = giftServices.findById(id);
        if (giftOpt.isPresent()) {
            GiftEntity gift = giftOpt.get();
            // Delete associated image file if exists
            if (gift.getImage() != null) {
                fileStorageService.deleteGiftFile(gift.getImage());
            }
            // Delete associated animation file if exists
            if (gift.getAnimation() != null) {
                fileStorageService.deleteGiftFile(gift.getAnimation());
            }
            giftServices.delete(id);
            return ResponseFactory.from(new GiftDto(), "success");
        }
        return ResponseFactory.notFound("Gift not found");
    }
}
