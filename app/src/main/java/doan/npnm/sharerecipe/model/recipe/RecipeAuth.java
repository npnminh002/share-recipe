package doan.npnm.sharerecipe.model.recipe;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "RecipeAuth")
public class RecipeAuth implements Serializable {
    @PrimaryKey
    public String AuthId="";
    public String AuthName="",Gender="",Address="",NickName="",Image="";

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
