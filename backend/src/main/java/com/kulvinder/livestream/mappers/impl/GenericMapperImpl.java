package com.kulvinder.livestream.mappers.impl;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import com.kulvinder.livestream.mappers.Mapper;

public class GenericMapperImpl<A, B> implements Mapper<A, B> {

    private final ModelMapper modelMapper;
    private final Class<A> entityClass;
    private final Class<B> dtoClass;

    public GenericMapperImpl(ModelMapper modelMapper, Class<A> entityClass, Class<B> dtoClass) {
        this.modelMapper = modelMapper;
        this.entityClass = entityClass;
        this.dtoClass = dtoClass;
    }

    @Override
    public B MapTo(A a) {
        return modelMapper.map(a, dtoClass);
    }

    @Override
    public A MapFrom(B b) {
        return modelMapper.map(b, entityClass);
    }

}
