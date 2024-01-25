package doan.npnm.sharerecipe.fragment.admin;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import doan.npnm.sharerecipe.adapter.UsersAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentHomeUserBinding;
import doan.npnm.sharerecipe.model.Users;

public class HomeAdminFragment extends BaseFragment<FragmentHomeUserBinding> {

    public AppViewModel homeviewModel;

    public HomeAdminFragment(AppViewModel appViewModel) {
        this.homeviewModel=appViewModel;
    }

    @Override
    protected FragmentHomeUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeUserBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {
        UsersAdapter us= new UsersAdapter();
        ArrayList<Users> listUser= new ArrayList<>();
        listUser.add(homeviewModel.users.getValue());

        binding.rcvData.setAdapter(us);

    //    us.setItems(listUser);
    }
}