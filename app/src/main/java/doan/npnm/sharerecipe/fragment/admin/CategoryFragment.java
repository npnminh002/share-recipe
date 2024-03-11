package doan.npnm.sharerecipe.fragment.admin;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import java.util.HashMap;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.admin.CategoryTableAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminCategoryBinding;
import doan.npnm.sharerecipe.dialog.ReportDialog;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.utility.Constant;
public class CategoryFragment extends BaseFragment<FragmentAdminCategoryBinding> {
    private AdminViewModel viewModel;

    // Constructor nhận một viewModel của Admin
    public CategoryFragment(AdminViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    // LiveData để lưu trữ danh mục được chọn và trạng thái chỉnh sửa danh mục
    private MutableLiveData<Category> selectCategory = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isEditCategory = new MutableLiveData<>(false);

    @Override
    protected FragmentAdminCategoryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminCategoryBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
        // Quan sát sự thay đổi trong selectCategory và cập nhật giao diện tương ứng
        selectCategory.observe(this, category -> {
            if (category != null) {
                loadImage(category.Image, binding.imgPreview);
                binding.categoryID.setText(category.Id);
                binding.nameCategory.setText(category.Name);
                binding.urlImg.setText("Url: " + category.Image);
                binding.btnRemove.setClickable(true);
                binding.btnRemove.setBackgroundResource(R.drawable.bg_click_start_enable);
            } else {
                binding.btnRemove.setClickable(false);
                binding.bntSave.setClickable(false);
                binding.btnRemove.setBackgroundResource(R.drawable.bg_click_start_disable);
                binding.bntSave.setBackgroundResource(R.drawable.bg_click_start_disable);
                loadImage("", binding.imgPreview);
                binding.categoryID.setText("");
                binding.nameCategory.setText("");
                binding.urlImg.setText("Url: ");
            }
        });

        // Quan sát sự thay đổi trong isEditCategory và cập nhật trạng thái của nút lưu
        isEditCategory.observe(this, data -> {
            binding.bntSave.setClickable(data);
            binding.bntSave.setBackgroundResource(data ? R.drawable.bg_click_start_enable : R.drawable.bg_click_start_disable);
        });

        // Quan sát danh sách các danh mục và cập nhật RecyclerView
        viewModel.categoryMutableLiveData.observe(this, data -> {
            new CategoryTableAdapter(binding.tableLayout, getContext(), category -> {
                selectCategory.postValue(category);
            }).onFinih(() -> {
                binding.progressLoad.setVisibility(View.GONE);
            }).setData(data);
        });
    }

    @Override
    public void OnClick() {
        // Xử lý sự kiện click trên nút chỉnh sửa hình ảnh
        binding.icEitImage.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });

        // Xử lý sự kiện click trên nút làm mới
        binding.reloaIcon.setOnClickListener(v -> {
            initView();
            binding.btnRemove.setClickable(false);
            binding.bntSave.setClickable(false);
            selectCategory.postValue(null);
            viewModel.ongetCategory();
        });

        // Xử lý sự kiện click trên nút thêm mới danh mục
        binding.btnAdd.setOnClickListener(v -> {
            addNewCategory();
        });

        // Xử lý sự kiện thay đổi văn bản của tên danh mục
        binding.nameCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (selectCategory.getValue() != null) {
                    if (!binding.nameCategory.getText().toString().equals(selectCategory.getValue().Name)) {
                        isEditCategory.postValue(true);
                    } else {
                        isEditCategory.postValue(false);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Xử lý sự kiện click trên nút xóa danh mục
        binding.btnRemove.setOnClickListener(v -> {
            deleteCategory();
        });

        // Xử lý sự kiện click trên nút lưu danh mục
        binding.bntSave.setOnClickListener(v -> {
            if (isAddNew) {
                // Xử lý thêm mới danh mục
                addNewCategory();
            } else {
                // Xử lý cập nhật danh mục
                updateCategory();
            }
            // Ẩn bàn phím sau khi xử lý xong
            hideKeyboard();
        });
    }

    // Phương thức xóa danh mục
    private void deleteCategory() {
        if (selectCategory.getValue() != null) {
            new ReportDialog(requireContext(), () -> {
                firestore.collection(Constant.CATEGORY)
                        .document(selectCategory.getValue().Id)
                        .delete()
                        .addOnCompleteListener(task -> {
                            showToast("Delete success");
                            viewModel.ongetCategory();
                            initView();
                        })
                        .addOnFailureListener(e -> {
                            showToast(e.getMessage());
                        });
            }).show();
        }
    }

    // Biến và hằng số cho việc chọn hình ảnh và lưu trữ dữ liệu
    private final int REQUEST_CODE_PICK_IMAGE = 100;
    private boolean isAddNew = false;
    private boolean isEditImage = false;
    private Uri uriImg;

    // Phương thức mở trình chọn hình ảnh
    private void openImagePicker() {
        isEditImage = true;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }

    // Xử lý kết quả trả về từ trình chọn hình ảnh
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                uriImg = data.getData();
                loadImage(uriImg, binding.imgPreview);
                isEditCategory.postValue(true);
            }
        }
    }

    // Phương thức thêm mới danh mục
    private void addNewCategory() {
        isAddNew = true;
        selectCategory.postValue(null);
        isEditCategory.postValue(true);
    }

    // Phương thức cập nhật danh mục
    private void updateCategory() {
        if (isEditImage) {
            viewModel.putImgToStorage(storageReference.child(Constant.CATEGORY), uriImg, new UserViewModel.OnPutImageListener() {
                @Override
                public void onComplete(String url) {
                    HashMap<String, Object> obj = new HashMap<>();
                    obj.put("Name", binding.nameCategory.getText().toString().trim());
                    obj.put("Image", url);
                    firestore.collection(Constant.CATEGORY)
                            .document(selectCategory.getValue().Id)
                            .update(obj)
                            .addOnSuccessListener(unused -> {
                                showToast("Update success");
                                viewModel.ongetCategory();
                                initView();
                            }).addOnFailureListener(e -> showToast(e.getMessage()));
                }

                @Override
                public void onFailure(String mess) {
                    showToast("Upload Img: " + mess);
                }
            });
        } else {
            firestore.collection(Constant.CATEGORY)
                    .document(selectCategory.getValue().Id)
                    .update("Name", binding.nameCategory.getText().toString().trim())
                    .addOnSuccessListener(unused -> {
                        showToast("Update success");
                        viewModel.ongetCategory();
                        initView();
                    }).addOnFailureListener(e -> showToast(e.getMessage()));
        }
    }
}
