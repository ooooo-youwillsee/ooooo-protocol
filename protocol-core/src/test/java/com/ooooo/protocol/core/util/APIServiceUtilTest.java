package com.ooooo.protocol.core.util;

import cn.hutool.core.codec.Base64;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * @since 1.0.0
 */
class APIServiceUtilTest {

    private TestParamRequest request;

    @BeforeEach
    void beforeEach() {
        request = new TestParamRequest("1", "2", new byte[]{1, 2, 3, 4});
    }

    @Test
    void toStringStringMapUsingJson() {
        Map<String, String> stringStringMap = APIServiceUtil.toStringStringMapUsingJson(request);

        assertEquals("1", stringStringMap.get("a"));
        assertEquals("2", stringStringMap.get("b"));
        assertEquals(Base64.encode(new byte[]{1, 2, 3, 4}), stringStringMap.get("c"));
    }

    @Test
    void toStringObjectMapUsingJson() {
        Map<String, Object> stringObjectMap = APIServiceUtil.toStringObjectMapUsingJson(request);

        assertEquals("1", stringObjectMap.get("a"));
        assertEquals("2", stringObjectMap.get("b"));
        assertArrayEquals(new byte[]{1, 2, 3, 4}, (byte[]) stringObjectMap.get("c"));
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Setter
    private static class TestParamRequest {

        private String a;

        private String b;

        private byte[] c;
    }
}