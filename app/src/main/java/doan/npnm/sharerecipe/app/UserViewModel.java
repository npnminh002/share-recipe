package doan.npnm.sharerecipe.app;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.database.AppDatabase;
import doan.npnm.sharerecipe.database.AppDatabaseProvider;
import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.database.models.LoveRecipe;
import doan.npnm.sharerecipe.database.models.SaveRecipe;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.model.recipe.RecipeStatus;
import doan.npnm.sharerecipe.utility.Constant;

// Khởi tạo lớp UserViewModel, kế thừa từ lớp ViewModel
public class UserViewModel extends ViewModel {

    // Lấy thể hiện FirebaseAuth để xác thực người dùng
    public FirebaseAuth auth = FirebaseAuth.getInstance();

    // Lấy thể hiện FirebaseFirestore để tương tác với Firestore database
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    // Lấy thể hiện FirebaseDatabase để tương tác với Realtime Database
    public FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();

    // Lấy thể hiện FirebaseStorage để lưu trữ và quản lý file
    public FirebaseStorage storage = FirebaseStorage.getInstance();

    // Lấy tham chiếu đến thư mục gốc trong FirebaseStorage
    public StorageReference storageReference = storage.getReference();

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi dữ liệu người dùng
    public MutableLiveData<Users> users = new MutableLiveData<>();

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi trạng thái đăng nhập
    public MutableLiveData<Boolean> isSingApp = new MutableLiveData<>(false);

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi trạng thái thêm công thức
    public MutableLiveData<Boolean> isAddRecipe = new MutableLiveData<>(false);

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi danh sách công thức
    public MutableLiveData<ArrayList<Recipe>> recipeLiveData = new MutableLiveData<>(new ArrayList<>());

    // Lấy thể hiện của database Room để lưu trữ dữ liệu cục bộ
    public AppDatabase database = AppDatabaseProvider.getDatabase();

    // Tạo danh sách để lưu trữ các URI của file đã bị xóa
    public ArrayList<Uri> listDeleted = new ArrayList<>();

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi từ khóa tìm kiếm
    public MutableLiveData<String> searchKey = new MutableLiveData<>("");

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi danh sách danh mục
    public MutableLiveData<ArrayList<Category>> categoriesArr = new MutableLiveData<>();

    // Tạo đối tượng MutableLiveData để lưu trữ và theo dõi công thức đã thay đổi trạng thái yêu thích
    public MutableLiveData<Recipe> onChangeLove = new MutableLiveData<>(null);

