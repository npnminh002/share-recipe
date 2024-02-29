package doan.npnm.sharerecipe.fragment.user;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentUpdateInfoBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.utility.Constant;

@SuppressLint("ResourceType")
public class UpdateInfoFragment extends BaseFragment<FragmentUpdateInfoBinding> {

    private UserViewModel viewModel;

    public UpdateInfoFragment(UserViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentUpdateInfoBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentUpdateInfoBinding.inflate(getLayoutInflater());
    }

    int indexGender = -1;

    ArrayList<String> listGender;

    @Override
    protected void initView() {
        listGender = new ArrayList<>(
                Arrays.asList(
                        getResources().getString(R.string.gender_male),
                        getResources().getString(R.string.gender_femail),
                        getResources().getString(R.string.undisclosed)
                )
        );
        viewModel.users.observe(this, users -> {
            binding.emailValue.setText(users.Email);
            binding.address.setText(users.Address);
            binding.fullName.setText(users.UserName);
            binding.nickName.setText(users.NickName);
            binding.gender.setText(users.Gender);
            indexGender = Objects.equals(users.Gender, "") ? -1 : listGender.indexOf(users.Gender);

        });
    }

    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(UpdateInfoFragment.this));
        binding.btnSave.setOnClickListener(v -> {
            new ConfirmDialog(UpdateInfoFragment.this.requireContext(), getString(R.string.cofirm_update), () -> {
                upDateInfo();
            }).show();
        });
        binding.changeGender.setOnClickListener(v -> {
            indexGender++;
            binding.gender.setText(listGender.get(indexGender));
            if (indexGender >= listGender.size() - 1) {
                indexGender = -1;
            }

        });

    }
    public static String formatString(String input) {
        String trimmedString = input.trim();
        String formattedString = trimmedString.replaceAll("\\s+", "_").toLowerCase();
        return "#"+formattedString;
    }


    private void upDateInfo() {
        loaddingDialog.show();
        String userName = binding.fullName.getText().toString();
        String nickName = binding.nickName.getText().toString();
        String address = binding.address.getText().toString();
        String gender = binding.gender.getText().toString();

        Users users = viewModel.users.getValue();

        users.UserName = userName.isEmpty() ? users.UserName : userName;
        users.NickName = nickName.isEmpty() ? users.NickName : formatString(nickName);
        users.Address = address.isEmpty() ? users.Address : address;
        users.Gender = gender.isEmpty() ? users.Gender : gender;

        firestore.collection(Constant.KEY_USER)
                .document(users.UserID)
                .set(users)
                .addOnSuccessListener(documentReference -> {
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                        loaddingDialog.dismiss();
                        showToast(getString(R.string.profile_update_success));
                    }, 1500L);

                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
    }


}