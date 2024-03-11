package doan.npnm.sharerecipe.fragment.user.addrecipe;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.ImageRecipeAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.databinding.FragmentFiveRecipeBinding;
public class FiveRecipeFragment extends BaseFragment<FragmentFiveRecipeBinding> {
    // ViewModel của người dùng và ViewModel của công thức
    public UserViewModel viewModel;
    private RecipeViewModel recipeViewModel;
    // Thông tin người dùng
    private Users users;

    // Constructor nhận ViewModels và thông tin người dùng
    public FiveRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
        this.users = viewModel.users.getValue();
    }

    // Đối tượng công thức
    public Recipe recipe;
    // Adapter cho danh sách hình ảnh
    private ImageRecipeAdapter adapter;

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentFiveRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFiveRecipeBinding.inflate(getLayoutInflater());
    }

    // Danh sách URI của hình ảnh
    ArrayList<Uri> listUri = new ArrayList<>();

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Quan sát sự kiện thêm công thức mới
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(FiveRecipeFragment.this);
        });
        // Khởi tạo adapter cho danh sách hình ảnh
        adapter = new ImageRecipeAdapter(new ImageRecipeAdapter.ImageItemEvent() {
            // Xử lý sự kiện thêm hình ảnh
            @Override
            public void onAdd() {
                selectImages();
            }

            // Xử lý sự kiện xóa hình ảnh
            @Override
            public void onRemove(int pos) {
                new ConfirmDialog(FiveRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
                    listUri.remove(pos);
                    recipeViewModel.listSelect.postValue(listUri);
                }).show();
            }
        });
        // Quan sát dữ liệu công thức từ ViewModel
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            recipe.RecipeAuth = users.Id;
        });
        // Quan sát danh sách URI hình ảnh từ ViewModel
        recipeViewModel.listSelect.observe(this, data -> {
            if (data.size() == 0) {
                data = new ArrayList<>();
                data.add(null);
            }
            adapter.setItems(data);
        });
        // Thiết lập adapter cho danh sách hình ảnh
        binding.listImg.setAdapter(adapter);
    }

    // Phương thức chọn hình ảnh
    private void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    // Xử lý kết quả trả về từ trình chọn hình ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();
                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    listUri.add(imageUri);
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                listUri.add(imageUri);
            }
            int indexNull = listUri.indexOf(null);
            if (indexNull >= 0) {
                listUri.remove(indexNull);
            }
            listUri.add(0, null);
            recipeViewModel.listSelect.postValue(listUri);
            adapter.setItems(listUri);
        }
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> closeFragment(FiveRecipeFragment.this));
        // Xử lý sự kiện khi nhấn nút tiếp theo
        binding.btnNext.setOnClickListener(v -> {
            if (recipeViewModel.listSelect.getValue().size() != 0) {
                replaceFragment(new PreviewRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
            } else {
                showToast("Please choose image ");
            }
        });
        // Xử lý sự kiện khi nhấn nút trở lại
        binding.btnPrev.setOnClickListener(v -> closeFragment(FiveRecipeFragment.this));
    }
}
