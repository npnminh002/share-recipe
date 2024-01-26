package doan.npnm.sharerecipe.activity.user;


import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;
import doan.npnm.sharerecipe.fragment.admin.HomeAdminFragment;
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

           if(position==4){
               showToast("hellp");
               openFragment(new ProfileUserFragment(appViewModel),true);
           }
           if(position==1){
               openFragment(new HomeUserFragment(appViewModel),false);
           }
           if(position==2){
               openFragment(new HomeAdminFragment(appViewModel),false);
           }
       });
    }

    public void openFragment(Fragment fragment, boolean value) {
        if (value) {
            binding.layoutData1.setVisibility(View.VISIBLE);
            binding.layoutMain.setVisibility(View.GONE);
            replaceFragment(fragment, R.id.layoutData1, true);
        } else {
            binding.layoutData1.setVisibility(View.GONE);
            binding.layoutMain.setVisibility(View.VISIBLE);
            replaceFragment(fragment, R.id.layoutData2, true);
        }
    }
}