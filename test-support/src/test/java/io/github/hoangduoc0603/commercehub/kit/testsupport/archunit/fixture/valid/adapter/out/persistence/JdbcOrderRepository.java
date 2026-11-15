package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.adapter.out.persistence;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.application.port.OrderRepository;
import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.domain.Order;

// Adapter ra cài đặt port: adapter -> application và adapter -> domain là chiều hợp lệ.
public class JdbcOrderRepository implements OrderRepository {
    @Override
    public void save(Order order) {
    }
}
