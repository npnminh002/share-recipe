package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import doan.npnm.sharerecipe.adapter.UsersAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentHomeAdminBinding;
import doan.npnm.sharerecipe.databinding.FragmentHomeUserBinding;
import doan.npnm.sharerecipe.model.Users;

public class HomeAdminFragment extends BaseFragment<FragmentHomeAdminBinding> {

    public AppViewModel homeviewModel;
    public HomeAdminFragment(AppViewModel appViewModel) {
        this.homeviewModel=appViewModel;
    }

    @Override
    public void OnClick() {

    }

    @Override
    protected FragmentHomeAdminBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeAdminBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }
}