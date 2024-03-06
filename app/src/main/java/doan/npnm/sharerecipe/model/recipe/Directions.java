package doan.npnm.sharerecipe.model.recipe;

import java.io.Serializable;

public class Directions implements Serializable {
    public String Name="";
    public int Id=-1;

    public Directions(int id, String name) {
        Id = id;
        Name = name;
    }

    public Directions() {
    }

    @Override
    public String toString() {
        return "Directions{" +
                "Id='" + Id + '\'' +
                ", Name='" + Name +'}';
    }
}
