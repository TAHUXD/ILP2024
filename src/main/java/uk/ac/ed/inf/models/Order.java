package uk.ac.ed.inf.models;

import java.time.LocalDate;
import java.util.List;

public class Order {
    private String orderNo;
    private LocalDate orderDate;
    private int priceTotalInPence;
    private List<Pizza> pizzasInOrder;
    private CreditCardInformation creditCardInformation;

    // Getters and setters
    public String getOrderNo() { return orderNo; }
    public void setOrderNo(String orderNo) { this.orderNo = orderNo; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public int getPriceTotalInPence() { return priceTotalInPence; }
    public void setPriceTotalInPence(int priceTotalInPence) { this.priceTotalInPence = priceTotalInPence; }

    public List<Pizza> getPizzasInOrder() { return pizzasInOrder; }
    public void setPizzasInOrder(List<Pizza> pizzasInOrder) { this.pizzasInOrder = pizzasInOrder; }

    public CreditCardInformation getCreditCardInformation() { return creditCardInformation; }
    public void setCreditCardInformation(CreditCardInformation creditCardInformation) { this.creditCardInformation = creditCardInformation; }
}

