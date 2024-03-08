package doan.npnm.sharerecipe.model.notification;

import doan.npnm.sharerecipe.model.BaseModel;

public class Notification  extends BaseModel<Notification> {
    public String Content="";
    public String Time="";
    public String AuthID="";
    public boolean IsView=false;
    public String Value="";

    public NotyType NotyType= doan.npnm.sharerecipe.model.notification.NotyType.DEFAULT;

    public Notification(String id,String content, String time, String authID, boolean isView, String value, doan.npnm.sharerecipe.model.notification.NotyType notyType) {
        Id=id;
        Content = content;
        Time = time;
        AuthID = authID;
        IsView = isView;
        Value = value;
        NotyType = notyType;
    }

    public Notification() {
    }

    @Override
    public String toString() {
        return "Notification{" +
                "Content='" + Content + '\'' +
                ", Time='" + Time + '\'' +
                ", AuthID='" + AuthID + '\'' +
                ", IsView='" + IsView + '\'' +
                ", Value='" + Value + '\'' +
                ", NotyType=" + NotyType +
                '}';
    }
}
