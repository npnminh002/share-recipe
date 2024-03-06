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
        recipeViewModel = new ViewModelProvider(this).get(RecipeViewModel.class);
        recipeViewModel.recipeLiveData.postValue(recipe);


        binding.nameOfRecipe.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                recipe.Name= binding.nameOfRecipe.getText().toString();
                recipeViewModel.recipeLiveData.postValue(recipe);
            }
        });

        binding.description.setOnFocusChangeListener((v, hasFocus) -> {
            if(!hasFocus){
                recipe.Name= binding.nameOfRecipe.getText().toString();
                recipeViewModel.recipeLiveData.postValue(recipe);
            }
        });
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.EDIT, new DirectionsAdapter.OnDirectionEvent() {
            @Override
            public void onNameChange(Directions directions, String value, int postion) {
                int indexOf = recipe.Directions.indexOf(directions);
                if (indexOf != -1) {
                    recipe.Directions.get(indexOf).Name = value;
                }
            }

            @Override
            public void onRemove(Directions id, int pos) {
                removeDirection(id, pos);
            }
        });
        binding.rcvDirection.setAdapter(directionsAdapter);
        adapter = new ImageRecipeAdapter(new ImageRecipeAdapter.ImageItemEvent() {
            @Override
            public void onAdd() {
                selectImages();
            }

            @Override
            public void onRemove(int pos) {
                new ConfirmDialog(EditRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
                    listUri.remove(pos);
                    recipeViewModel.listSelect.postValue(listUri);
                }).show();

            }
        });

        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.EDIT, new IngridentsAdapter.OnIngridentEvent() {
            @Override
            public void onNameChange(Ingredients ingredients, String value, int po) {
                int indexOf = recipe.Ingredients.indexOf(ingredients);
                if (indexOf != -1) {
                    recipe.Ingredients.get(indexOf).Name = value;
                }
            }

            @Override
            public void onQuantitiveChange(Ingredients ingredients, String value, int pos) {
                recipe.Ingredients.get(pos).Quantitative = Float.parseFloat(value);
            }

            @Override
            public void onRemove(Ingredients id, int pos) {
                removeIngridents(id, pos);
            }
        });
        binding.rcvIngrident.setAdapter(ingridentsAdapter);
        binding.listImg.setAdapter(adapter);

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            if (recipeViewModel.imgUri != null) {
                loadImage(recipeViewModel.imgUri, binding.imgProduct);
            } else {
                loadImage(data.ImgUrl, binding.imgProduct);
            }
            binding.nameOfRecipe.setText(data.Name == null ? "" : data.Name);
            binding.description.setText(data.Description == null ? "" : data.Description);
            binding.timePrepare.setText(data.PrepareTime.Time);
            binding.selectMinutePP.setText(data.PrepareTime.TimeType);
            binding.timeCook.setText(data.CookTime.Time);
            binding.selectMinutePP.setText(data.CookTime.TimeType);
            binding.txtLever.setText(data.Level);

            directionsAdapter.setItems(data.Directions);
            ingridentsAdapter.setItems(data.Ingredients);
        });

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

    private void removeDirection(Directions id, int pos) {
        new ConfirmDialog(EditRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            recipe.Directions.remove(pos);
            directionsAdapter.setItems(recipe.Directions);
        }).show();
    }

    private void removeIngridents(Ingredients id, int pos) {
        new ConfirmDialog(EditRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            recipe.Ingredients.remove(pos);
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
        if (requestCode == 1 && resultCode == RESULT_OK) {

            isChoosePreivewImg = true;
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
        } else if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK) {
            isChooseMainImg = true;
            if (data != null && data.getData() != null) {
                uriImg = data.getData();
                loadImage(String.valueOf(uriImg), binding.imgProduct);
                recipeViewModel.imgUri = uriImg;
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
        firestore.collection(Constant.RECIPE)
                .document(value.Id)
                .set(recipe).addOnSuccessListener(task -> {
                    showToast("Add data success full");
                    loaddingDialog.dismiss();

                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                    loaddingDialog.dismiss();
                });
    }
}



















