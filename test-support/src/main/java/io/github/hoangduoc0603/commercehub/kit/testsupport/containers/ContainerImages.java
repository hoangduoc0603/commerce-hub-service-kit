package io.github.hoangduoc0603.commercehub.kit.testsupport.containers;

import org.testcontainers.utility.DockerImageName;

/**
 * Tag image mà test của mọi service dùng cho Testcontainers. Đây là nguồn duy nhất: service không tự ghi tag.
 *
 * <p>Quy ước (java-library.md, mục "Testcontainers và bảng tag image"):
 * <ul>
 *   <li>Cùng major.minor với production trong repo {@code gitops}. Production là nguồn sự thật; một job theo
 *       lịch ở {@code platform} so ba nơi (gitops, compose, bảng này) và đỏ khi lệch (spec G0).</li>
 *   <li>Ghim tag patch đầy đủ, không dùng {@code latest} hay chỉ major: test hôm nay và tháng sau chạy cùng bản.</li>
 * </ul>
 */
public final class ContainerImages {

    /**
     * PostgreSQL 18.7. Production: {@code gitops/infra/pg-main/cluster.yaml}, image CloudNativePG
     * {@code 18.7-...-standard-trixie}. Test dùng image chính thức của Docker Hub, cùng bản PostgreSQL.
     */
    public static final DockerImageName POSTGRES = DockerImageName.parse("postgres:18.7");

    // Class chỉ chứa hằng số, không tạo instance.
    private ContainerImages() {
    }
}
