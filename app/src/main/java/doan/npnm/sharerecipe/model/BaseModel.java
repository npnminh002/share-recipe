package doan.npnm.sharerecipe.model;

import com.google.gson.Gson;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class BaseModel<T> implements Serializable {
    public String Id;


    public String toJson() {
        Gson gson = new Gson();
        return this == null ? "null" : gson.toJson(this);
    }

    public T fromJson(String jsonString) {
        Gson gson = new Gson();
        return (T) gson.fromJson(jsonString, this.getClass());
    }

    public String formatTimes(Object timestamp) {
        if (timestamp instanceof Long) {
            long timestampLong = (Long) timestamp;
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(date);
        }
        return "";
    }

    public String getTimeNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(new Date());
    }

    public ArrayList<String> History= new ArrayList<>();
}