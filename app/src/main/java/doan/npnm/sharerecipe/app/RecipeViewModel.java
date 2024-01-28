package doan.npnm.sharerecipe.app;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import doan.npnm.sharerecipe.model.recipe.Recipe;

public class RecipeViewModel extends ViewModel {

    public MutableLiveData<Recipe> recipeLiveData= new MutableLiveData<>();

    public Uri imgUri= null;


    public MutableLiveData<ArrayList<Uri>> listSelect= new MutableLiveData<>(null);
}
