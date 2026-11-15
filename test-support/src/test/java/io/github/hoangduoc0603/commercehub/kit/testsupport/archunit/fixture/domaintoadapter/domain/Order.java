package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.domaintoadapter.domain;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.domaintoadapter.adapter.out.persistence.OrderRow;

// Vi phạm cố ý: domain biết dạng lưu của persistence.
public record Order(String id) {
    public OrderRow toRow() {
        return new OrderRow(id);
    }
}
