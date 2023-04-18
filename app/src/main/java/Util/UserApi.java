package Util;

import android.app.Application;
//      Singleton Class to hold username and email of signed in account
// added in the manifest file to include application wide valid

public class UserApi extends Application {
    private String userName;
    private String userEmail;

    private static UserApi instance;


    public static UserApi getInstance(){
        if(instance == null) instance = new UserApi();
        return instance;
    }

    public UserApi(){}

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }
}
