package org.androidpn.server.service.impl;

import org.androidpn.server.dao.BussinessDao;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.service.BussinessService;

import java.util.List;

public class BussinessServiceImpl implements BussinessService {
    private BussinessDao bussinessDao;

    public BussinessServiceImpl() {

    }

    @Override
    public Bussiness getBussiness(String bussinessId) {
        return bussinessDao.getBussiness(new Long(bussinessId));
    }

    @Override
    public List<Bussiness> getBussinesses() {
        return bussinessDao.getBussinesses();
    }

    public BussinessDao getBussinessDao() {
        return bussinessDao;
    }

    public void setBussinessDao(BussinessDao bussinessDao) {
        this.bussinessDao = bussinessDao;
    }
}
