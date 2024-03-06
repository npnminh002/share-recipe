package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminUserBinding;

public class UsersAdminFragment extends BaseFragment<FragmentAdminUserBinding> {
    private AdminViewModel viewModel;
    public UsersAdminFragment(AdminViewModel userViewModel) {
        this.viewModel= userViewModel;
    }

    @Override
    protected FragmentAdminUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminUserBinding.inflate(inflater);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void OnClick() {

    }
}
