package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.DirectionsAdapter;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.model.recipe.Directions;
import doan.npnm.sharerecipe.model.recipe.Ingredients;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.databinding.FragmentFourRecipeBinding;
public class FourRecipeFragment extends BaseFragment<FragmentFourRecipeBinding> {
    // ViewModel của người dùng và ViewModel của công thức
    public UserViewModel viewModel;
    public RecipeViewModel recipeViewModel;
    // Đối tượng công thức
    private Recipe recipe;
    // Danh sách hướng dẫn mặc định
    public ArrayList<Directions> listDefDirection = new ArrayList<>();

    // Constructor nhận ViewModels
    public FourRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentFourRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFourRecipeBinding.inflate(getLayoutInflater());
    }

    // Adapter cho danh sách hướng dẫn
    private DirectionsAdapter directionsAdapter;

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Quan sát sự kiện thêm công thức mới
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(FourRecipeFragment.this);
        });
        // Khởi tạo adapter cho danh sách hướng dẫn
        directionsAdapter = new DirectionsAdapter(DirectionsAdapter.DIR_TYPE.EDIT, new DirectionsAdapter.OnDirectionEvent() {
            // Xử lý sự kiện thay đổi tên hướng dẫn
            @Override
            public void onNameChange(Directions directions) {
                for (Directions id : recipe.Directions) {
                    if (id.Id == directions.Id) {
                        id.Name = directions.Name;
                        break;
                    }
                }
            }

            // Xử lý sự kiện xóa hướng dẫn
            @Override
            public void onRemove(Directions id, int pos) {
                removeDirection(id, pos);
            }
        });
        // Thiết lập adapter cho RecyclerView hiển thị danh sách hướng dẫn
        binding.rcvDirection.setAdapter(directionsAdapter);
        // Quan sát dữ liệu công thức từ ViewModel
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;
            if (data == null || data.Directions == null || data.Directions.size() == 0) {
                // Nếu danh sách hướng dẫn rỗng, tạo danh sách mặc định
                listDefDirection = new ArrayList<>(Arrays.asList(
                        new Directions(1, getContext().getString(R.string.directions) + " 1"),
                        new Directions(2, getContext().getString(R.string.directions) + " 2"),
                        new Directions(3, getContext().getString(R.string.directions) + " 3")
                ));
                data.Directions = listDefDirection;
            } else {
                listDefDirection = data.Directions;
            }
            // Hiển thị dữ liệu lên giao diện
            setDataToView(data.Directions);
        });
    }

    // Phương thức đặt dữ liệu lên giao diện
    private void setDataToView(ArrayList<Directions> directions) {
        Collections.sort(directions, (o1, o2) -> String.valueOf(o1.Id).compareTo(String.valueOf(o2.Id)));
        directionsAdapter.setItems(directions);
    }

    // Phương thức xóa hướng dẫn
    private void removeDirection(Directions id, int pos) {
        new ConfirmDialog(FourRecipeFragment.this.getContext(), getString(R.string.cf_delete), () -> {
            listDefDirection.remove(pos);
            setDataToView(listDefDirection);
        }).show();
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(FourRecipeFragment.this);
            postValue(false);
        });
        // Xử lý sự kiện khi nhấn nút tiếp theo
        binding.btnNext.setOnClickListener(v -> {
            replaceFragment(new FiveRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
            postValue(true);
        });
        // Xử lý sự kiện khi nhấn nút thêm hướng dẫn
        binding.icAddIngrident.setOnClickListener(v -> {
            int index = listDefDirection.size() + 1;
            listDefDirection.add(0, new Directions(index, getString(R.string.ingredients) + " " + index));
            setDataToView(listDefDirection);
        });
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(FourRecipeFragment.this);
            postValue(true);
        });
    }

    // Phương thức gửi dữ liệu công thức
    private void postValue(boolean b) {
        recipe.Directions = b ? listDefDirection : new ArrayList<>();
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();
    }
}
