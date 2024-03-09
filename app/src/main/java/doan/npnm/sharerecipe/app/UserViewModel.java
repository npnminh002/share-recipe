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

import com.google.android.gms.tasks.OnSuccessListener;
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

public class UserViewModel extends ViewModel {
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageReference = storage.getReference();

    public MutableLiveData<Users> users = new MutableLiveData<>();

    public MutableLiveData<Boolean> isSingApp = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> isAddRecipe = new MutableLiveData<>(false);

    public MutableLiveData<ArrayList<Recipe>> recipeLiveData = new MutableLiveData<>(new ArrayList<>());

    public AppDatabase database = AppDatabaseProvider.getDatabase();
    public ArrayList<Uri> listDeleted = new ArrayList<>();

    public MutableLiveData<String> searchKey = new MutableLiveData<>("");

    public MutableLiveData<ArrayList<Category>> categoriesArr = new MutableLiveData<>();

    public MutableLiveData<Recipe> onChangeLove = new MutableLiveData<>(null);

    public UserViewModel() {
        if (auth.getCurrentUser() != null) {
            getDataFromUserId(auth.getCurrentUser().getUid());
            new Thread(() -> {
                ongetCategory();
                onGetRecipeData();
            }).start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                new Thread(this::onGetAuth).start();
            }
        } else {
            new Thread(() -> {
                ongetCategory();
                onGetRecipeDataNoUser();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    onGetAuth();
                }
            }).start();
        }
    }

    private void onGetRecipeDataNoUser() {
        ArrayList<Recipe> rcpList = new ArrayList<>();
        firestore.collection(Constant.RECIPE)
                .addSnapshotListener((value, error) -> {
                    for (DocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.exists()) {
                            Recipe rcp = documentSnapshot.toObject(Recipe.class);
                            if (rcp != null) {
                                if (rcp.RecipeStatus == RecipeStatus.PREVIEW) {
                                    rcpList.add(rcp);
                                }

                            } else {
                                showToast("Recipe is null");
                            }

                        } else {

                            showToast("Document does not exist");
                        }
                    }
                    recipeLiveData.postValue(rcpList);
                });
    }

    private void ongetCategory() {
        ArrayList<Category> categories = new ArrayList<>();
        firestore.collection(Constant.CATEGORY)
                .get().addOnCompleteListener(task -> {
                    for (DocumentSnapshot doc : task.getResult()) {
                        categories.add(doc.toObject(Category.class));
                    }
                    categoriesArr.postValue(categories);
                });
    }

    public MutableLiveData<Boolean> isFollow = new MutableLiveData<>(false);


    public void onFollow(Users usfollower) {
        Users authLogin = getUsers().getValue();
        database.followerDao().addRecentView(new Follower() {{
            AuthID = usfollower.UserID;
        }});
        new Thread(() -> {
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

            DocumentReference docRef = firestore.collection(Constant.KEY_USER)
                    .document(authLogin.UserID);
            docRef.update("Follow", authLogin.Follow + 1);

            DocumentReference docRef2 = firestore.collection(Constant.KEY_USER)
                    .document(usfollower.UserID);
            docRef2.get().addOnCompleteListener(task -> {
                Users usGet = task.getResult().toObject(Users.class);
                docRef2.update("Follower", usGet.Follower + 1);

            });

        }).start();
    }

    public void onUnFollow(Users usfollower) {
        Users authLogin = getUsers().getValue();
        database.followerDao().removeRecent(usfollower.UserID);
        new Thread(() -> {
            fbDatabase.getReference(Constant.FOLLOW_USER)
                    .child(authLogin.UserID)
                    .child("YouFollow")
                    .child(usfollower.UserID)
                    .removeValue();
            fbDatabase.getReference(Constant.FOLLOW_USER)
                    .child(usfollower.UserID)
                    .child("OtherFollow")
                    .child(usfollower.UserID)
                    .removeValue();

            DocumentReference docRef = firestore.collection(Constant.KEY_USER)
                    .document(authLogin.UserID);
            docRef.update("Follow", authLogin.Follow <= 1 ? 0 : authLogin.Follow - 1);

            DocumentReference docRef2 = firestore.collection(Constant.KEY_USER)
                    .document(usfollower.UserID);
            docRef2.get().addOnCompleteListener(task -> {
                Users usGet = task.getResult().toObject(Users.class);
                docRef2.update("Follower", usGet.Follower <= 1 ? 0 : usGet.Follower + 1);

            });

        }).start();

    }

    public void checkFollowByUid(String uid) {
        boolean isFollow = database.followerDao().checkExisId(uid);
        this.isFollow.postValue(isFollow);
    }

    private  String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private  String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;
        String phrase = "";
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase += Character.toUpperCase(c);
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
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


    public void firstStartApp(String uId) {

        ArrayList<String> hstr= getUsers().getValue().History;
        hstr.add("Login at: "+getDeviceName()+" at: "+getTimeNow());
        firestore.collection(Constant.KEY_USER)
                        .document(uId)
                                .update("History",hstr)
                                        .addOnSuccessListener(unused -> {

                                        });
        onLoadFollower(uId);
        loadSaveRecipe(uId);
        loadOnLoveRecipe(uId);
    }

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


    public MutableLiveData<ArrayList<Users>> recipeAuth = new MutableLiveData<>();
    public MutableLiveData<ArrayList<String>> myRecipeArr = new MutableLiveData<>(new ArrayList<>());


    public void onGetRecipeData() {
        recipeLiveData.postValue(new ArrayList<>());
        String uid = auth.getCurrentUser().getUid();
        ArrayList<String> myRecipe = new ArrayList<>();
        ArrayList<Recipe> rcpList = new ArrayList<>();
        firestore.collection(Constant.RECIPE)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        if (documentSnapshot.exists()) {
                            Recipe rcp = documentSnapshot.toObject(Recipe.class);
                            if (rcp != null) {
                                if (rcp.RecipeAuth.equals(uid)&&rcp.RecipeStatus!=RecipeStatus.DELETED) {
                                    myRecipe.add(rcp.toJson());
                                }
                                if (rcp.RecipeStatus == RecipeStatus.PREVIEW) {
                                    rcpList.add(rcp);
                                }

                            } else {
                                showToast("Recipe is null");
                            }


                        } else {

                            showToast("Document does not exist");
                        }
                    }
                    recipeLiveData.postValue(rcpList);
                    myRecipeArr.postValue(myRecipe);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void onGetAuth() {
        ArrayList<Users> users = new ArrayList<>();
        firestore.collection(Constant.KEY_USER)
                .get()
                .addOnCompleteListener(query -> {
                    if (query.isSuccessful()) {
                        QuerySnapshot querySnapshot = query.getResult();
                        if (querySnapshot != null && !querySnapshot.isEmpty()) {
                            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                                Users user = doc.toObject(Users.class);
                                users.add(user);
                            }
                            users.sort(Comparator.comparingInt(o -> -o.Recipe));
                            users.subList(0, 15);

                            this.recipeAuth.postValue(users);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    showToast(e.getMessage());
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
        firestore.collection(Constant.RECIPE)
                .document(data)
                .get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        fetchByID.onSuccess(documentSnapshot.toObject(Recipe.class));
                    } else {
                        fetchByID.onErr("Can't not get value");
                    }
                });

    }

    public void onLoveRecipe(Recipe rcp) {
        database.loveRecipeDao().saveNewLove(new LoveRecipe() {{
            RecipeID = rcp.Id;
            Recipe = rcp.toJson();
        }});
        fbDatabase.getReference(Constant.LOVE)
                .child(users.getValue().UserID)
                .child(rcp.Id)
                .setValue(rcp.Id)
                .addOnSuccessListener(unused -> {
                    if (!database.loveRecipeDao().checkExist(rcp.Id)) {
                        Executors.newCachedThreadPool().execute(() -> {

                            firestore.collection(Constant.RECIPE)
                                    .document(rcp.Id)
                                    .update("Love", FieldValue.increment(1))
                                    .addOnFailureListener(e -> {
                                        Log.d("TAG", "onLoveRecipe: " + e.getMessage());
                                    });
                        });
                    }
                })
                .addOnFailureListener(e -> showToast(e.getMessage()));
    }


    public void onUnlove(Recipe rcp) {
        database.loveRecipeDao().unLove(rcp.Id);
        fbDatabase.getReference(Constant.LOVE)
                .child(users.getValue().UserID)
                .child(rcp.Id)
                .removeValue((error, ref) -> {
                    if (error != null) {
                        // Handle error if needed
                        Log.d("TAG", "onUnlove: Error removing value");
                        return;
                    }

                    if (database.loveRecipeDao().checkExist(rcp.Id)) {
                        Executors.newCachedThreadPool().execute(() -> {

                            firestore.collection(Constant.RECIPE)
                                    .document(rcp.Id)
                                    .update("Love", FieldValue.increment(-1))
                                    .addOnFailureListener(e -> {
                                        Log.d("TAG", "onUnlove: " + e.getMessage());
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
        firestore.collection(Constant.KEY_USER)
                .document(uid)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        Users us = value.toObject(Users.class);
                        fetch.onSuccess(us);
                    } else {
                        showToast("Error: " + error.getMessage());
                        fetch.onErr(error);
                    }

                });
    }

    public void signUpApp(String email, String name, String pass, MutableLiveData<Boolean> isEmailExists) {
        firestore.collection(Constant.KEY_USER)
                .whereEqualTo("Email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        createUserInFirestore(email, name, pass);
                        isEmailExists.postValue(false);
                    } else {
                        isEmailExists.postValue(true);
                    }
                })
                .addOnFailureListener(e -> {
                    showToast("Error checking email existence: " + e.getMessage());
                    isEmailExists.postValue(false);
                });
    }

    public MutableLiveData<Users> getUsers() {
        return users;
    }

    private void createUserInFirestore(String email, String name, String pass) {
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {
                    authResult.getUser().getIdToken(true)
                            .addOnSuccessListener(getTokenResult -> {
                                String idToken = getTokenResult.getToken();
                                Users user = new Users(authResult.getUser().getUid(), name,
                                        email, pass, idToken, "", new Date().toString(), "", "", formatString(name), 0, 0, 0, 0);
                                firestore.collection(Constant.KEY_USER)
                                        .document(user.UserID)
                                        .set(user)
                                        .addOnSuccessListener(unused -> {
                                            showToast("Sign Up Success");
                                            users.setValue(user);
                                        })
                                        .addOnFailureListener(error -> {
                                            showToast(error.getMessage());
                                        });
                            })
                            .addOnFailureListener(error -> {
                                showToast("Failed to retrieve ID token: " + error.getMessage());
                            });
                })
                .addOnFailureListener(error -> {
                    showToast(error.getMessage());
                });
    }


    public void showToast(String mess) {
        Toast.makeText(AppContext.getContext(), mess, Toast.LENGTH_LONG).show();
    }


    public void signOutDatabase() {
        database.recentViewDao().SignOutApp();
        database.followerDao().SignOutApp();
        database.recipeDao().SignOutApp();
        database.searchDao().SignOutApp();
        database.saveRecipeDao().SignOutApp();
        database.loveRecipeDao().SignOutApp();
    }

}
