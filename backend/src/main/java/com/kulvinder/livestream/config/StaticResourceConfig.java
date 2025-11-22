package com.kulvinder.livestream.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

@Configuration
public class StaticResourceConfig implements WebMvcConfigurer {

    @Value("${assets.gifts.path:assets/gifts}")
    private String assetsGiftsPath;
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Get the absolute path to the assets folder
        // This will be relative to the working directory (backend root)
        String assetsPath = Paths.get(assetsGiftsPath).toAbsolutePath().toString();
        
        // Ensure path ends with separator for proper file serving
        if (!assetsPath.endsWith("/") && !assetsPath.endsWith("\\")) {
            assetsPath += "/";
        }
        
        // Serve files from assets/gifts folder (including subdirectories) at /assets/gifts/** URL
        // This will serve files from both assets/gifts/images/ and assets/gifts/animations/
        registry.addResourceHandler("/assets/gifts/**")
                .addResourceLocations("file:" + assetsPath);
    }
}

