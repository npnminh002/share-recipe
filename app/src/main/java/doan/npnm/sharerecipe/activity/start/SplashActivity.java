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
                        // Khi hoàn thành hiệu ứng tiến trình
                        if (LanguageUtil.isFirstOpenApp()) {
                            // Nếu đây là lần mở ứng dụng đầu tiên, chuyển đến LanguageActivity
                            startActivity(new Intent(SplashActivity.this, LanguageActivity.class));
                            finish();
                        } else {
                            // Nếu không phải là lần mở ứng dụng đầu tiên
                            if (userViewModel.users.getValue() == null) {
                                // Nếu không có người dùng đăng nhập, chuyển đến MainActivity
                                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                finish();
                            } else {
                                // Nếu có người dùng đăng nhập
                                if (userViewModel.users.getValue().AccountType == 0) {
                                    // Nếu người dùng là người dùng thường, chuyển đến MainActivity
                                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                                } else {
                                    // Nếu người dùng là quản trị viên, chuyển đến AdminMainActivity
                                    startActivity(new Intent(SplashActivity.this, AdminMainActivity.class));
                                }
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onProgress(float progress) {
                        // Callback khi tiến trình đang diễn ra, không cần xử lý gì trong trường hợp này
                    }
                })
                .startAutoAnimation();

    }
}