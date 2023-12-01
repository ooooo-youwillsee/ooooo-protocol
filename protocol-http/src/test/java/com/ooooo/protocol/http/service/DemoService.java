package com.ooooo.protocol.http.service;

import com.ooooo.protocol.core.annotation.APIService;
import com.ooooo.protocol.http.annotation.HttpMapping;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@APIService
public interface DemoService {

    @HttpMapping(value = "/testPostJson", method = HttpMethod.POST, contentType = MediaType.APPLICATION_JSON_VALUE,
            note = "测试提交json")
    String testPostJson(Map<String, Object> body);


    @HttpMapping(value = "/testPostForm", method = HttpMethod.POST, contentType =
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            note = "测试提交form")
    String testPostForm(Map<String, Object> body);


    @HttpMapping(value = "/testPostFile", method = HttpMethod.POST, contentType = MediaType.MULTIPART_FORM_DATA_VALUE,
            note = "测试上传文件")
    String testPostFile(Map<String, Object> body);


    @HttpMapping(value = "/testGetQuery", method = HttpMethod.GET, note = "测试GET")
    String testGetQuery(Object nullBody, Map<String, Object> query);
}
