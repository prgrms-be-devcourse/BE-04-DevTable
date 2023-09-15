package com.mdh.owner;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
@ComponentScan("com.mdh.common")
public class JpaTestConfig {
}
