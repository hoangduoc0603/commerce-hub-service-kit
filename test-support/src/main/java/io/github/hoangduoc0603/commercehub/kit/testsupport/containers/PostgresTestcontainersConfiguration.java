package io.github.hoangduoc0603.commercehub.kit.testsupport.containers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * PostgreSQL thật cho test của service, nối vào DataSource bằng {@code @ServiceConnection}.
 *
 * <p>Service bật bằng:
 * <pre>{@code
 * @SpringBootTest
 * @Import(PostgresTestcontainersConfiguration.class)
 * class OrderRepositoryIT { ... }
 * }</pre>
 * Service cần có DataSource (spring-boot-starter-jdbc hoặc data-jpa) và driver PostgreSQL. Các class test cùng
 * cấu hình dùng chung một Spring context, nên dùng chung một container.
 */
// @TestConfiguration: không bị component scan của service nhặt nhầm, chỉ vào context khi được @Import.
// proxyBeanMethods = false: không tạo proxy CGLIB vì các @Bean không gọi nhau; Boot docs khuyên vậy cho cấu hình test.
@TestConfiguration(proxyBeanMethods = false)
public class PostgresTestcontainersConfiguration {

    // @ServiceConnection: Boot đọc kiểu trả về để biết đây là PostgreSQL, tạo JdbcConnectionDetails từ
    // container (URL, user, mật khẩu ngẫu nhiên). Không cần @DynamicPropertySource hay spring.datasource.*.
    // Container là bean, nên Spring khởi động nó trước bean phụ thuộc (DataSource) và dừng khi context đóng.
    @Bean
    @ServiceConnection
    PostgreSQLContainer postgresContainer() {
        return new PostgreSQLContainer(ContainerImages.POSTGRES);
    }
}
