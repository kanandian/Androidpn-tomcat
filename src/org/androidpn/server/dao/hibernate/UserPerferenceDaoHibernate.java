package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.UserPerferenceDao;
import org.androidpn.server.util.UserPerference;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.List;

public class UserPerferenceDaoHibernate extends HibernateDaoSupport implements UserPerferenceDao {
    @Override
    public List<UserPerference> getUserPerferencesByUserNameOrderByNum(String userName) {
        return getHibernateTemplate().find("from UserPerference up where up.userName = ? order by num desc ", userName);
    }

    @Override
    public void saveUserPerference(UserPerference userPerference) {
        getHibernateTemplate().saveOrUpdate(userPerference);
        getHibernateTemplate().flush();
    }

    @Override
    public UserPerference getUserPerference(String userName, String tag) {
        List<UserPerference> userPerferenceList = getHibernateTemplate().find("from UserPerference up where up.userName = ? and up.tag = ?", new Object[] {userName, tag});

        if (userPerferenceList == null || userPerferenceList.isEmpty()) {
            return null;
        }

        return userPerferenceList.get(0);
    }
}
