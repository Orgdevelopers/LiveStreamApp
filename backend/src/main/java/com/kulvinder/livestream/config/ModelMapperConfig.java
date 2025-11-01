package com.kulvinder.livestream.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.kulvinder.livestream.domain.models.dtos.LiveStreamDto;
import com.kulvinder.livestream.domain.models.dtos.UserDto;
import com.kulvinder.livestream.domain.models.entities.LiveStreamEntity;
import com.kulvinder.livestream.domain.models.entities.UserEntity;
import com.kulvinder.livestream.mappers.Mapper;
import com.kulvinder.livestream.mappers.impl.GenericMapperImpl;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public Mapper<UserEntity,UserDto> userMapper(ModelMapper mapper){
        return new GenericMapperImpl<>(mapper, UserEntity.class, UserDto.class);
    }

    @Bean
    public Mapper<LiveStreamEntity, LiveStreamDto> livestreamMapper(ModelMapper mapper){
        return new GenericMapperImpl<>(mapper, LiveStreamEntity.class, LiveStreamDto.class);
    }
    
}