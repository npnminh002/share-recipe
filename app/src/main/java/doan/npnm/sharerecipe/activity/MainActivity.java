package doan.npnm.sharerecipe.activity;


import android.content.Intent;

import androidx.annotation.NonNull;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.start.SignInActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;
import doan.npnm.sharerecipe.fragment.HomeFragment;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @NonNull
    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    int count = 0;

    @Override
    protected void createView() {

        binding.AddData.setOnClickListener(v -> {
            appViewModel.auth.signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        });

        addFragment(new HomeFragment(appViewModel), R.id.layoutData, true);

        appViewModel.users.observe(this, users -> binding.userData.setText(users.toString()));
    }
}