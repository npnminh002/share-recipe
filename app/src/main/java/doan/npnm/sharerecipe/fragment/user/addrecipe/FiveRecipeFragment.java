package doan.npnm.sharerecipe.fragment.user.addrecipe;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.ImageRecipeAdapter;
import doan.npnm.sharerecipe.app.AppViewModel;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentFiveRecipeBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class FiveRecipeFragment extends BaseFragment<FragmentFiveRecipeBinding> {
    public AppViewModel viewModel;
    private RecipeViewModel recipeViewModel;

    public FiveRecipeFragment(AppViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
        this.users = viewModel.users.getValue();

    }

    private Users users;


    @Override
    protected FragmentFiveRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFiveRecipeBinding.inflate(getLayoutInflater());
    }

    public Recipe recipe;
    private ImageRecipeAdapter adapter;

    @Override
    protected void initView() {

        adapter = new ImageRecipeAdapter(new ImageRecipeAdapter.ImageItemEvent() {
            @Override
            public void onAdd() {
                selectImages();
            }

            @Override
            public void onRemove(int pos) {
                new ConfirmDialog(FiveRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
                    listUri.remove(pos);
                    recipeViewModel.listSelect.postValue(listUri);
                }).show();

            }
        });

        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            recipe.RecipeAuth = users.Id;
        });
        recipeViewModel.listSelect.observe(this, data -> {
            if (data == null) {
                data = new ArrayList<>();
                data.add(null);
            }
            adapter.setItems(data);
        });

        binding.listImg.setAdapter(adapter);
    }
    ArrayList<Uri> listUri =new ArrayList<>();

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
            int indexNull= listUri.indexOf(null);
            if(indexNull>=0){
                listUri.remove(indexNull);
            }

            listUri.add(0,null);
            recipeViewModel.listSelect.postValue(listUri);
            adapter.setItems(listUri);
        }
    }


    @Override
    public void OnClick() {
        binding.backIcon.setOnClickListener(v -> closeFragment(FiveRecipeFragment.this));
        binding.btnNext.setOnClickListener(v -> putDataToFireStore());
        binding.btnPrev.setOnClickListener(v -> closeFragment(FiveRecipeFragment.this));
    }

    private void putDataToFireStore() {
        replaceFragment(new PreviewRecipeFragment(viewModel,recipeViewModel),android.R.id.content,true);
    }


}
