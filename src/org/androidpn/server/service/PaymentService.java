package org.androidpn.server.service;

import org.androidpn.server.model.Payment;

import java.util.List;

public interface PaymentService {

    public void savePayment(Payment payment);
    public void deletePayment(long paymentId);
    public Payment getPaymentById(long paymentId);

    public List<Payment> getPaymentsByFromUserName(String userName);
    public List<Payment> getPaymentsByToUserName(String userName);

}
