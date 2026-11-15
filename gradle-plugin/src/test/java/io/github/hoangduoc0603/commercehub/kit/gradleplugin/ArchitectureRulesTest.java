package io.github.hoangduoc0603.commercehub.kit.gradleplugin;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.gradle.testkit.runner.TaskOutcome.FAILED;
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Service áp plugin, viết đúng một class test như hướng dẫn trong HexagonalRules, và ./gradlew check chạy
// luật kiến trúc (G0-18 tiêu chí 3, story 59).
@DisplayName("Luật kiến trúc qua convention plugin (G0-18, story 59)")
class ArchitectureRulesTest {

    @TempDir
    Path projectDir;

    ServiceProject service;

    @BeforeEach
    void createService() {
        // spring-core để class domain vi phạm có thứ để import.
        service = new ServiceProject(projectDir)
                .withDependencies("implementation 'org.springframework:spring-core'")
                // Cách service bật luật (quyết định Q4 của ticket): một class test, không có task Gradle riêng.
                .write("src/test/java/demo/ArchitectureTest.java", """
                        package demo;

                        import com.tngtech.archunit.core.importer.ImportOption;
                        import com.tngtech.archunit.junit.AnalyzeClasses;
                        import com.tngtech.archunit.junit.ArchTest;
                        import com.tngtech.archunit.junit.ArchTests;
                        import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.HexagonalRules;

                        @AnalyzeClasses(packages = "demo", importOptions = ImportOption.DoNotIncludeTests.class)
                        class ArchitectureTest {
                            @ArchTest
                            static final ArchTests hexagonal = ArchTests.in(HexagonalRules.class);
                        }
                        """);
    }

    @Test
    void checkPassesForPlainJavaDomain() {
        service.write("src/main/java/demo/domain/Order.java", """
                package demo.domain;

                public record Order(String id) {
                }
                """);

        BuildResult result = service.gradle("check").build();

        assertEquals(SUCCESS, result.task(":test").getOutcome());
    }

    @Test
    void checkFailsWhenDomainImportsSpring() throws IOException {
        service.write("src/main/java/demo/domain/Order.java", """
                package demo.domain;

                import org.springframework.util.Assert;

                public record Order(String id) {
                    public Order {
                        Assert.hasText(id, "id");
                    }
                }
                """);

        BuildResult result = service.gradle("check").buildAndFail();

        assertEquals(FAILED, result.task(":test").getOutcome());
        // Đỏ đúng lý do: báo cáo JUnit của ArchitectureTest nêu class domain và class Spring nó gọi.
        String report = Files.readString(service.resolve("build/test-results/test/TEST-demo.ArchitectureTest.xml"));
        assertTrue(report.contains("demo.domain.Order") && report.contains("org.springframework.util.Assert"), report);
    }
}
