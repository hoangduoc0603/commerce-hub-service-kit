package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.adapter.in.web;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.valid.application.PlaceOrder;

// Adapter vào: adapter -> application là chiều hợp lệ.
public class OrderController {
    private final PlaceOrder placeOrder;

    public OrderController(PlaceOrder placeOrder) {
        this.placeOrder = placeOrder;
    }

    public String post(String id) {
        return placeOrder.place(id).id();
    }
}
