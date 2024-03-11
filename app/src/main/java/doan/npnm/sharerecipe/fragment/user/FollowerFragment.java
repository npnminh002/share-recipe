package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.FollowerAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.utility.Constant;
import doan.npnm.sharerecipe.databinding.FragmentFollowerBinding;


public class FollowerFragment extends BaseFragment<FragmentFollowerBinding> {

    public enum FOLLOW {
        FOLLOW,
        FOLLOWER
    }

    private UserViewModel viewModel;
    public FOLLOW follow = FOLLOW.FOLLOWER;

    public FollowerFragment(UserViewModel viewModel, FOLLOW follow) {
        this.viewModel = viewModel;
        this.follow = follow;
    }

    @Override
    protected FragmentFollowerBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFollowerBinding.inflate(inflater);
    }


    private FollowerAdapter youFollow;
    private FollowerAdapter otherFollow;

    @Override
    protected void initView() {
        binding.Profile.setText(getString(follow == FOLLOW.FOLLOW ? R.string.follow : R.string.followers));
        youFollow = new FollowerAdapter(new FollowerAdapter.OnFollowEvent() {
            @Override
            public void onView(String value) {
                addFragment(new DetailAuthFragment(viewModel, new Users().fromJson(value)), android.R.id.content, true);
            }

            @Override
            public void onFollower(String value) {

            }

            @Override
            public void onUnFollow(String value) {
                viewModel.onUnFollow(new Users().fromJson(value));
                youFollow.removeItem(value);
            }
        }, viewModel.database);

        otherFollow = new FollowerAdapter(new FollowerAdapter.OnFollowEvent() {
            @Override
            public void onView(String value) {
                addFragment(new DetailAuthFragment(viewModel, new Users().fromJson(value)), android.R.id.content, true);
            }

            @Override
            public void onFollower(String value) {
                viewModel.onFollow(new Users().fromJson(value));
            }

            @Override
            public void onUnFollow(String value) {

            }
        }, viewModel.database);

        if (follow == FOLLOW.FOLLOW) {
            loadFollow(Objects.requireNonNull(viewModel.getUsers().getValue()).UserID);
        }
        else {
            loadFollower(Objects.requireNonNull(viewModel.getUsers().getValue()).UserID);
        }


    }

    // lay danh sach ca nhan nguoi khac theo doi

    private void loadFollower(String userID) {
        viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                .child(userID)
                .child("YouFollow")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> listValue = new ArrayList<>();
                    for (DataSnapshot doc : task.getResult().getChildren()) {
                        listValue.add(doc.getValue(String.class));
                    }
                    if (listValue.size() != 0) {
                        binding.rcvItemFolow.setAdapter(otherFollow);
                        otherFollow.setItems(listValue);
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    // lay danh sach ca nhan minh theo doi
    private void loadFollow(String userID) {
        viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                .child(userID)
                .child("YouFollow")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> listValue = new ArrayList<>();
                    for (DataSnapshot doc : task.getResult().getChildren()) {
                        listValue.add(doc.getValue(String.class));
                    }
                    if (listValue.size() != 0) {
                        binding.rcvItemFolow.setAdapter(youFollow);
                        youFollow.setItems(listValue);
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    public void OnClick() {
        binding.backIcon2.setOnClickListener(v -> closeFragment(FollowerFragment.this));
    }
}