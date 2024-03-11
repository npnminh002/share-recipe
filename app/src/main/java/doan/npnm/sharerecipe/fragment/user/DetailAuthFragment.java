package doan.npnm.sharerecipe.fragment.user;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.adapter.users.RecipeAdapter;
import doan.npnm.sharerecipe.app.UserViewModel;
import doan.npnm.sharerecipe.base.BaseFragment;
import doan.npnm.sharerecipe.database.models.RecentView;
import doan.npnm.sharerecipe.databinding.FragmentDetailAuthBinding;
import doan.npnm.sharerecipe.dialog.NoUserDialog;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class DetailAuthFragment extends BaseFragment<FragmentDetailAuthBinding> {

    // khoi tao va lay du lieu nguoi dung tu man truoc de su dung
    private UserViewModel viewModel;
    private Users users;


    public DetailAuthFragment(UserViewModel viewModel, Users idAuth) {
        this.viewModel = viewModel;
        this.users = idAuth;
    }

    @Override
    protected FragmentDetailAuthBinding getBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDetailAuthBinding.inflate(getLayoutInflater());
    }


    RecipeAdapter recipeAdapter;

    @Override
    protected void initView() {

        // Khởi tạo bộ chuyển đổi RecipeAdapter với một giao diện xử lý sự kiện trên công thức (OnRecipeEvent)
        recipeAdapter = new RecipeAdapter(new RecipeAdapter.OnRecipeEvent() {
            @Override
            public void onView(Recipe rcp) { // Xử lý sự kiện xem công thức
                // Kiểm tra xem công thức đã được xem gần đây chưa
                if (viewModel.database.recentViewDao().checkExistence(rcp.Id)) {
                    // Nếu đã xem, xóa khỏi danh sách xem gần đây
                    viewModel.database.recentViewDao().removeRecent(rcp.Id);
                }
                // Thêm công thức vào danh sách xem gần đây
                viewModel.database.recentViewDao().addRecentView(new RecentView() {{
                    AuthID = rcp.RecipeAuth;
                    RecipeID = rcp.Id;
                    ViewTime = getTimeNow();
                    Recipe = rcp.toJson();
                }});

                // Chuyển đến Fragment chi tiết công thức
                addFragment(new DetailRecipeFragment(rcp, viewModel), android.R.id.content, true);
            }

            @Override
            public void onLove(Recipe rcp, int pos, boolean isLove) { // Xử lý sự kiện yêu thích công thức
                if (viewModel.auth.getCurrentUser() == null) {
                    showToast(getString(R.string.no_us)); // Hiển thị thông báo chưa đăng nhập
                } else {
                    showToast(isLove ? "Đã yêu thích" : "Đã bỏ yêu thích"); // Hiển thị thông báo yêu thích/bỏ yêu thích
                    if (!isLove) { // Yêu thích công thức
                        viewModel.onLoveRecipe(rcp); // Gọi hàm yêu thích công thức trong ViewModel
                        recipeAdapter.notifyItemChanged(pos); // Cập nhật trạng thái yêu thích trên item
                    } else { // Bỏ yêu thích công thức
                        viewModel.onUnlove(rcp); // Gọi hàm bỏ yêu thích công thức trong ViewModel
                        recipeAdapter.notifyItemChanged(pos); // Cập nhật trạng thái yêu thích trên item
                    }
                }
            }
        }, viewModel.database);

// Thiết lập bộ chuyển đổi recipeAdapter cho RecyclerView
        binding.rcvRecipe.setAdapter(recipeAdapter);


        if (!Objects.equals(users.UrlImg, "")) { // Kiểm tra nếu URL ảnh người dùng không rỗng
            loadImage(users.UrlImg, binding.ImgUser); // Hiển thị ảnh người dùng
        }
        binding.txtEmail.setText(users.Email); // Hiển thị email người dùng
        binding.userName.setText(users.UserName); // Hiển thị tên người dùng
        binding.txtRecipe.setText("" + users.Recipe); // Hiển thị số lượng công thức của người dùng (có thể cần định dạng)
        binding.txtFollow.setText("" + users.Follow); // Hiển thị số lượng người đang theo dõi
        binding.txtFollower.setText("" + users.Follower); // Hiển thị số lượng người được theo dõi
        if (viewModel.auth.getCurrentUser() != null) { // Kiểm tra nếu người dùng đã đăng nhập
            viewModel.checkFollowByUid(users.UserID); // Kiểm tra xem đã theo dõi người dùng này chưa
            viewModel.isFollow.observe(this, isFollow -> { // Theo dõi LiveData của trạng thái theo dõi
                binding.btnFollow.setText(getString(isFollow ? R.string.un_follow : R.string.follow)); // Hiển thị nút Theo dõi/Hủy theo dõi
            });
        }

        onFetchRecipeyUs(new FetchByID<ArrayList<Recipe>>() { // Gọi hàm lấy danh sách công thức của người dùng
            @Override
            public void onSuccess(ArrayList<Recipe> data) {
                recipeAdapter.setItems(data); // Cập nhật danh sách công thức cho bộ chuyển đổi
            }

            @Override
            public void onErr(Object err) {
                showToast(err.toString()); // Hiển thị thông báo lỗi
            }
        });

    }

    // Hàm lấy danh sách công thức của người dùng
    private void onFetchRecipeyUs(FetchByID<ArrayList<Recipe>> arrayListFetchByID) {
        // Hiển thị progress bar để báo hiệu đang tải dữ liệu
        binding.prLoadData.setVisibility(View.VISIBLE);

        // Khởi tạo danh sách để chứa các công thức
        ArrayList<Recipe> recipes = new ArrayList<>();

        // Lấy dữ liệu từ bộ sưu tập "RECIPE" trên Firestore
        viewModel.firestore.collection(Constant.RECIPE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    // Duyệt qua các công thức trong bộ sưu tập
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        // Chuyển đổi dữ liệu thành đối tượng Recipe
                        Recipe recipe = doc.toObject(Recipe.class);

                        // Kiểm tra xem công thức này có trùng với UserID hay không
                        if (Objects.equals(recipe.RecipeAuth, users.UserID)) {
                            // Nếu trùng, thêm công thức vào danh sách recipes
                            recipes.add(recipe);
                        }

                        // In thông tin log để kiểm tra
                        Log.d("TESTRCP", "onFetchRecipeyUs: " + "USID:" + users.UserID + "  RECIPE: " + recipe.RecipeAuth);
                    }

                    // Nếu danh sách công thức không rỗng
                    if (!recipes.isEmpty()) {
                        // Gọi hàm onSuccess của đối tượng arrayListFetchByID để xử lý kết quả thành công
                        arrayListFetchByID.onSuccess(recipes);
                    }

                    // Ẩn progress bar
                    binding.prLoadData.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Xử lý trường hợp lấy dữ liệu thất bại
                    arrayListFetchByID.onErr(e.getMessage()); // Gọi hàm onErr để thông báo lỗi
                    binding.prLoadData.setVisibility(View.GONE); // Ẩn progress bar
                });
    }


    @Override
    public void OnClick() {
        // Xử lý sự kiện click cho nút back
        binding.backIcon2.setOnClickListener(v -> {
            closeFragment(DetailAuthFragment.this); // Đóng fragment chi tiết người dùng
        });

// Xử lý sự kiện click cho nút theo dõi
        binding.btnFollow.setOnClickListener(v -> {
            if (viewModel.auth.getCurrentUser() == null) { // Kiểm tra nếu người dùng chưa đăng nhập
                // Hiển thị dialog thông báo cần đăng nhập
                new NoUserDialog(requireContext(), () -> {
                    viewModel.isSingApp.postValue(true); // Đánh dấu yêu cầu đăng nhập
                }).show();
            } else {
                // Kiểm tra trạng thái theo dõi hiện tại
                if (viewModel.isFollow.getValue()) { // Đang theo dõi
                    binding.txtFollower.setText("" + (users.Follower - 1)); // Giảm số người được theo dõi
                    viewModel.onUnFollow(users); // Gọi hàm hủy theo dõi trong ViewModel
                } else { // Chưa theo dõi
                    viewModel.onFollow(users); // Gọi hàm theo dõi trong ViewModel
                    binding.txtFollower.setText("" + (users.Follower + 1)); // Tăng số người được theo dõi
                }
                // Kiểm tra lại trạng thái theo dõi sau khi tương tác
                viewModel.checkFollowByUid(users.UserID);
            }
        });


    }
}
