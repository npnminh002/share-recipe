package doan.npnm.sharerecipe.model.recipe;

import java.io.Serializable;
import java.util.ArrayList;

import doan.npnm.sharerecipe.R;
import doan.npnm.sharerecipe.app.context.AppContext;
import doan.npnm.sharerecipe.model.Material;

public class Recipe implements Serializable {
    public String Id = "", Name, Description, TimeInit = "" + System.currentTimeMillis();
    public String ImgUrl = "";
    public RecipeAuth RecipeAuth = new RecipeAuth();
    public PrepareTime PrepareTime = new PrepareTime();
    public CookTime CookTime = new CookTime();
    public String Level = "Easy";
    public ArrayList<Ingredients> Ingredients = new ArrayList<>();

    public ArrayList<Directions> Directions = new ArrayList<>();

    public int View = 0, Love = 0;
    public boolean IsConfirm = false;
    public Material Material = new Material();

    public Recipe(String id, String name, String description, String timeInit, String imgUrl,
                  doan.npnm.sharerecipe.model.recipe.RecipeAuth recipeAuth,
                  doan.npnm.sharerecipe.model.recipe.PrepareTime prepareTime,
                  doan.npnm.sharerecipe.model.recipe.CookTime cookTime, String level,
                  ArrayList<doan.npnm.sharerecipe.model.recipe.Ingredients> ingredients,
                  ArrayList<doan.npnm.sharerecipe.model.recipe.Directions> directions, int view, int love, boolean isConfirm, Material material) {
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
        Material = material;
    }

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
                ", IsConfirm=" + IsConfirm +
                '}';
    }
}
