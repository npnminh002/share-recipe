package doan.npnm.sharerecipe.adapter.users;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.function.Consumer;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.databinding.ItemNotificationViewChildBinding;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.notification.Notification;
import doan.npnm.sharerecipe.utility.Constant;

public class ChildNotiAdapter extends BaseAdapter<Notification, ItemNotificationViewChildBinding> {


    final Consumer<Notification> consumer;

    public ChildNotiAdapter(Consumer<Notification> consumer) {
        this.consumer = consumer;
    }

    @Override
    protected ItemNotificationViewChildBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemNotificationViewChildBinding.inflate(inflater,parent,false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void bind(ItemNotificationViewChildBinding binding, Notification item, int position) {
        FirebaseFirestore.getInstance()
                .collection(Constant.KEY_USER)
                .document(item.AuthID)
                .get().addOnFailureListener(e -> {
                  binding.getRoot().setVisibility(View.GONE);
                }).addOnSuccessListener(documentSnapshot -> {
                    Users noti= documentSnapshot.toObject(Users.class);
                    binding.circleImageView3.loadImage(noti.UrlImg==""? R.drawable.img_1:noti.UrlImg);
                    binding.txtUserName.setText(noti.UserName);
                    binding.txtContent.setText(item.Content);
                    binding.txtTime.setText(item.Time);
                    binding.getRoot().setOnClickListener(v -> consumer.accept(item));
                });

    }
}
