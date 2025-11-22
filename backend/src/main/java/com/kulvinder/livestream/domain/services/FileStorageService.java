package com.kulvinder.livestream.domain.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${assets.gifts.path:assets/gifts}")
    private String assetsGiftsPath;

    @Value("${server.servlet.context-path:/}")
    private String contextPath;

    @Value("${assets.gifts.base-url:}")
    private String baseUrl;

    /**
     * Store a gift animation file (lottie or json)
     * @param file The uploaded file
     * @param giftName The name of the gift (used for filename)
     * @return The relative URL path to access the file (e.g., /assets/gifts/animations/rose.lottie)
     */
    public String storeGiftFile(MultipartFile file, String giftName) throws IOException {
        return storeGiftFile(file, giftName, "animations");
    }

    /**
     * Store a gift image file (png, jpg, etc.)
     * @param file The uploaded file
     * @param giftName The name of the gift (used for filename)
     * @return The relative URL path to access the file (e.g., /assets/gifts/images/rose.png)
     */
    public String storeGiftImageFile(MultipartFile file, String giftName) throws IOException {
        return storeGiftFile(file, giftName, "images");
    }

    /**
     * Store a gift file (animation or image)
     * @param file The uploaded file
     * @param giftName The name of the gift (used for filename)
     * @param subdirectory The subdirectory ("images" or "animations")
     * @return The relative URL path to access the file
     */
    private String storeGiftFile(MultipartFile file, String giftName, String subdirectory) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // Create subdirectory path: assets/gifts/images or assets/gifts/animations
        Path subdirectoryPath = Paths.get(assetsGiftsPath, subdirectory);
        if (!Files.exists(subdirectoryPath)) {
            Files.createDirectories(subdirectoryPath);
        }

        // Get original filename extension
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generate filename: giftName + extension (e.g., "rose.png" or "rose.lottie")
        // Sanitize gift name to remove special characters
        String sanitizedName = giftName.toLowerCase()
                .replaceAll("[^a-z0-9]", "_")
                .replaceAll("_+", "_");
        
        String filename = sanitizedName + extension;
        
        // If file already exists, add UUID to make it unique
        Path targetPath = subdirectoryPath.resolve(filename);
        if (Files.exists(targetPath)) {
            String uniqueFilename = sanitizedName + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;
            targetPath = subdirectoryPath.resolve(uniqueFilename);
            filename = uniqueFilename;
        }

        // Save the file
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Build the URL path
        String urlPath = "/assets/gifts/" + subdirectory + "/" + filename;
        
        // If baseUrl is configured, prepend it
        if (baseUrl != null && !baseUrl.isEmpty()) {
            String base = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
            return base + urlPath;
        }
        
        // Otherwise return relative path (will be handled by frontend)
        return urlPath;
    }

    /**
     * Delete a gift file
     * @param fileUrl The URL path of the file (e.g., /assets/gifts/images/rose.png or /assets/gifts/animations/rose.lottie)
     * @return true if file was deleted, false otherwise
     */
    public boolean deleteGiftFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            // Extract path after /assets/gifts/ (e.g., "images/rose.png" or "animations/rose.lottie")
            String pathAfterAssets = fileUrl.replaceFirst("^/assets/gifts/", "");
            Path filePath = Paths.get(assetsGiftsPath, pathAfterAssets);
            
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                return true;
            }
        } catch (IOException e) {
            // Log error if needed
            return false;
        }
        
        return false;
    }

    /**
     * Get the absolute path to the assets directory
     */
    public Path getAssetsDirectory() {
        return Paths.get(assetsGiftsPath).toAbsolutePath();
    }
}

