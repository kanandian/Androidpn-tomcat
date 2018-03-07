package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.BussinessDao;
import org.androidpn.server.model.Bussiness;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class BussinessDaoHibernate extends HibernateDaoSupport implements BussinessDao {

    public BussinessDaoHibernate() {

    }

    @Override
    public Bussiness getBussiness(long id) {
        return (Bussiness) getHibernateTemplate().get(Bussiness.class, id);
    }

    @Override
    public List<Bussiness> getBussinesses() {
        return getHibernateTemplate().find("from Bussiness b");
    }

    @Override
    public Bussiness saveBussiness(Bussiness bussiness) {
        getHibernateTemplate().saveOrUpdate(bussiness);
        getHibernateTemplate().flush();
        return bussiness;
    }

    @Override
    public List<Bussiness> getBussinessesByClassification(String classification) {
        return getHibernateTemplate().find("from Bussiness b where b.classification = '"+classification+"'");
    }


}
