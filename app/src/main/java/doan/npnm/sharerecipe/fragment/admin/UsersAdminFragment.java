package doan.npnm.sharerecipe.fragment.admin;

import static doan.npnm.sharerecipe.fragment.user.UpdateInfoFragment.formatString;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminUserBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.utility.Constant;

public class UsersAdminFragment extends BaseFragment<FragmentAdminUserBinding> {
    private AdminViewModel viewModel;

    public UsersAdminFragment(AdminViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    @Override
    protected FragmentAdminUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminUserBinding.inflate(inflater);
    }

    // Biến lưu vị trí giới tính
    private int indexGender = -1;

    // Danh sách giới tính
    private ArrayList<String> listGender;

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Khởi tạo danh sách giới tính
        listGender = new ArrayList<>(
                Arrays.asList(
                        getResources().getString(R.string.gender_male),
                        getResources().getString(R.string.gender_femail),
                        getResources().getString(R.string.undisclosed)
                )
        );
        // Quan sát dữ liệu người dùng từ ViewModel
        viewModel.userLogin.observe(this, users -> {
            if (users != null) {
                // Hiển thị thông tin người dùng
                binding.emailValue.setText(users.Email);
                binding.address.setText(users.Address);
                binding.fullName.setText(users.UserName);
                binding.nickName.setText(users.NickName);
                binding.gender.setText(users.Gender);
                // Xác định vị trí giới tính trong danh sách
                indexGender = Objects.equals(users.Gender, "") ? -1 : listGender.indexOf(users.Gender);
            }
        });
    }

    @Override
    public void OnClick() {

        // Xử lý sự kiện khi nhấn nút lưu thông tin
        binding.btnSave.setOnClickListener(v -> {
            // Hiển thị dialog xác nhận trước khi lưu thông tin
            new ConfirmDialog(UsersAdminFragment.this.requireContext(), getString(R.string.cofirm_update), () -> {
                upDateInfo();
            }).show();
        });

        // Xử lý sự kiện khi nhấn nút thay đổi giới tính
        binding.changeGender.setOnClickListener(v -> {
            // Tăng index của giới tính
            indexGender++;
            // Hiển thị giới tính mới
            binding.gender.setText(listGender.get(indexGender));
            // Nếu đã đến cuối danh sách thì quay lại giá trị mặc định
            if (indexGender >= listGender.size() - 1) {
                indexGender = -1;
            }
        });
    }

    private void upDateInfo() {
        // Hiển thị dialog loading
        loaddingDialog.show();
        // Lấy thông tin từ giao diện
        String userName = binding.fullName.getText().toString();
        String nickName = binding.nickName.getText().toString();
        String address = binding.address.getText().toString();
        String gender = binding.gender.getText().toString();

        // Lấy thông tin người dùng từ ViewModel
        Users users = viewModel.userLogin.getValue();

        // Cập nhật thông tin người dùng
        users.UserName = userName.isEmpty() ? users.UserName : userName;
        users.NickName = nickName.isEmpty() ? users.NickName : formatString(nickName);
        users.Address = address.isEmpty() ? users.Address : address;
        users.Gender = gender.isEmpty() ? users.Gender : gender;

        // Cập nhật thông tin lên Firebase Firestore
        firestore.collection(Constant.KEY_USER)
                .document(users.UserID)
                .set(users)
                .addOnSuccessListener(documentReference -> {
                    // Đóng dialog loading sau khi cập nhật thành công
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                        loaddingDialog.dismiss();
                        showToast(getString(R.string.profile_update_success));
                    }, 1500L);

                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo lỗi nếu cập nhật không thành công
                    showToast(e.getMessage());
                });
    }
}
