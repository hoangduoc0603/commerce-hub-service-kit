package io.github.hoangduoc0603.commercehub.kit.testsupport.containers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;

import static org.assertj.core.api.Assertions.assertThat;

// Dựng context giống một service: auto-configuration tạo DataSource, @ServiceConnection trỏ nó vào container.
@DisplayName("Testcontainers PostgreSQL (G0-18)")
@SpringBootTest(classes = PostgresTestcontainersConfigurationTest.TestApplication.class)
@Import(PostgresTestcontainersConfiguration.class)
class PostgresTestcontainersConfigurationTest {

    // test-support không có class @SpringBootApplication; class lồng này đóng vai đó cho riêng test.
    // Không có @ComponentScan, nên fixture ArchUnit không bị nạp vào context.
    @SpringBootConfiguration
    @EnableAutoConfiguration
    static class TestApplication {
    }

    @Autowired
    JdbcClient jdbc;

    @Test
    void connectsToPostgresVersionFromImageTable() {
        // Ví dụ: "18.7 (Debian 18.7-1.pgdg13+2)".
        String serverVersion = jdbc.sql("show server_version").query(String.class).single();

        // getVersionPart() là phần sau dấu ':' của tag, ở đây "18.7". Thêm dấu cách để "18.7" không khớp nhầm "18.70".
        assertThat(serverVersion).startsWith(ContainerImages.POSTGRES.getVersionPart() + " ");
    }
}
