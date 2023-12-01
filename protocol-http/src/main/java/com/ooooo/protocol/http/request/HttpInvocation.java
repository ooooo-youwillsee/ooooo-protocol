package com.ooooo.protocol.http.request;

import com.ooooo.protocol.core.Invocation;
import com.ooooo.protocol.core.exception.APIException;
import com.ooooo.protocol.core.util.APIServiceUtil;
import com.ooooo.protocol.http.annotation.HttpMapping;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
public class HttpInvocation implements Invocation {

    public static final int BODY_PARAMETER_INDEX = 0;

    public static final int QUERY_PARAM_PARAMETER_INDEX = 1;

    private Invocation invocation;

    public HttpInvocation(Invocation invocation) {
        this.invocation = invocation;
    }

    public String getContentType() {
        return getHttpMapping().contentType();
    }

    public String getUrl() {
        return getHttpMapping().value();
    }

    public HttpMethod getHttpMethod() {
        return getHttpMapping().method();
    }

    public HttpHeaders getHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.CONTENT_TYPE, getContentType());
        return httpHeaders;
    }

    /**
     * @return
     * @see MediaType#APPLICATION_JSON_VALUE
     */
    public Object getApplicationJsonBody() {
        return APIServiceUtil.getArgument(invocation.getArguments(), BODY_PARAMETER_INDEX);
    }


    /**
     * @return
     * @see MediaType#MULTIPART_FORM_DATA_VALUE
     */
    @SuppressWarnings({"DuplicatedCode", "unchecked"})
    public MultiValueMap<String, Object> getMultipartFormDataBody() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Object arg = APIServiceUtil.getArgument(invocation.getArguments(), BODY_PARAMETER_INDEX);
        if (arg == null) {
            return body;
        }

        if (arg instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) arg).entrySet()) {
                body.add(entry.getKey(), entry.getValue());
            }
            return body;
        }

        ReflectionUtils.doWithFields(arg.getClass(), f -> {
            ReflectionUtils.makeAccessible(f);
            Object value = ReflectionUtils.getField(f, arg);
            if (value != null) {
                body.add(f.getName(), value);
            }
        });
        return body;
    }

    /**
     * @return
     * @see MediaType#APPLICATION_FORM_URLENCODED_VALUE
     */
    @SuppressWarnings({"DuplicatedCode", "unchecked"})
    public MultiValueMap<String, Object> getApplicationFormUrlEncodedBody() {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        Object arg = APIServiceUtil.getArgument(invocation.getArguments(), BODY_PARAMETER_INDEX);
        if (arg == null) {
            return body;
        }

        if (arg instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) arg).entrySet()) {
                body.add(entry.getKey(), entry.getValue());
            }
            return body;
        }

        ReflectionUtils.doWithFields(arg.getClass(), f -> {
            ReflectionUtils.makeAccessible(f);
            Object value = ReflectionUtils.getField(f, arg);
            if (value != null) {
                body.add(f.getName(), value);
            }
        });
        return body;
    }

    /**
     * @return
     * @see MediaType#APPLICATION_OCTET_STREAM_VALUE
     */
    public byte[] getApplicationOctetStreamBody() {
        Object arg = APIServiceUtil.getArgument(invocation.getArguments(), BODY_PARAMETER_INDEX);
        if (arg == null) {
            return null;
        }

        if (arg instanceof byte[]) {
            return (byte[]) arg;
        }

        throw new APIException("The arg0 must be type of byte[] when content-type is APPLICATION_OCTET_STREAM_VALUE");
    }

    public Map<String, String> getQueryParams() {
        Object obj = APIServiceUtil.getArgument(invocation.getArguments(), QUERY_PARAM_PARAMETER_INDEX);
        return APIServiceUtil.toStringStringMapUsingJson(obj);
    }


    public Map<String, String> getUrlMap() {
        Map<String, String> urlMap = new HashMap<>();
        String url = getHttpMapping().value();
        if (url.contains("=")) {
            // 使用原先的配置形式， subSystemName = value1 , functionName = value2
            String[] split = url.split(",");
            for (String s : split) {
                String[] kv = s.split("=");
                urlMap.put(kv[0].trim(), kv[1].trim());
            }
        }

        return urlMap;
    }

    private HttpMapping getHttpMapping() {
        return APIServiceUtil.getAnnotation(invocation.getMethod(), HttpMapping.class);
    }

    @Override
    public Method getMethod() {
        return invocation.getMethod();
    }

    @Override
    public Object[] getArguments() {
        return invocation.getArguments();
    }


}
