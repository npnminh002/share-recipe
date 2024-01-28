package doan.npnm.sharerecipe.model.recipe;

import java.io.Serializable;



public class CookTime implements Serializable{
    public String Time="0",TimeType="s";

    public CookTime(String time, String timeType) {
        Time = time;
        TimeType = timeType;
    }

    public CookTime() {
    }

    @Override
    public String toString() {
        return "CookTime{" +
                "Time='" + Time + '\'' +
                ", TimeType='" + TimeType + '\'' +
                '}';
    }
}
