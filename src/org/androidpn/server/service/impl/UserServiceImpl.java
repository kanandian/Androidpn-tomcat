/*
 * Copyright (C) 2010 Moduad Co., Ltd.
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */
package org.androidpn.server.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityExistsException;

import org.androidpn.server.dao.UserDao;
import org.androidpn.server.dao.UserPerferenceDao;
import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.Collection;
import org.androidpn.server.model.User;
import org.androidpn.server.service.UserExistsException;
import org.androidpn.server.service.UserNotFoundException;
import org.androidpn.server.service.UserService;
import org.androidpn.server.util.ResultModel;
import org.androidpn.server.util.UserPerference;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 
 * This class is the implementation of UserService.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
@Service
public class UserServiceImpl implements UserService {

    protected final Log log = LogFactory.getLog(getClass());

    private UserDao userDao;

    private UserPerferenceDao userPerferenceDao;

    public void setUserPerferenceDao(UserPerferenceDao userPerferenceDao) {
        this.userPerferenceDao = userPerferenceDao;
    }

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public User getUser(String userId) {
        return userDao.getUser(new Long(userId));
    }

    public List<User> getUsers() {
        return userDao.getUsers();
    }
    
    public List<User> getUsersFromCreatedDate(Date createDate) {
    	 return userDao.getUsersFromCreatedDate(createDate);
    }

    public User saveUser(User user) throws UserExistsException {
        try {
            return userDao.saveUser(user);
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        } catch (EntityExistsException e) { // needed for JPA
            e.printStackTrace();
            log.warn(e.getMessage());
            throw new UserExistsException("User '" + user.getUsername()
                    + "' already exists!");
        }
    }

    public User getUserByUsername(String username) throws UserNotFoundException {
        return (User) userDao.getUserByUsername(username);
    }

    public void removeUser(Long userId) {
        log.debug("removing user: " + userId);
        userDao.removeUser(userId);
    }

    @Override
    @Transactional
    public ResultModel payment(String fromUserName, String toUserName, double price) throws UserNotFoundException {
        ResultModel resultModel = new ResultModel();

        User fromUser = userDao.getUserByUsername(fromUserName);

        if (!fromUser.getRealUser()) {
            resultModel.setErrcode(1);
            resultModel.setErrMessage("用户未登录");
            return resultModel;
        }

        if (fromUser.getBalance() < price) {
            resultModel.setErrcode(1);
            resultModel.setErrMessage("余额不足");
            return resultModel;
        }

        User toUser = userDao.getUserByUsername(toUserName);

        fromUser.setBalance(fromUser.getBalance() - price);
        toUser.setBalance(toUser.getBalance() + price);

        userDao.saveUser(fromUser);
        userDao.saveUser(toUser);

        return resultModel;
    }

    @Override
    public boolean existUser(String userName) {
        try {
            User user = userDao.getUserByUsername(userName);
        } catch (UserNotFoundException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<String> getPerferences(String userName) {
        List<String> tagList = new ArrayList<String>();
        List<UserPerference> userPerferenceList = userPerferenceDao.getUserPerferencesByUserNameOrderByNum(userName);

        if (userPerferenceList == null || userPerferenceList.isEmpty()) {
            return tagList;
        }

        for (UserPerference up : userPerferenceList) {
            tagList.add(up.getTag());
        }

        return tagList;
    }

    @Override
    public void addPerferences(String userName, String tag) {
        UserPerference userPerference = userPerferenceDao.getUserPerference(userName, tag);
        if (userPerference == null) {
            userPerference = new UserPerference();
            userPerference.setUserName(userName);
            userPerference.setTag(tag);
            userPerference.setNum(0);
        }

        userPerference.setNum(userPerference.getNum() + 1);

        userPerferenceDao.saveUserPerference(userPerference);
    }

    @Override
    public ResultModel updateUserInfo(String userName, String name, String mobile) {
        ResultModel resultModel = new ResultModel();
        try {
            User user = userDao.getUserByUsername(userName);
            user.setName(name);
            user.setMobile(mobile);

            userDao.saveUser(user);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
            resultModel.setErrcode(1);
            resultModel.setErrMessage("未找到用户!");
        }

        return resultModel;
    }

    @Override
    public void addCollection(String userName, String bussinessId) {
        Collection collection = new Collection();
        collection.setUserName(userName);
        collection.setBussinessId(Long.parseLong(bussinessId));
        userDao.addCollection(collection);
    }

    @Override
    public void removeCollection(String userName, String bussinessId) {
        long bussinessID = Long.parseLong(bussinessId);
        userDao.removeCollection(userName, bussinessID);
    }

    @Override
    public boolean existColldection(String userName, String bussinessId) {
        long bussinessID = Long.parseLong(bussinessId);
        return userDao.existCollection(userName, bussinessID);
    }

    @Override
    public List<Bussiness> getCollectedBussinesses(String userName) {
        List<Long> idlist = userDao.getCollectedBussinessesId(userName);
        return userDao.getBussinessesByIds(idlist);
    }

    @Override
    public void updateImageForUser(String userName, String imageURL) {
        try {
            User user = userDao.getUserByUsername(userName);
            if (user != null) {
                user.setImageURL(imageURL);
            }
            userDao.saveUser(user);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }

    }


}
