package doan.npnm.sharerecipe.activity.start;

import android.annotation.SuppressLint;
import android.content.Intent;

import doan.npnm.sharerecipe.activity.admin.AdminMainActivity;
import doan.npnm.sharerecipe.activity.user.LanguageActivity;
import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.app.lang.LanguageUtil;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivitySplashBinding;
import doan.npnm.sharerecipe.lib.widget.ProgressCustom;

@SuppressLint("CustomSplashScreen")
public class SplashActivity extends BaseActivity<ActivitySplashBinding> {
    @Override
    public void OnClick() {

    }

    @Override
    protected ActivitySplashBinding getViewBinding() {
        return ActivitySplashBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {
        binding.customProgress
                .timeAnim(2)
                .restartAnim(false)
                .progressEvent(new ProgressCustom.OnProgressListener() {
                    @Override
                    public void onEnd() {
                        if (userViewModel.auth.getCurrentUser() == null) {
                            startActivity(new Intent(SplashActivity.this, SignInActivity.class));
                            finish();
                        } else {
                            if (LanguageUtil.isFirstOpenApp()) {
                                startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
                                finish();
                            } else {

                                boolean isAdmin=   userViewModel.getUsers().getValue().AccountType==0;
                                startActivity(new Intent(SplashActivity.this,
                                   isAdmin   ? MainActivity.class : AdminMainActivity.class));
                                finish();
                            }

                        }
                    }

                    @Override
                    public void onProgress(float progress) {
                    }
                })
                .startAutoAnimation();


    }
}