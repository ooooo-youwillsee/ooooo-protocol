package com.ooooo.protocol.http.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.ooooo.protocol.http.request.HttpProtocolConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
@Getter
@Setter
public class RegexRoutingHttpProtocolConfig extends HttpProtocolConfig {

    private String regex;

    @JSONField(serialize = false, deserialize = false)
    private Pattern regexPattern;

    public boolean match(String url) {
        if (regex == null) {
            throw new IllegalArgumentException("regex is null");
        }
        if (regexPattern == null) {
            regexPattern = Pattern.compile(regex);
        }
        Matcher matcher = regexPattern.matcher(url);
        return matcher.find();
    }
}
