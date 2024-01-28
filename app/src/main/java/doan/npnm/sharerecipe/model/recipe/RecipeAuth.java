package doan.npnm.sharerecipe.model.recipe;

import java.io.Serializable;

public class RecipeAuth implements Serializable {
    public String AuthId="",AuthName="",Gender="",Address="",NickName="",Image="";

    public RecipeAuth(String authId, String authName, String gender, String address,String nickName,String image) {
        AuthId = authId;
        AuthName = authName;
        Gender = gender;
        Address = address;
        NickName=nickName;
        Image=image;
    }

    public RecipeAuth() {
    }

    @Override
    public String toString() {
        return "RecipeAuth{" +
                "AuthId='" + AuthId + '\'' +
                ", AuthName='" + AuthName + '\'' +
                ", Gender='" + Gender + '\'' +
                ", Address='" + Address + '\'' +
                '}';
    }
}
