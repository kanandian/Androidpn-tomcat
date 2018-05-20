package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.PaymentDao;
import org.androidpn.server.model.Payment;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class PaymentDaoHibernate extends HibernateDaoSupport implements PaymentDao {
    @Override
    public void savePayment(Payment payment) {
        getHibernateTemplate().saveOrUpdate(payment);
        getHibernateTemplate().flush();
    }

    @Override
    public void deletePayment(long paymentId) {
        Payment payment = new Payment();
        payment.setPaymentId(paymentId);
        getHibernateTemplate().delete(payment);
    }

    @Override
    public Payment getPaymentById(long paymentId) {
        return (Payment) getHibernateTemplate().get(Payment.class, paymentId);
    }

    @Override
    public List<Payment> getPaymentsByFromUserName(String userName) {
        return getHibernateTemplate().find("from Payment p where p.fromUserName = ?", userName);
    }

    @Override
    public List<Payment> getPaymentsByToUserName(String userName) {
        return getHibernateTemplate().find("from Payment p where p.toUserName = ?", userName);
    }
}
