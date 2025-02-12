package uk.ac.ed.inf;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import uk.ac.ed.inf.models.*;
import uk.ac.ed.inf.controllers.RestServiceController;
import java.time.LocalDate;
import java.util.*;

public class OrderValidationTest {

    @Test
    void testValidateOrderValid() {
        RestServiceController controller = new RestServiceController();
        Order validOrder = createValidOrder();

        OrderValidationResult result = controller.performOrderValidation(validOrder);

        assertEquals(OrderStatus.VALID, result.getOrderStatus());
        assertEquals(OrderValidationCode.NO_ERROR, result.getOrderValidationCode());
    }

    @Test
    void testValidateOrderEmpty() {
        RestServiceController controller = new RestServiceController();
        Order emptyOrder = createEmptyOrder();

        OrderValidationResult result = controller.performOrderValidation(emptyOrder);

        assertEquals(OrderStatus.INVALID, result.getOrderStatus());
        assertEquals(OrderValidationCode.EMPTY_ORDER, result.getOrderValidationCode());
    }

    @Test
    void testValidateOrderInvalidExpiryDate() {
        RestServiceController controller = new RestServiceController();
        Order invalidOrder = createInvalidOrderExpiryDate();

        OrderValidationResult result = controller.performOrderValidation(invalidOrder);

        assertEquals(OrderStatus.INVALID, result.getOrderStatus());
        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, result.getOrderValidationCode());
    }

    // Helper methods to create test orders
    private Order createValidOrder() {
        Order order = new Order();
        order.setOrderNo("12345");
        order.setOrderDate(LocalDate.parse("2025-01-05"));
        order.setPriceTotalInPence(1100);
        Pizza pizza = new Pizza("R1: Margarita", 1000);
        order.setPizzasInOrder(Arrays.asList(pizza));
        CreditCardInformation cc = new CreditCardInformation();
        cc.setCreditCardNumber("4485959141852684");
        cc.setCreditCardExpiry("12/25");
        cc.setCvv("123");
        order.setCreditCardInformation(cc);
        return order;
    }

    private Order createEmptyOrder() {
        Order order = new Order();
        order.setOrderNo("12346");
        order.setOrderDate(LocalDate.now());
        order.setPriceTotalInPence(100);
        order.setPizzasInOrder(Collections.emptyList());
        CreditCardInformation cc = new CreditCardInformation();
        cc.setCreditCardNumber("4485959141852684");
        cc.setCreditCardExpiry("12/25");
        cc.setCvv("123");
        order.setCreditCardInformation(cc);
        return order;
    }

    private Order createInvalidOrderExpiryDate() {
        Order order = new Order();
        order.setOrderNo("12345");
        order.setOrderDate(LocalDate.parse("2025-01-05"));
        order.setPriceTotalInPence(1100);
        Pizza pizza = new Pizza("R1: Margarita", 1000);
        order.setPizzasInOrder(Arrays.asList(pizza));
        CreditCardInformation cc = new CreditCardInformation();
        cc.setCreditCardNumber("4485959141852684");
        cc.setCreditCardExpiry("04/18");
        cc.setCvv("123");
        order.setCreditCardInformation(cc);
        return order;
    }
}

