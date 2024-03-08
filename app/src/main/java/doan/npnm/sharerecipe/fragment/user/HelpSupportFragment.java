package doan.npnm.sharerecipe.fragment.user;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentHelpSupportBinding;


public class HelpSupportFragment extends BaseFragment<FragmentHelpSupportBinding> {
    private UserViewModel viewModel;
    public HelpSupportFragment(UserViewModel viewModel) {
        this.viewModel=viewModel;
    }
    @Override
    protected FragmentHelpSupportBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHelpSupportBinding.inflate(inflater);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void OnClick() {

    }
}