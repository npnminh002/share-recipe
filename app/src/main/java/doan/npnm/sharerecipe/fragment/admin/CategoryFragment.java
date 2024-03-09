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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.admin.CategoryTableAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminCategoryBinding;
import doan.npnm.sharerecipe.dialog.DeleteDialog;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.utility.Constant;

public class CategoryFragment extends BaseFragment<FragmentAdminCategoryBinding> {
    private AdminViewModel viewModel;

    public CategoryFragment(AdminViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    private MutableLiveData<Category> selectCategory = new MutableLiveData<>(null);
    private MutableLiveData<Boolean> isEditCategory = new MutableLiveData<>(false);

    @Override
    protected FragmentAdminCategoryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminCategoryBinding.inflate(inflater);
    }

    @Override
    protected void initView() {
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

        isEditCategory.observe(this, data -> {
            binding.bntSave.setClickable(data);
            binding.bntSave.setBackgroundResource(data ? R.drawable.bg_click_start_enable : R.drawable.bg_click_start_disable);
        });

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
        binding.icEitImage.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });
        binding.reloaIcon.setOnClickListener(v -> {
            initView();
            binding.btnRemove.setClickable(false);
            binding.bntSave.setClickable(false);
            selectCategory.postValue(null);
            viewModel.ongetCategory();
        });
        binding.btnAdd.setOnClickListener(v -> {
            addNewCategory();
        });

        binding.nameCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

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
            public void afterTextChanged(Editable s) {


            }
        });

        binding.btnRemove.setOnClickListener(v -> {
            deleteCategory();
        });

        binding.bntSave.setOnClickListener(v -> {
            if (isAddNew) {
                viewModel.putImgToStorage(storageReference.child(Constant.CATEGORY), uriImg, new UserViewModel.OnPutImageListener() {
                    @Override
                    public void onComplete(String url) {
                        Category ct = new Category(
                                String.valueOf(viewModel.categoryMutableLiveData.getValue().size() + 1),
                                binding.nameCategory.getText().toString(),
                                url
                        );
                        firestore.collection(Constant.CATEGORY)
                                .document(ct.Id)
                                .set(ct)
                                .addOnSuccessListener(documentReference -> {
                                    showToast("Add new  success");
                                    viewModel.ongetCategory();

                                });
                        initView();
                    }

                    @Override
                    public void onFailure(String mess) {

                    }
                });
            } else {
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
                                    }).addOnFailureListener(e -> showToast(e.getMessage()));
                        }

                        @Override
                        public void onFailure(String mess) {
                            showToast("Upload Img: " + mess);
                        }

                    });
                    initView();
                } else {
                    firestore.collection(Constant.CATEGORY)
                            .document(selectCategory.getValue().Id)
                            .update("Name", binding.nameCategory.getText().toString().trim())
                            .addOnSuccessListener(unused -> {
                                showToast("Update success");
                                viewModel.ongetCategory();

                            }).addOnFailureListener(e -> showToast(e.getMessage()));
                    initView();
                }
            }
            hideKeyboard();

        });


    }

    private void addNewCategory() {
        isAddNew=true;

        selectCategory.postValue(null);
        isEditCategory.postValue(true);
    }
    private void deleteCategory(){
        if(selectCategory.getValue()!=null){
            new DeleteDialog(requireContext(), () -> {
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

    private final int REQUEST_CODE_PICK_IMAGE = 100;

    private void openImagePicker() {
        isEditImage = true;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
    }


    private boolean isAddNew = false;
    private Uri uriImg;

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

    private boolean isEditImage = false;
}
