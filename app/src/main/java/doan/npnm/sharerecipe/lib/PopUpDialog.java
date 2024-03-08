package doan.npnm.sharerecipe.lib;

import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.viewbinding.ViewBinding;

import java.util.function.Function;

import doan.npnm.sharerecipe.utility.Utils;

public class PopUpDialog {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <VB extends ViewBinding> void showPopupMenu(View anchorView,
                                                              Function<LayoutInflater, VB> bindingInflater,
                                                              int width,
                                                              @Nullable Integer height,
                                                              @Nullable PopUpLocation popUpLocation,
                                                              OnViewBinder<VB> ev) {
        PopupWindow popupWindow;
        LayoutInflater inflater = LayoutInflater.from(anchorView.getContext());
        VB popupView = bindingInflater.apply(inflater);
        View rootView = popupView.getRoot();
        popupWindow = new PopupWindow(rootView, width, height != null ? height : WindowManager.LayoutParams.WRAP_CONTENT, true);
        ev.onView(popupView,popupWindow);
        anchorView.post(() -> {
            int[] location = new int[2];
            anchorView.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            int screenWidth = anchorView.getContext().getResources().getDisplayMetrics().widthPixels;
            int screenHeight = anchorView.getContext().getResources().getDisplayMetrics().heightPixels;

            if (popUpLocation != null) {
                switch (popUpLocation) {
                    case DEFAULT_BOTTOM:
                    case DEFAULT_TOP:
                        x = (screenWidth - width) / 2;
                        y += Utils.getHeightPercent(1f);
                        break;
                    case TOP:
                        x = (screenWidth - width) / 2;
                        y = Utils.getHeightPercent(1f);
                        break;
                    case BOTTOM:
                        x = (screenWidth - width) / 2;
                        y = screenHeight - anchorView.getWidth() - Utils.getHeightPercent(1f);
                        break;
                }
            }
            popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, x, y);
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public static <VB extends ViewBinding> void showPopupMenu(View anchorView,
                                                              Function<LayoutInflater, VB> bindingInflater,
                                                              int width,
                                                              int height,
                                                              int locationX,
                                                              int locationY,OnViewBinder<VB> event) {
        PopupWindow popupWindow;
        LayoutInflater inflater = LayoutInflater.from(anchorView.getContext());
        VB popupView = bindingInflater.apply(inflater);

        View rootView = popupView.getRoot();
        popupWindow = new PopupWindow(rootView, width, height, true);
        event.onView(popupView,popupWindow);
        popupWindow.showAtLocation(anchorView, Gravity.NO_GRAVITY, locationX, locationY);

    }

    public interface OnViewBinder<VB extends ViewBinding>{
        void onView(VB binding,PopupWindow popup);
    }
}


