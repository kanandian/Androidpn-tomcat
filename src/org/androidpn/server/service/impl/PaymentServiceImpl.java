package org.androidpn.server.service.impl;

import org.androidpn.server.dao.PaymentDao;
import org.androidpn.server.model.Payment;
import org.androidpn.server.service.PaymentService;

import java.util.List;

public class PaymentServiceImpl implements PaymentService {

    private PaymentDao paymentDao;

    public PaymentServiceImpl() {
    }

    @Override
    public void savePayment(Payment payment) {
        paymentDao.savePayment(payment);
    }

    @Override
    public void deletePayment(long paymentId) {
        paymentDao.deletePayment(paymentId);
    }

    @Override
    public Payment getPaymentById(long paymentId) {
        return paymentDao.getPaymentById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsByFromUserName(String userName) {
        return paymentDao.getPaymentsByFromUserName(userName);
    }

    @Override
    public List<Payment> getPaymentsByToUserName(String userName) {
        return paymentDao.getPaymentsByToUserName(userName);
    }

    public PaymentDao getPaymentDao() {
        return paymentDao;
    }

    public void setPaymentDao(PaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }
}
