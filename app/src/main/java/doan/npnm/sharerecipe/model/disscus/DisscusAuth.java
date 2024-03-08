package doan.npnm.sharerecipe.model.disscus;

import java.io.Serializable;

public class DisscusAuth implements Serializable {
    public String AuthId = "";
    public String AuthName = "", Address = "", Image = "";
    public DisscusAuth(String authId, String authName, String address, String image) {
        AuthId = authId;
        AuthName = authName;
        Address = address;
        Image = image;
    }
    public DisscusAuth() {
    }
}
