package com.ooooo.protocol.http.request;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
@Data
public class HttpProtocolConfig {

    /**
     * eg: http://127.0.0.1:8080/
     */
    protected String url;

    /**
     * default is '.json',so /abc -> /abc.json, /abc -> /abc.json
     */
    protected String suffix = "";

    private Map<String, String> headers = new LinkedHashMap<>();

    protected int readTimeout = 60 * 1000 * 3;

    protected int connectTimeout = 10 * 1000;

    protected String certPath;

    protected String tlsVersion = "TLSv1.2";

    public InputStream getCert() {
        if (certPath == null) {
            return null;
        }

        log.info("load certPath [{}]", certPath);
        PathMatchingResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver();
        try {
            Resource resource = patternResolver.getResources(certPath)[0];
            return resource.getInputStream();
        } catch (IOException e) {
            throw new IllegalArgumentException("load cert fail, path: " + certPath, e);
        }
    }
}
