package doan.npnm.sharerecipe.model;

import java.io.Serializable;

public class Users extends BaseModel<Users> {
    public String UserID,UserName,Email,Password,Token,UrlImg,TimeLog,Address,Gender,NickName;
    public int Follower,Follow,Recipe;
    public int AccountType;

    public Users() {
    }

    public Users(String userID, String userName, String email, String password, String token, String urlImg, String timeLog, String address, String gender, String nickName, int follower, int follow, int recipe, int accountType) {
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
        Follower = follower;
        Follow = follow;
        Recipe = recipe;
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
                ", Follower=" + Follower +
                ", Follow=" + Follow +
                ", Recipe=" + Recipe +
                ", AccountType=" + AccountType +
                '}';
    }
}
