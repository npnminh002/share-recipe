package doan.npnm.sharerecipe.activity.user;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import javax.annotation.Nullable;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;
import doan.npnm.sharerecipe.fragment.user.HomeUserFragment;
import doan.npnm.sharerecipe.fragment.user.ProfileUserFragment;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @NonNull
    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {

        binding.bottomNavigationViewLinear.setNavigationChangeListener((view, position) -> {
            switch (position) {
                case 0:
                    openFragment(new HomeUserFragment(appViewModel), true, true);
                    break;
                case 1:
                    openFragment(new ProfileUserFragment(appViewModel), true, true);
                    break;
                case 2:
                    openFragment(new ProfileUserFragment(appViewModel), true, true);
                    break;
                case 3:
                    openFragment(new ProfileUserFragment(appViewModel), true, true);
                    break;
            }
        });
    }

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