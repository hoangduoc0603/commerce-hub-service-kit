package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.adaptertoadapter.adapter.in.web;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.adaptertoadapter.adapter.out.persistence.JdbcOrderRepository;

// Vi phạm cố ý (Q1 bước 3): controller gọi thẳng persistence, bỏ qua application.
public class OrderController {
    private final JdbcOrderRepository orders = new JdbcOrderRepository();

    public void post(String id) {
        orders.save(id);
    }
}
