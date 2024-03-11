package doan.npnm.sharerecipe.app;

import android.net.Uri;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.recipe.Recipe;

// khoi tao du lieu liveData khi them cong thuc moi giups chuyen giua cac man  du lieu khong bi mat


public class RecipeViewModel extends ViewModel {

    public MutableLiveData<Recipe> recipeLiveData= new MutableLiveData<>();

    public Uri imgUri= null;
    public MutableLiveData<ArrayList<Uri>> listSelect= new MutableLiveData<>(new ArrayList<>());

}
