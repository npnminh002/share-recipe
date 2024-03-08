package doan.npnm.sharerecipe.activity.user;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.Nullable;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.start.SignInActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.database.models.UserFollower;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;
import doan.npnm.sharerecipe.fragment.user.DetailRecipeFragment;
import doan.npnm.sharerecipe.fragment.user.HomeUserFragment;
import doan.npnm.sharerecipe.fragment.user.NotificationFragment;
import doan.npnm.sharerecipe.fragment.user.ProfileUserFragment;
import doan.npnm.sharerecipe.fragment.user.SeachFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @NonNull
    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    String key = "";

    @Override
    protected void createView() {

        userViewModel.isSingApp.observe(this, data -> {
            if (data) {
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        });

        loaddingDialog.show();
        userViewModel.searchKey.observe(this, key -> {
            if (!key.isEmpty()) {
                this.key = key;
                binding.bottomNavigation.setSelectedItemId(R.id.icon_search_user);
            }

        });
        String data = getIntent().getStringExtra(Constant.RECIPE);
        if (data != null && data.length() > 0) {
            userViewModel.getRecipeByID(data, new FetchByID<Recipe>() {

                @Override
                public void onSuccess(Recipe data) {
                    addFragment(new DetailRecipeFragment(data, userViewModel), android.R.id.content, true);
                }

                @Override
                public void onErr(Object err) {
                    openFragment(new HomeUserFragment(userViewModel), true, true);
                }
            });
        }

        addFragment(new HomeUserFragment(userViewModel),R.id.layoutHome,true);
        binding.layoutHome.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            binding.getRoot().setVisibility(View.VISIBLE);
            loaddingDialog.dismiss();
        }, 2000);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(OnBottomEventSelect);
        userViewModel.users.observe(this, users -> {
            if(users!=null){
                listenerUserFollow(users);
            }
        });
    }

    private void listenerUserFollow(Users users) {
        userViewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                .child(users.UserID)
                .child("OtherFollow")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        userViewModel.database.userFollowerDao().SignOutApp();
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            userViewModel.database.userFollowerDao()
                                    .addUserFollower(new UserFollower() {{
                                        AuthID = doc.getKey();
                                    }});

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("NewApi")
    private BottomNavigationView.OnNavigationItemSelectedListener OnBottomEventSelect = item -> {
        if (item.getItemId() == R.id.icon_home_user) {
            binding.layoutHome.setVisibility(View.VISIBLE);
            binding.layoutMain.setVisibility(View.GONE);

            return true;
        } else if (item.getItemId() == R.id.icon_search_user) {
            binding.layoutHome.setVisibility(View.GONE);
            openFragment(new SeachFragment(userViewModel).newInstance(new HashMap<String, Serializable>() {{
                put("Key", key);
            }}), true, true);
            return true;
        } else if (item.getItemId() == R.id.icon_notification_user) {
            binding.layoutHome.setVisibility(View.GONE);
            openFragment(new NotificationFragment(userViewModel), true, false);
            return true;
        } else if (item.getItemId() == R.id.icon_users_user) {
            binding.layoutHome.setVisibility(View.GONE);
            openFragment(new ProfileUserFragment(userViewModel), true, true);
            return true;
        } else {
            return false;
        }
    };

    @Override
    public void OnClick() {
    }

    public void openFragment(Fragment fragment, boolean fullView, @Nullable boolean isAdd) {
        if (fullView) {
            binding.layoutData1.setVisibility(View.VISIBLE);
            binding.layoutMain.setVisibility(View.GONE);
            if (isAdd) {
                addFragment(fragment, R.id.layoutData1, true);
            } else {
                replaceFragment(fragment, R.id.layoutData1, true);
            }

        } else {
            binding.layoutData1.setVisibility(View.GONE);
            binding.layoutMain.setVisibility(View.VISIBLE);
            if (isAdd) {
                addFragment(fragment, R.id.layoutData2, true);
            } else {
                replaceFragment(fragment, R.id.layoutData2, true);
            }
        }
    }
}