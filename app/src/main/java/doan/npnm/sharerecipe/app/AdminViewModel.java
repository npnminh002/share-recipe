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

    public void initRecipeData() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        ArrayList<Recipe> recipesApprove = new ArrayList<>();
        ArrayList<Recipe> recipesReport = new ArrayList<>();
        firestore.collection(Constant.RECIPE)
                .addSnapshotListener((value, error) -> {
                    for (DocumentSnapshot documentSnapshot : value) {
                        if (documentSnapshot.exists()) {
                            Recipe rcp = documentSnapshot.toObject(Recipe.class);
                            if (rcp != null) {
                                recipes.add(rcp);
                                if (rcp.RecipeStatus.equals(RecipeStatus.WAIT_CONFIRM)) {
                                    recipesApprove.add(rcp);
                                }
                                if (rcp.RecipeStatus == RecipeStatus.WAS_REPORT) {
                                    recipesReport.add(rcp);
                                }


                                recipeLiveData.postValue(recipes);
                                recipeApproveLiveData.postValue(recipesApprove);
                                recipeReportLiveData.postValue(recipesReport);
                            }
                        }
                    }

                });
    }

    public void getDataFromUserId(String recipeAuth, FetchByID<Users> view) {
        firestore.collection(Constant.KEY_USER)
                .document(recipeAuth)
                .addSnapshotListener((value, error) -> {
                    if (value != null) {
                        Users us = value.toObject(Users.class);
                        view.onSuccess(us);
                    } else {
                        showToast("Error: " + error.getMessage());
                        view.onErr(error);
                    }

                });
    }

    public void showToast(String mess) {
        Toast.makeText(AppContext.getContext(), mess, Toast.LENGTH_LONG).show();
    }
}




















