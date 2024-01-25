package doan.npnm.sharerecipe.activity.start;

import android.annotation.SuppressLint;
import android.content.Intent;

import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivitySplashBinding;
import doan.npnm.sharerecipe.lib.widget.ProgressCustom;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    @Override
    protected ActivitySplashBinding getViewBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {
        binding.customProgress.progressEvent(new ProgressCustom.OnProgressListener() {
            @Override
            public void onEnd() {
                if(appViewModel.auth.getCurrentUser()==null){
                    startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                    finish();
                }
                else {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
            @Override
            public void onProgress(float progess) {
            }
        });
        binding.customProgress.restartAnim(false);
    }
}