package com.kulvinder.livestream.beans;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.kulvinder.livestream.config.Constants;

import java.util.List;
import java.util.Optional;

public class ResponseFactory {

    public static <T> ResponseEntity<ApiResponse<T>> from(Optional<T> optional, String msg) {

        if (optional.isPresent()) {

            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ApiResponse<>(Constants.REQUEST_SUCCESS,msg, optional.get()));
        }
        return ResponseFactory.notFound(msg);

    }

    public static <T> ResponseEntity<ApiResponse<List<T>>> from(List<T> list, String msg) {
        if (list != null && !list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(Constants.REQUEST_SUCCESS,msg, list));

        }
        return ResponseFactory.notFound(msg);

    }

    public static <T> ResponseEntity<ApiResponse<T>> from(T obj,String msg) {
        if (obj != null) {
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(Constants.REQUEST_SUCCESS,msg, obj));

        }
        return ResponseFactory.notFound(msg);

    }

    public static <T> ResponseEntity<ApiResponse<T>> createdFrom(T obj,String msg) {
        if (obj != null) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(Constants.REQUEST_SUCCESS,msg, obj));

        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(Constants.REQUEST_SUCCESS,msg, null));

    }

    public static <T> ResponseEntity<ApiResponse<T>> notFound(String msg) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(Constants.REQUEST_NO_RECORDS,msg, null));

    }

    public static <T> ResponseEntity<ApiResponse<T>> invalidRequest(String msg) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiResponse<>(Constants.BAD_REQUEST,msg, null));

    }

    public static <T> ResponseEntity<ApiResponse<T>> conflict(String msg) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiResponse<>(Constants.REQUEST_CONFLICT,msg, null));

    }

    public static <T> ResponseEntity<ApiResponse<T>> error(String msg) {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiResponse<>(Constants.REQUEST_ERROR,msg, null));

    }

}
