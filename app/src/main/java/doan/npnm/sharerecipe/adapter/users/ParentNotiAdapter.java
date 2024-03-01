package doan.npnm.sharerecipe.adapter.users;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.function.Consumer;

import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemNotificationViewParentBinding;
import doan.npnm.sharerecipe.model.notification.Notification;
import doan.npnm.sharerecipe.model.notification.ParentNotification;

public class ParentNotiAdapter extends BaseAdapter<ParentNotification, ItemNotificationViewParentBinding> {

    private final Consumer<Notification> consumer;

    public ParentNotiAdapter(Consumer<Notification> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected ItemNotificationViewParentBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemNotificationViewParentBinding.inflate(inflater,parent,false);
    }

    ChildNotiAdapter adapter;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void bind(ItemNotificationViewParentBinding binding, ParentNotification item, int position) {
        adapter= new ChildNotiAdapter(notification -> {
            consumer.accept(notification);
        });
       binding.txtTime.setText(item.Time);
       binding.rcvItemNoti.setAdapter(adapter);
       adapter.setItems(item.notifications);
    }




}
