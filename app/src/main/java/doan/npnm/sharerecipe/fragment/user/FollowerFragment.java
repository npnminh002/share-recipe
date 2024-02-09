package doan.npnm.sharerecipe.fragment.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.FollowerAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentFollowerBinding;
import doan.npnm.sharerecipe.utility.Constant;


public class FollowerFragment extends BaseFragment<FragmentFollowerBinding>{

    public enum FOLLOW{
        FOLLOW,
        FOLLOWER
    }

    private AppViewModel viewModel;
    public FOLLOW follow=FOLLOW.FOLLOWER;

    public FollowerFragment(AppViewModel viewModel, FOLLOW follow) {
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
        binding.Profile.setText(getString(follow==FOLLOW.FOLLOW ? R.string.follow :R.string.followers));
        youFollow= new FollowerAdapter(new FollowerAdapter.OnFollowEvent() {
            @Override
            public void onView(String value) {

            }

            @Override
            public void onFollower(String value) {

            }

            @Override
            public void onUnFollow(String value) {

            }
        },viewModel.database);

        otherFollow= new FollowerAdapter(new FollowerAdapter.OnFollowEvent() {
            @Override
            public void onView(String value) {

            }

            @Override
            public void onFollower(String value) {

            }

            @Override
            public void onUnFollow(String value) {

            }
        },viewModel.database);

        if(follow==FOLLOW.FOLLOW){
            loadFollow(Objects.requireNonNull(viewModel.getUsers().getValue()).UserID);
        }

    }

    private void loadFollow(String userID) {
        viewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                .child(userID)
                .child("YouFollow")
                .get()
                .addOnCompleteListener(task -> {
                    ArrayList<String> listValue= new ArrayList<>();
                    for (DataSnapshot doc : task.getResult().getChildren()){
                        listValue.add(doc.getValue(String.class));
                    }
                    if(listValue.size()!=0){
                        binding.rcvItemFolow.setAdapter(youFollow);
                        youFollow.setItems(listValue);
                    }
                })
                .addOnFailureListener(e -> {

                });
    }

    @Override
    public void OnClick() {
        binding.backIcon2.setOnClickListener(v->closeFragment(FollowerFragment.this));
    }
}