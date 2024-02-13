package doan.npnm.sharerecipe.app;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Build;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.database.AppDatabase;
import doan.npnm.sharerecipe.database.AppDatabaseProvider;
import doan.npnm.sharerecipe.database.models.Follower;
import doan.npnm.sharerecipe.interfaces.FetchByID;
import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.Users;
import doan.npnm.sharerecipe.model.recipe.Recipe;
import doan.npnm.sharerecipe.utility.Constant;

public class AppViewModel extends ViewModel {
    public FirebaseAuth auth = FirebaseAuth.getInstance();
    public FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    public FirebaseDatabase fbDatabase = FirebaseDatabase.getInstance();
    public FirebaseStorage storage = FirebaseStorage.getInstance();
    public StorageReference storageReference = storage.getReference();

    public MutableLiveData<Users> users = new MutableLiveData<>();

    public MutableLiveData<Boolean> isAddRecipe = new MutableLiveData<>(false);

    public MutableLiveData<ArrayList<Recipe>> recipeLiveData = new MutableLiveData<>(new ArrayList<>());

    public AppDatabase database = AppDatabaseProvider.getDatabase();

    public AppViewModel() {
        if (auth.getCurrentUser() != null) {
            getDataFromUserId(auth.getCurrentUser().getUid());
             }
        new Thread(this::onGetRecipeData).start();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            new Thread(this::onGetAuth).start();
        }

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


    public void firstStartApp(String uId) {
        onLoadFollower(uId);
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
    public ArrayList<String> myRecipeArr= new ArrayList<>();


    public void onGetRecipeData() {
        String loginID= Objects.requireNonNull(auth.getCurrentUser()).getUid();
        ArrayList<Recipe> rcpList = new ArrayList<>();
        firestore.collection(Constant.RECIPE)
                .addSnapshotListener((value, error) -> {
                    for (DocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.exists()) {
                            Recipe rcp = documentSnapshot.toObject(Recipe.class);
                            if (rcp != null) {
                                rcpList.add(rcp);
                                if (rcp.RecipeAuth.equals(loginID)) {
                                    myRecipeArr.add(rcp.toJson());
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


    public ArrayList<Category> getListCategory() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category("1", AppContext.getContext().getString(R.string.bakery), R.drawable.category_bakery));
        categories.add(new Category("2", AppContext.getContext().getString(R.string.beverages), R.drawable.category_beverages));
        categories.add(new Category("3", AppContext.getContext().getString(R.string.dairy), R.drawable.category_dairy));
        categories.add(new Category("4", AppContext.getContext().getString(R.string.frozen), R.drawable.category_frozen));
        categories.add(new Category("5", AppContext.getContext().getString(R.string.fruit), R.drawable.category_fruit));
        categories.add(new Category("6", AppContext.getContext().getString(R.string.meat), R.drawable.category_meat));
        categories.add(new Category("7", AppContext.getContext().getString(R.string.poultry), R.drawable.category_poultry));
        categories.add(new Category("8", AppContext.getContext().getString(R.string.seafood), R.drawable.category_seafood));
        categories.add(new Category("9", AppContext.getContext().getString(R.string.vegetable), R.drawable.category_vegettable));
        return categories;

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

                        users.postValue(us != null ? us : null);
                    } else {
                        showToast("Error: " + error.getMessage());
                        users.postValue(null);
                    }

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

}
