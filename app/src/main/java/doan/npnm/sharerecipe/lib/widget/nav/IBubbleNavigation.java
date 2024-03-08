package doan.npnm.sharerecipe.lib.widget.nav;

import android.graphics.Typeface;
import doan.npnm.sharerecipe.lib.widget.nav.listener.BubbleNavigationChangeListener;

@SuppressWarnings("unused")
public interface IBubbleNavigation {
    void setNavigationChangeListener(BubbleNavigationChangeListener navigationChangeListener);

    void setTypeface(Typeface typeface);

    int getCurrentActiveItemPosition();

    void setCurrentActiveItem(int position);

    void setBadgeValue(int position, String value);
}
