package com.kulvinder.livestream.mappers;

public interface Mapper<A, B> {

    B MapTo(A a);

    A MapFrom(B b);
    
}
