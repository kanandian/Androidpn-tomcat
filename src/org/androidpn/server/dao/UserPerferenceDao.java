package org.androidpn.server.dao;

import org.androidpn.server.model.User;
import org.androidpn.server.util.UserPerference;

import java.util.List;

public interface UserPerferenceDao {

    public List<UserPerference> getUserPerferencesByUserNameOrderByNum(String userName);

    public void saveUserPerference(UserPerference userPerference);

    public UserPerference getUserPerference(String userName, String tag);




}
