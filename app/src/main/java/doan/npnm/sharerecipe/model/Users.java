package doan.npnm.sharerecipe.model;

import java.io.Serializable;

public class Users implements Serializable {
    public String UserID,UserName,Email,Password,Token,UrlImg,TimeLog,Address,Gender,NickName;
    public int AccountType;

    public Users() {
    }

    public Users(String userID, String userName, String email, String password, String token, String urlImg, String timeLog, String address, String gender, String nickName, int accountType) {
        UserID = userID;
        UserName = userName;
        Email = email;
        Password = password;
        Token = token;
        UrlImg = urlImg;
        TimeLog = timeLog;
        Address = address;
        Gender = gender;
        NickName = nickName;
        AccountType = accountType;
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
                ", Address='" + Address + '\'' +
                ", Gender='" + Gender + '\'' +
                ", NickName='" + NickName + '\'' +
                ", AccountType=" + AccountType +
                '}';
    }
}
