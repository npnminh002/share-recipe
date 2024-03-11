package doan.npnm.sharerecipe.fragment.user.addrecipe;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentFirstRecipeBinding;
import doan.npnm.sharerecipe.lib.widget.TextValue;
import doan.npnm.sharerecipe.model.recipe.Recipe;
public class FirstRecipeFragment extends BaseFragment<FragmentFirstRecipeBinding> {
    // ViewModel của người dùng và ViewModel của công thức
    public UserViewModel viewModel;
    private RecipeViewModel recipeViewModel;
    // Đối tượng công thức
    private Recipe recipe;

    // Constructor nhận ViewModels
    public FirstRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentFirstRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFirstRecipeBinding.inflate(getLayoutInflater());
    }

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Khởi tạo các trường văn bản để gán giá trị vào công thức
        name = new TextValue(binding.nameOfRecipe).onValueChange(s -> recipe.Name = s);
        description = new TextValue(binding.description).onValueChange(s -> recipe.Description = s);
        // Quan sát sự kiện thêm công thức mới
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(FirstRecipeFragment.this);
        });

        // Quan sát dữ liệu công thức từ ViewModel
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;

            // Hiển thị hình ảnh đã chọn nếu có
            if (recipeViewModel.imgUri != null) {
                loadImage(recipeViewModel.imgUri, binding.imgProduct);
                binding.txtChooseImage.setVisibility(View.GONE);
            }
            // Gán giá trị cho các trường văn bản
            binding.nameOfRecipe.setText(data.Name == null ? "" : data.Name);
            binding.description.setText(data.Description == null ? "" : data.Description);
        });
    }

    // Trường văn bản để gán giá trị vào tên và mô tả công thức
    TextValue name;
    TextValue description;

    // Mã yêu cầu để chọn hình ảnh
    private int REQUEST_CODE_IMAGE = 1;

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> closeFragment(FirstRecipeFragment.this));

        // Xử lý sự kiện khi nhấn nút tiếp theo
        binding.btnNext.setOnClickListener(v -> {
            // Kiểm tra các giá trị nhập vào
            if (checkValue()) {
                // Nếu công thức chưa được khởi tạo, tạo mới
                if (recipe == null) {
                    recipe = new Recipe();
                }
                // Cập nhật công thức vào ViewModel và chuyển sang màn hình tiếp theo
                recipeViewModel.recipeLiveData.postValue(recipe);
                addFragment(new SecondRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
                hideKeyboard();
            }
        });

        // Xử lý sự kiện khi nhấn vào chọn hình ảnh
        binding.llChoseImage.setOnClickListener(v -> {
            // Kiểm tra quyền truy cập hình ảnh
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });
    }

    // Phương thức kiểm tra giá trị nhập vào
    private boolean checkValue() {
        if (recipe.Name.isEmpty()) {
            // Nếu tên công thức trống, hiển thị lỗi
            name.onError();
            return false;
        } else if (recipe.Description == null || recipe.Description.equals("")) {
            // Nếu mô tả công thức trống, hiển thị lỗi
            description.onError();
            return false;
        } else return recipeViewModel.imgUri != null;
    }

    // Mở trình chọn hình ảnh
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    // Uri của hình ảnh đã chọn
    private Uri uriImg;

    // Xử lý kết quả trả về từ trình chọn hình ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                uriImg = data.getData();
                // Hiển thị hình ảnh đã chọn
                loadImage(String.valueOf(uriImg), binding.imgProduct);
                recipeViewModel.imgUri = uriImg;
                binding.txtChooseImage.setVisibility(View.GONE);
            }
        }
    }
}

