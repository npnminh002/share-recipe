package doan.npnm.sharerecipe.activity.user;


import androidx.annotation.NonNull;

import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @NonNull
    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {

//       binding.bottomNavigationViewLinear.setNavigationChangeListener(new BubbleNavigationChangeListener() {
//            @Override
//            public void onNavigationChanged(View view, int position) {
//                //navigation changed, do something
//            }
//        });
    }
}