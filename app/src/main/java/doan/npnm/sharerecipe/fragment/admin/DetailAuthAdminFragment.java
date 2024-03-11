package doan.npnm.sharerecipe.fragment.admin;

import android.content.Intent;
import android.os.Message;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.net.PasswordAuthentication;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.app.AdminViewModel;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthAdminBinding;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthBinding;
import doan.npnm.sharerecipe.dialog.NoUserDialog;
import doan.npnm.sharerecipe.fragment.user.DetailRecipeFragment;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;
public class DetailAuthAdminFragment extends BaseFragment<FragmentDetailAuthAdminBinding> {

    private AdminViewModel viewModel;
    private Users users;

    // Constructor nhận ViewModel và đối tượng Users để hiển thị chi tiết thông tin người dùng.
    public DetailAuthAdminFragment(AdminViewModel viewModel, Users idAuth) {
        this.viewModel = viewModel;
        this.users = idAuth;
    }

    // Phương thức getBinding để khởi tạo và trả về đối tượng Binding của fragment.
    @Override
    protected FragmentDetailAuthAdminBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailAuthAdminBinding.inflate(getLayoutInflater());
    }

    RecipeAdapter recipeAdapter;

    // Phương thức khởi tạo giao diện và hiển thị dữ liệu của người dùng và danh sách công thức nấu ăn.
    @Override
    protected void initView() {
        // Khởi tạo adapter cho danh sách công thức nấu ăn.
        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            // Xử lý sự kiện khi người dùng xem một công thức nấu ăn.
            @Override
            public void onView(Recipe rcp) {
                // Kiểm tra xem công thức đã được xem trước đó hay chưa, nếu có thì xóa khỏi danh sách xem gần đây.
                if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                    viewModel.database.recentViewDao().removeRecent(rcp.Id);
                }
                // Thêm công thức nấu ăn vào danh sách xem gần đây.
                viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                    AuthID = rcp.RecipeAuth;
                    RecipeID = rcp.Id;
                    ViewTime = getTimeNow();
                    Recipe = rcp.toJson();
                }});

                // Mở fragment chi tiết công thức nấu ăn.
                addFragment(new DetailAdminRecipeFragment(rcp, viewModel), android.R.id.content, true);
            }

            // Xử lý sự kiện khi người dùng yêu thích hoặc bỏ yêu thích một công thức nấu ăn.
            @Override
            public void onLove(Recipe rcp, int pos, boolean isLove) {
                // TODO: Xử lý logic khi người dùng thực hiện thao tác yêu thích công thức nấu ăn.
            }
        }, viewModel.database);

        // Thiết lập adapter cho RecyclerView hiển thị danh sách công thức nấu ăn.
        binding.rcvRecipe.setAdapter(recipeAdapter);

        // Hiển thị thông tin người dùng.
        if (!Objects.equals(users.UrlImg, "")) {
            loadImage(users.UrlImg, binding.ImgUser);
        }
        binding.userId.setText("Id: " + users.UserID);
        binding.txtEmail.setText("Email: " + users.Email);
        binding.txtNickname.setText("#" + users.NickName);
        binding.userName.setText("Name: " + users.UserName);
        binding.txtRecipe.setText("" + users.Recipe);
        binding.txtFollow.setText("" + users.Follow);
        binding.txtFollower.setText("" + users.Follower);
        binding.txtimeLastLog.setText("" + formatDateString(users.TimeLog));
        binding.txtAddress.setText("Address: " + users.Address);
        binding.txtGender.setText("Gender: " + users.Gender);

        // Hiển thị lịch sử hoạt động của người dùng.
        String hts = "";
        for (String s : users.History) {
            hts += s + "\n";
        }
        binding.txtHistory.setText(hts);

        // Lấy danh sách công thức nấu ăn của người dùng.
        onFetchRecipeyUs(new FetchByID<ArrayList<Recipe>>() {
            @Override
            public void onSuccess(ArrayList<Recipe> data) {
                // Hiển thị danh sách công thức nấu ăn lên giao diện.
                recipeAdapter.setItems(data);
            }

            @Override
            public void onErr(Object err) {
                showToast(err);
            }
        });
    }

    // Phương thức lấy danh sách công thức nấu ăn của người dùng từ Firestore.
    private void onFetchRecipeyUs(FetchByID<ArrayList<Recipe>> arrayListFetchByID) {
        // Hiển thị tiến trình đang tải dữ liệu.
        binding.prLoadData.setVisibility(View.VISIBLE);
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Truy vấn danh sách công thức nấu ăn từ Firestore.
        viewModel.firestore.collection(Constant.RECIPE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Recipe recipe = doc.toObject(Recipe.class);
                        // Kiểm tra xem công thức nấu ăn thuộc về người dùng hiện tại hay không.
                        if (Objects.equals(recipe.RecipeAuth, users.UserID)) {
                            recipes.add(recipe);
                        }
                        Log.d("TESTRCP", "onFetchRecipeyUs: " + "USID:" + users.UserID + "  RECIPE: " + recipe.RecipeAuth);
                    }
                    // Nếu có công thức nấu ăn, gọi callback onSuccess để hiển thị lên giao diện.
                    if (!recipes.isEmpty()) {
                        arrayListFetchByID.onSuccess(recipes);
                    }
                    // Ẩn tiến trình đang tải dữ liệu khi hoàn thành.
                    binding.prLoadData.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Gọi callback onErr nếu có lỗi xảy ra.
                    arrayListFetchByID.onErr(e.getMessage());
                    // Ẩn tiến trình đang tải dữ liệu khi xảy ra lỗi.
                    binding.prLoadData.setVisibility(View.GONE);
                });
    }

    // Phương thức xử lý sự kiện người dùng click trên các nút.
    @Override
    public void OnClick() {
        // Xử lý sự kiện click trên nút quay lại.
        binding.backIcon.setOnClickListener(v -> {
            closeFragment(DetailAuthAdminFragment.this);
        });

        // Xử lý sự kiện click trên nút xóa người dùng.
        binding.btnDelete.setOnClickListener(v -> {
            // TODO: Xử lý logic khi người dùng muốn xóa người dùng.
        });
    }
}
