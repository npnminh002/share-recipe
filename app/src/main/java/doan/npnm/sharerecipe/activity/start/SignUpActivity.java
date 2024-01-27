package doan.npnm.sharerecipe.activity.start;

import android.content.Intent;
import android.text.TextUtils;

import java.util.Date;

import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivitySignUpBinding;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.utility.Constant;

public class SignUpActivity extends BaseActivity<ActivitySignUpBinding> {

    @Override
    public void OnClick() {
        binding.signIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            finish();
        });
        binding.signUpApp.setOnClickListener(v -> {
            String name = binding.name.getText().toString();
            String email = binding.email.getText().toString();
            String pass = binding.passs.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                showToast("Please input all values");
            } else {
                signUpApp(email, name, pass);
            }
        });
    }

    @Override
    protected ActivitySignUpBinding getViewBinding() {
        return ActivitySignUpBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void createView() {
        appViewModel.getUsers().observe(this,users -> {
            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
            finish();
        });

    }
    public void signUpApp(String email, String name, String pass) {
        firestore.collection(Constant.KEY_USER)
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        createUserInFirestore(email, name, pass);

                    } else {
                        showToast("Email is exit");
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error checking email existence: " + e.getMessage());

                });
    }

    private void createUserInFirestore(String email, String name, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    authResult.getUser().getIdToken(true)
                            .addOnSuccessListener(getTokenResult -> {
                                String idToken = getTokenResult.getToken();
                                Users user = new Users(authResult.getUser().getUid(), name,
                                        email, pass, idToken, "", new Date().toString(),"","","",0);
                                firestore.collection(Constant.KEY_USER)
                                        .document(user.UserID)
                                        .set(user)
                                        .addOnSuccessListener(unused -> {
                                            showToast("Sign Up Success");
                                            appViewModel.users.setValue(user);
                                        })
                                        .addOnFailureListener(error -> {
                                            showToast(error.getMessage());
                                        });
                            })
                            .addOnFailureListener(error -> {
                                showToast("Failed to retrieve ID token: " + error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    showToast(error.getMessage());
                });
    }




}