package doan.npnm.sharerecipe.fragment.admin;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import doan.npnm.sharerecipe.adapter.admin.RecipeTableLayout;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.databinding.FragmentAdminHomeBinding;
import doan.npnm.sharerecipe.dialog.BottomManagerRecipe;
import doan.npnm.sharerecipe.dialog.ConfirmDialog;
import doan.npnm.sharerecipe.dialog.ReportDialog;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
@RequiresApi(api = Build.VERSION_CODES.N)
public class HomeAdminFragment extends BaseFragment<FragmentAdminHomeBinding> {
    public AdminViewModel viewModel;

    // Constructor để nhận ViewModel từ activity.
    public HomeAdminFragment(AdminViewModel userViewModel) {
        this.viewModel = userViewModel;
    }

    // Phương thức getBinding để khởi tạo và trả về đối tượng Binding của fragment.
    @Override
    protected FragmentAdminHomeBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAdminHomeBinding.inflate(getLayoutInflater());
    }

    ArrayList<String> listMenuItem = new ArrayList<>();

    // Phương thức khởi tạo giao diện và hiển thị dữ liệu.
    @Override
    protected void initView() {
        // Lắng nghe LiveData để cập nhật số lượng công thức, người dùng, danh mục, và báo cáo.
        viewModel.recipeLiveData.observe(this, data -> {
            binding.txtCountRecipe.setText("" + data.size());
            // Tạo adapter cho bảng hiển thị danh sách công thức.
            new RecipeTableLayout(binding.tableLayout, requireContext(), new RecipeTableLayout.OnEventSelect() {
                // Xử lý sự kiện khi người dùng xem chi tiết một công thức.
                @Override
                public void onView(Recipe recipe) {
                    // Hiển thị fragment chi tiết công thức.
                    replaceFullViewFragment(new DetailAdminRecipeFragment(recipe, viewModel), android.R.id.content, true);
                }

                // Xử lý sự kiện khi người dùng quản lý một công thức.
                @Override
                public void onManager(Recipe recipe) {
                    // Hiển thị bottom sheet quản lý công thức.
                    new BottomManagerRecipe(recipe, new BottomManagerRecipe.OnBottomSheetEvent() {
                        // Xử lý sự kiện chi tiết công thức.
                        @Override
                        public void onDetail(Recipe recipe) {
                            replaceFullViewFragment(new DetailAdminRecipeFragment(recipe, viewModel), android.R.id.content, true);
                        }

                        // Xử lý sự kiện hiển thị thông tin người dùng.
                        @Override
                        public void onAuth(String authID) {
                            viewModel.getDataFromUserId(authID, new FetchByID<Users>() {
                                @Override
                                public void onSuccess(Users data) {
                                    replaceFullViewFragment(new DetailAuthAdminFragment(viewModel, data), android.R.id.content, true);
                                }

                                @Override
                                public void onErr(Object err) {
                                    showToast(err.toString());
                                }
                            });
                        }

                        // Xử lý sự kiện phê duyệt công thức.
                        @Override
                        public void onApprove(Recipe recipe) {
                            new ConfirmDialog(requireContext(), "Do you want approve this recipe?", () -> {
                                firestore.collection(Constant.RECIPE)
                                        .document(recipe.Id)
                                        .update("RecipeStatus", "PREVIEW")
                                        .addOnSuccessListener(unused -> {
                                            showToast("Success");
                                        });
                            }).show();
                        }

                        // Xử lý sự kiện xóa công thức.
                        @Override
                        public void onDelete(Recipe rcp) {
                            new ReportDialog(requireContext(), () -> {
                                firestore.collection(Constant.RECIPE)
                                        .document(recipe.Id)
                                        .delete()
                                        .addOnCompleteListener(task -> {
                                            showToast("Delete successfully");
                                        }).addOnFailureListener(e -> {
                                            showToast("Error: " + e.getMessage());
                                        });
                            }).show();
                        }

                        // Xử lý sự kiện phân loại công thức.
                        @Override
                        public void onClassify(Recipe recipe) {
                            replaceFullViewFragment(new ClassifyCategoryFragment(recipe, viewModel), android.R.id.content, true);
                        }

                        // Xử lý sự kiện khóa công thức.
                        @Override
                        public void onLocked(Recipe recipe) {
                            new ConfirmDialog(requireContext(), "Do you want lock this recipe", () -> {
                                firestore.collection(Constant.RECIPE)
                                        .document(recipe.Id)
                                        .update("RecipeStatus", "LOCKED")
                                        .addOnSuccessListener(unused -> {
                                            showToast("Success");
                                        });
                            }).show();
                        }
                    }, requireActivity()).show();
                }
            }).onFinih(() -> {
                // Ẩn tiến trình tải dữ liệu khi hoàn thành.
                binding.progressLoad.setVisibility(View.GONE);
            }).setData(data);
        });

        // Lắng nghe LiveData để cập nhật số lượng công thức được phê duyệt.
        viewModel.recipeApproveLiveData.observe(this, data -> {
            binding.txtAppove.setText("" + data.size());
        });

        // Lắng nghe LiveData để cập nhật số lượng người dùng.
        viewModel.usersLiveData.observe(this, data -> {
            binding.txtUser.setText("" + data.size());
        });

        // Lắng nghe LiveData để cập nhật số lượng báo cáo công thức.
        viewModel.recipeReportLiveData.observe(this, data -> {
            binding.txtReport.setText("" + data.size());
        });

        // Lắng nghe LiveData để cập nhật số lượng danh mục.
        viewModel.categoryMutableLiveData.observe(this, data -> {
            binding.txtCategory.setText("" + data.size());
        });
    }

    // Phương thức xử lý sự kiện click.
    @Override
    public void OnClick() {
        // Không cần xử lý sự kiện click trong fragment này.
    }
}
