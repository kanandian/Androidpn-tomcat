package org.androidpn.server.service;

import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.util.ResultModel;

import java.util.List;

public interface BussinessService {
    public Bussiness saveBussiness(Bussiness bussiness);

    public Bussiness getBussiness(String bussinessId);
    public List<Bussiness> getBussinesses();

    public ResultModel addComment(String bussinessId, Comment comment);

    public List<Bussiness> getBussinessesByClassification(String classification);
    public List<Bussiness> getBussinessesByTag(List<String> tagList);

    public List<Bussiness> getBussinessesByUserName(String userName);
}
