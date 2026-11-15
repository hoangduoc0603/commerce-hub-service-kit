package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.springindomain.domain;

import org.springframework.util.Assert;

// Vi phạm cố ý: domain gọi một class của Spring. Đây là case trong tiêu chí nghiệm thu của ticket.
public record Order(String id) {
    public Order {
        Assert.hasText(id, "id");
    }
}
