package doan.npnm.sharerecipe.fragment.user;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Build;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Locale;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.adapter.users.SearchAdapter;
import doan.npnm.sharerecipe.adapter.users.SearchCategoryAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.database.models.Search;
import doan.npnm.sharerecipe.databinding.FragmentSearchBinding;
import doan.npnm.sharerecipe.interfaces.OnGetEvent;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
public class SeachFragment extends BaseFragment<FragmentSearchBinding>
{
    // ViewModel của người dùng
    private UserViewModel viewModel;

    // Constructor nhận ViewModel của người dùng
    public SeachFragment(UserViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    // Phương thức lấy dữ liệu Binding
    @Override
    protected FragmentSearchBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSearchBinding.inflate(inflater);
    }

    // Adapter cho danh sách các mục tìm kiếm
    SearchCategoryAdapter categoryAdapter;
    // Adapter cho danh sách kết quả tìm kiếm
    SearchAdapter searchAdapter;
    // Adapter cho danh sách công thức
    RecipeAdapter recipeAdapter;

    // Phương thức khởi tạo giao diện
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void initView() {
        // Khởi tạo adapter cho danh sách tìm kiếm
        searchAdapter = new SearchAdapter(s -> {
            // Xử lý sự kiện khi chọn mục tìm kiếm
            binding.edtSearchData.setText(s);
            searchValue(s, this::setToView);
        });
        // Thiết lập dữ liệu cho adapter từ Cơ sở dữ liệu Room
        searchAdapter.setItems((ArrayList<Search>) viewModel.database.searchDao().getListCurrent());

        // Khởi tạo adapter cho danh sách các mục tìm kiếm theo danh mục
        categoryAdapter = new SearchCategoryAdapter(category -> {
            // Xử lý sự kiện khi chọn một danh mục tìm kiếm
            searchValue(category.Id, this::setToView);
            binding.edtSearchData.setText(category.Name);
        });

        // Lắng nghe thay đổi danh sách các danh mục từ ViewModel
        viewModel.categoriesArr.observe(this, data -> {
            categoryAdapter.setItems(data);
        });

        // Lấy dữ liệu từ Bundle để tìm kiếm ngay khi Fragment được tạo
        String key = getData("Key").toString();
        if (key.length() != 0) {
            Category ct = checkIDCategory(key);
            if (ct != null) {
                binding.edtSearchData.setText(ct.Name);
            } else {
                binding.edtSearchData.setText(key);
            }
            searchValue(key, this::setToView);
        }

        // Thiết lập adapter cho RecyclerView
        binding.rcvManufact.setAdapter(categoryAdapter);
        binding.rcvCurrent.setAdapter(searchAdapter);

        // Khởi tạo và thiết lập adapter cho danh sách kết quả tìm kiếm
        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) {
                // Xử lý sự kiện khi nhấn xem công thức
                if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                    viewModel.database.recentViewDao().removeRecent(rcp.Id);
                }
                viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                    AuthID = rcp.RecipeAuth;
                    RecipeID = rcp.Id;
                    ViewTime = getTimeNow();
                    Recipe = rcp.toJson();
                }});

                addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
            }

            @Override
            public void onLove(Recipe rcp, int pos, boolean isLove) {
                // Xử lý sự kiện khi nhấn yêu thích hoặc bỏ yêu thích công thức
                if (viewModel.auth.getCurrentUser() == null) {
                    showToast(getString(R.string.no_us));
                } else {
                    showToast(isLove);
                    if (!isLove) {
                        viewModel.onLoveRecipe(rcp);
                        recipeAdapter.notifyItemChanged(pos);
                    } else {
                        viewModel.onUnlove(rcp);
                        recipeAdapter.notifyItemChanged(pos);
                    }
                }
            }
        }, viewModel.database);

        // Thiết lập adapter cho RecyclerView hiển thị kết quả tìm kiếm
        binding.rcvResultSreach.setAdapter(recipeAdapter);
    }

    // Phương thức kiểm tra danh mục theo ID
    private Category checkIDCategory(String key) {
        ArrayList<Category> categories = viewModel.categoriesArr.getValue();
        for (Category ct : categories) {
            if (ct.Id.equals(key)) {
                return ct;
            }
        }
        return null;
    }

    // Phương thức kiểm tra danh mục theo tên
    private Category checkCategory(String key) {
        ArrayList<Category> categories = viewModel.categoriesArr.getValue();
        for (Category ct : categories) {
            if (ct.Name.equals(key)) {
                return ct;
            }
        }
        return null;
    }

    // Phương thức mở chức năng nhận diện giọng nói
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(requireContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Mã yêu cầu cho kết quả nhận diện giọng nói
    private final int REQ_CODE_SPEECH_INPUT = 100;

    // Xử lý kết quả trả về từ việc nhận diện giọng nói
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    binding.edtSearchData.setText(result.get(0));
                }
                break;
            }
        }
    }

    // Xử lý sự kiện click trên giao diện
    @Override
    public void OnClick() {
        // Xử lý sự kiện tìm kiếm khi nhấn nút tìm kiếm
        binding.icSearch.setOnClickListener(v -> {
            searchValue(binding.edtSearchData.getText().toString(), this::setToView);
        });

        // Xử lý sự kiện quay lại khi nhấn nút quay lại
        binding.icBack.setOnClickListener(v -> {
            // Thiết lập lại dữ liệu tìm kiếm về mặc định
            searchAdapter.setItems((ArrayList<Search>) viewModel.database.searchDao().getListCurrent());
            binding.llResult.setVisibility(View.GONE);
        });

        // Xử lý sự kiện khi nhấn nút nhận diện giọng nói
        binding.icInputSpeech.setOnClickListener(v -> {
            promptSpeechInput();
        });
    }

    // Phương thức cập nhật kết quả tìm kiếm lên giao diện
    private void setToView(ArrayList<Recipe> data) {
        String key = binding.edtSearchData.toString();
        // Kiểm tra xem có phải là danh mục không
        Category ct = checkCategory(key);
        if (ct != null) {
            // Thêm mục tìm kiếm vào Cơ sở dữ liệu Room
            viewModel.database.searchDao().addRecentView(new Search() {{
                CurrentKey = ct.Name;
            }});
        } else {
            // Thêm mục tìm kiếm vào Cơ sở dữ liệu Room
            viewModel.database.searchDao().addRecentView(new Search() {{
                CurrentKey = binding.edtSearchData.getText().toString();
            }});
        }
        // Ẩn bàn phím ảo
        hideKeyboard();
        // Hiển thị kết quả tìm kiếm
        binding.llResult.setVisibility(View.VISIBLE);
        binding.txtResult.setText(binding.edtSearchData.getText().toString());
        if (data == null || data.size() == 0) {
            // Hiển thị thông báo khi không tìm thấy kết quả
            binding.llNoResult.setVisibility(View.VISIBLE);
            binding.rcvResultSreach.setVisibility(View.GONE);
        } else {
            // Hiển thị kết quả tìm kiếm
            binding.rcvResultSreach.setVisibility(View.VISIBLE);
            recipeAdapter.setItems(data);
            binding.llNoResult.setVisibility(View.GONE);
        }
    }

    // Phương thức tìm kiếm giá trị
    private void searchValue(String string, OnGetEvent<Recipe> event) {
        loaddingDialog.show();
        if (string.isEmpty()) {
            event.onSuccess(null);
        } else {
            ArrayList<Recipe> arrayList = new ArrayList<>();
            // Truy vấn Cơ sở dữ liệu Firestore để tìm kiếm
            viewModel.firestore.collection(Constant.RECIPE)
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                            Recipe recipe = document.toObject(Recipe.class);
                            if (recipe.Name.toLowerCase().replace(" ", "").contains(string.toLowerCase().replace(" ", "")) ||
                                    checkContaintValue(string, recipe.Category)) {
                                arrayList.add(recipe);
                            }
                        }
                        // Gửi kết quả tìm kiếm qua sự kiện
                        event.onSuccess(arrayList);
                        loaddingDialog.dismiss();
                    }).addOnFailureListener(exception -> {
                        loaddingDialog.dismiss();
                        event.onSuccess(null);
                    });
        }
    }

    // Kiểm tra giá trị có chứa trong danh sách không
    private boolean checkContaintValue(String string, ArrayList<String> category) {
        for (String s : category) {
            if (string.contains(s)) {
                return true;
            }
        }
        return false;
    }
}















