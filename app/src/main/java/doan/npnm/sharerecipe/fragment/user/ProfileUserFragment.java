package doan.npnm.sharerecipe.fragment.user;

import android.annotation.SuppressLint;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.MyRecipeViewAdapter;
import doan.npnm.sharerecipe.adapter.users.RecipeRecentViewAdapter;
import doan.npnm.sharerecipe.adapter.users.RecipeLoveViewAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.LoveRecipe;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.databinding.FragmentProfileUserBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.dialog.DeleteDialog;
import doan.npnm.sharerecipe.fragment.user.addrecipe.FirstRecipeFragment;
import doan.npnm.sharerecipe.interfaces.OnRecipeEvent;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeStatus;
import doan.npnm.sharerecipe.utility.Constant;

public class ProfileUserFragment extends BaseFragment<FragmentProfileUserBinding> {
    private UserViewModel viewModel;
    private RecipeViewModel recipeViewModel;

    public ProfileUserFragment(UserViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút cài đặt
        binding.icSetting.setOnClickListener(v -> {
            addFragment(new SettingFragment(viewModel), android.R.id.content, true);
        });

        // Xử lý sự kiện khi nhấn nút đăng nhập
        binding.signInApp.setOnClickListener(v -> {
            viewModel.isSingApp.postValue(true);
        });

        // Xử lý sự kiện khi nhấn nút thêm công thức nấu ăn mới
        binding.icAddRecipe.setOnClickListener(v -> {
            // Khởi tạo ViewModel cho công thức nấu ăn
            recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
            recipeViewModel.recipeLiveData.postValue(new Recipe());
            addFragment(new FirstRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
        });
    }

    @Override
    protected FragmentProfileUserBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileUserBinding.inflate(getLayoutInflater());
    }

    private RecipeRecentViewAdapter recentViewAdapter;
    private RecipeLoveViewAdapter loveViewAdapter;

