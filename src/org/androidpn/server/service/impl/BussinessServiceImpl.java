package org.androidpn.server.service.impl;

import org.androidpn.server.dao.BussinessDao;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.util.ResultModel;

import java.util.List;

public class BussinessServiceImpl implements BussinessService {
    private BussinessDao bussinessDao;

    public BussinessServiceImpl() {

    }


    @Override
    public Bussiness saveBussiness(Bussiness bussiness) {
        bussinessDao.saveBussiness(bussiness);
        return bussiness;
    }

    @Override
    public Bussiness getBussiness(String bussinessId) {
        return bussinessDao.getBussiness(new Long(bussinessId));
    }

    @Override
    public List<Bussiness> getBussinesses() {
        return bussinessDao.getBussinesses();
    }

    @Override
    public ResultModel addComment(String bussinessId, Comment comment) {
        long bussinessID = Long.parseLong(bussinessId);
        bussinessDao.updateByComment(bussinessID, comment);
        return bussinessDao.addCommont(bussinessID, comment);
    }

    @Override
    public List<Bussiness> getBussinessesByClassification(String classification) {
        return bussinessDao.getBussinessesByClassification(classification);
    }

    @Override
    public List<Bussiness> getBussinessesByTag(List<String> tagList) {
        return bussinessDao.getBussinessesByTag(tagList);
    }

    @Override
    public List<Bussiness> getBussinessesByUserName(String userName) {
        return bussinessDao.getBussinessesByUserName(userName);
    }

    @Override
    public void updateImageForBussiness(String bussinessId, String imageURL) {
        long bussinessID = Long.parseLong(bussinessId);
        Bussiness bussiness = bussinessDao.getBussiness(bussinessID);

        bussiness.setImageURL(imageURL);

        bussinessDao.saveBussiness(bussiness);
    }

    public BussinessDao getBussinessDao() {
        return bussinessDao;
    }

    public void setBussinessDao(BussinessDao bussinessDao) {
        this.bussinessDao = bussinessDao;
    }
}
