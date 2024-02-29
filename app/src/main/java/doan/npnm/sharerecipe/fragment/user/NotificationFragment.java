package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentNotificationBinding;

public class NotificationFragment extends BaseFragment<FragmentNotificationBinding> {

    private UserViewModel viewModel;
    public NotificationFragment(UserViewModel userViewModel) {
        this.viewModel=userViewModel;
    }

    @Override
    protected FragmentNotificationBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentNotificationBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        binding.edtSearchData.setText(""+viewModel.database.userFollowerDao().getDataList().size());
    }

    @Override
    public void OnClick() {

    }
}
