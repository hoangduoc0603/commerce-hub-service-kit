package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.application;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.application.port.OrderRepository;
import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.domain.Order;

// Use case: application -> domain và application -> port là chiều hợp lệ.
public class PlaceOrder {
    private final OrderRepository orders;

    public PlaceOrder(OrderRepository orders) {
        this.orders = orders;
    }

    public Order place(String id) {
        Order order = new Order(id);
        orders.save(order);
        return order;
    }
}
