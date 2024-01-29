package doan.npnm.sharerecipe.model.recipe;

import androidx.annotation.Keep;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

import doan.npnm.sharerecipe.model.Category;
import doan.npnm.sharerecipe.model.Material;


public class Recipe implements Serializable {
    public String Id = "";
    public String Name, Description, TimeInit = "" + System.currentTimeMillis();
    public String ImgUrl = "";
    public RecipeAuth RecipeAuth = new RecipeAuth();
    public PrepareTime PrepareTime = new PrepareTime();
    public CookTime CookTime = new CookTime();
    public String Level = "Easy";
    public ArrayList<Ingredients> Ingredients = new ArrayList<>();

    public ArrayList<Directions> Directions = new ArrayList<>();

    public int View = 0, Love = 0;
    public boolean IsConfirm = false;
    public Category Category = new Category();
    public ArrayList<String> ImagePreview = new ArrayList<>();

    public Recipe(String id, String name, String description, String timeInit, String imgUrl,
                  doan.npnm.sharerecipe.model.recipe.RecipeAuth recipeAuth,
                  doan.npnm.sharerecipe.model.recipe.PrepareTime prepareTime,
                  doan.npnm.sharerecipe.model.recipe.CookTime cookTime, String level,
                  ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients> ingredients,
                  ArrayList<doan.npnm.sharerecipe.model.recipe.Directions> directions, int view,
                  int love, boolean isConfirm, Category category, ArrayList<String> imagePreview) {
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
        IsConfirm = isConfirm;
        Category = category;
        ImagePreview = imagePreview;
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
                ", ImgUrl=" + ImgUrl +
                ", RecipeAuth=" + RecipeAuth.toString() +
                ", PrepareTime=" + PrepareTime +
                ", CookTime=" + CookTime.toString() +
                ", Level='" + Level + '\'' +
                ", Ingredients=" + Ingredients.toString() +
                ", Directions=" + Directions.toString() +
                ", View=" + View +
                ", Love=" + Love +
                ",ImagePreview" + ImagePreview +
                ", IsConfirm=" + IsConfirm +
                '}';
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
    public  Recipe fromJson(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Recipe.class);
    }
}
