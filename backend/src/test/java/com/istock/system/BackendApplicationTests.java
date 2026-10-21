package com.istock.system;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = { "spring.datasource.url=jdbc:h2:mem:context-tests;NON_KEYWORDS=SYSTEM_USER", "spring.datasource.driver-class-name=org.h2.Driver", "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect" })
class BackendApplicationTests {

    @Test
    void contextLoads() {
    }

}
