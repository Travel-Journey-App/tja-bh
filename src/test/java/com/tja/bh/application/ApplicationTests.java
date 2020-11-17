package com.tja.bh.application;

import com.tja.bh.config.LocalTestConfiguration;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@AutoConfigureEmbeddedDatabase(beanName = "dataSource")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LocalTestConfiguration.class)
class ApplicationTests {

    @Test
    void contextLoads() {
    }

}
