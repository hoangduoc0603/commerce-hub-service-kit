package io.github.hoangduoc0603.commercehub.kit.gradleplugin;

import org.gradle.testkit.runner.GradleRunner;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Một service tạm cho test TestKit, dựng giống service thật: build.gradle chỉ có
 * {@code plugins { id 'commercehub.spring-service' }}, settings.gradle khai repository một chỗ với
 * FAIL_ON_PROJECT_REPOS và lấy group của kit từ một repository riêng ({@code exclusiveContent}).
 *
 * <p>Repository của kit ở đây là thư mục {@code build/testkit-repo} mà {@code :test-support} vừa publish
 * vào (build.gradle của gradle-plugin truyền đường dẫn qua system property {@code kit.testKitRepo}).
 * Service thật dùng GitHub Packages ở đúng chỗ đó.
 */
final class ServiceProject {

    private static final String KIT_GROUP = "io.github.hoangduoc0603.commercehub.kit";

    private final Path dir;

    ServiceProject(Path dir) {
        this.dir = dir;
    }

    /** Ghi settings.gradle và build.gradle; {@code dependencies} là các dòng trong khối dependencies {}. */
    ServiceProject withDependencies(String... dependencies) {
        String kitRepository = Path.of(System.getProperty("kit.testKitRepo")).toUri().toString();
        write("settings.gradle", """
                rootProject.name = 'demo'
                dependencyResolutionManagement {
                    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
                    repositories {
                        // Group của kit chỉ tìm ở repository này, và repository này chỉ được hỏi cho group đó.
                        exclusiveContent {
                            forRepository {
                                maven { url = '%s' }
                            }
                            filter {
                                includeGroup '%s'
                            }
                        }
                        mavenCentral()
                    }
                }
                """.formatted(kitRepository, KIT_GROUP));
        write("build.gradle", """
                plugins {
                    id 'commercehub.spring-service'
                }

                dependencies {
                    %s
                }
                """.formatted(String.join("\n    ", dependencies)));
        return this;
    }

    ServiceProject write(String relativePath, String content) {
        try {
            Path file = dir.resolve(relativePath);
            Files.createDirectories(file.getParent());
            Files.writeString(file, content);
            return this;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Path resolve(String relativePath) {
        return dir.resolve(relativePath);
    }

    GradleRunner gradle(String... tasks) {
        List<String> arguments = new ArrayList<>(List.of(tasks));
        // Chạy với configuration cache: plugin nào đọc Project lúc chạy task sẽ làm test đỏ ngay ở đây.
        arguments.add("--configuration-cache");
        return GradleRunner.create()
                .withProjectDir(dir.toFile())
                // Classpath của plugin đang viết (kèm spring-boot-gradle-plugin), do java-gradle-plugin sinh.
                .withPluginClasspath()
                .withArguments(arguments)
                // In log của build tạm ra console của test, dễ đọc khi test đỏ.
                .forwardOutput();
    }
}
