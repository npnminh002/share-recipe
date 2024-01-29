package doan.npnm.sharerecipe.model;

import java.io.Serializable;

public class Category implements Serializable {

    public String Id,Name;
    public Object Image;

    public Category(String id, String name, Object image) {
        Id = id;
        Name = name;
        Image = image;
    }

    @Override
    public String toString() {
        return "Category{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Image='" + Image + '\'' +
                '}';
    }

    public Category() {
    }
}
