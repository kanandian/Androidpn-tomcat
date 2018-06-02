package org.androidpn.server.dao.hibernate;

import org.androidpn.server.dao.BussinessDao;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.util.ResultModel;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import java.util.ArrayList;
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
    public List<Bussiness> getBussinessesByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<Bussiness>();
        }
        StringBuilder condition = new StringBuilder();
        condition.append("(");
        condition.append(ids.get(0));
        for (int i=1;i<ids.size();i++) {
            condition.append(","+ids.get(i));
        }
        condition.append(")");
        String con = condition.toString();
        return getHibernateTemplate().find("from Bussiness b where b.bussinessId in "+con);
    }

    @Override
    public List<Bussiness> getBussinessesByClassification(String classification) {
        return getHibernateTemplate().find("from Bussiness b where b.classification = '"+classification+"'");
    }

    @Override
    public List<Bussiness> getBussinessesByTag(List<String> tagList) {
        StringBuilder tags = new StringBuilder();

        tags.append("(");
        boolean isFirst = true;
        for (String tag : tagList) {
            if (isFirst) {
                isFirst = false;
            } else {
                tags.append(",");
            }
            tags.append("'"+tag+"'");
        }

        tags.append(")");

        return getHibernateTemplate().find("from Bussiness b where b.tag in "+tags.toString());
    }

    @Override
    public ResultModel addCommont(long bussinessId, Comment comment) {
        ResultModel resultModel = new ResultModel();

        Bussiness bussiness = (Bussiness) getHibernateTemplate().get(Bussiness.class, bussinessId);

        List<Comment> commentList = bussiness.getCommentList();

        comment.setBussiness(bussiness);
        commentList.add(comment);

        getHibernateTemplate().save(comment);

        return resultModel;
    }

    @Override
    public void updateByComment(long bussinessId, Comment comment) {
        Bussiness bussiness = (Bussiness) getHibernateTemplate().get(Bussiness.class, bussinessId);

        bussiness.setPersonCount(bussiness.getPersonCount()+1);
        bussiness.setPrice(bussiness.getPrice()+comment.getAmount());
        bussiness.setLevel(bussiness.getLevel()+comment.getStar());

        getHibernateTemplate().saveOrUpdate(bussiness);
        getHibernateTemplate().flush();
    }

    @Override
    public List<Bussiness> getBussinessesByUserName(String userName) {
        return getHibernateTemplate().find("from Bussiness b where b.holder = '"+userName+"'");
    }

    @Override
    public List<Comment> getAllComments() {
        return getHibernateTemplate().find("from Comment c");
    }

    @Override
    public void deleteBussiness(long bussinessId) {
        Bussiness bussiness = getBussiness(bussinessId);

        if (bussiness != null) {
            getHibernateTemplate().delete(bussiness);
        }
    }

}
