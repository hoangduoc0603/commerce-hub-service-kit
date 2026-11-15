package io.github.hoangduoc0603.commercehub.kit.gradleplugin;

import org.gradle.testkit.runner.BuildResult;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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

    @Test
    void compilesMainCodeToJava25Bytecode() throws IOException {
        writeService("");
        write("src/main/java/demo/App.java", "package demo; public class App {}");

        BuildResult result = gradle("compileJava").build();

        assertEquals(SUCCESS, result.task(":compileJava").getOutcome());
        // Class file Java 25 có major version 69 (JVMS, mục 4.1).
        assertEquals(69, classFileMajorVersion(projectDir.resolve("build/classes/java/main/demo/App.class")));
    }

    @Test
    void managesSpringDependencyVersionsFromKitCatalog() throws IOException {
        // Service khai phụ thuộc Spring không ghi version.
        writeService("implementation 'org.springframework.boot:spring-boot-starter'");

        BuildResult result = gradle("dependencies", "--configuration", "runtimeClasspath").build();

        // Gradle in "group:name -> version" khi version đến từ BOM.
        String expected = "org.springframework.boot:spring-boot-starter -> " + System.getProperty("kit.springBootVersion");
        assertTrue(result.getOutput().contains(expected), result::getOutput);
    }

    @Test
    void runsJunitPlatformTests() throws IOException {
        writeService("testImplementation 'org.springframework.boot:spring-boot-starter-test'");
        write("src/test/java/demo/SmokeTest.java", """
                package demo;

                import org.junit.jupiter.api.Test;

                class SmokeTest {
                    @Test
                    void runs() {
                    }
                }
                """);

        BuildResult result = gradle("test").build();

        assertEquals(SUCCESS, result.task(":test").getOutcome());
        // Có file kết quả nghĩa là JUnit Platform đã tìm và chạy đúng class này. Thiếu useJUnitPlatform() thì
        // Gradle 9 làm task test đỏ ("did not discover any tests"), nên test này cũng đỏ.
        assertTrue(Files.exists(projectDir.resolve("build/test-results/test/TEST-demo.SmokeTest.xml")));
    }

    // settings.gradle giống service thật: repository khai một chỗ, FAIL_ON_PROJECT_REPOS. Nhờ vậy mọi test
    // cũng là test hồi quy cho Q1: plugin mà khai repository thì cả ba test đỏ.
    private void writeService(String dependency) throws IOException {
        write("settings.gradle", """
                rootProject.name = 'demo'
                dependencyResolutionManagement {
                    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
                    repositories {
                        mavenCentral()
                    }
                }
                """);
        write("build.gradle", """
                plugins {
                    id 'commercehub.spring-service'
                }

                dependencies {
                    %s
                }
                """.formatted(dependency));
    }

    private GradleRunner gradle(String... tasks) {
        List<String> arguments = new ArrayList<>(List.of(tasks));
        // Chạy với configuration cache: plugin nào đọc Project lúc chạy task sẽ làm test đỏ ngay ở đây.
        arguments.add("--configuration-cache");
        return GradleRunner.create()
                .withProjectDir(projectDir.toFile())
                // Classpath của plugin đang viết (kèm spring-boot-gradle-plugin), do java-gradle-plugin sinh.
                .withPluginClasspath()
                .withArguments(arguments)
                // In log của build tạm ra console của test, dễ đọc khi test đỏ.
                .forwardOutput();
    }

    private void write(String relativePath, String content) throws IOException {
        Path file = projectDir.resolve(relativePath);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
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
