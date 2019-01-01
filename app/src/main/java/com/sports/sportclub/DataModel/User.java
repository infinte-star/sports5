package com.sports.sportclub.DataModel;

import java.io.Serializable;

import cn.bmob.v3.BmobUser;

/**
 * the User Model for the app
 * include:
 * String: UserName
 * String: UserEmail
 * String: UserPass
 */

public class User extends BmobUser implements Serializable {

    public User(String userName, String userPass){
        super();
        super.setUsername(userName);
        super.setPassword(userPass);
    }

    public User(){
        super();

    }

}
