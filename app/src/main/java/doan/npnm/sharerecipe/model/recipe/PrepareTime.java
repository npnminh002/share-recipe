package doan.npnm.sharerecipe.model.recipe;

import java.io.Serializable;

public class PrepareTime implements Serializable {
    public String Time="0", TimeType="s";

    public PrepareTime(String time, String timeType) {
        Time = time;
        TimeType = timeType;
    }

    public PrepareTime() {
    }

    @Override
    public String toString() {
        return "PrepareTime{" +
                "Time='" + Time + '\'' +
                ", TimeType='" + TimeType + '\'' +
                '}';
    }
}
