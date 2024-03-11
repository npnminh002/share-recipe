package doan.npnm.sharerecipe.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.start.SignInActivity;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.lib.shared_preference.SharedPreference;
import doan.npnm.sharerecipe.databinding.FragmentSettingBinding;
public class SettingFragment extends BaseFragment<FragmentSettingBinding> {
    // ViewModel của người dùng
    public UserViewModel viewModel;

    // Constructor nhận ViewModel của người dùng
    public SettingFragment(UserViewModel viewModel) {
        this.viewModel = viewModel;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentSettingBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSettingBinding.inflate(inflater);
    }

    // Mã yêu cầu cho việc chọn ảnh từ thư viện
    private int REQUEST_CODE_PICK_IMAGE = 1000;

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        onLoadUserProfile();
    }

    // Load thông tin người dùng
    private void onLoadUserProfile() {
        viewModel.users.observe(this, users -> {
            if (users == null) {
                // Chuyển hướng đến màn hình đăng nhập nếu không có người dùng đăng nhập
                startActivity(new Intent(requireContext(), SignInActivity.class));
            } else {
                // Hiển thị thông tin người dùng
                if (!users.UrlImg.isEmpty()) {
                    loadImage(users.UrlImg, binding.ImgUser);
                }
                binding.userName.setText(users.UserName);
                binding.emailValue.setText(users.Email);
            }
        });
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Load trạng thái thông báo từ SharedPreferences và thiết lập cho switch button
        boolean isChecked = new SharedPreference().getBoolean("IsNoty", true);
        binding.onOfNoti.setChecked(isChecked);

        // Xử lý sự kiện khi nhấn chọn ảnh đại diện
        binding.icEitImage.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });

        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> closeFragment(SettingFragment.this));

        // Xử lý sự kiện khi nhấn nút hủy thay đổi ảnh đại diện
        binding.btnCanelImage.setOnClickListener(v -> binding.layoutChangeImage.setVisibility(View.GONE));

        // Xử lý sự kiện khi nhấn nút làm mới thông tin người dùng
        binding.reloaIcon.setOnClickListener(v -> onLoadUserProfile());

        // Xử lý sự kiện khi nhấn nút thay đổi thông tin người dùng
        binding.changeProfile.setOnClickListener(v -> addFragment(new UpdateInfoFragment(viewModel), android.R.id.content, true));

        // Xử lý sự kiện khi nhấn nút thay đổi ngôn ngữ
        binding.changeLanguage.setOnClickListener(v -> {
            addFragment(new LanguageFragment(), android.R.id.content, true);
        });

        // Xử lý sự kiện khi nhấn nút đăng xuất
        binding.btnSignOut.setOnClickListener(v -> {
            // Hiển thị dialog xác nhận trước khi đăng xuất
            new ConfirmDialog(requireContext(), getString(R.string.cf_sign_Out), () -> {
                loaddingDialog.show();
                viewModel.auth.signOut();
                viewModel.signOutDatabase();

                // Đăng xuất sau 1.5 giây và chuyển hướng đến màn hình đăng nhập
                new Handler().postDelayed(() -> {
                    loaddingDialog.dismiss();
                    viewModel.users.postValue(null);
                    viewModel.isSingApp.postValue(true);
                    viewModel = new ViewModelProvider(SettingFragment.this).get(UserViewModel.class);
                }, 1500);
            }).show();
        });

        // Xử lý sự kiện khi thay đổi trạng thái của switch button thông báo
        binding.onOfNoti.setOnCheckedChangeListener((buttonView, isChecked1) -> new SharedPreference().putBoolean("IsNoty", isChecked1));

        // Xử lý sự kiện khi nhấn nút thay đổi thông tin liên hệ
        binding.changeContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, "bdong0610@gmail.com");
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        });

        // Xử lý sự kiện khi nhấn nút trợ giúp và hỗ trợ
        binding.helpSupport.setOnClickListener(v -> {
            addFragment(new HelpSupportFragment(viewModel), android.R.id.content, true);
        });

        // Xử lý sự kiện khi nhấn nút chính sách bảo mật
        binding.privacyPolicy.setOnClickListener(v -> replaceFragment(new PrivacyFragment(), android.R.id.content, true));
    }

    // Phương thức mở màn hình chọn ảnh từ thư viện
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    // Xử lý kết quả trả về từ việc chọn ảnh
    private Uri uriImg;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                uriImg = data.getData();
                loadImage(String.valueOf(uriImg), binding.ImgUser);
                binding.layoutChangeImage.setVisibility(View.VISIBLE);
            }
        }
    }
}
