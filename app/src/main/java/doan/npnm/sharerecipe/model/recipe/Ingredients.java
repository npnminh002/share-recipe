package doan.npnm.sharerecipe.model.recipe;

import java.io.Serializable;

import doan.npnm.sharerecipe.model.Material;

public class Ingredients implements Serializable {
    public int Id=-1;
    public String Name="";

    public float Quantitative;

    public Ingredients(int id,String name, float quantitative) {
        Id=id;
        Name = name;
        Quantitative = quantitative;
    }

    public Ingredients() {
    }

    @Override
    public String toString() {
        return "Ingredients{" +
                "Id=" + Id +
                ", Name='" + Name + '\'' +
                ", Quantitative=" + Quantitative +
                '}';
    }
}
