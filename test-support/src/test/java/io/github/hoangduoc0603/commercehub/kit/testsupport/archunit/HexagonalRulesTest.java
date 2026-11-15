package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.FieldSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

// Mã yêu cầu trong tên hiển thị để truy vết (CLAUDE.md): ticket G0-18, story 59 của spec G0.
@DisplayName("Luật hexagonal (G0-18, story 59)")
class HexagonalRulesTest {

    // Tính từ package của chính class test, đổi tên package không phải sửa chuỗi.
    private static final String FIXTURE = HexagonalRulesTest.class.getPackageName() + ".fixture.";

    // Mọi luật trong HexagonalRules. Thêm luật mới thì thêm vào đây để nó cũng chạy với 'valid' và 'empty'.
    static final List<ArchRule> ALL_RULES = List.of(
            HexagonalRules.LAYER_DEPENDENCIES,
            HexagonalRules.DOMAIN_INDEPENDENT_OF_SPRING,
            HexagonalRules.ADAPTERS_INDEPENDENT);

    // Chiều xanh: layout đúng không bị luật nào chặn (tránh luật quá chặt).
    @ParameterizedTest
    @FieldSource("ALL_RULES")
    void validLayoutPassesEveryRule(ArchRule rule) {
        rule.check(importFixture("valid"));
    }

    // Template hello chưa có lớp nào vẫn phải qua (Q3 bước 3).
    @ParameterizedTest
    @FieldSource("ALL_RULES")
    void serviceWithoutLayersPassesEveryRule(ArchRule rule) {
        rule.check(importFixture("empty"));
    }

    // Chiều đỏ: mỗi luật có ít nhất một fixture làm nó ném lỗi, đúng class vi phạm.
    @Test
    void domainDependingOnSpringFails() {
        assertViolation(HexagonalRules.DOMAIN_INDEPENDENT_OF_SPRING, "springindomain", "springindomain.domain.Order");
    }

    @Test
    void domainDependingOnAdapterFails() {
        assertViolation(HexagonalRules.LAYER_DEPENDENCIES, "domaintoadapter", "domaintoadapter.domain.Order");
    }

    @Test
    void classOutsideLayersCallingApplicationFails() {
        assertViolation(HexagonalRules.LAYER_DEPENDENCIES, "rootwiring", "rootwiring.config.Wiring");
    }

    @Test
    void adapterDependingOnAnotherAdapterFails() {
        assertViolation(HexagonalRules.ADAPTERS_INDEPENDENT, "adaptertoadapter",
                "adaptertoadapter.adapter.in.web.OrderController");
    }

    // Mỗi lần chỉ import một fixture, để class của fixture khác không làm nhiễu kết quả.
    private static JavaClasses importFixture(String name) {
        return new ClassFileImporter().importPackages(FIXTURE + name);
    }

    private static void assertViolation(ArchRule rule, String fixture, String offendingClass) {
        // check() ném AssertionError khi có vi phạm; thông báo liệt kê từng phụ thuộc sai kèm tên class.
        AssertionError error = assertThrows(AssertionError.class, () -> rule.check(importFixture(fixture)));
        assertTrue(error.getMessage().contains(offendingClass), error::getMessage);
    }
}
