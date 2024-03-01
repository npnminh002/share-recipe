package doan.npnm.sharerecipe.model.notification;

import java.util.ArrayList;

import doan.npnm.sharerecipe.model.BaseModel;

public class ParentNotification extends BaseModel<ParentNotification> {
    public String Time="";
    public ArrayList<Notification> notifications= new ArrayList<>();

    public ParentNotification(String time, ArrayList<Notification> notifications) {
        Time = time;
        this.notifications = notifications;
    }
}
