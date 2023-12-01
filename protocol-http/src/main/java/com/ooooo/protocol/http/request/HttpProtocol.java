package com.ooooo.protocol.http.request;

import cn.hutool.core.util.StrUtil;
import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.Protocol;
import com.ooooo.protocol.core.context.APIServiceContext;
import com.ooooo.protocol.core.exception.APIException;
import com.ooooo.protocol.core.request.ProtocolProperties;
import com.ooooo.protocol.core.util.APIServiceUtil;
import com.ooooo.protocol.http.util.RestTemplateBuilder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Slf4j
public class HttpProtocol implements Protocol {

    private final Map<String, RestTemplate> restTemplateMap = new ConcurrentHashMap<>();

    @Getter
    protected ProtocolProperties properties;

    protected HttpProtocolConfig config;

    public HttpProtocol(ProtocolProperties properties) {
        this.properties = properties;
        this.config = properties.resolveProtocolConfig(HttpProtocolConfig.class);
    }

    protected RestTemplate getRestTemplate(HttpProtocolConfig config) {
        return restTemplateMap.computeIfAbsent(config.getUrl(),
                __ -> RestTemplateBuilder.builder().connectTimeout(config.getConnectTimeout()).readTimeout(config.getReadTimeout()).isHttps(StrUtil.startWithIgnoreCase(config.getUrl(), "https://")).tlsVersion(config.getTlsVersion()).cert(config.getCert()).build());
    }

    @Override
    public Object execute(Invocation invocation) throws Throwable {
        HttpInvocation httpInvocation = new HttpInvocation(invocation);
        ResponseEntity<byte[]> responseEntity = doExecute(httpInvocation);
        return handleResponseEntity(responseEntity);
    }


    protected ResponseEntity<byte[]> doExecute(HttpInvocation invocation) {
        doHandleHttpInvocation(invocation);

        // body
        String contentType = invocation.getContentType();
        Object body = null;
        switch (contentType) {
            case APPLICATION_JSON_VALUE:
                body = invocation.getApplicationJsonBody();
                break;
            case MULTIPART_FORM_DATA_VALUE:
                body = invocation.getMultipartFormDataBody();
                break;
            case APPLICATION_FORM_URLENCODED_VALUE:
                body = invocation.getApplicationFormUrlEncodedBody();
                break;
            case APPLICATION_OCTET_STREAM_VALUE:
                body = invocation.getApplicationOctetStreamBody();
                break;
            case "":
                // GET 无 Content-type
                body = null;
                break;
            default:
                throw new APIException("contentType['" + contentType + "'] not handle");
        }
        HttpProtocolConfig config = getConfig(invocation);
        URI uri = getURI(invocation, config);
        HttpMethod httpMethod = invocation.getHttpMethod();
        HttpHeaders httpHeaders = invocation.getHttpHeaders();
        HttpEntity<?> httpEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<byte[]> responseEntity = getRestTemplate(config).exchange(uri, httpMethod, httpEntity,
                byte[].class);
        return responseEntity;
    }

    // 子类可以重写
    protected Object handleResponseEntity(ResponseEntity<byte[]> entity) {
        if (!entity.getStatusCode().equals(HttpStatus.OK)) {
            throw new APIException(entity.getStatusCode().toString(), new String(entity.getBody(), getCharset(entity)));
        }

        Method method = APIServiceContext.getAPIMethodConfig().getMethod();
        if (method.getReturnType().isAssignableFrom(byte[].class) || entity.getBody() == null) {
            return entity.getBody();
        }

        return doHandleResponseEntity(method, new String(entity.getBody(), getCharset(entity)));
    }

    /**
     * 扩展点，可用于添加cookie
     *
     * @param invocation
     * @return
     */
    protected void doHandleHttpInvocation(HttpInvocation invocation) {
    }

    /**
     * 扩展点，可用于获取cookie
     *
     * @param method
     * @param body
     * @return
     */
    protected Object doHandleResponseEntity(Method method, String body) {
        return APIServiceUtil.parseData(method.getReturnType(), body);
    }

    /**
     * default charset is UTF-8
     * see StringHttpMessageConverter#getContentTypeCharset(MediaType)
     *
     * @return
     */
    protected Charset getCharset(ResponseEntity<byte[]> responseEntity) {
        if (responseEntity == null) {
            throw new APIException("responseEntity is null");
        }
        MediaType contentType = responseEntity.getHeaders().getContentType();
        if (contentType == null) {
            return StandardCharsets.UTF_8;
        }

        Charset charset = contentType.getCharset();
        if (charset != null) {
            return charset;
        }
        return StandardCharsets.UTF_8;
    }

    /**
     * 构建url
     *
     * @param invocation
     * @param config
     * @return url
     */
    protected URI getURI(HttpInvocation invocation, HttpProtocolConfig config) {
        UriComponentsBuilder builder =
                UriComponentsBuilder.newInstance().uri(URI.create(config.getUrl())).path(invocation.getUrl()).path(config.getSuffix());

        invocation.getQueryParams().forEach(builder::queryParam);
        URI uri = builder.build().encode(StandardCharsets.UTF_8).toUri();
        return uri;
    }

    protected HttpProtocolConfig getConfig(HttpInvocation invocation) {
        return config;
    }

}
