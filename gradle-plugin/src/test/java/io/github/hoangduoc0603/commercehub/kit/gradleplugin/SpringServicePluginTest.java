package io.github.hoangduoc0603.commercehub.kit.gradleplugin;

import org.gradle.testkit.runner.BuildResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Mỗi test dựng một service tạm chỉ có plugins { id 'commercehub.spring-service' }, chạy Gradle thật qua TestKit,
// rồi kiểm một điều plugin hứa (G0-18, story 58).
@DisplayName("Convention plugin commercehub.spring-service (G0-18, story 58)")
class SpringServicePluginTest {

    // JUnit tạo thư mục rỗng cho mỗi test và xóa khi xong: các test không dùng chung project.
    @TempDir
    Path projectDir;

    ServiceProject service;

    @BeforeEach
    void createService() {
        service = new ServiceProject(projectDir);
    }

    @Test
    void compilesMainCodeToJava25Bytecode() throws IOException {
        service.withDependencies()
                .write("src/main/java/demo/App.java", "package demo; public class App {}");

        BuildResult result = service.gradle("compileJava").build();

        assertEquals(SUCCESS, result.task(":compileJava").getOutcome());
        // Class file Java 25 có major version 69 (JVMS, mục 4.1).
        assertEquals(69, classFileMajorVersion(service.resolve("build/classes/java/main/demo/App.class")));
    }

    @Test
    void managesSpringDependencyVersionsFromKitCatalog() {
        // Service khai phụ thuộc Spring không ghi version.
        service.withDependencies("implementation 'org.springframework.boot:spring-boot-starter'");

        BuildResult result = service.gradle("dependencies", "--configuration", "runtimeClasspath").build();

        // Gradle in "group:name -> version" khi version đến từ BOM.
        String expected = "org.springframework.boot:spring-boot-starter -> " + System.getProperty("kit.springBootVersion");
        assertTrue(result.getOutput().contains(expected), result::getOutput);
    }

    @Test
    void runsJunitPlatformTests() {
        service.withDependencies("testImplementation 'org.springframework.boot:spring-boot-starter-test'")
                .write("src/test/java/demo/SmokeTest.java", """
                        package demo;

                        import org.junit.jupiter.api.Test;

                        class SmokeTest {
                            @Test
                            void runs() {
                            }
                        }
                        """);

        BuildResult result = service.gradle("test").build();

        assertEquals(SUCCESS, result.task(":test").getOutcome());
        // Có file kết quả nghĩa là JUnit Platform đã tìm và chạy đúng class này. Thiếu useJUnitPlatform() thì
        // Gradle 9 làm task test đỏ ("did not discover any tests"), nên test này cũng đỏ.
        assertTrue(Files.exists(service.resolve("build/test-results/test/TEST-demo.SmokeTest.xml")));
    }

    @Test
    void addsTestSupportWithPluginVersion() {
        service.withDependencies();

        BuildResult result = service.gradle("dependencies", "--configuration", "testRuntimeClasspath").build();

        // Cùng version với plugin: hai artifact build từ một commit, publish cùng một tag.
        String expected = "io.github.hoangduoc0603.commercehub.kit:test-support:" + System.getProperty("kit.version");
        assertTrue(result.getOutput().contains(expected), result::getOutput);
    }

    // Class file bắt đầu bằng magic (4 byte), minor version (2 byte), major version (2 byte).
    private static int classFileMajorVersion(Path classFile) throws IOException {
        try (DataInputStream in = new DataInputStream(Files.newInputStream(classFile))) {
            in.readInt();
            in.readUnsignedShort();
            return in.readUnsignedShort();
        }
    }
}
