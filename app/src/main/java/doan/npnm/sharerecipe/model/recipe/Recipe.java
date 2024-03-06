package doan.npnm.sharerecipe.model.recipe;

import androidx.annotation.Keep;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;


public class Recipe implements Serializable {
    public String Id = "";
    public String Name, Description, TimeInit = "" + System.currentTimeMillis();
    public String ImgUrl = "";
    public String RecipeAuth = "";
    public PrepareTime PrepareTime = new PrepareTime();
    public CookTime CookTime = new CookTime();
    public String Level = "Easy";
    public ArrayList<Ingredients> Ingredients = new ArrayList<>();

    public ArrayList<Directions> Directions = new ArrayList<>();

    public int View = 0, Love = 0, Share = 0;
    public RecipeStatus RecipeStatus = doan.npnm.sharerecipe.model.recipe.RecipeStatus.PREVIEW;
    public ArrayList<String> Category = new ArrayList<>();
    public ArrayList<String> ImagePreview = new ArrayList<>();
    public ArrayList<String> History = new ArrayList<>();

    public Recipe(String id, String name, String description, String timeInit, String imgUrl, String recipeAuth, doan.npnm.sharerecipe.model.recipe.PrepareTime prepareTime, doan.npnm.sharerecipe.model.recipe.CookTime cookTime, String level, ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients> ingredients, ArrayList<doan.npnm.sharerecipe.model.recipe.Directions> directions, int view, int love, int share, doan.npnm.sharerecipe.model.recipe.RecipeStatus recipeStatus, ArrayList<String> category, ArrayList<String> imagePreview, ArrayList<String> history) {
        Id = id;
        Name = name;
        Description = description;
        TimeInit = timeInit;
        ImgUrl = imgUrl;
        RecipeAuth = recipeAuth;
        PrepareTime = prepareTime;
        CookTime = cookTime;
        Level = level;
        Ingredients = ingredients;
        Directions = directions;
        View = view;
        Love = love;
        Share = share;
        RecipeStatus = recipeStatus;
        Category = category;
        ImagePreview = imagePreview;
        History = history;
    }

    @Keep
    public Recipe() {
    }


    @Override
    public String toString() {
        return "Recipe{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Description='" + Description + '\'' +
                ", TimeInit='" + TimeInit + '\'' +
                ", ImgUrl='" + ImgUrl + '\'' +
                ", RecipeAuth='" + RecipeAuth + '\'' +
                ", PrepareTime=" + PrepareTime +
                ", CookTime=" + CookTime +
                ", Level='" + Level + '\'' +
                ", Ingredients=" + Ingredients +
                ", Directions=" + Directions +
                ", View=" + View +
                ", Love=" + Love +
                ", Share=" + Share +
                ", RecipeStatus=" + RecipeStatus +
                ", Category=" + Category +
                ", ImagePreview=" + ImagePreview +
                ", History=" + History +
                '}';
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public Recipe fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Recipe.class);
    }
}
