package doan.npnm.sharerecipe.activity.start;

import android.content.Intent;
import android.os.Build;
import android.text.TextUtils;

import java.util.Date;

import doan.npnm.sharerecipe.activity.admin.AdminMainActivity;
import doan.npnm.sharerecipe.activity.user.MainActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivitySignUpBinding;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.utility.Constant;

public class SignUpActivity extends BaseActivity<ActivitySignUpBinding> {

    @Override
    public void OnClick() {
        binding.signIn.setOnClickListener(v -> {
            // Khi người dùng nhấn vào nút đăng nhập, mở SignInActivity và kết thúc SignUpActivity
            startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            finish();
        });
        binding.signUpApp.setOnClickListener(v -> {
            // Khi người dùng nhấn vào nút đăng ký ứng dụng
            String name = binding.name.getText().toString();
            String email = binding.email.getText().toString();
            String pass = binding.passs.getText().toString();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                // Nếu ô nhập tên, email hoặc mật khẩu trống, hiển thị thông báo
                showToast("Vui lòng nhập đầy đủ thông tin");
            } else {
                // Ngược lại, thực hiện đăng ký ứng dụng với tên, email và mật khẩu đã nhập
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
        userViewModel.getUsers().observe(this, users -> {
            // Quan sát dữ liệu người dùng từ ViewModel
            if (users != null) {
                // Nếu dữ liệu người dùng không rỗng
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    // Kiểm tra phiên bản SDK của thiết bị
                    // Nếu phiên bản SDK lớn hơn hoặc bằng N (API 24)
                    startActivity(new Intent(SignUpActivity.this, users.AccountType == 1 ? AdminMainActivity.class : MainActivity.class));
                    // Mở AdminMainActivity nếu loại tài khoản của người dùng là 1, ngược lại mở MainActivity
                }
                finish();
                // Kết thúc SignUpActivity sau khi chuyển màn hình
            }
        });


    }

    public void signUpApp(String email, String name, String pass) {
        // Phương thức để đăng ký ứng dụng với email, tên và mật khẩu
        firestore.collection(Constant.KEY_USER)
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Nếu truy vấn thành công
                    if (queryDocumentSnapshots.isEmpty()) {
                        // Nếu không có tài khoản nào tồn tại với email đã nhập
                        createUserInFirestore(email, name, pass);
                    } else {
                        // Nếu đã tồn tại tài khoản với email đã nhập, hiển thị thông báo
                        showToast("Email đã tồn tại");
                    }
                })
                .addOnFailureListener(e -> {
                    // Nếu có lỗi trong quá trình kiểm tra sự tồn tại của email, hiển thị thông báo lỗi
                    showToast("Lỗi khi kiểm tra sự tồn tại của email: " + e.getMessage());
                });
    }

    private void createUserInFirestore(String email, String name, String pass) {
        // Phương thức để tạo người dùng mới trong Firestore
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    // Nếu việc tạo tài khoản thành công
                    authResult.getUser().getIdToken(true)
                            .addOnSuccessListener(getTokenResult -> {
                                // Nếu việc nhận mã thông báo thành công
                                String idToken = getTokenResult.getToken();
                                // Tạo một đối tượng Users mới với thông tin người dùng
                                Users user = new Users(authResult.getUser().getUid(), name,
                                        email, pass, idToken, "", new Date().toString(), "", "", "", 0, 0, 0, 0);
                                // Thêm người dùng vào Firestore
                                firestore.collection(Constant.KEY_USER)
                                        .document(user.UserID)
                                        .set(user)
                                        .addOnSuccessListener(unused -> {
                                            // Nếu thêm người dùng thành công, hiển thị thông báo và cập nhật LiveData users
                                            showToast("Đăng ký thành công");
                                            userViewModel.users.setValue(user);
                                        })
                                        .addOnFailureListener(error -> {
                                            // Nếu thêm người dùng không thành công, hiển thị thông báo lỗi
                                            showToast(error.getMessage());
                                        });
                            })
                            .addOnFailureListener(error -> {
                                // Nếu việc nhận mã thông báo không thành công, hiển thị thông báo lỗi
                                showToast("Lấy mã thông báo thất bại: " + error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    // Nếu việc tạo tài khoản không thành công, hiển thị thông báo lỗi
                    showToast(error.getMessage());
                });
    }



}