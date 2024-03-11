package doan.npnm.sharerecipe.activity.user;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.Nullable;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.activity.start.SignInActivity;
import doan.npnm.sharerecipe.base.BaseActivity;
import doan.npnm.sharerecipe.database.models.UserFollower;
import doan.npnm.sharerecipe.databinding.ActivityMainBinding;
import doan.npnm.sharerecipe.fragment.user.DetailRecipeFragment;
import doan.npnm.sharerecipe.fragment.user.HomeUserFragment;
import doan.npnm.sharerecipe.fragment.user.NotificationFragment;
import doan.npnm.sharerecipe.fragment.user.ProfileUserFragment;
import doan.npnm.sharerecipe.fragment.user.SeachFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;


public class MainActivity extends BaseActivity<ActivityMainBinding> {
    @NonNull
    @Override
    protected ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    String key = "";

    @Override
    protected void createView() {

        // Quan sát sự kiện đăng nhập từ ViewModel
        userViewModel.isSingApp.observe(this, data -> {
            if (data) {
                // Nếu đang nhập, chuyển đến SignInActivity và kết thúc MainActivity
                startActivity(new Intent(MainActivity.this, SignInActivity.class));
                finish();
            }
        });

// Hiển thị dialog loading
        loaddingDialog.show();

// Quan sát từ khóa tìm kiếm từ ViewModel
        userViewModel.searchKey.observe(this, key -> {
            if (!key.isEmpty()) {
                // Nếu từ khóa tìm kiếm không trống, gán vào biến key và chuyển đến mục tìm kiếm trong bottom navigation
                this.key = key;
                binding.bottomNavigation.setSelectedItemId(R.id.icon_search_user);
            }
        });

// Lấy dữ liệu từ intent
        String data = getIntent().getStringExtra(Constant.RECIPE);
        if (data != null && data.length() > 0) {
            // Nếu có dữ liệu từ intent, lấy dữ liệu recipe từ ViewModel
            userViewModel.getRecipeByID(data, new FetchByID<Recipe>() {

                @Override
                public void onSuccess(Recipe data) {
                    // Nếu thành công, hiển thị fragment chi tiết recipe
                    addFragment(new DetailRecipeFragment(data, userViewModel), android.R.id.content, true);
                }

                @Override
                public void onErr(Object err) {
                    // Nếu có lỗi, mở fragment HomeUserFragment
                    openFragment(new HomeUserFragment(userViewModel), true, true);
                }
            });
        }

// Thêm fragment HomeUserFragment vào layoutHome
        addFragment(new HomeUserFragment(userViewModel), R.id.layoutHome, true);
// Hiển thị layoutHome
        binding.layoutHome.setVisibility(View.VISIBLE);

// Ẩn giao diện root trong một khoảng thời gian và sau đó ẩn dialog loading
        new Handler().postDelayed(() -> {
            binding.getRoot().setVisibility(View.VISIBLE);
            loaddingDialog.dismiss();
        }, 2000);

// Thiết lập sự kiện chọn item trong bottom navigation
        binding.bottomNavigation.setOnNavigationItemSelectedListener(OnBottomEventSelect);

// Quan sát sự thay đổi trong dữ liệu người dùng từ ViewModel
        userViewModel.users.observe(this, users -> {
            if (users != null) {
                // Nếu dữ liệu người dùng không rỗng, thực hiện các hành động liên quan đến người dùng
                listenerUserFollow(users);
            }
        });

    }

    // Phương thức để lắng nghe sự thay đổi của người theo dõi
    private void listenerUserFollow(Users users) {
        userViewModel.fbDatabase.getReference(Constant.FOLLOW_USER)
                .child(users.UserID)
                .child("OtherFollow")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        // Đánh dấu người dùng đã đăng xuất khỏi ứng dụng
                        userViewModel.database.userFollowerDao().SignOutApp();
                        // Duyệt qua danh sách người theo dõi
                        for (DataSnapshot doc : snapshot.getChildren()) {
                            // Thêm người theo dõi vào cơ sở dữ liệu
                            userViewModel.database.userFollowerDao()
                                    .addUserFollower(new UserFollower() {{
                                        AuthID = doc.getKey();
                                    }});
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý khi có lỗi xảy ra trong quá trình đọc dữ liệu
                    }
                });
    }

    // Sự kiện chọn item trong BottomNavigationView
    @SuppressLint("NewApi")
    private BottomNavigationView.OnNavigationItemSelectedListener OnBottomEventSelect = item -> {
        if (item.getItemId() == R.id.icon_home_user) {
            // Hiển thị layout chứa trang chính
            binding.layoutHome.setVisibility(View.VISIBLE);
            binding.layoutMain.setVisibility(View.GONE);
            return true;
        } else if (item.getItemId() == R.id.icon_search_user) {
            // Mở fragment tìm kiếm
            binding.layoutHome.setVisibility(View.GONE);
            openFragment(new SeachFragment(userViewModel).newInstance(new HashMap<String, Serializable>() {{
                put("Key", key);
            }}), true, true);
            return true;
        } else if (item.getItemId() == R.id.icon_notification_user) {
            // Mở fragment thông báo
            binding.layoutHome.setVisibility(View.GONE);
            openFragment(new NotificationFragment(userViewModel), true, false);
            return true;
        } else if (item.getItemId() == R.id.icon_users_user) {
            // Mở fragment hồ sơ người dùng
            binding.layoutHome.setVisibility(View.GONE);
            openFragment(new ProfileUserFragment(userViewModel), true, true);
            return true;
        } else {
            return false;
        }
    };


    @Override
    public void OnClick() {
    }

    public void openFragment(Fragment fragment, boolean fullView, @Nullable boolean isAdd) {
        if (fullView) {
            binding.layoutData1.setVisibility(View.VISIBLE);
            binding.layoutMain.setVisibility(View.GONE);
            if (isAdd) {
                addFragment(fragment, R.id.layoutData1, true);
            } else {
                replaceFragment(fragment, R.id.layoutData1, true);
            }

        } else {
            binding.layoutData1.setVisibility(View.GONE);
            binding.layoutMain.setVisibility(View.VISIBLE);
            if (isAdd) {
                addFragment(fragment, R.id.layoutData2, true);
            } else {
                replaceFragment(fragment, R.id.layoutData2, true);
            }
        }
    }
}