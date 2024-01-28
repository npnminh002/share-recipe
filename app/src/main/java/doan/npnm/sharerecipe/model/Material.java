package doan.npnm.sharerecipe.model;

import java.io.Serializable;

public class Material implements Serializable {
    public String Id,Name;
    public Object Image;

    public Material(String id, String name, Object image) {
        Id = id;
        Name = name;
        Image = image;
    }

    public Material() {
    }

    @Override
    public String toString() {
        return "Material{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name + '\'' +
                ", Image=" + Image +
                '}';
    }
}
