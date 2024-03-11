package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.IngridentsAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentThirdRecipeBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;
public class ThirdRecipeFragment extends BaseFragment<FragmentThirdRecipeBinding> {
    public UserViewModel viewModel;
    public RecipeViewModel recipeViewModel;
    private Recipe recipe;
    public ArrayList<Ingredients> listDefautIngrident = new ArrayList<>();

    // Constructor nhận ViewModels
    public ThirdRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentThirdRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentThirdRecipeBinding.inflate(getLayoutInflater());
    }

    // Biến Adapter cho danh sách nguyên liệu
    private IngridentsAdapter ingridentsAdapter;

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Khởi tạo danh sách mặc định của nguyên liệu
        listDefautIngrident = new ArrayList<>(Arrays.asList(
                new Ingredients(1, getString(R.string.ingredients) + " 1", 0),
                new Ingredients(2, getString(R.string.ingredients) + " 2", 0),
                new Ingredients(3, getString(R.string.ingredients) + " 3", 0)
        ));

        // Quan sát sự kiện thêm công thức mới
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(ThirdRecipeFragment.this);
        });

        // Khởi tạo Adapter cho danh sách nguyên liệu
        ingridentsAdapter = new IngridentsAdapter(IngridentsAdapter.IGR_TYPE.EDIT, new IngridentsAdapter.OnIngridentEvent() {
            @Override
            public void onNameChange(Ingredients ingredients) {
                for (Ingredients id : recipe.Ingredients) {
                    if (id.Id == ingredients.Id) {
                        id.Name = ingredients.Name;
                        break;
                    }
                }
            }

            @Override
            public void onQuantitiveChange(Ingredients ingredients) {
                for (Ingredients id : recipe.Ingredients) {
                    if (id.Id == ingredients.Id) {
                        id.Quantitative = ingredients.Quantitative;
                        break;
                    }
                }
            }

            @Override
            public void onRemove(Ingredients id, int pos) {
                removeIngridents(id, pos);
            }
        });

        // Gán Adapter cho RecyclerView
        binding.rcvIngrident.setAdapter(ingridentsAdapter);

        // Quan sát dữ liệu công thức từ ViewModel
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            // Nếu dữ liệu công thức rỗng hoặc không có nguyên liệu thì sử dụng danh sách mặc định
            if (data == null || data.Ingredients == null || data.Ingredients.isEmpty()) {
                recipe.Ingredients = listDefautIngrident;
            } else {
                listDefautIngrident = data.Ingredients;
            }
            // Hiển thị dữ liệu lên giao diện
            setToView(recipe.Ingredients);
        });
    }

    // Xử lý sự kiện xóa nguyên liệu
    private void removeIngridents(Ingredients id, int pos) {
        new ConfirmDialog(ThirdRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            recipe.Ingredients.remove(pos);
            setToView(recipe.Ingredients);
        }).show();
    }

    // Đặt dữ liệu vào giao diện
    private void setToView(ArrayList<Ingredients> ingredients) {
        Collections.sort(ingredients, (o1, o2) -> Integer.compare(o1.Id, o2.Id));
        ingridentsAdapter.setItems(ingredients);
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(ThirdRecipeFragment.this);
            recipe.Ingredients = new ArrayList<>();
            recipeViewModel.recipeLiveData.postValue(recipe);
            hideKeyboard();
        });

        // Xử lý sự kiện khi nhấn nút tiếp theo
        binding.btnNext.setOnClickListener(v -> {
            replaceFragment(new FourRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
            postValue();
        });

        // Xử lý sự kiện khi nhấn nút thêm nguyên liệu mới
        binding.icAddIngrident.setOnClickListener(v -> {
            addNewIngridents();
        });

        // Xử lý sự kiện khi nhấn nút quay lại
        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(ThirdRecipeFragment.this);
            postValue();
        });
    }

    // Thêm nguyên liệu mới
    private void addNewIngridents() {
        int index = recipe.Ingredients.size() + 1;
        Ingredients newIngredient = new Ingredients(index, getString(R.string.ingredients) + " " + index, 0f);
        recipe.Ingredients.add(newIngredient);
        recipeViewModel.recipeLiveData.postValue(recipe);
    }

    // Gửi dữ liệu công thức tới ViewModel
    private void postValue() {
        recipe.Ingredients = listDefautIngrident;
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();
    }
}
