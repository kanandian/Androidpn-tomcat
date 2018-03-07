package org.androidpn.server.service;

import org.androidpn.server.model.Bussiness;

import java.util.List;

public interface BussinessService {
    public Bussiness getBussiness(String bussinessId);
    public List<Bussiness> getBussinesses();

    public List<Bussiness> getBussinessByClassification(String classification);
}
