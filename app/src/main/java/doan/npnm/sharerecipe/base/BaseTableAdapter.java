package doan.npnm.sharerecipe.base;

import android.widget.TableLayout;

import androidx.viewbinding.ViewBinding;

import java.util.ArrayList;

public abstract class BaseTableAdapter<T, V extends ViewBinding> {
    private ArrayList<T> listData = new ArrayList<>();
    private TableLayout tableLayout;
    private int count = 0;

    private V binding;

    public BaseTableAdapter(TableLayout tableLayout) {
        this.tableLayout = tableLayout;
    }

    private void setDataToView() {
        int childCount = tableLayout.getChildCount();
        if (childCount > 1) {
            tableLayout.removeViews(1, childCount - 1);
        }
        for (T item : listData) {
            binding = initLayout();
            onBind(binding, item, count);
            count++;
            tableLayout.addView(binding.getRoot());
            if (count == listData.size() - 1) {
                if(event!=null){
                    event.onFinish();
                }

            }
        }
    }

    protected abstract V initLayout();

    protected abstract void onBind(V binding, T item, int position);

    public void setData(ArrayList<T> data) {
        this.listData = data;
        setDataToView();
    }

    private OnFinishEvent event;

    public BaseTableAdapter<T, V> onFinih(OnFinishEvent finishEvent) {
        this.event = finishEvent;
        return this;
    }

    public interface OnFinishEvent {
        void onFinish();
    }

}

