package doan.npnm.sharerecipe.base;

import android.annotation.SuppressLint;
import android.widget.TableLayout;

import androidx.lifecycle.MutableLiveData;
import androidx.viewbinding.ViewBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.viewbinding.ViewBinding;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public abstract class BaseTableAdapter<T, V extends ViewBinding> {
    private ArrayList<T> listData = new ArrayList<>();
    private TableLayout tableLayout;
    private int count=0;

    private V binding;

    public String formatTimes(Object timestamp) {
        if (timestamp instanceof Long) {
            long timestampLong = (Long) timestamp;
            Date date = new Date(timestampLong);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            return sdf.format(date);
        }
        return "";
    }

    public BaseTableAdapter(TableLayout tableLayout) {
        this.tableLayout = tableLayout;
    }
    private void setDataToView() {
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }
        for (T item : listData){
            binding= initLayout();
            onBind(binding,item,count);
            count++;
            tableLayout.addView(binding.getRoot());
        }
    }

    protected abstract V  initLayout();

    protected abstract void onBind(V binding, T item,int position);

    public void setData(ArrayList<T> data){
        this.listData=data;
        setDataToView();
    }

}

