package doan.npnm.sharerecipe.activity.admin;

import android.annotation.SuppressLint;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityAdminMainBinding;
import doan.npnm.sharerecipe.fragment.admin.CategoryFragment;
import doan.npnm.sharerecipe.fragment.admin.HomeAdminFragment;
import doan.npnm.sharerecipe.fragment.admin.AccountAdminFragment;
import doan.npnm.sharerecipe.fragment.admin.UsersAdminFragment;

public class AdminMainActivity extends BaseActivity<ActivityAdminMainBinding> {

    private AdminViewModel adminViewModel;

    @Override
    protected ActivityAdminMainBinding getViewBinding() {
        return ActivityAdminMainBinding.inflate(getLayoutInflater());
    }

    @SuppressLint("NewApi")
    @Override
    protected void createView() {
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.userLogin.postValue(userViewModel.getUsers().getValue());
        addFragment(new HomeAdminFragment(adminViewModel), R.id.llViewAdminHome, true);
        addFragment(new CategoryFragment(adminViewModel),R.id.llViewCategory,true);
        addFragment(new AccountAdminFragment(adminViewModel),R.id.llAccountAdmin,true);
        addFragment(new UsersAdminFragment(adminViewModel),R.id.llUsers,true);
        binding.bottomNavigation.setOnNavigationItemSelectedListener(OnBottomEventSelect);
    }

    @SuppressLint("NewApi")
    private BottomNavigationView.OnNavigationItemSelectedListener OnBottomEventSelect = item -> {
        if (item.getItemId() == R.id.icon_home_admin) {
            binding.llViewAdminHome.setVisibility(View.VISIBLE);
            binding.llViewCategory.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            return true;
        } else if (item.getItemId() == R.id.icon_category_admin) {
            binding.llViewAdminHome.setVisibility(View.GONE);
            binding.llViewCategory.setVisibility(View.VISIBLE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            return true;
        } else if (item.getItemId() == R.id.icon_users_admin) {
            binding.llViewAdminHome.setVisibility(View.GONE);
            binding.llViewCategory.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.VISIBLE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            return true;
        } else if (item.getItemId() == R.id.icon_account_admin) {
            binding.llViewAdminHome.setVisibility(View.GONE);
            binding.llViewCategory.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.VISIBLE);
            return true;
        } else {
            return false;
        }
    };

    @Override
    public void OnClick() {

    }
}