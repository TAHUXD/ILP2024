package uk.ac.ed.inf.models;

public class OrderValidationResult {
    private OrderStatus orderStatus;
    private OrderValidationCode orderValidationCode;

    // Getters and setters
    public OrderStatus getOrderStatus() { return orderStatus; }
    public void setOrderStatus(OrderStatus orderStatus) { this.orderStatus = orderStatus; }

    public OrderValidationCode getOrderValidationCode() { return orderValidationCode; }
    public void setOrderValidationCode(OrderValidationCode orderValidationCode) { this.orderValidationCode = orderValidationCode; }
}
