package doan.npnm.sharerecipe.app;

import android.content.ContentResolver;
import android.net.Uri;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.database.AppDatabase;
import doan.npnm.sharerecipe.database.AppDatabaseProvider;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeStatus;
import doan.npnm.sharerecipe.utility.Constant;

public class AdminViewModel extends ViewModel {
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageReference = storage.getReference();
    public AppDatabase database = AppDatabaseProvider.getDatabase();


    public MutableLiveData<ArrayList<Recipe>> recipeLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Recipe>> recipeApproveLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Recipe>> recipeReportLiveData = new MutableLiveData<>();
    public MutableLiveData<ArrayList<Users>> usersLiveData = new MutableLiveData<>();
    public MutableLiveData<Users> userLogin = new MutableLiveData<>();

    public MutableLiveData<ArrayList<Category>> categoryMutableLiveData = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<Boolean> updateValue= new MutableLiveData<>(false);

    public AdminViewModel() {
        initRecipeData();
        initGetAuth();
        ongetCategory();
    }

    public void putImgToStorage(StorageReference storageReference, Uri uri, UserViewModel.OnPutImageListener onPutImage) {
        StorageReference fileRef = storageReference.child(System.currentTimeMillis() + "." + file_extension(uri));

        UploadTask uploadTask = fileRef.putFile(uri);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            fileRef.getDownloadUrl().addOnSuccessListener(uri1 -> onPutImage.onComplete(uri1.toString()))
                    .addOnFailureListener(ex -> onPutImage.onFailure(ex.getMessage()));
        }).addOnFailureListener(e -> onPutImage.onFailure(e.getMessage()));
    }

    private String file_extension(Uri uri) {
        ContentResolver cr = AppContext.getContext().getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }


    // lay ra danh sach tat ca cac danh muc du tren fibase
    public void ongetCategory() {
        ArrayList<Category> categories = new ArrayList<>();
        firestore.collection(Constant.CATEGORY)
                .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot doc : task.getResult()) {
                        categories.add(doc.toObject(Category.class));
                    }
                    categoryMutableLiveData.postValue(categories);
                }).addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
    }

    // lay ra tat ca danh sach tai khoan

    private void initGetAuth() {
        ArrayList<Users> users = new ArrayList<>();
        firestore.collection(Constant.KEY_USER)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        users.add(doc.toObject(Users.class));
                    }
                    usersLiveData.postValue(users);
                });
    }

    // Hàm khởi tạo dữ liệu công thức
    public void initRecipeData() {
        // Khởi tạo các danh sách công thức
        ArrayList<Recipe> recipes = new ArrayList<>(); // Danh sách tất cả công thức
        ArrayList<Recipe> recipesApprove = new ArrayList<>(); // Danh sách công thức chờ xác nhận
        ArrayList<Recipe> recipesReport = new ArrayList<>(); // Danh sách công thức bị báo cáo

        // Lắng nghe thay đổi dữ liệu trong bộ sưu tập "RECIPE" trên Firestore
        firestore.collection(Constant.RECIPE)
                .addSnapshotListener((value, error) -> {
                    // Duyệt qua các công thức trong bộ sưu tập
                    for (DocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.exists()) {
                            // Chuyển đổi dữ liệu thành đối tượng công thức
                            Recipe rcp = documentSnapshot.toObject(Recipe.class);
                            if (rcp != null) {
                                // Thêm công thức vào danh sách chung
                                recipes.add(rcp);

                                // Phân loại công thức theo trạng thái
                                if (rcp.RecipeStatus.equals(RecipeStatus.WAIT_CONFIRM)) { // Công thức chờ xác nhận
                                    recipesApprove.add(rcp);
                                } else if (rcp.RecipeStatus == RecipeStatus.WAS_REPORT) { // Công thức bị báo cáo
                                    recipesReport.add(rcp);
                                }

                                // Cập nhật các LiveData để hiển thị dữ liệu lên giao diện
                                recipeLiveData.postValue(recipes);
                                recipeApproveLiveData.postValue(recipesApprove);
                                recipeReportLiveData.postValue(recipesReport);
                            }
                        }
                    }
                });
    }

    // Hàm lấy dữ liệu người dùng theo ID
    public void getDataFromUserId(String recipeAuth, FetchByID<Users> view) {
        // Lắng nghe thay đổi dữ liệu của tài liệu có ID tương ứng trong bộ sưu tập "KEY_USER" trên Firestore
        firestore.collection(Constant.KEY_USER)
                .document(recipeAuth)
                .addSnapshotListener((value, error) -> {
                    // Nếu lấy dữ liệu thành công
                    if (value != null) {
                        // Chuyển đổi dữ liệu thành đối tượng người dùng
                        Users us = value.toObject(Users.class);
                        // Gọi hàm onSuccess của đối tượng view để xử lý kết quả
                        view.onSuccess(us);
                    } else {
                        // Nếu xảy ra lỗi
                        showToast("Error: " + error.getMessage()); // Hiển thị Toast thông báo lỗi
                        // Gọi hàm onErr của đối tượng view để xử lý lỗi
                        view.onErr(error);
                    }
                });
    }


    public void showToast(String mess) {
        Toast.makeText(AppContext.getContext(), mess, Toast.LENGTH_LONG).show();
    }
}




















