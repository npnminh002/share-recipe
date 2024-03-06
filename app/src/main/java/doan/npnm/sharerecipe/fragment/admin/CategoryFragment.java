package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminChartBinding;

public class CategoryFragment extends BaseFragment<FragmentAdminChartBinding> {
    private AdminViewModel viewModel;
    public CategoryFragment(AdminViewModel userViewModel) {
        this.viewModel= userViewModel;
    }

    @Override
    protected FragmentAdminChartBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminChartBinding.inflate(inflater);
    }

    @Override
    protected void initView() {

    }

    @Override
    public void OnClick() {

    }
}
