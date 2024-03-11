package doan.npnm.sharerecipe.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.DirectionsAdapter;
import doan.npnm.sharerecipe.adapter.users.ImageRecipeAdapter;
import doan.npnm.sharerecipe.adapter.users.IngridentsAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
import doan.npnm.sharerecipe.databinding.FragmentEditRecipeBinding;

public class EditRecipeFragment extends BaseFragment<FragmentEditRecipeBinding> {

    private UserViewModel viewModel;
    private RecipeViewModel recipeViewModel;

    public EditRecipeFragment(UserViewModel viewModel, Recipe recipe) {
        this.viewModel = viewModel;
        this.recipe = recipe;

    }

    private Recipe recipe;

    @Override
    protected FragmentEditRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentEditRecipeBinding.inflate(getLayoutInflater());
    }

    private DirectionsAdapter directionsAdapter;

    private IngridentsAdapter ingridentsAdapter;

    private ImageRecipeAdapter adapter;

    @Override
    protected void initView() {
        // Khởi tạo ViewModel để quản lý dữ liệu công thức
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeViewModel.recipeLiveData.postValue(recipe);

        // Xử lý sự kiện khi trường tên công thức mất focus
        binding.nameOfRecipe.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                recipe.Name = binding.nameOfRecipe.getText().toString();
                recipeViewModel.recipeLiveData.postValue(recipe);
            }
        });

        // Xử lý sự kiện khi trường mô tả mất focus
        binding.description.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                recipe.Description = binding.description.getText().toString();
                recipeViewModel.recipeLiveData.postValue(recipe);
            }
        });

        // Khởi tạo adapter cho phần hướng dẫn
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.EDIT, new DirectionsAdapter.OnDirectionEvent() {
            @Override
            public void onNameChange(Directions directions) {
                // Thay đổi tên hướng dẫn
                for (Directions id : recipe.Directions) {
                    if (id.Id == directions.Id) {
                        id.Name = directions.Name;
                        break;
                    }
                }
            }

            @Override
            public void onRemove(Directions id, int pos) {
                // Xóa hướng dẫn
                removeDirection(id, pos);
            }
        });
        binding.rcvDirection.setAdapter(directionsAdapter);

        // Khởi tạo adapter cho phần hình ảnh
        adapter = new ImageRecipeAdapter(new ImageRecipeAdapter.ImageItemEvent() {
            @Override
            public void onAdd() {
                // Thêm hình ảnh
                selectImages();
            }

            @Override
            public void onRemove(int pos) {
                // Xóa hình ảnh
                new ConfirmDialog(EditRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
                    listUri.remove(pos);
                    recipeViewModel.listSelect.postValue(listUri);
                }).show();
            }
        });
        binding.listImg.setAdapter(adapter);

        // Khởi tạo adapter cho phần nguyên liệu
        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.EDIT, new IngridentsAdapter.OnIngridentEvent() {
            @Override
            public void onNameChange(Ingredients ingredients) {
                // Thay đổi tên nguyên liệu
                for (Ingredients id : recipe.Ingredients) {
                    if (id.Id == ingredients.Id) {
                        id.Name = ingredients.Name;
                        break;
                    }
                }
            }

            @Override
            public void onQuantitiveChange(Ingredients ingredients) {
                // Thay đổi số lượng nguyên liệu
                for (Ingredients id : recipe.Ingredients) {
                    if (id.Id == ingredients.Id) {
                        id.Quantitative = ingredients.Quantitative;
                        break;
                    }
                }
            }

            @Override
            public void onRemove(Ingredients id, int pos) {
                // Xóa nguyên liệu
                removeIngridents(id, pos);
            }
        });
        binding.rcvIngrident.setAdapter(ingridentsAdapter);

        // Quan sát sự thay đổi của dữ liệu công thức
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            // Hiển thị thông tin công thức
            displayRecipeInfo(data);
        });

        // Khởi tạo danh sách URI và quan sát sự thay đổi của nó
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            listUri.add(0, null);
            recipe.ImagePreview.forEach(s -> {
                listUri.add(Uri.parse(s));
            });
            recipeViewModel.listSelect.postValue(listUri);
        }

        recipeViewModel.listSelect.observe(this, data -> {
            if (data != null) {
                adapter.setItems(data);
            }
        });
    }

    // Phương thức để hiển thị thông tin của công thức
    private void displayRecipeInfo(Recipe recipe) {
        if (recipeViewModel.imgUri != null) {
            loadImage(recipeViewModel.imgUri, binding.imgProduct);
        } else {
            loadImage(recipe.ImgUrl, binding.imgProduct);
        }
        binding.nameOfRecipe.setText(recipe.Name == null ? "" : recipe.Name);
        binding.description.setText(recipe.Description == null ? "" : recipe.Description);
        binding.timePrepare.setText(recipe.PrepareTime.Time);
        binding.selectMinutePP.setText(recipe.PrepareTime.TimeType);
        binding.timeCook.setText(recipe.CookTime.Time);
        binding.selectMinutePP.setText(recipe.CookTime.TimeType);
        binding.txtLever.setText(recipe.Level);

        directionsAdapter.setItems(recipe.Directions);
        ingridentsAdapter.setItems(recipe.Ingredients);
    }

    private void removeDirection(Directions id, int pos) {
        // Hiển thị hộp thoại xác nhận xóa hướng dẫn
        new ConfirmDialog(EditRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            // Xóa hướng dẫn khỏi danh sách
            recipe.Directions.remove(pos);
            // Cập nhật lại danh sách hướng dẫn trong adapter
            directionsAdapter.setItems(recipe.Directions);
        }).show();
    }

    private void removeIngridents(Ingredients id, int pos) {
        // Hiển thị hộp thoại xác nhận xóa nguyên liệu
        new ConfirmDialog(EditRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            // Xóa nguyên liệu khỏi danh sách
            recipe.Ingredients.remove(pos);
            // Cập nhật lại danh sách nguyên liệu trong adapter
            ingridentsAdapter.setItems(recipe.Ingredients);
        }).show();
    }

    ArrayList<Uri> listUri = new ArrayList<>();

    private void selectImages() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Xác định nếu request code là 1 và kết quả là RESULT_OK
        if (requestCode == 1 && resultCode == RESULT_OK) {

            isChoosePreivewImg = true; // Đã chọn hình xem trước
            // Nếu dữ liệu trả về là nhiều hình ảnh
            if (data.getClipData() != null) {
                int count = data.getClipData().getItemCount();

                for (int i = 0; i < count; i++) {
                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    listUri.add(imageUri); // Thêm các URI của hình ảnh vào danh sách
                }
            } else if (data.getData() != null) {
                Uri imageUri = data.getData();
                listUri.add(imageUri); // Thêm URI của hình ảnh vào danh sách
            }
            // Xác định vị trí của URI null trong danh sách và loại bỏ nếu có
            int indexNull = listUri.indexOf(null);
            if (indexNull >= 0) {
                listUri.remove(indexNull);
            }

            listUri.add(0, null); // Thêm một URI null vào đầu danh sách
            recipeViewModel.listSelect.postValue(listUri); // Cập nhật LiveData với danh sách URI đã chọn
            adapter.setItems(listUri); // Cập nhật adapter với danh sách URI
        } else if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            isChooseMainImg = true; // Đã chọn hình chính
            // Nếu dữ liệu trả về không null và có URI hình ảnh
            if (data != null && data.getData() != null) {
                uriImg = data.getData();
                loadImage(String.valueOf(uriImg), binding.imgProduct); // Load hình ảnh vào ImageView
                recipeViewModel.imgUri = uriImg; // Cập nhật URI hình ảnh trong ViewModel
            }
        }
    }




    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(EditRecipeFragment.this));
        binding.cancelIcon.setOnClickListener(v -> closeFragment(EditRecipeFragment.this));
        binding.btnSaveData.setOnClickListener(v -> {
            loaddingDialog.show();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                startUploadData();
                updateDataRecipe(recipeViewModel.recipeLiveData.getValue());
            }
        });

        binding.llChoseImage.setOnClickListener(v -> {
            if (allPermissionsGranted()) {
                permissionLauncher.launch(permissions);
            } else {
                openImagePicker();
            }
        });
    }

    private boolean isChoosePreivewImg = false;
    private boolean isChooseMainImg = false;


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void startUploadData() {
        StorageReference reference = storage.getReference(Constant.RECIPE).child(recipe.Id);
        ArrayList<String> listUrl = new ArrayList<>();

        if (isChooseMainImg) {
            uploadMainImage(reference, listUrl);
        } else if (isChoosePreivewImg) {
            uploadPreviewImages(reference, listUrl);
        } else {
            postRecipeLiveData(recipe);
        }
    }

    private void uploadMainImage(StorageReference reference, ArrayList<String> listUrl) {
        viewModel.putImgToStorage(reference, recipeViewModel.imgUri, new UserViewModel.OnPutImageListener() {
            @Override
            public void onComplete(String urlApp) {
                recipe.ImgUrl = urlApp;
                if (isChoosePreivewImg) {
                    uploadPreviewImages(reference, listUrl);
                } else {
                    postRecipeLiveData(recipe);
                }
            }

            @Override
            public void onFailure(String mess) {
                showToast(mess);
                loaddingDialog.dismiss();
            }
        });
    }

    private void uploadPreviewImages(StorageReference reference, ArrayList<String> listUrl) {
        AtomicInteger count = new AtomicInteger(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            recipeViewModel.listSelect.getValue().forEach(uri -> {
                viewModel.putImgToStorage(reference, uri, new UserViewModel.OnPutImageListener() {
                    @Override
                    public void onComplete(String url) {
                        listUrl.add(url);
                        if (count.incrementAndGet() == recipeViewModel.listSelect.getValue().size()) {
                            recipe.ImagePreview = listUrl;
                            postRecipeLiveData(recipe);
                        }
                    }

                    @Override
                    public void onFailure(String mess) {
                        showToast(mess);
                        loaddingDialog.dismiss();
                    }
                });
            });
        }
    }

    private void postRecipeLiveData(Recipe recipe) {
        recipeViewModel.recipeLiveData.postValue(recipe);
    }


    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_CODE_IMAGE);
    }

    private int REQUEST_CODE_IMAGE = 1000;
    private Uri uriImg;


    private void updateDataRecipe(Recipe value) {
        // Cập nhật dữ liệu công thức vào Firestore
        firestore.collection(Constant.RECIPE)
                .document(value.Id)
                .set(recipe) // Đặt dữ liệu của công thức
                .addOnSuccessListener(task -> {
                    showToast("Thêm dữ liệu thành công"); // Hiển thị thông báo thành công
                    loaddingDialog.dismiss(); // Ẩn dialog loading
                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage()); // Hiển thị thông báo lỗi
                    loaddingDialog.dismiss(); // Ẩn dialog loading
                });
    }

}



















