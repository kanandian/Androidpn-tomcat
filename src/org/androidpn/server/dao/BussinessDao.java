package org.androidpn.server.dao;

import org.androidpn.server.model.Bussiness;

import java.util.List;

public interface BussinessDao {
    public Bussiness getBussiness(long id);
    public List<Bussiness> getBussinesses();
    public Bussiness saveBussiness(Bussiness bussiness);
}
