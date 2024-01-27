package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentFirstRecipeBinding;

public class FirstRecipeFragment extends BaseFragment<FragmentFirstRecipeBinding>{
    public AppViewModel viewModel;

    public FirstRecipeFragment(AppViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentFirstRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFirstRecipeBinding.inflate(getLayoutInflater());
    }

    @Override
    protected void initView() {

    }

    @Override
    public void OnClick() {

    }
}