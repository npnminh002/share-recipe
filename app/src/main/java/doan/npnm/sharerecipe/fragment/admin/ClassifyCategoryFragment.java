package doan.npnm.sharerecipe.fragment.admin;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import doan.npnm.sharerecipe.adapter.admin.ClassifyAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentClassifyCateogryBinding;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.interfaces.OnGetEvent;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeStatus;
import doan.npnm.sharerecipe.utility.Constant;
public class ClassifyCategoryFragment extends BaseFragment<FragmentClassifyCateogryBinding> {

    private Recipe recipe;
    private AdminViewModel viewModel;

    private ClassifyAdapter classifyAdapter;
    private ClassifyAdapter categoryAdapter;

    private ArrayList<Category> categories = new ArrayList<>();
    private ArrayList<Category> classifies = new ArrayList<>();

    // Constructor nhận một recipe và viewModel của Admin
    public ClassifyCategoryFragment(Recipe recipe, AdminViewModel viewModel) {
        this.recipe = recipe;
        this.viewModel = viewModel;
    }

    @Override
    protected FragmentClassifyCateogryBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentClassifyCateogryBinding.inflate(getLayoutInflater());
    }

    // Phương thức để lấy danh sách category từ Firestore
    public void ongetCategory(OnGetEvent<Category> categoryOnGetEvent) {
        ArrayList<Category> categories = new ArrayList<>();
        firestore.collection(Constant.CATEGORY).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot doc : task.getResult()) {
                    categories.add(doc.toObject(Category.class));
                }
                categoryOnGetEvent.onSuccess(categories);
            } else {
                showToast("Error: " + task.getException().getMessage());
            }
        });
    }

    @Override
    protected void initView() {
        // Khởi tạo adapter cho RecyclerView hiển thị danh mục và nhóm
        classifyAdapter = new ClassifyAdapter(ClassifyAdapter.ClassifyManager.REMOVE, new ClassifyAdapter.OnEventCategory() {
            @Override
            public void onAdd(Category ct) {
                // Phương thức này được gọi khi một danh mục được thêm vào nhóm
            }

            @Override
            public void onRemove(Category ct) {
                // Phương thức này được gọi khi một danh mục được xóa khỏi nhóm
                categories.add(ct);
                classifies.remove(ct);
                classifyAdapter.removeItem(ct);
                categoryAdapter.addItem(ct, 0);
            }
        });

        categoryAdapter = new ClassifyAdapter(ClassifyAdapter.ClassifyManager.ADD, new ClassifyAdapter.OnEventCategory() {
            @Override
            public void onAdd(Category ct) {
                // Phương thức này được gọi khi một danh mục được thêm vào nhóm
                classifies.add(ct);
                categories.remove(ct);
                categoryAdapter.removeItem(ct);
                classifyAdapter.addItem(ct, 0);
            }

            @Override
            public void onRemove(Category ct) {
                // Phương thức này được gọi khi một danh mục được xóa khỏi nhóm
            }
        });

        // Thiết lập adapter cho RecyclerView hiển thị danh mục và nhóm
        binding.rcvCategory.setAdapter(categoryAdapter);
        binding.rcvClassify.setAdapter(classifyAdapter);

        // Gọi phương thức để lấy danh sách category từ Firestore và cập nhật RecyclerView sau khi lấy dữ liệu thành công
        ongetCategory(data -> {
            if (data != null) {
                categories = data;
                refreshItem();
            } else {
                showToast("Failed to retrieve categories.");
            }
        });
    }

    // Phương thức để cập nhật RecyclerView hiển thị danh mục và nhóm
    private void refreshItem() {
        categoryAdapter.setItems(categories);
        classifyAdapter.setItems(classifies);
    }

    @Override
    public void OnClick() {
        // Xử lý sự kiện click trên nút lưu
        binding.btnSave.setOnClickListener(v -> {
            // Hiển thị dialog xác nhận trước khi lưu thay đổi
            new ConfirmDialog(requireContext(), "Do you want to save changes?", () -> {
                // Tạo một danh sách categoryId để lưu danh mục đã được phân loại
                ArrayList<String> categoryId = new ArrayList<>();
                for (Category ct : classifies) {
                    categoryId.add(ct.Id);
                }
                // Tạo một hashMap để lưu dữ liệu cập nhật vào Firestore
                HashMap<String, Object> data = new HashMap<>();
                data.put("Category", categoryId);
                data.put("RecipeStatus", RecipeStatus.PREVIEW);
                // Hiển thị dialog loading
                loaddingDialog.show();
                // Cập nhật dữ liệu vào Firestore
                firestore.collection(Constant.RECIPE).document(recipe.Id).update(data).addOnSuccessListener(unused -> {
                    showToast("Success");
                    // Cập nhật lại dữ liệu cho viewModel và đóng fragment sau khi lưu thành công
                    viewModel.initRecipeData();
                    new Handler(Looper.myLooper()).postDelayed(() -> {
                        closeFragment(ClassifyCategoryFragment.this);
                        loaddingDialog.dismiss();
                    }, 2000);
                }).addOnFailureListener(e -> {
                    showToast("Error: " + e.getMessage());
                    loaddingDialog.dismiss();
                });
            }).show();
        });
        // Xử lý sự kiện click trên nút quay lại
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(ClassifyCategoryFragment.this);
        });
    }
}