    private MyRecipeViewAdapter myViewAdapter;

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        if (viewModel.users.getValue() == null) {
            binding.llProfile.setVisibility(View.GONE);
            binding.llNoProfile.setVisibility(View.VISIBLE);
        } else {
            // Nếu đã đăng nhập, hiển thị thông tin cá nhân
            binding.llProfile.setVisibility(View.VISIBLE);
            binding.llNoProfile.setVisibility(View.GONE);
            viewModel.users.observe(this, users -> {
                if (users != null) {
                    // Load ảnh đại diện người dùng nếu có
                    if (!Objects.equals(users.UrlImg, "")) {
                        loadImage(users.UrlImg, binding.ImgUser);
                    }
                    // Hiển thị thông tin người dùng
                    binding.txtEmail.setText(users.Email);
                    binding.userName.setText(users.UserName);
                    binding.txtRecipe.setText("" + users.Recipe);
                    binding.txtFollow.setText("" + users.Follow);
                    binding.txtFollower.setText("" + users.Follower);
                    binding.txtNickName.setText("#" + users.NickName);
                }
            });

            // Khởi tạo và cài đặt adapter cho danh sách công thức xem gần đây
            recentViewAdapter = new RecipeRecentViewAdapter(new OnRecipeEvent() {
                @Override
                public void onView(Recipe rcp) {
                    // Xử lý sự kiện khi nhấn xem công thức
                    if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                        viewModel.database.recentViewDao().removeRecent(rcp.Id);
                    }
                    viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                        AuthID = rcp.RecipeAuth;
                        RecipeID = rcp.Id;
                        ViewTime = getTimeNow();
                        Recipe = rcp.toJson();
                    }});
                    addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
                }

                @Override
                public void onSave(Recipe rcp) {
                    // Xử lý sự kiện khi nhấn lưu công thức
                    if (viewModel.database.saveRecipeDao().checkExistence(rcp.Id)) {
                        viewModel.database.saveRecipeDao().removeRecent(rcp.Id);
                    }
                    viewModel.database.saveRecipeDao().addRecentView(new SaveRecipe() {{
                        AuthID = rcp.RecipeAuth;
                        RecipeID = rcp.Id;
                        SaveTime = getTimeNow();
                        Recipe = rcp.toJson();
                    }});
                }
            });
            // Cài đặt adapter cho danh sách công thức xem gần đây
            binding.rcvRecentView.setAdapter(recentViewAdapter);
            ArrayList<RecentView> recentViews = (ArrayList<RecentView>) viewModel.database.recentViewDao().getListRecentView();
            if (recentViews.isEmpty()) {
                binding.txtNo1.setVisibility(View.VISIBLE);
            }
            recentViewAdapter.setItems(recentViews);

            // Khởi tạo và cài đặt adapter cho danh sách công thức yêu thích
            loveViewAdapter = new RecipeLoveViewAdapter(new RecipeLoveViewAdapter.OnRecipeEvent() {
                @Override
                public void onView(Recipe rcp) {
                    // Xử lý sự kiện khi nhấn xem công thức yêu thích
                    if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                        viewModel.database.recentViewDao().removeRecent(rcp.Id);
                    }
                    viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                        AuthID = rcp.RecipeAuth;
                        RecipeID = rcp.Id;
                        ViewTime = getTimeNow();
                        Recipe = rcp.toJson();
                    }});
                    addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
                }

                @Override
                public void onRemove(Recipe recipe, int pos) {
                    // Xử lý sự kiện khi nhấn bỏ yêu thích công thức
                    viewModel.database.saveRecipeDao().removeRecent(recipe.Id);
                    viewModel.onUnlove(recipe);
                    loveViewAdapter.removeItem(pos);
                }
            });
            // Cài đặt adapter cho danh sách công thức yêu thích
            binding.rcvSaveRecipe.setAdapter(loveViewAdapter);
            ArrayList<LoveRecipe> loveRecipes = (ArrayList<LoveRecipe>) viewModel.database.loveRecipeDao().getLoveArr();
            if (loveRecipes.isEmpty()) {
                binding.txtNo2.setVisibility(View.VISIBLE);
            }
            loveViewAdapter.setItems(loveRecipes);

            // Khởi tạo và cài đặt adapter cho danh sách công thức của người dùng
            myViewAdapter = new MyRecipeViewAdapter(new MyRecipeViewAdapter.OnRecipeEvent() {
                @Override
                public void onView(Recipe rcp) {
                    // Xử lý sự kiện khi nhấn xem công thức của người dùng
                    addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
                }

                @Override
                public void onRemove(Recipe recipe, int pos) {
                    // Xử lý sự kiện khi nhấn xóa công thức của người dùng
                    new DeleteDialog(ProfileUserFragment.this.requireContext(), getString(R.string.cf_delete), () -> {
                        // Thực hiện xóa công thức trên Firestore
                        HashMap<String,Object> update= new HashMap<>();
                        update.put("RecipeStatus",RecipeStatus.DELETED);
                        ArrayList<String> hstr= recipe.History;
                        hstr.add("Auth want delete :"+getTimeNow());
                        update.put("History",hstr);
                        firestore.collection(Constant.RECIPE)
                                .document(recipe.Id)
                                .update(update)
                                .addOnSuccessListener(unused -> {
                                    showToast(R.string.del_suc_rcp);
                                }).addOnFailureListener(e -> {
                                    showToast(e.getMessage());
                                });
                    }).show();
                }

                @Override
                public void onEdit(Recipe recipe) {
                    // Xử lý sự kiện khi nhấn sửa công thức của người dùng
                    replaceFullViewFragment(new EditRecipeFragment(viewModel, recipe), android.R.id.content, true);
                }
            });

            // Hiển thị danh sách công thức của người dùng
            binding.rcvMyRecipe.setAdapter(myViewAdapter);
            viewModel.myRecipeArr.observe(this, data -> {
                if (data.isEmpty()) {
                    binding.txtNo3.setVisibility(View.VISIBLE);
                } else {
                    binding.txtNo3.setVisibility(View.GONE);
                    binding.txtRecipe.setText("" + data.size());
                    myViewAdapter.setItems(data);
                }
            });

            // Xử lý sự kiện khi nhấn vào phần theo dõi và người theo dõi
            binding.llFollow.setOnClickListener(v -> {
                addFragment(new FollowerFragment(viewModel, FollowerFragment.FOLLOW.FOLLOW), android.R.id.content, true);
            });

            binding.llFollower.setOnClickListener(v -> {
                addFragment(new FollowerFragment(viewModel, FollowerFragment.FOLLOW.FOLLOWER), android.R.id.content, true);
            });
        }
    }
}
