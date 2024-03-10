package doan.npnm.sharerecipe.activity.start;

import android.content.Intent;
import android.text.TextUtils;

import doan.npnm.sharerecipe.activity.admin.AdminMainActivity;
import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivitySignInBinding;
import doan.npnm.sharerecipe.lib.widget.TextValue;

public class SignInActivity extends BaseActivity<ActivitySignInBinding> {

    @Override
    public void OnClick() {

    }

    private TextValue email;
    private TextValue password;

    // Khoi tao chung duoc ke thua tu base
    @Override
    protected ActivitySignInBinding getViewBinding() {
        return ActivitySignInBinding.inflate(getLayoutInflater());
    }

    // khoi tao view doc kke thua tu base
    @Override
    protected void createView() {
        email=new TextValue(binding.email);
        password= new TextValue(binding.passs);

        // dung viewmodel du lieu song trong suot vong doi ung dung
        // usertViewModel lang ghe su thay doi cap nhat du lieu nguoi dung tu ham getUse()  kiem tra neu != nul bat dau man mo
        // kiem tra neu du lieu nguoi dung thay doi neu AccoutnType= 1 bat dau man Main cua Admin nguoc lai bat dat man Main cua nguoi dung
        userViewModel.getUsers().observe(this, users -> {
            if (users != null) {

                startActivity(new Intent(SignInActivity.this, users.AccountType==1? AdminMainActivity.class : MainActivity.class));
                // finhsh: xoa bo man truoc khong cho phep quay tro lai
                finish();
            }
        });



        binding.signIn.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });
        binding.signInApp.setOnClickListener(v -> {
            if (TextUtils.isEmpty(email.value()) || TextUtils.isEmpty(password.value())) {
                showToast("Please input all values");
            } else {
                signIn(email.value(), password.value());
            }
        });
    }


    private void signIn(String email, String pass) {
        auth.signInWithEmailAndPassword(email, pass).addOnSuccessListener(authResult -> {
            userViewModel.getDataFromUserId(authResult.getUser().getUid());
            userViewModel.firstStartApp(authResult.getUser().getUid());

            showToast("Sign-in successful");
        }).addOnFailureListener(e -> {

            showToast("Authentication failed: " + e.getMessage());
        });


    }
}