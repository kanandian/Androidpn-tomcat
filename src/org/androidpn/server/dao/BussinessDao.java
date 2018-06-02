package org.androidpn.server.dao;

import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Comment;
import org.androidpn.server.util.ResultModel;

import java.util.List;

public interface BussinessDao {
    public Bussiness getBussiness(long id);
    public List<Bussiness> getBussinesses();
    public Bussiness saveBussiness(Bussiness bussiness);

    public List<Bussiness> getBussinessesByIds(List<Long> ids);

    public List<Bussiness> getBussinessesByClassification(String classification);
    public List<Bussiness> getBussinessesByTag(List<String> tagList);

    public ResultModel addCommont(long bussinessId, Comment comment);

    public void updateByComment(long bussinessId, Comment comment);

    public List<Bussiness> getBussinessesByUserName(String userName);

    public List<Comment> getAllComments();

    public void deleteBussiness(long bussinessId);
}
