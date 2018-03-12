package org.androidpn.server.dao;

import org.androidpn.server.model.Bussiness;

import java.util.List;

public interface BussinessDao {
    public Bussiness getBussiness(long id);
    public List<Bussiness> getBussinesses();
    public Bussiness saveBussiness(Bussiness bussiness);

    public List<Bussiness> getBussinessesByClassification(String classification);
    public List<Bussiness> getBussinessesByTag(List<String> tagList);
}
