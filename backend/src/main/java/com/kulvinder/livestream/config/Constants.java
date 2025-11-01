package com.kulvinder.livestream.config;

import org.springframework.data.domain.Sort;

public class Constants {

    // response headers
    public static final String REQUEST_SUCCESS = "200";
    public static final String REQUEST_SUCCESS_MSG = "success";

    public static final String REQUEST_NO_RECORDS = "204";
    public static final String REQUEST_NO_RECORDS_MSG = "no records found";

    public static final String REQUEST_ERROR = "404";
    public static final String REQUEST_ERROR_MSG = "error";

    public static final String BAD_REQUEST = "400";
    public static final String BAD_REQUEST_MSG = "invalid request or params";

    public static final String REQUEST_CONFLICT = "409";
    public static final String REQUEST_CONFLICT_MSG = "conflict";

    // App settings

    // default starting point for showAll functions
    public static final String STARTINGPOINT = "0";
    // default Limit for show all functions
    public static final String LIMIT = "10";

    // Sorting for user list
    public static final Sort USERSORTING = Sort.by("id").ascending();
    // Sorting for livestream list
    public static final Sort LIVESTREAMSORTING = Sort.by("id").ascending();

}
