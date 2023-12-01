package com.ooooo.protocol.http
        ;

import cn.hutool.core.io.FileUtil;
import com.ooooo.protocol.http.service.DemoService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Map;

/**
 * @author <a href="https://github.com/ooooo-youwillsee">ooooo</a>
 * 2022/3/18 16:14
 * @since 1.0.0
 */
@SpringBootTest(classes = HttpProtocolTestConfiguration.class, webEnvironment =
        SpringBootTest.WebEnvironment.DEFINED_PORT)
public class DemoServiceTest {

    @Autowired
    private DemoService demoService;

    @Test
    void test() {
        Map<String, Object> map = Collections.singletonMap("abc", "123");

        // 第一个约定为body参数，即使不传值也要占用一个位置
        // 第二个约定为query参数，会添加在 url 上面
        String result1 = demoService.testGetQuery(null, map);
        Assertions.assertThat(result1).isEqualTo("123");

        String result2 = demoService.testPostJson(map);
        Assertions.assertThat(result2).isEqualTo("123");

        String result3 = demoService.testPostForm(map);
        Assertions.assertThat(result3).isEqualTo("123");

        File file = new File("test");
        FileUtil.appendString("123", file, StandardCharsets.UTF_8);
        try {
            Map<String, Object> files = Collections.singletonMap("abc", new FileSystemResource(file));
            String result4 = demoService.testPostFile(files);
            Assertions.assertThat(result4).isEqualTo("123");
        } finally {
            FileUtil.del(file);
        }

    }

}
