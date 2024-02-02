package doan.npnm.sharerecipe.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentSettingBinding;

public class SettingFragment extends BaseFragment<FragmentSettingBinding> {
    public AppViewModel viewModel;

    public SettingFragment(AppViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentSettingBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSettingBinding.inflate(inflater);
    }

    private int REQUEST_CODE_PICK_IMAGE = 1000;

    @Override
    protected void initView() {
        onLoadUserProfile();
    }

    private void onLoadUserProfile() {
        viewModel.users.observe(this, users -> {
            if (!users.UrlImg.isEmpty()) {
                loadImage(users.UrlImg, binding.ImgUser);
            }
            binding.userName.setText(users.UserName);
            binding.emailValue.setText(users.Email);
        });
    }

    @Override
    public void OnClick() {
        binding.icEitImage.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });
        binding.backIcon.setOnClickListener(v -> closeFragment(SettingFragment.this));
        binding.btnCanelImage.setOnClickListener(v -> binding.layoutChangeImage.setVisibility(View.GONE));
        binding.reloaIcon.setOnClickListener(v -> onLoadUserProfile());

        binding.changeProfile.setOnClickListener(v -> addFragment(new UpdateInfoFragment(viewModel), android.R.id.content, true));
        binding.changeLanguage.setOnClickListener(v -> {
            addFragment(new LanguageFragment(), android.R.id.content, true);
        });

    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

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