    public UserViewModel() {
        // Kiểm tra nếu người dùng đã đăng nhập
        if (auth.getCurrentUser() != null) {
            // Lấy dữ liệu từ Firebase theo ID của người dùng hiện tại
            getDataFromUserId(auth.getCurrentUser().getUid());

            // Tạo luồng mới để thực hiện các tác vụ không đồng bộ
            new Thread(() -> {
                // Lấy danh sách danh mục từ database
                ongetCategory();

                // Lấy danh sách công thức từ database
                onGetRecipeData();
            }).start();

            // Nếu SDK phiên bản lớn hơn hoặc bằng N, thực hiện kiểm tra xác thực trong luồng riêng
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new Thread(this::onGetAuth).start();
            }
        } else {
            // Nếu người dùng chưa đăng nhập, thực hiện các tác vụ tương tự trong luồng riêng
            new Thread(() -> {
                ongetCategory();
                onGetRecipeDataNoUser();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onGetAuth();
                }
            }).start();
        }
    }


    // Lấy danh sách công thức khi người dùng chưa đăng nhập
    private void onGetRecipeDataNoUser() {
        // Tạo một danh sách để lưu trữ các công thức
        ArrayList<Recipe> rcpList = new ArrayList<>();

        // Lắng nghe sự thay đổi trong bộ sưu tập 'RECIPE' trên Firestore
        firestore.collection(Constant.RECIPE)
                .addSnapshotListener((value, error) -> {
                    // Duyệt qua các tài liệu trong bộ sưu tập
                    for (DocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.exists()) {
                            // Chuyển đổi tài liệu thành đối tượng Recipe
                            Recipe rcp = documentSnapshot.toObject(Recipe.class);

                            if (rcp != null) {
                                // Chỉ thêm công thức ở trạng thái 'PREVIEW'
                                if (rcp.RecipeStatus == RecipeStatus.PREVIEW) {
                                    rcpList.add(rcp);
                                }
                            } else {
                                // Hiển thị thông báo lỗi nếu đối tượng Recipe trống
                                showToast("Recipe is null");
                            }
                        } else {
                            // Hiển thị thông báo nếu tài liệu không tồn tại
                            showToast("Document does not exist");
                        }
                    }

                    // Cập nhật danh sách công thức
                    recipeLiveData.postValue(rcpList);
                });
    }

    // Lấy danh sách danh mục từ Firestore
    private void ongetCategory() {
        // Tạo một danh sách để lưu trữ danh mục
        ArrayList<Category> categories = new ArrayList<>();

        // Lấy tất cả tài liệu trong bộ sưu tập 'CATEGORY'
        firestore.collection(Constant.CATEGORY)
                .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot doc : task.getResult()) {
                        // Chuyển đổi tài liệu thành đối tượng Category
                        categories.add(doc.toObject(Category.class));
                    }

                    // Cập nhật danh sách danh mục
                    categoriesArr.postValue(categories);
                });
    }

    // Tạo một MutableLiveData để theo dõi trạng thái theo dõi
    public MutableLiveData<Boolean> isFollow = new MutableLiveData<>(false);

    // Hàm để thực hiện hành động theo dõi
    public void onFollow(Users usfollower) {
        // Lấy đối tượng người dùng hiện tại
        Users authLogin = getUsers().getValue();

        // Lưu người dùng được theo dõi vào database cục bộ
        database.followerDao().addRecentView(new Follower() {{
            AuthID = usfollower.UserID;
        }});

        // Tạo luồng mới để thực hiện các tác vụ không đồng bộ
        new Thread(() -> {
            // Cập nhật dữ liệu theo dõi trên FirebaseDatabase
            fbDatabase.getReference(Constant.FOLLOW_USER)
                    .child(authLogin.UserID)
                    .child("YouFollow")
                    .child(usfollower.UserID)
                    .setValue(usfollower.toJson());
            fbDatabase.getReference(Constant.FOLLOW_USER)
                    .child(usfollower.UserID)
                    .child("OtherFollow")
                    .child(authLogin.UserID)
                    .setValue(authLogin.toJson());

            // Cập nhật số lượt theo dõi của người dùng hiện tại trên Firestore
            DocumentReference docRef = firestore.collection(Constant.KEY_USER)
                    .document(authLogin.UserID);
            docRef.update("Follow", authLogin.Follow + 1);

            // Cập nhật số người theo dõi của người dùng được theo dõi trên Firestore
            DocumentReference docRef2 = firestore.collection(Constant.KEY_USER)
                    .document(usfollower.UserID);
            docRef2.get().addOnCompleteListener(task -> {
                Users usGet = task.getResult().toObject(Users.class);
                docRef2.update("Follower", usGet.Follower + 1);
            });
        }).start();
    }


    // Hàm để hủy theo dõi người dùng
    public void onUnFollow(Users usfollower) {
        // Lấy đối tượng người dùng hiện tại
        Users authLogin = getUsers().getValue();

        // Xóa người dùng được theo dõi khỏi database cục bộ
        database.followerDao().removeRecent(usfollower.UserID);

        // Tạo luồng mới để thực hiện các tác vụ không đồng bộ
        new Thread(() -> {
            // Xóa dữ liệu theo dõi trên FirebaseDatabase
            fbDatabase.getReference(Constant.FOLLOW_USER)
                    .child(authLogin.UserID)
                    .child("YouFollow")
                    .child(usfollower.UserID)
                    .removeValue();
            fbDatabase.getReference(Constant.FOLLOW_USER)
                    .child(usfollower.UserID)
                    .child("OtherFollow")
                    .child(authLogin.UserID)
                    .removeValue();

            // Cập nhật số lượt theo dõi của người dùng hiện tại trên Firestore
            DocumentReference docRef = firestore.collection(Constant.KEY_USER)
                    .document(authLogin.UserID);
            docRef.update("Follow", authLogin.Follow <= 1 ? 0 : authLogin.Follow - 1);

            // Cập nhật số người theo dõi của người dùng được theo dõi trên Firestore
            DocumentReference docRef2 = firestore.collection(Constant.KEY_USER)
                    .document(usfollower.UserID);
            docRef2.get().addOnCompleteListener(task -> {
                Users usGet = task.getResult().toObject(Users.class);
                docRef2.update("Follower", usGet.Follower <= 1 ? 0 : usGet.Follower - 1);
            });
        }).start();
    }

    // Kiểm tra xem có đang theo dõi người dùng với ID được cung cấp
    public void checkFollowByUid(String uid) {
        // Kiểm tra trong database cục bộ
        boolean isFollow = database.followerDao().checkExisId(uid);

        // Cập nhật trạng thái theo dõi vào MutableLiveData
        this.isFollow.postValue(isFollow);
    }

    // Lấy tên thiết bị
    private String getDeviceName() {
        // Lấy tên hãng sản xuất và tên model thiết bị
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;

        // Kiểm tra nếu tên model bắt đầu bằng tên hãng
        if (model.startsWith(manufacturer)) {
            // Chuyển ký tự đầu thành chữ in hoa
            return capitalize(model);
        } else {
            // Nối tên hãng và tên model, in hoa ký tự đầu
            return capitalize(manufacturer) + " " + capitalize(model);
        }
    }

    // Hàm chuyển đổi chuỗi thành chữ in hoa ở ký tự đầu mỗi từ
    private String capitalize(String str) {
        // Kiểm tra chuỗi có trống không
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        // Chuyển chuỗi thành mảng ký tự
        char[] arr = str.toCharArray();

        // Biến kiểm soát việc in hoa ký tự tiếp theo
        boolean capitalizeNext = true;

        // Chuỗi kết quả
        String phrase = "";

        // Duyệt qua từng ký tự trong mảng
        for (char c : arr) {
            // Kiểm tra nếu cần in hoa ký tự tiếp theo và ký tự là chữ cái
            if (capitalizeNext && Character.isLetter(c)) {
                // Chuyển ký tự thành chữ in hoa và thêm vào chuỗi kết quả
                phrase += Character.toUpperCase(c);
                // Đặt cờ để không in hoa ký tự tiếp theo
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                // Nếu là khoảng trắng, đặt cờ để in hoa ký tự tiếp theo
                capitalizeNext = true;
            }
            // Thêm ký tự vào chuỗi kết quả
            phrase += c;
        }

        return phrase;
    }


    public String formatToCurrency(float value) {
        Locale locale = new Locale("vi", "VN"); // Set the Vietnamese locale
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(locale);
        return currencyFormat.format(value);
    }

    public String formatTimes(Object timestamp) {
        if (timestamp instanceof Long) {
            long timestampLong = (Long) timestamp;
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(date);
        }
        return "";
    }

    public String getTimeNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    // Hàm thực hiện các tác vụ khi khởi động ứng dụng lần đầu
    public void firstStartApp(String uId) {
        // Lấy danh sách lịch sử của người dùng và thêm thông tin đăng nhập mới
        ArrayList<String> hstr = getUsers().getValue().History;
        hstr.add("Đăng nhập trên: " + getDeviceName() + " lúc: " + getTimeNow());

        // Cập nhật lịch sử đăng nhập lên Firestore
        firestore.collection(Constant.KEY_USER)
                .document(uId)
                .update("History", hstr)
                .addOnSuccessListener(unused -> {
                    // Lắng nghe thành công
                });

        // Tải dữ liệu liên quan
        onLoadFollower(uId);
        loadSaveRecipe(uId);
        loadOnLoveRecipe(uId);
    }

    // Tải các công thức đã yêu thích từ Firebase và lưu vào database cục bộ
    private void loadOnLoveRecipe(String uId) {
        fbDatabase.getReference(Constant.LOVE)
                .child(uId)
                .get()
                .addOnCompleteListener(task -> {
                    for (DataSnapshot doc : task.getResult().getChildren()) {
                        database.loveRecipeDao().saveNewLove(new LoveRecipe() {{
                            RecipeID = doc.getKey();
                            Recipe = doc.getValue().toString();
                        }});
                    }
                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
    }

    // Tải các công thức đã lưu từ Firebase và lưu vào database cục bộ
    private void loadSaveRecipe(String uId) {
        fbDatabase.getReference(Constant.RECIPE)
                .child(uId)
                .get()
                .addOnCompleteListener(task -> {
                    for (DataSnapshot doc : task.getResult().getChildren()) {
                        Recipe rcp = doc.getValue(Recipe.class);
                        database.saveRecipeDao().addRecentView(new SaveRecipe() {{
                            AuthID = rcp.RecipeAuth;
                            RecipeID = rcp.Id;
                            Recipe = rcp.toJson();
                        }});
                    }
                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
    }

    // Tải danh sách những người dùng đang theo dõi từ Firebase và lưu vào database cục bộ
    private void onLoadFollower(String usID) {
        fbDatabase.getReference(Constant.FOLLOW_USER)
                .child(usID)
                .child("YouFollow")
                .get()
                .addOnCompleteListener(task -> {
                    for (DataSnapshot doc : task.getResult().getChildren()) {
                        database.followerDao().addRecentView(new Follower() {{
                            AuthID = doc.getKey();
                        }});
                    }
                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage());
                });
    }

    // Tạo hai đối tượng MutableLiveData để lưu trữ và theo dõi dữ liệu
    public MutableLiveData<ArrayList<Users>> recipeAuth = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> myRecipeArr = new MutableLiveData<>(new ArrayList<>());


    public void onGetRecipeData() {
        // Tạo và  khởi tạo mới giá trị cho LiveData chứa danh sách công thức
        recipeLiveData.postValue(new ArrayList<>());

// Lấy ID của người dùng hiện tại
        String uid = auth.getCurrentUser().getUid();

// Tạo các danh sách:
// - myRecipe: để lưu trữ công thức của người dùng hiện tại
// - rcpList: để lưu trữ công thức ở trạng thái PREVIEW
        ArrayList<String> myRecipe = new ArrayList<>();
        ArrayList<Recipe> rcpList = new ArrayList<>();

// Lấy tất cả công thức từ bộ sưu tập "Recipe" trong Firestore
        firestore.collection(Constant.RECIPE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        if (documentSnapshot.exists()) { // Kiểm tra nếu tài liệu tồn tại
                            Recipe rcp = documentSnapshot.toObject(Recipe.class); // Chuyển đổi dữ liệu thành đối tượng Recipe

                            if (rcp != null) { // Kiểm tra nếu đối tượng Recipe không bị lỗi
                                // Nếu là công thức của người dùng hiện tại và chưa bị xóa
                                if (rcp.RecipeAuth.equals(uid) && rcp.RecipeStatus != RecipeStatus.DELETED) {
                                    myRecipe.add(rcp.toJson()); // Thêm JSON của công thức vào danh sách myRecipe
                                }

                                // Nếu là công thức ở trạng thái PREVIEW
                                if (rcp.RecipeStatus == RecipeStatus.PREVIEW) {
                                    rcpList.add(rcp); // Thêm vào danh sách rcpList
                                }
                            } else {
                                // Hiển thị thông báo lỗi nếu đối tượng Recipe bị lỗi
                                showToast("Recipe is null");
                            }
                        } else {
                            // Hiển thị thông báo lỗi nếu tài liệu không tồn tại
                            showToast("Document does not exist");
                        }
                    }

                    // Cập nhật các LiveData với các danh sách công thức đã xử lý
                    recipeLiveData.postValue(rcpList);
                    myRecipeArr.postValue(myRecipe);
                });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onGetAuth() {
        // Tạo danh sách để lưu trữ người dùng

// Lấy tất cả người dùng từ bộ sưu tập "KEY_USER" trong Firestore
        firestore.collection(Constant.KEY_USER)
                .get()
                .addOnCompleteListener(query -> {
                    if (query.isSuccessful()) { // Kiểm tra nếu lấy dữ liệu thành công
                        QuerySnapshot querySnapshot = query.getResult();
                        List<Users> usersArr = new ArrayList<>();

                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Users user = doc.toObject(Users.class); // Chuyển đổi dữ liệu thành đối tượng Users
                                usersArr.add(user); // Thêm vào danh sách users
                            }
                            // Sắp xếp danh sách người dùng theo số lượng công thức giảm dần
                            usersArr.sort(Comparator.comparingInt(o -> -o.Recipe));

                            ArrayList<Users> sublist = new ArrayList<>(usersArr.subList(0, 15));
                            this.recipeAuth.postValue(sublist);

                        }
                    }
                })
                .addOnFailureListener(e -> { // Xử lý lỗi nếu lấy dữ liệu thất bại
                    showToast(e.getMessage()); // Hiển thị Toast với thông báo lỗi
                });

    }


    public void putImgToStorage(StorageReference storageReference, Uri uri, OnPutImageListener onPutImage) {
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

    public void getRecipeByID(String data, FetchByID<Recipe> fetchByID) {
        // Lấy tài liệu có ID tương ứng trong bộ sưu tập "Recipe" từ Firestore
        firestore.collection(Constant.RECIPE)
                .document(data)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) { // Kiểm tra nếu tài liệu tồn tại
                        // Chuyển đổi dữ liệu thành đối tượng Recipe và gọi lại phương thức onSuccess
                        fetchByID.onSuccess(documentSnapshot.toObject(Recipe.class));
                    } else {
                        // Gọi lại phương thức onErr với thông báo lỗi nếu tài liệu không tồn tại
                        fetchByID.onErr("Không thể lấy dữ liệu");
                    }
                });

    }

    public void onLoveRecipe(Recipe rcp) {
        // Lưu công thức yêu thích vào database cục bộ
        database.loveRecipeDao().saveNewLove(new LoveRecipe() {{
            RecipeID = rcp.Id;
            Recipe = rcp.toJson();
        }});

// Lưu ID công thức yêu thích lên Firebase Database
        fbDatabase.getReference(Constant.LOVE)
                .child(users.getValue().UserID)
                .child(rcp.Id)
                .setValue(rcp.Id)
                .addOnSuccessListener(unused -> { // Nếu lưu thành công
                    // Kiểm tra nếu công thức chưa tồn tại trong database cục bộ
                    if (!database.loveRecipeDao().checkExist(rcp.Id)) {
                        // Thực hiện tác vụ cập nhật số người yêu thích trên nền luồng riêng
                        Executors.newCachedThreadPool().execute(() -> {
                            firestore.collection(Constant.RECIPE)
                                    .document(rcp.Id)
                                    .update("Love", FieldValue.increment(1)) // Tăng số người yêu thích lên 1
                                    .addOnFailureListener(e -> {
                                        Log.d("TAG", "onLoveRecipe: " + e.getMessage()); // Ghi log lỗi ra console
                                    });
                        });
                    }
                })
                .addOnFailureListener(e -> showToast(e.getMessage())); // Hiển thị Toast với thông báo lỗi

    }


    public void onUnlove(Recipe rcp) {
        // Xoá công thức khỏi danh sách yêu thích trong database cục bộ
        database.loveRecipeDao().unLove(rcp.Id);

// Xoá ID công thức khỏi Firebase Database
        fbDatabase.getReference(Constant.LOVE)
                .child(users.getValue().UserID)
                .child(rcp.Id)
                .removeValue((error, ref) -> {
                    if (error != null) { // Nếu xảy ra lỗi khi xoá
                        // Xử lý lỗi nếu cần
                        Log.d("TAG", "onUnlove: Error removing value");
                        return; // Dừng thực hiện
                    }

                    // Kiểm tra nếu công thức vẫn còn tồn tại trong database cục bộ
                    if (database.loveRecipeDao().checkExist(rcp.Id)) {
                        // Thực hiện tác vụ cập nhật số người yêu thích trên nền luồng riêng
                        Executors.newCachedThreadPool().execute(() -> {
                            firestore.collection(Constant.RECIPE)
                                    .document(rcp.Id)
                                    .update("Love", FieldValue.increment(-1)) // Giảm số người yêu thích xuống 1
                                    .addOnFailureListener(e -> {
                                        Log.d("TAG", "onUnlove: " + e.getMessage()); // Ghi log lỗi ra console
                                    });
                        });
                    }
                });

    }


    public interface OnPutImageListener {
        void onComplete(String url);

        void onFailure(String mess);
    }

    public static String formatString(String input) {
        String trimmedString = input.trim();
        String formattedString = trimmedString.replaceAll("\\s+", "_").toLowerCase();
        return "#" + formattedString;
    }

    public void getDataFromUserId(String uid) {
        firestore.collection(Constant.KEY_USER)
                .document(uid)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        Users us = value.toObject(Users.class);
                        users.postValue(us);
                    } else {
                        showToast("Error: " + error.getMessage());
                        users.postValue(null);
                    }

                });
    }

    public void updateToken(String userID) {

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(s -> FirebaseFirestore.getInstance()
                .collection(Constant.KEY_USER)
                .document(userID)
                .update("Token", s).addOnSuccessListener(unused -> Log.d("Test", "Update Token Success"))
                .addOnFailureListener(e -> Log.d("Test", "Update Token Error: " + e.getMessage()))).addOnFailureListener(e -> {
            showToast(e.getMessage());
        });

    }

    public void getDataFromUserId(String uid, FetchByID<Users> fetch) {
        // Lắng nghe thay đổi dữ liệu của tài liệu có ID tương ứng trong bộ sưu tập "KEY_USER" từ Firestore
        firestore.collection(Constant.KEY_USER)
                .document(uid)
                .get()
                .addOnSuccessListener(value -> {

                    if (value != null) { // Nếu lấy dữ liệu thành công
                        // Chuyển đổi dữ liệu thành đối tượng Users và gọi lại phương thức onSuccess
                        Users us = value.toObject(Users.class);
                        fetch.onSuccess(us);
                    }
                }).addOnFailureListener(e -> {
                    fetch.onErr(e.getMessage());
                });
    }

    public void signUpApp(String email, String name, String pass, MutableLiveData<Boolean> isEmailExists) {
        // Kiểm tra xem email có tồn tại trong bộ sưu tập "KEY_USER" của Firestore
        firestore.collection(Constant.KEY_USER)
                .whereEqualTo("Email", email) // Lọc theo trường "Email" bằng email được cung cấp
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) { // Nếu không tìm thấy tài liệu nào
                        // Tạo tài khoản người dùng mới trong Firestore
                        createUserInFirestore(email, name, pass);
                        // Cập nhật LiveData isEmailExists thành false (email chưa tồn tại)
                        isEmailExists.postValue(false);
                    } else {
                        // Cập nhật LiveData isEmailExists thành true (email đã tồn tại)
                        isEmailExists.postValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    // Hiển thị thông báo lỗi nếu xảy ra vấn đề
                    showToast("Error checking email existence: " + e.getMessage());
                    // Cập nhật LiveData isEmailExists thành false (phòng trường hợp lỗi)
                    isEmailExists.postValue(false);
                });

    }

    public MutableLiveData<Users> getUsers() {
        return users;
    }

    private void createUserInFirestore(String email, String name, String pass) {
        // Tạo tài khoản người dùng mới với email và mật khẩu trong Firebase Authentication
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    // Nếu tạo tài khoản thành công
                    Users user = new Users(authResult.getUser().getUid(), name,
                            email, pass, "", "", new Date().toString(), "", "", formatString(name), 0, 0, 0, 0);
                    // Lưu đối tượng người dùng vào Firestore
                    firestore.collection(Constant.KEY_USER)
                            .document(user.UserID)
                            .set(user)
                            .addOnSuccessListener(unused -> {
                                // Nếu lưu thành công
                                showToast("Đăng ký thành công");
                                users.setValue(user); // Cập nhật LiveData users
                                updateToken(user.UserID);
                            })
                            .addOnFailureListener(error -> {
                                // Nếu lưu thất bại
                                showToast(error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    // Nếu tạo tài khoản thất bại
                    showToast(error.getMessage());
                });

    }


    public void showToast(String mess) {
        Toast.makeText(AppContext.getContext(), mess, Toast.LENGTH_LONG).show();
    }


    // Xoa het du lieu ofline da luu lai
    public void signOutDatabase() {
        database.recentViewDao().SignOutApp();
        database.followerDao().SignOutApp();
        database.recipeDao().SignOutApp();
        database.searchDao().SignOutApp();
        database.saveRecipeDao().SignOutApp();
        database.loveRecipeDao().SignOutApp();
    }

}
