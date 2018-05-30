package org.androidpn.server.service.impl;

import org.androidpn.server.dao.BussinessDao;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.service.BussinessService;
import org.androidpn.server.util.Location;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.util.SortUtil;
import org.hsqldb.lib.Sort;

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
    public List<Bussiness> getBussinessOrderByDistance(Location location) {
        List<Bussiness> bussinessList = this.getBussinesses();
        SortUtil.sortBussinessesByDistance(bussinessList, location);
        return bussinessList;
    }

    @Override
    public List<Bussiness> getBussinessesByIds(List<Long> ids) {
        return bussinessDao.getBussinessesByIds(ids);
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
    public List<Bussiness> getBussinessesByClassificationOrderByDistance(String classification, Location location) {
        List<Bussiness> bussinessList = this.getBussinessesByClassification(classification);

        SortUtil.sortBussinessesByDistance(bussinessList, location);

        return bussinessList;
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

    @Override
    public List<Comment> getAllComments() {
        return bussinessDao.getAllComments();
    }

    @Override
    public void updateBussiness(Bussiness bussiness) {
        Bussiness oldBussiness = bussinessDao.getBussiness(bussiness.getBussinessId());

        oldBussiness.setFeature(bussiness.getFeature());
        oldBussiness.setStartTime(bussiness.getStartTime());
        oldBussiness.setEndTime(bussiness.getEndTime());
        oldBussiness.setDes(bussiness.getDes());
        oldBussiness.setMobile(bussiness.getMobile());
//        oldBussiness.setBusinessName(bussiness.getBusinessName());
//        oldBussiness.setTag(bussiness.getTag());
//        oldBussiness.setClassification(bussiness.getClassification());

        bussinessDao.saveBussiness(oldBussiness);


    }

    public BussinessDao getBussinessDao() {
        return bussinessDao;
    }

    public void setBussinessDao(BussinessDao bussinessDao) {
        this.bussinessDao = bussinessDao;
    }
}
