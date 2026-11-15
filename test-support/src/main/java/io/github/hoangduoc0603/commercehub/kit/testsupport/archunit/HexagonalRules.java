package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit;

import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

/**
 * Luật kiến trúc cho layout hexagonal của mọi service (ticket 11 của map, story 59 của spec G0).
 *
 * <p>Service bật luật bằng một class test:
 * <pre>{@code
 * @AnalyzeClasses(packagesOf = HelloApplication.class, importOptions = ImportOption.DoNotIncludeTests.class)
 * class ArchitectureTest {
 *     @ArchTest
 *     static final ArchTests hexagonal = ArchTests.in(HexagonalRules.class);
 * }
 * }</pre>
 *
 * <p>Mẫu package là tương đối ({@code ..domain..}), nên luật không chứa tên package gốc của service nào.
 */
public final class HexagonalRules {

    /**
     * Chiều gọi giữa các lớp: domain không biết application và adapter; application không biết adapter;
     * adapter không được lớp nào gọi vào. ArchUnit tính mọi phụ thuộc, nên class nằm ngoài ba lớp
     * (ví dụ package {@code config}) gọi vào lớp nào cũng là vi phạm.
     *
     * <p>Mọi adapter gom vào một mẫu {@code ..adapter..}: thêm adapter mới (ví dụ {@code adapter.out.shopee})
     * không phải sửa kit. Việc cấm adapter gọi nhau giao cho {@link #ADAPTERS_INDEPENDENT}.
     */
    @ArchTest
    public static final ArchRule LAYER_DEPENDENCIES = onionArchitecture()
            .domainModels("..domain..")
            .applicationServices("..application..")
            .adapter("adapter", "..adapter..")
            // Mặc định mỗi lớp phải có class. Template hello rỗng và nhiều service không có adapter kafka.
            .withOptionalLayers(true)
            .because("domain không biết application và adapter, application không biết adapter (ticket 11)");

    /**
     * Luật onion chỉ kiểm ai gọi vào lớp nào, không cấm domain gọi ra thư viện ngoài, nên cần luật này.
     * {@code jakarta.persistence} vẫn được phép trong domain cho service JPA (ticket 11, Q11).
     */
    @ArchTest
    public static final ArchRule DOMAIN_INDEPENDENT_OF_SPRING = noClasses()
            .that().resideInAPackage("..domain..")
            .should().dependOnClassesThat().resideInAnyPackage("org.springframework..")
            // Service chưa có domain thì luật không áp lên class nào; lỗi gõ sai mẫu package đã có
            // HexagonalRulesTest bắt.
            .allowEmptyShould(true)
            .because("domain là Java thuần, test không cần Spring context (ticket 11)");

    /**
     * Mỗi nhóm {@code adapter.<in|out>.<tên>} là một slice; các slice không được gọi nhau.
     * Controller muốn ghi DB thì đi qua use case trong application.
     */
    @ArchTest
    public static final ArchRule ADAPTERS_INDEPENDENT = slices()
            // Hai (*) bắt chiều (in/out) và tên adapter: in.web, out.persistence, out.shopee...
            .matching("..adapter.(*).(*)..")
            .should().notDependOnEachOther()
            .allowEmptyShould(true)
            .because("adapter chỉ nói chuyện với nhau qua application (ticket 11)");

    // Class chỉ chứa luật, không tạo instance.
    private HexagonalRules() {
    }
}
