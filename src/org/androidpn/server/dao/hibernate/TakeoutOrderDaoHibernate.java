package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.TakeoutOrderDao;
import org.androidpn.server.model.TakeoutOrder;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class TakeoutOrderDaoHibernate extends HibernateDaoSupport implements TakeoutOrderDao {
    @Override
    public void saveTakeoutOrder(TakeoutOrder takeoutOrder) {
        getHibernateTemplate().saveOrUpdate(takeoutOrder);
        getHibernateTemplate().flush();
    }

    @Override
    public List<TakeoutOrder> getTakeoutOrderListByBussinessId(long bussinessId) {
        return getHibernateTemplate().find("from TakeoutOrder to where to.bussinessId=?", bussinessId);
    }
}
