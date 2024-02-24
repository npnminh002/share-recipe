package doan.npnm.sharerecipe.activity.start;

import android.content.Intent;
import android.text.TextUtils;

import doan.npnm.sharerecipe.activity.admin.AdminMainActivity;
import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivitySignInBinding;

public class SignInActivity extends BaseActivity<ActivitySignInBinding> {

    @Override
    public void OnClick() {

    }

    @Override
    protected ActivitySignInBinding getViewBinding() {
        return ActivitySignInBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {
        userViewModel.getUsers().observe(this, users -> {
            if (users != null) {

                startActivity(new Intent(SignInActivity.this, users.AccountType==1? AdminMainActivity.class : MainActivity.class));
                finish();
            }
        });

        binding.signIn.setOnClickListener(v -> {
            startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
            finish();
        });
        binding.signInApp.setOnClickListener(v -> {
            String email = binding.email.getText().toString();
            String pass = binding.passs.getText().toString();

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                showToast("Please input all values");
            } else {
                signIn(email, pass);
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