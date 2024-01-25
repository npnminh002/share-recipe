package doan.npnm.sharerecipe.fragment.admin;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.UsersAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentHomeBinding;
import doan.npnm.sharerecipe.model.Users;

public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    public AppViewModel homeviewModel;

    public HomeFragment(AppViewModel appViewModel) {
        this.homeviewModel=appViewModel;
    }

    @Override
    protected FragmentHomeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(getLayoutInflater());
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