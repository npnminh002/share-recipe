package doan.npnm.sharerecipe.fragment.user.addrecipe;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.RecipeViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentSecondRecipeBinding;
import doan.npnm.sharerecipe.model.recipe.CookTime;
import doan.npnm.sharerecipe.model.recipe.PrepareTime;
import doan.npnm.sharerecipe.model.recipe.Recipe;

public class SecondRecipeFragment extends BaseFragment<FragmentSecondRecipeBinding> {
    public UserViewModel viewModel;
    public RecipeViewModel recipeViewModel;
    private Recipe recipe;

    // Constructor nhận ViewModels
    public SecondRecipeFragment(UserViewModel viewModel, RecipeViewModel recipeViewModel) {
        this.viewModel = viewModel;
        this.recipeViewModel = recipeViewModel;
    }

    // Biến thời gian nấu nướng và chuẩn bị
    private CookTime cookTime;
    public PrepareTime prepareTime;

    // Danh sách loại thời gian và mức độ khó
    public ArrayList<String> listTimeType;
    public ArrayList<String> listLever;

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentSecondRecipeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSecondRecipeBinding.inflate(getLayoutInflater());
    }

    // Khởi tạo giao diện
    @Override
    protected void initView() {
        // Quan sát sự kiện thêm công thức mới
        viewModel.isAddRecipe.observe(this, val -> {
            if (val) closeFragment(SecondRecipeFragment.this);
        });

        // Khởi tạo danh sách loại thời gian và mức độ khó
        listLever = new ArrayList<>(Arrays.asList(getString(R.string.easy), getString(R.string.normal), getString(R.string.difficul)));
        listTimeType = new ArrayList<>(Arrays.asList("s", "m", "h"));

        // Quan sát dữ liệu công thức từ ViewModel
        recipeViewModel.recipeLiveData.observe(this, data -> {
            this.recipe = data;

            // Xử lý dữ liệu chuẩn bị
            prepareTime = (data.PrepareTime == null) ? new PrepareTime() : data.PrepareTime;
            // Xử lý dữ liệu nấu nướng
            cookTime = (data.CookTime == null) ? new CookTime() : data.CookTime;

            // Hiển thị dữ liệu chuẩn bị
            if (prepareTime.Time == null || prepareTime.Time.equals("0") || prepareTime.Time.equals("")) {
                binding.timePrepare.setHint("0");
            } else {
                binding.timePrepare.setText(prepareTime.Time);
            }

            // Hiển thị dữ liệu nấu nướng
            if (cookTime.Time == null || cookTime.Time.equals("0") || prepareTime.Time.equals("")) {
                binding.timeCook.setHint("0");
            } else {
                binding.timeCook.setText(cookTime.Time);
            }

            // Hiển thị loại thời gian cho chuẩn bị và nấu nướng
            binding.selectMinutePP.setText(prepareTime.TimeType == null ? "s" : prepareTime.TimeType);
            binding.selectMinuteCook.setText(cookTime.TimeType == null ? "s" : cookTime.TimeType);

            // Hiển thị mức độ khó
            binding.txtLever.setText(data.Level == null ? "" : data.Level);
        });
    }

    // Xử lý sự kiện click
    @Override
    public void OnClick() {
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(SecondRecipeFragment.this);
            resetRecipeData();
            hideKeyboard();
        });

        // Xử lý sự kiện khi nhấn nút tiếp theo
        binding.btnNext.setOnClickListener(v -> {
            postValue();
            if (checkData()) {
                addFragment(new ThirdRecipeFragment(viewModel, recipeViewModel), android.R.id.content, true);
            }
        });

        // Xử lý sự kiện thay đổi thời gian chuẩn bị
        binding.changePPtime.setOnClickListener(v -> changePpTime());
        // Xử lý sự kiện thay đổi thời gian nấu nướng
        binding.changeCookTime.setOnClickListener(v -> changeCookTime());
        // Xử lý sự kiện thay đổi mức độ khó
        binding.changeLevel.setOnClickListener(v -> changeLevel());
        // Xử lý sự kiện khi nhấn nút quay lại
        binding.btnPrev.setOnClickListener(v -> {
            closeFragment(SecondRecipeFragment.this);
            postValue();
        });
    }

    // Kiểm tra dữ liệu nhập vào
    private boolean checkData() {
        if (recipe.CookTime.Time.equals("0") || recipe.PrepareTime.Time.equals("0")) {
            showToast(getString(R.string.time_dif_0));
            return false;
        }
        return true;
    }

    // Gửi dữ liệu công thức tới ViewModel
    private void postValue() {
        recipe.Level = binding.txtLever.getText().toString();
        prepareTime = new PrepareTime(binding.timePrepare.getText().toString(), binding.selectMinutePP.getText().toString());
        cookTime = new CookTime(binding.timeCook.getText().toString(), binding.selectMinuteCook.getText().toString());
        recipe.PrepareTime = prepareTime;
        recipe.CookTime = cookTime;
        recipeViewModel.recipeLiveData.postValue(recipe);
        hideKeyboard();
    }

    // Thay đổi loại thời gian chuẩn bị
    private void changePpTime() {
        indexPpTime++;
        binding.selectMinutePP.setText(listTimeType.get(indexPpTime));
        if (indexPpTime >= listTimeType.size() - 1) {
            indexPpTime = -1;
        }
    }

    private int indexCookTime = 0;
    private int indexPpTime = 0;

    // Thay đổi loại thời gian nấu nướng
    private void changeCookTime() {
        indexCookTime++;
        binding.selectMinuteCook.setText(listTimeType.get(indexCookTime));
        if (indexCookTime >= listTimeType.size() - 1) {
            indexCookTime = -1;
        }
    }

    private int indexLevel = 0;

    // Thay đổi mức độ khó
    private void changeLevel() {
        indexLevel++;
        binding.txtLever.setText(listLever.get(indexLevel));
        if (indexLevel >= listLever.size() - 1) {
            indexLevel = -1;
        }
    }

    // Đặt lại dữ liệu công thức
    private void resetRecipeData() {
        recipe.Level = "";
        prepareTime = new PrepareTime();
        cookTime = new CookTime();
        recipe.PrepareTime = prepareTime;
        recipe.CookTime = cookTime;
        recipeViewModel.recipeLiveData.postValue(recipe);
    }
}
