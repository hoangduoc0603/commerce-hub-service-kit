package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.application.port;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.domain.Order;

// Port nằm trong application (ticket 11: "application (use case + port)"); adapter.out cài đặt nó.
public interface OrderRepository {
    void save(Order order);
}
