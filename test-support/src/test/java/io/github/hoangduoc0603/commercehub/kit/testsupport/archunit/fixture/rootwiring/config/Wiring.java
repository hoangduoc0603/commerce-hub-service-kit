package io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.rootwiring.config;

import io.github.hoangduoc0603.commercehub.kit.testsupport.archunit.fixture.rootwiring.application.PlaceOrder;

// Vi phạm cố ý (Q2 bước 3): class ngoài ba lớp, giống một @Configuration tự tạo bean use case.
public class Wiring {
    public PlaceOrder placeOrder() {
        return new PlaceOrder();
    }
}
