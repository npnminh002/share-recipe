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

    // Phương thức trả về đối tượng Binding cho activity.
    @Override
    protected ActivityAdminMainBinding getViewBinding() {
        return ActivityAdminMainBinding.inflate(getLayoutInflater());
    }

    // Phương thức khởi tạo giao diện.
    @SuppressLint("NewApi")
    @Override
    protected void createView() {

        adminViewModel= new ViewModelProvider(this).get(AdminViewModel.class);
        // Khởi tạo ViewModel cho activity.
        adminViewModel = new ViewModelProvider(this).get(AdminViewModel.class);
        // Gán người dùng đăng nhập vào LiveData của ViewModel.
        adminViewModel.userLogin.postValue(userViewModel.getUsers().getValue());

        // Thêm fragment HomeAdminFragment vào layout llViewAdminHome.
        addFragment(new HomeAdminFragment(adminViewModel), R.id.llViewAdminHome, true);
        // Thêm fragment CategoryFragment vào layout llViewCategory.
        addFragment(new CategoryFragment(adminViewModel), R.id.llViewCategory, true);
        // Thêm fragment AccountAdminFragment vào layout llAccountAdmin.
        addFragment(new AccountAdminFragment(adminViewModel), R.id.llAccountAdmin, true);
        // Thêm fragment UsersAdminFragment vào layout llUsers.
        addFragment(new UsersAdminFragment(adminViewModel), R.id.llUsers, true);

        // Lắng nghe sự kiện chọn item trên bottom navigation.
        binding.bottomNavigation.setOnNavigationItemSelectedListener(OnBottomEventSelect);
    }

    // Xử lý sự kiện chọn item trên bottom navigation.
    @SuppressLint("NewApi")
    private BottomNavigationView.OnNavigationItemSelectedListener OnBottomEventSelect = item -> {
        // Xử lý khi chọn item home.
        if (item.getItemId() == R.id.icon_home_admin) {
            binding.llViewAdminHome.setVisibility(View.VISIBLE);
            binding.llViewCategory.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            binding.llUsers.setVisibility(View.GONE);
            return true;
        }
        // Xử lý khi chọn item category.
        else if (item.getItemId() == R.id.icon_category_admin) {
            binding.llViewAdminHome.setVisibility(View.GONE);
            binding.llViewCategory.setVisibility(View.VISIBLE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            binding.llUsers.setVisibility(View.GONE);
            return true;
        }
        // Xử lý khi chọn item users.
        else if (item.getItemId() == R.id.icon_users_admin) {
            binding.llViewAdminHome.setVisibility(View.GONE);
            binding.llViewCategory.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.GONE);
            binding.llUsers.setVisibility(View.VISIBLE);
            return true;
        }
        // Xử lý khi chọn item account.
        else if (item.getItemId() == R.id.icon_account_admin) {
            binding.llViewAdminHome.setVisibility(View.GONE);
            binding.llViewCategory.setVisibility(View.GONE);
            binding.llAccountAdmin.setVisibility(View.VISIBLE);
            binding.llUsers.setVisibility(View.GONE);
            return true;
        } else {
            return false;
        }
    };

    @Override
    public void OnClick() {
        // Không cần xử lý sự kiện click trong activity này.
    }
}
