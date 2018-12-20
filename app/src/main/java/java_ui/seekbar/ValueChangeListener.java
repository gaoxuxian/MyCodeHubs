package java_ui.seekbar;

import android.view.MotionEvent;

/**
 * @author Gxx
 * Created by Gxx on 2018/10/22.
 */
public interface ValueChangeListener<T extends SemiFinishedSeekBar>
{
    void onValueChange(T seekBar, float value, float lastValue, MotionEvent event);
}
