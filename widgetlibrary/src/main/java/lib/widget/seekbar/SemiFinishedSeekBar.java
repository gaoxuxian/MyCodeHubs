package lib.widget.seekbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;

public abstract class SemiFinishedSeekBar<T extends IConfig, E extends ValueChangeListener> extends View
{
    private boolean mCanTriggerTouchEvent;
    private boolean mEventLock;
    protected T mConfig;
    protected E mListener;

    protected Paint mPaint;
    protected Matrix mMatrix;

    // 当前可以滑动的最大值
    protected float mCanTouchMaxValue;
    // 当前可以滑动的最小值
    protected float mCanTouchMinValue;

    protected float mLastValue;
    protected float mCurrentValue;

    public SemiFinishedSeekBar(Context context)
    {
        super(context);
        onInitBaseData();
    }

    public abstract void setSelectedValue(float value);

    public abstract float getLastValue();

    public abstract float getCurrentValue();

    protected abstract void oddDown(MotionEvent event);

    protected abstract void oddMove(MotionEvent event);

    protected abstract void oddUp(MotionEvent event);

    protected abstract void onDrawToCanvas(Canvas canvas);

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mEventLock) return true;

        switch (event.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:
            {
                mCanTriggerTouchEvent = true;
                oddDown(event);
                break;
            }

            case MotionEvent.ACTION_MOVE:
            {
                if (mCanTriggerTouchEvent)
                {
                    oddMove(event);
                }
                break;
            }

            case MotionEvent.ACTION_OUTSIDE:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
            {
                if (mCanTriggerTouchEvent)
                {
                    oddUp(event);
                }
                break;
            }

            case MotionEvent.ACTION_POINTER_DOWN:
            {
                mCanTriggerTouchEvent = false;
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        onDrawToCanvas(canvas);
    }

    public void setEventLock(boolean lock)
    {
        mEventLock = lock;
    }

    public void update()
    {
        invalidate();
    }

    protected void onInitBaseData()
    {
        mPaint = new Paint();
        mMatrix = new Matrix();
        mCanTriggerTouchEvent = true;
        setEventLock(false);
        resetCanTouchMaxMinValue();
    }

    public void onClear()
    {
        mListener = null;
    }

    /**
     * 设置当前可以被滑动的最大值
     */
    public void setCanTouchMaxValue(float max)
    {
        mCanTouchMaxValue = max;
    }

    /**
     * 设置当前可以被滑动的最小值
     */
    public void setCanTouchMinValue(float min)
    {
        mCanTouchMinValue = min;
    }

    /**
     * 将可以滑动的最大值小值恢复至默认
     */
    public void resetCanTouchMaxMinValue()
    {
        mCanTouchMaxValue = Float.MAX_VALUE;
        mCanTouchMinValue = -mCanTouchMaxValue;
    }

    public void setConfig(T config)
    {
        mConfig = config;
    }

    public T getConfig()
    {
        return mConfig;
    }

    protected boolean isConfigAvailable()
    {
        return mConfig != null;
    }

    public void setValueChangeListener(E listener)
    {
        mListener = listener;
    }

    protected boolean isValueChangeListenerAvailable()
    {
        return mListener != null;
    }

    protected E getValueChangeListener()
    {
        return mListener;
    }
}
