package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminNotificationBinding;

public class NotificationAdminFragment extends BaseFragment<FragmentAdminNotificationBinding> {
    private AdminViewModel viewModel;
    public NotificationAdminFragment(AdminViewModel userViewModel) {
        this.viewModel= userViewModel;
    }

    @Override
    protected FragmentAdminNotificationBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminNotificationBinding.inflate(inflater);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void OnClick() {

    }
}
