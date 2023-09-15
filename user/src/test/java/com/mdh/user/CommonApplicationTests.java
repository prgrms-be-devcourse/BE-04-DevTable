package com.mdh.user;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

@SpringBootTest
@ComponentScan({
        "com.mdh.common"
})
class CommonApplicationTests {

    @Test
    void contextLoads() {
    }

}
