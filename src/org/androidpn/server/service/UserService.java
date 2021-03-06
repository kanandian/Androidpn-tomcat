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
package org.androidpn.server.service;

import java.util.Date;
import java.util.List;

import org.androidpn.server.model.Bussiness;
import org.androidpn.server.model.User;
import org.androidpn.server.util.ResultModel;

import javax.swing.text.html.HTML;

/** 
 * Business service interface for the user management.
 *
 * @author Sehwan Noh (devnoh@gmail.com)
 */
public interface UserService {

    public User getUser(String userId);

    public List<User> getUsers();
    
    public List<User> getUsersFromCreatedDate(Date createDate);

    public User saveUser(User user) throws UserExistsException;

    public User getUserByUsername(String username) throws UserNotFoundException;

    public User getUserByMobile(String mobile);

    public void removeUser(Long userId);

    public ResultModel payment(String fromUserName, String toUserName, double price) throws UserNotFoundException;

    public boolean existUser(String userName);

    public boolean existMobile(String mobile);

    public List<String> getPerferences(String userName);

    public void addPerferences(String userName, String tag);

    public ResultModel updateUserInfo(String userName, String name, String mobile);

    public void addCollection(String userName, String bussinessId);

    public void removeCollection(String userName, String bussinessId);

    public boolean existColldection(String userName, String bussinessId);

    public List<Bussiness> getCollectedBussinesses(String userName);

    public void updateImageForUser(String userName, String imageURL);

}
