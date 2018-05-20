package org.androidpn.server.dao;

import org.androidpn.server.model.Payment;

import java.util.List;

public interface PaymentDao {

    public void savePayment(Payment payment);
    public void deletePayment(long paymentId);
    public Payment getPaymentById(long paymentId);

    public List<Payment> getPaymentsByFromUserName(String userName);
    public List<Payment> getPaymentsByToUserName(String userName);


}
