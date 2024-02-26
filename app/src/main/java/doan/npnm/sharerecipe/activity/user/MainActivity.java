package doan.npnm.sharerecipe.activity.user;


import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.Nullable;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;
import doan.npnm.sharerecipe.fragment.user.DetailRecipeFragment;
import doan.npnm.sharerecipe.fragment.user.HomeUserFragment;
import doan.npnm.sharerecipe.fragment.user.NotificationFragment;
import doan.npnm.sharerecipe.fragment.user.ProfileUserFragment;
import doan.npnm.sharerecipe.fragment.user.SeachFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @NonNull
    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {
        loaddingDialog.show();
        userViewModel.searchKey.observe(this,key->{
            if(!key.isEmpty()){
                userViewModel.database.searchDao().addRecentView(new Search(){{
                    CurrentKey=key;
                }});
                openFragment(new SeachFragment(userViewModel).newInstance(new HashMap<String, Serializable>(){{
                    put("Key",key);
                }}),true,true);
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

        openFragment(new HomeUserFragment(userViewModel), true, true);
        new Handler().postDelayed(()->{
            binding.getRoot().setVisibility(View.VISIBLE);
            loaddingDialog.dismiss();
        },2000);

        binding.bottomNavigation.setOnNavigationItemSelectedListener(OnBottomEventSelect);


    }

    @SuppressLint("NewApi")
    private BottomNavigationView.OnNavigationItemSelectedListener OnBottomEventSelect = item -> {
        if (item.getItemId() == R.id.icon_home_user) {
            openFragment(new HomeUserFragment(userViewModel), true, true);
            return true;
        }  else if (item.getItemId() == R.id.icon_search_user) {
            openFragment(new SeachFragment(userViewModel).newInstance(new HashMap<String, Serializable>(){{
                put("Key","Test");
            }}),true,true);
            return true;
        } else if (item.getItemId() == R.id.icon_notification_user) {
            openFragment(new NotificationFragment(userViewModel),false,false);
            return true;
        } else if (item.getItemId() == R.id.icon_users_admin) {
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