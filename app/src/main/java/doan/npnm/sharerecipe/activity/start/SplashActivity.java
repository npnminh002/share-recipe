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
                        if (LanguageUtil.isFirstOpenApp()) {
                            startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
                            finish();
                        } else {
                            if (userViewModel.users.getValue() == null) {
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                if (userViewModel.users.getValue().AccountType == 0) {
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                                }
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