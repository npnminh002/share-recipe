package doan.npnm.sharerecipe.model;

import java.io.Serializable;

public class Users implements Serializable {
    public String UserID,UserName,Email,Password,Token,UrlImg,TimeLog;
    public int AccountType;

    public Users() {
    }

    public Users(String userID, String userName, String email, String password, String token, String urlImg, String timeLog,int accountType) {
        UserID = userID;
        UserName = userName;
        Email = email;
        Password = password;
        Token = token;
        UrlImg = urlImg;
        AccountType=accountType;
        TimeLog = timeLog;
    }


    @Override
    public String toString() {
        return "Users{" +
                "UserID='" + UserID + '\'' +
                ", UserName='" + UserName + '\'' +
                ", Email='" + Email + '\'' +
                ", Password='" + Password + '\'' +
                ", Token='" + Token + '\'' +
                ", UrlImg='" + UrlImg + '\'' +
                ", TimeLog='" + TimeLog + '\'' +
                '}';
    }
}
