package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentProfileUserBinding;

public class ProfileUserFragment extends BaseFragment<FragmentProfileUserBinding> {
    private AppViewModel viewModel;

    public ProfileUserFragment(AppViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void OnClick() {

    }

    @Override
    protected FragmentProfileUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileUserBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        binding.icSetting.setOnClickListener(v -> {
            addFragment(new SettingFragment(viewModel),android.R.id.content,true);
        });
    }
}