package com.pbl.loadtestweb.jdbcrequest.mapper;

import com.pbl.loadtestweb.jdbcrequest.payload.response.JdbcDataResponse;
import org.mapstruct.Mapping;

public interface JdbcRequestMapper {
    @Mapping(source = "threadName", target = "threadName")
    @Mapping(source = "iterations", target = "iterations")
    @Mapping(source = "startAt", target = "startAt")
    @Mapping(source = "responseCode", target = "responseCode")
    @Mapping(source = "responseMessage", target = "responseMessage")
    @Mapping(source = "contentType", target = "contentType")
    @Mapping(source = "dataEncoding", target = "dataEncoding")
    @Mapping(source = "requestMethod", target = "requestMethod")
    @Mapping(source = "loadTime", target = "loadTime")
    @Mapping(source = "connectTime", target = "connectTime")
    @Mapping(source = "latency", target = "latency")
    @Mapping(source = "headerSize", target = "headerSize")
    @Mapping(source = "bodySize", target = "bodySize")
    JdbcDataResponse toHttpDataResponse(
            String threadName,
            String iterations,
            String startAt,
            String responseCode,
            String responseMessage,
            String contentType,
            String dataEncoding,
            String requestMethod,
            String loadTime,
            String connectTime,
            String latency,
            String headerSize,
            String bodySize);
}
