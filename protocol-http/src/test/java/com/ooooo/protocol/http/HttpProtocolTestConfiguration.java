package com.ooooo.protocol.http;

import lombok.SneakyThrows;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@RestController
@SpringBootApplication
public class HttpProtocolTestConfiguration {


    @PostMapping("/testPostJson")
    public String testPostJson(@RequestBody Map<String, Object> body) {
        return (String) body.get("abc");
    }

    @PostMapping("/testPostForm")
    public String testPostForm(String abc) {
        return abc;
    }

    @SneakyThrows
    @PostMapping("/testPostFile")
    public String testPostFile(MultipartFile abc) {
        return new String(abc.getBytes(), StandardCharsets.UTF_8);
    }

    @GetMapping("/testGetQuery")
    public String testGetQuery(String abc) {
        return abc;
    }
}
