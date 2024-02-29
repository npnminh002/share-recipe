package doan.npnm.sharerecipe.activity.admin;

import android.annotation.SuppressLint;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.databinding.ActivityAdminMainBinding;
import doan.npnm.sharerecipe.fragment.admin.ChartAdminFragment;
import doan.npnm.sharerecipe.fragment.admin.HomeAdminFragment;
import doan.npnm.sharerecipe.fragment.admin.NotificationAdminFragment;
import doan.npnm.sharerecipe.fragment.admin.ReportAdminFragment;
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
        adminViewModel= new ViewModelProvider(this).get(AdminViewModel.class);
        adminViewModel.userLogin.postValue(userViewModel.getUsers().getValue());
        openFragment(new HomeAdminFragment(adminViewModel));
        binding.bottomNavigation.setOnNavigationItemSelectedListener(OnBottomEventSelect);
    }

    @SuppressLint("NewApi")
    private BottomNavigationView.OnNavigationItemSelectedListener OnBottomEventSelect = item -> {
        if (item.getItemId() == R.id.icon_home_admin) {
            openFragment(new HomeAdminFragment(adminViewModel));
            return true;
        } else if (item.getItemId() == R.id.icon_chart_admin) {
            openFragment(new ChartAdminFragment(adminViewModel));
            return true;
        } else if (item.getItemId() == R.id.icon_notification_admin) {
            openFragment(new NotificationAdminFragment(adminViewModel));
            return true;
        } else if (item.getItemId() == R.id.icon_users_admin) {
            openFragment(new UsersAdminFragment(adminViewModel));
            return true;
        } else if (item.getItemId() == R.id.icon_report_admin) {
            openFragment(new ReportAdminFragment(adminViewModel));
            return true;
        } else {
            return false;
        }
    };

    private void openFragment(Fragment fragment) {
        addFragment(fragment, R.id.llViewAdmin, true);
    }


    @Override
    public void OnClick() {

    }
}