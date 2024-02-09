package doan.npnm.sharerecipe.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseAdapter;
import doan.npnm.sharerecipe.database.AppDatabase;
import doan.npnm.sharerecipe.databinding.ItemFollowViewBinding;
import doan.npnm.sharerecipe.model.Users;

public class FollowerAdapter extends BaseAdapter<String, ItemFollowViewBinding> {
    final OnFollowEvent event;
    final AppDatabase database;

    public FollowerAdapter(OnFollowEvent event, AppDatabase database) {
        this.event = event;
        this.database = database;
    }

    @Override
    protected ItemFollowViewBinding createBinding(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return ItemFollowViewBinding.inflate(inflater, parent, false);
    }

    @Override
    protected void bind(ItemFollowViewBinding binding, String value, int position) {
        Users item = new Users().fromJson(value);
        boolean isFollow = database.followerDao().checkExisId(item.UserID);

        binding.btnUnFollow.setVisibility(isFollow ? View.VISIBLE : View.GONE);

        binding.btnFollow.setVisibility(isFollow ? View.GONE : View.VISIBLE);
        binding.btnFollow.setOnClickListener(v -> event.onFollower(value));
        binding.btnUnFollow.setOnClickListener(v -> event.onUnFollow(value));
        loadImage(item.UrlImg == "" ? R.drawable.img_demo_user : item.UrlImg, binding.imgChef);
        binding.recipeView.setText("" + item.Recipe);
        binding.chefName.setText(item.UserName);
        binding.getRoot().setOnClickListener(v -> event.onView(value));
    }


    public interface OnFollowEvent {
        void onView(String value);

        void onFollower(String value);

        void onUnFollow(String value);
    }
}
