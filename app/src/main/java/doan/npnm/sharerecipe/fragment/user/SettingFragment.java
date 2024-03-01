package doan.npnm.sharerecipe.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.start.SignInActivity;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentSettingBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.lib.shared_preference.SharedPreference;

public class SettingFragment extends BaseFragment<FragmentSettingBinding> {
    public UserViewModel viewModel;

    public SettingFragment(UserViewModel viewModel) {
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
            if (users == null) {
                startActivity(new Intent(requireContext(), SignInActivity.class));
            } else {
                if (!users.UrlImg.isEmpty()) {
                    loadImage(users.UrlImg, binding.ImgUser);
                }
                binding.userName.setText(users.UserName);
                binding.emailValue.setText(users.Email);
            }

        });
    }

    @Override
    public void OnClick() {
        boolean isChecked = new SharedPreference().getBoolean("IsNoty", true);
        binding.onOfNoti.setChecked(isChecked);
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

        binding.btnSignOut.setOnClickListener(v -> {
            new ConfirmDialog(requireContext(), getString(R.string.cf_sign_Out), () -> {
                loaddingDialog.show();
                viewModel.auth.signOut();
                viewModel.signOutDatabase();

                new Handler().postDelayed(() -> {
                    loaddingDialog.dismiss();
                    viewModel.users.postValue(null);
                    startActivity(new Intent(requireContext(), SignInActivity.class));
                    viewModel = new ViewModelProvider(SettingFragment.this).get(UserViewModel.class);
                }, 1500);
            }).show();
        });

        binding.onOfNoti.setOnCheckedChangeListener((buttonView, isChecked1) -> new SharedPreference().putBoolean("IsNoty", isChecked1));

        binding.changeContact.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_EMAIL, "bdong0610@gmail.com");
            intent.setType("message/rfc822");
            startActivity(Intent.createChooser(intent, "Choose an Email client :"));
        });

        binding.helpSupport.setOnClickListener(v -> {
            addFragment(new HelpSupportFragment(viewModel),android.R.id.content,true);
        });
        binding.privacyPolicy.setOnClickListener(v -> replaceFragment(new PrivacyFragment(),android.R.id.content,true));
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
