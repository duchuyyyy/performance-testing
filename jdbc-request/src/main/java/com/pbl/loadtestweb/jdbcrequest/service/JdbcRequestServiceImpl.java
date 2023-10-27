package com.pbl.loadtestweb.jdbcrequest.service;

import com.mysql.cj.jdbc.JdbcConnection;
import com.mysql.jdbc.Driver;
import com.pbl.loadtestweb.common.common.CommonFunction;
import com.pbl.loadtestweb.common.constant.CommonConstant;
import com.pbl.loadtestweb.jdbcrequest.mapper.JdbcRequestMapper;
import com.pbl.loadtestweb.jdbcrequest.payload.response.JdbcDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JdbcRequestServiceImpl {
  private final JdbcRequestMapper jdbcRequestMapper;

  public JdbcDataResponse handleJdbcRequest(
      String databaseUrl, String jdbcDriverClass, String username, String password, String query)
      throws ClassNotFoundException {
    Map<String, String> result =
        this.loadTestThread(databaseUrl, jdbcDriverClass, username, password, query);
    return buildJdbcDataResponse(result);
  }

  private JdbcDataResponse buildJdbcDataResponse(Map<String, String> result) {
    return jdbcRequestMapper.toJdbcDataResponse(
        result.get(CommonConstant.THREAD_NAME),
        // result.get(CommonConstant.ITERATIONS),
        result.get(CommonConstant.START_AT),
        result.get(CommonConstant.RESPONSE_CODE),
        result.get(CommonConstant.RESPONSE_MESSAGE),
        result.get(CommonConstant.CONTENT_TYPE),
        result.get(CommonConstant.DATA_ENCODING),
        // result.get(CommonConstant.REQUEST_METHOD),
        result.get(CommonConstant.LOAD_TIME),
        result.get(CommonConstant.CONNECT_TIME),
        result.get(CommonConstant.LATENCY),
        result.get(CommonConstant.HEADER_SIZE),
        result.get(CommonConstant.BODY_SIZE));
  }

  private long calcBodySize(
      Connection connection,
      String databaseUrl,
      String jdbcDriverClass,
      String username,
      String password,
      String query)
      throws ClassNotFoundException, SQLException {
    Class.forName(jdbcDriverClass);
    connection = DriverManager.getConnection(databaseUrl, username, password);
    Statement statement = connection.createStatement();
    ResultSet resultSet = statement.executeQuery(query);
    long bodySize = 0;
    while (resultSet.next()) {
      StringBuilder rowBuilder = new StringBuilder();
      for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
        rowBuilder.append(resultSet.getString(i));
      }
      bodySize += rowBuilder.toString().getBytes().length;
    }
    return bodySize;
  }

  public Map<String, String> loadTestThread(
      String databaseUrl, String jdbcDriverClass, String username, String password, String query)
      throws ClassNotFoundException {
    Map<String, String> result = new HashMap<>();

    long startTime = System.currentTimeMillis();
    Class.forName(jdbcDriverClass);

    try {
      Connection connection = DriverManager.getConnection(databaseUrl, username, password);
      long connectTime = System.currentTimeMillis() - startTime;
      Statement statement = connection.createStatement();
      ResultSet resultSet = statement.executeQuery(query);

      long latency = 76;
      long loadTime = 77;
      result.put(CommonConstant.LOAD_TIME, String.valueOf(loadTime));
      result.put(CommonConstant.CONNECT_TIME, String.valueOf(connectTime));
      result.put(CommonConstant.LATENCY, String.valueOf(latency));
      result.put(CommonConstant.HEADER_SIZE, String.valueOf(0));
      result.put(
          CommonConstant.BODY_SIZE,
          String.valueOf(
              this.calcBodySize(
                  connection, databaseUrl, jdbcDriverClass, username, password, query)));
      result.put(
          CommonConstant.START_AT,
          CommonFunction.formatDateToString(CommonFunction.getCurrentDateTime()));
      result.put(CommonConstant.THREAD_NAME, Thread.currentThread().getName());
      // result.put(CommonConstant.ITERATIONS, Integer.toString(iterations));
      result.put(
          CommonConstant.RESPONSE_CODE, Integer.toString(resultSet.getWarnings().getErrorCode()));
      result.put(CommonConstant.RESPONSE_MESSAGE, resultSet.getWarnings().getMessage());
      result.put(CommonConstant.CONTENT_TYPE, "Text");
      result.put(CommonConstant.DATA_ENCODING, "Chịu");

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    return result;
  }
}
