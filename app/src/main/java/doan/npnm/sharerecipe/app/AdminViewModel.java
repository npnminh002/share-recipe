package doan.npnm.sharerecipe.app;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import doan.npnm.sharerecipe.database.AppDatabase;
import doan.npnm.sharerecipe.database.AppDatabaseProvider;
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


    public AdminViewModel() {
        initRecipeData();
        initGetAuth();
    }

    private void initGetAuth() {
        ArrayList<Users> users = new ArrayList<>();
        firestore.collection(Constant.KEY_USER)
                .addSnapshotListener((value, error) -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        value.getDocuments().forEach(documentSnapshot -> {
                            users.add(documentSnapshot.toObject(Users.class));
                        });
                    }
                    usersLiveData.postValue(users);
                });
    }

    private void initRecipeData() {
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
}
