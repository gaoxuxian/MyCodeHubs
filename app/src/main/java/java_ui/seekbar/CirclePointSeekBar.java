package java_ui.seekbar;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.graphics.*;
import android.text.TextUtils;
import android.view.MotionEvent;

public class CirclePointSeekBar extends SemiFinishedSeekBar<CirclePointConfig, ValueChangeListener<CirclePointSeekBar>>
{
    // 原点
    private Bitmap mZeroPointBmp;
    private Bitmap mPointBmp;
    private Bitmap mMovablePointBmp;

    // 如果画图要叠加颜色
    private PorterDuffColorFilter[] mColorFilterArr;
    // 可移动的点, 中心点的 x，y
    private float mMovablePointX;
    private float mMovablePointY;

    // 如果 config 配置了 可移动圆 的颜色是渐变，需要用到此值
    private int mMovablePointGradientColor;

    // 文案
    private String mValueText;

    private boolean mInit;

    public CirclePointSeekBar(Context context)
    {
        super(context);
        mInit = true;
    }

    /**
     * @param value 注意: 该值非 index，是具体的值，受 config 定义的 原点位置、点的总数 影响
     */
    @Override
    public void setSelectedValue(float value)
    {
        if (isConfigAvailable())
        {
            CirclePointConfig config = getConfig();
            int minValue = 0 - config.mZeroIndex;
            int maxValue = config.mPointSum - 1 - config.mZeroIndex;

            if (value < minValue)
            {
                value = minValue;
            }
            else if (value > maxValue)
            {
                value = maxValue;
            }

            PointF pointF = countMovablePointByValue(config, value);
            mMovablePointX = pointF.x;
            mMovablePointY = pointF.y;
        }
    }

    @Override
    public void setConfig(CirclePointConfig config)
    {
        super.setConfig(config);

        mColorFilterArr = null;

        if (config != null)
        {
            mCurrentValue = config.mSelectedValue;

            if (config.mPointDrawType == CirclePointConfig.PointDrawType.resource)
            {
                mPointBmp = BitmapFactory.decodeResource(getResources(), config.mPointBmpResId);
                if (config.mPointColorArr != null)
                {
                    int length = config.mPointColorArr.length;
                    mColorFilterArr = new PorterDuffColorFilter[length];
                    for (int i = 0; i < length; i++)
                    {
                        mColorFilterArr[i] = new PorterDuffColorFilter(config.mPointColorArr[i], PorterDuff.Mode.SRC_IN);
                    }
                }
            }

            if (config.mZeroPointDrawType == CirclePointConfig.PointDrawType.resource)
            {
                mZeroPointBmp = BitmapFactory.decodeResource(getResources(), config.mZeroPointBmpResId);
            }

            if (config.mMovableDrawType == CirclePointConfig.PointDrawType.resource)
            {
                mMovablePointBmp = BitmapFactory.decodeResource(getResources(), config.mMovableBmpResId);
            }
        }
    }

    @Override
    public float getCurrentValue()
    {
        return mCurrentValue;
    }

    @Override
    public float getLastValue()
    {
        return mLastValue;
    }

    public int getTotal()
    {
        return isConfigAvailable() ? getConfig().mPointSum : 0;
    }

    public int getZeroIndex()
    {
        return isConfigAvailable() ? getConfig().mZeroIndex : -1;
    }

    private String countValueText(CirclePointConfig config, float value)
    {
        String out = null;
        if (config != null && config.mShowSelectedValue)
        {
            if (value > 0)
            {
                if (config.mShowValuePlusLogo)
                {
                    if (config.mDataType == CirclePointConfig.DataType.type_int)
                    {
                        out = "+" + Math.round(value);
                    }
                    else
                    {
                        out = "+" + value;
                    }
                }
                else
                {
                    if (config.mDataType == CirclePointConfig.DataType.type_int)
                    {
                        out = String.valueOf(Math.round(value));
                    }
                    else
                    {
                        out = String.valueOf(value);
                    }
                }
            }
            else if (value < 0)
            {
                if (config.mDataType == CirclePointConfig.DataType.type_int)
                {
                    out = String.valueOf(Math.round(value));
                }
                else
                {
                    out = String.valueOf(value);
                }
            }
            else
            {
                out = String.valueOf(0);
            }
        }
        return out;
    }

    private PointF countMovablePointByValue(CirclePointConfig config, float selectedValue)
    {
        PointF out = new PointF();

        if (config != null)
        {
            switch (config.mDataType)
            {
                case CirclePointConfig.DataType.type_float:
                {
                    int max = config.mPointSum - 1 - config.mZeroIndex;
                    int min = 0 - config.mZeroIndex;
                    float percent = (selectedValue - min) / (float) (max - min);
                    float realW = config.mPointW * (config.mPointSum -1) + config.mDistanceBetweenPointAndPoint * (config.mPointSum - 1);
                    out.x = config.mLeftMargin + config.mPointW / 2f + realW * percent;
                    out.y = getMeasuredHeight() / 2f + config.mPointsTranslationY;
                    if (config.mMovablePointColorType == CirclePointConfig.PointColorType.gradient)
                    {
                        int ceil = (int) Math.ceil(selectedValue);
                        int floor = (int) Math.floor(selectedValue);
                        int ceilIndex;
                        int floorIndex;
                        float fraction;
                        if (ceil == floor)
                        {
                            // 同一个数
                            ceilIndex = ceil + config.mZeroIndex;
                            floorIndex = ceilIndex;
                            fraction = 1;
                        }
                        else
                        {
                            if (floor > ceil)
                            {
                                floor = floor + ceil;
                                ceil = floor - ceil;
                                floor = floor - ceil;
                            }
                            ceilIndex = ceil + config.mZeroIndex;
                            floorIndex = floor + config.mZeroIndex;
                            fraction = Math.abs(selectedValue - floor);
                        }
                        ArgbEvaluator argbEvaluator = new ArgbEvaluator();
                        mMovablePointGradientColor = (int) argbEvaluator.evaluate(fraction, config.mPointColorArr[floorIndex], config.mPointColorArr[ceilIndex]);
                    }

                    mValueText = countValueText(config, selectedValue);
                    mLastValue = mCurrentValue;
                    mCurrentValue = selectedValue;
                    break;
                }

                case CirclePointConfig.DataType.type_int:
                {
                    int value = (int) selectedValue;
                    int index = value + config.mZeroIndex;
                    if (config.mPointSum > 0)
                    {
                        if (index < 0)
                        {
                            index = 0;
                        }
                        else if (index >= config.mPointSum)
                        {
                            index = config.mPointSum - 1;
                        }

                        out.x = config.mLeftMargin + config.mPointW * index + config.mDistanceBetweenPointAndPoint * index + config.mPointW / 2f;
                        out.y = getMeasuredHeight() / 2f + config.mPointsTranslationY;

                        if (config.mMovablePointColorType == CirclePointConfig.PointColorType.gradient)
                        {
                            if (index < config.mPointColorArr.length)
                            {
                                mMovablePointGradientColor = config.mPointColorArr[index];
                            }
                        }
                    }
                    mValueText = countValueText(config, value);
                    mLastValue = mCurrentValue;
                    mCurrentValue = value;
                    break;
                }
            }
        }
        return out;
    }

    private PointF countMovablePointXY(CirclePointConfig config, float x)
    {
        PointF out = new PointF();

        if (config != null)
        {
            float minX = config.mLeftMargin + config.mPointW / 2f;
            float maxX = config.mLeftMargin + config.mPointW * config.mPointSum + config.mDistanceBetweenPointAndPoint * (config.mPointSum - 1) - config.mPointW / 2f;
            float realW = config.mPointW * (config.mPointSum -1) + config.mDistanceBetweenPointAndPoint * (config.mPointSum - 1);

            if (x < minX)
            {
                x = minX;
            }
            else if (x > maxX)
            {
                x = maxX;
            }

            if (config.mDataType == CirclePointConfig.DataType.type_float)
            {
                float percent = (x - minX) / realW;
                int minValue = 0 - config.mZeroIndex;
                int maxValue = config.mPointSum - 1 - config.mZeroIndex;
                float value = minValue + (maxValue - minValue) * percent;
                int valueInt = (int) (value * 100);
                value = (float) valueInt / 10f;
                valueInt = Math.round(value);
                value = (float) valueInt / 10f;
                if (value >= mCanTouchMaxValue)
                {
                    value = mCanTouchMaxValue;
                }
                else if (value <= mCanTouchMinValue)
                {
                    value = mCanTouchMinValue;
                }

                return countMovablePointByValue(config, value);
            }
            else if (config.mDataType == CirclePointConfig.DataType.type_int)
            {
                float percent = (x - minX) / realW;
                int minValue = 0 - config.mZeroIndex;
                int maxValue = config.mPointSum - 1 - config.mZeroIndex;
                float value = minValue + (maxValue - minValue) * percent;

                boolean hasAchievedMaxOrMin = false; // 是否到达可滑动的最大值 或 最小值
                if (value >= mCanTouchMaxValue)
                {
                    value = (int) mCanTouchMaxValue;
                    hasAchievedMaxOrMin = true;
                }
                else if (value <= mCanTouchMinValue)
                {
                    value = (int) mCanTouchMinValue;
                    hasAchievedMaxOrMin = true;
                }

                int intValue = Math.round(value);

                mValueText = countValueText(config, intValue);
                mLastValue = mCurrentValue;
                mCurrentValue = intValue;

                int index = intValue + config.mZeroIndex;
                if (config.mPointSum > 0)
                {
                    if (index < 0)
                    {
                        index = 0;
                    }
                    else if (index >= config.mPointSum)
                    {
                        index = config.mPointSum - 1;
                    }
                    if (config.mMovablePointColorType == CirclePointConfig.PointColorType.gradient)
                    {
                        if (index < config.mPointColorArr.length)
                        {
                            mMovablePointGradientColor = config.mPointColorArr[index];
                        }
                    }
                }
                if (hasAchievedMaxOrMin)
                {
                    out.x = config.mLeftMargin + config.mPointW * (index + 1) + config.mDistanceBetweenPointAndPoint * index - config.mPointW / 2f;
                    out.y = getMeasuredHeight() / 2f + config.mPointsTranslationY;
                }
                else
                {
                    out.x = x;
                    out.y = getMeasuredHeight() / 2f + config.mPointsTranslationY;
                }
            }
        }
        return out;
    }

    @Override
    protected void oddDown(MotionEvent event)
    {
        PointF pointF = countMovablePointXY(getConfig(), event.getX());
        mMovablePointX = pointF.x;
        mMovablePointY = pointF.y;

        if (isValueChangeListenerAvailable())
        {
            getValueChangeListener().onValueChange(this, mCurrentValue, mLastValue, event);
        }

        update();
    }

    @Override
    protected void oddMove(MotionEvent event)
    {
        PointF pointF = countMovablePointXY(getConfig(), event.getX());

        mMovablePointX = pointF.x;
        mMovablePointY = pointF.y;

        if (isValueChangeListenerAvailable())
        {
            getValueChangeListener().onValueChange(this, mCurrentValue, mLastValue, event);
        }

        update();
    }

    @Override
    protected void oddUp(MotionEvent event)
    {
        if (isConfigAvailable())
        {
            CirclePointConfig config = getConfig();
            if (config.mDataType == CirclePointConfig.DataType.type_int)
            {
                // 自动吸附
                float x = event.getX();

                float minX = config.mLeftMargin + config.mPointW / 2f;
                float maxX = config.mLeftMargin + config.mPointW * config.mPointSum + config.mDistanceBetweenPointAndPoint * (config.mPointSum - 1) - config.mPointW / 2f;
                float realW = config.mPointW * (config.mPointSum -1) + config.mDistanceBetweenPointAndPoint * (config.mPointSum - 1);

                if (x < minX)
                {
                    x = minX;
                }
                else if (x > maxX)
                {
                    x = maxX;
                }

                float percent = (x - minX) / realW;
                int minValue = 0 - config.mZeroIndex;
                int maxValue = config.mPointSum - 1 - config.mZeroIndex;
                int value = Math.round(minValue + (maxValue - minValue) * percent);

                if (value >= mCanTouchMaxValue)
                {
                    value = (int) mCanTouchMaxValue;
                }
                else if (value <= mCanTouchMinValue)
                {
                    value = (int) mCanTouchMinValue;
                }

                PointF pointF = countMovablePointByValue(config, value);
                mMovablePointX = pointF.x;
                mMovablePointY = pointF.y;
                mLastValue = mCurrentValue;
                mCurrentValue = value;
                update();
            }
        }

        if (isValueChangeListenerAvailable())
        {
            getValueChangeListener().onValueChange(this, mCurrentValue, mLastValue, event);
        }
    }

    @Override
    protected void onDrawToCanvas(Canvas canvas)
    {
        if (isConfigAvailable())
        {
            int save = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);

            CirclePointConfig config = getConfig();
            // 先画点
            switch (config.mPointDrawType)
            {
                case CirclePointConfig.PointDrawType.resource:
                {
                    drawPointByRes(canvas, config);
                    break;
                }

                case CirclePointConfig.PointDrawType.self:
                {
                    drawPointBySelf(canvas, config);
                    break;
                }
            }

            // 再画原点
            switch (config.mZeroPointDrawType)
            {
                case CirclePointConfig.PointDrawType.resource:
                {
                    drawZeroPointByRes(canvas, config);
                    break;
                }

                case CirclePointConfig.PointDrawType.self:
                {
                    drawZeroPointBySelf(canvas, config);
                    break;
                }
            }

            // 再画可操作的点
            switch (config.mMovableDrawType)
            {
                case CirclePointConfig.PointDrawType.resource:
                {
                    drawMovablePointByRes(canvas, config);
                    break;
                }

                case CirclePointConfig.PointDrawType.self:
                {
                    drawMovablePointBySelf(canvas, config);
                    break;
                }
            }

            // 画文案
            drawValueText(canvas, config);

            canvas.restoreToCount(save);
        }
    }

    private void drawMovablePointBySelf(Canvas canvas, CirclePointConfig config)
    {
        if (config != null)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            switch (config.mMovablePointColorType)
            {
                case CirclePointConfig.PointColorType.fixed_one_color:
                {
                    mPaint.setColor(config.mMovablePointColor);
                    break;
                }

                case CirclePointConfig.PointColorType.gradient:
                {
                    mPaint.setColor(mMovablePointGradientColor);
                    break;
                }
            }
            canvas.drawCircle(mMovablePointX, mMovablePointY, config.mMovablePointWH / 2f, mPaint);
            canvas.restore();
        }
    }

    private void drawValueText(Canvas canvas, CirclePointConfig config)
    {
        if (config != null)
        {
            canvas.save();

            if (config.mShowSelectedValue && !TextUtils.isEmpty(mValueText))
            {
                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                mPaint.setTextSize(config.mValueTextSize);
                mPaint.setColor(config.mValueTextColor);
                float textW = mPaint.measureText(mValueText, 0, mValueText.length());
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float x = mMovablePointX - textW / 2f;
                float y = mMovablePointY - config.mMovablePointWH / 2f - config.mDistanceBetweenPointAndValue - fontMetrics.descent;
                canvas.drawText(mValueText, x, y, mPaint);
            }

            canvas.restore();
        }
    }

    private void drawZeroPointByRes(Canvas canvas, CirclePointConfig config)
    {
        if (config != null && mZeroPointBmp != null)
        {
            canvas.save();
            int viewH = getMeasuredHeight();
            int size = config.mPointSum;
            for (int i = 0; i < size; i++)
            {
                if (i == config.mZeroIndex)
                {
                    float x = config.mLeftMargin + config.mPointW * i + config.mDistanceBetweenPointAndPoint * i;
                    float scale = Math.min((float) config.mPointW / mZeroPointBmp.getWidth(), (float) config.mPointW / mZeroPointBmp.getHeight());
                    float bmpH = mZeroPointBmp.getHeight() * scale;
                    float y = viewH / 2f + config.mPointsTranslationY - bmpH / 2f;
                    mMatrix.reset();
                    mMatrix.postScale(scale, scale);
                    mMatrix.postTranslate(x, y);
                    mPaint.reset();
                    mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                    if (mColorFilterArr != null && i < mColorFilterArr.length)
                    {
                        mPaint.setColorFilter(mColorFilterArr[i]);
                    }
                    canvas.drawBitmap(mZeroPointBmp, mMatrix, mPaint);
                    break;
                }
            }
            canvas.restore();
        }
    }

    private void drawPointByRes(Canvas canvas, CirclePointConfig config)
    {
        if (config != null && mPointBmp != null)
        {
            canvas.save();
            int viewH = getMeasuredHeight();
            int size = config.mPointSum;
            float x;
            float y;
            for (int i = 0; i < size; i++)
            {
                if (i == config.mZeroIndex) continue;

                x = config.mLeftMargin + config.mPointW * i + config.mDistanceBetweenPointAndPoint * i;
                float scale = (float) config.mPointW / mPointBmp.getWidth();
                float bmpH = mPointBmp.getHeight() * scale;
                y = viewH / 2f + config.mPointsTranslationY - bmpH / 2f;
                mMatrix.reset();
                mMatrix.postScale(scale, scale);
                mMatrix.postTranslate(x, y);
                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                if (mColorFilterArr != null && i < mColorFilterArr.length)
                {
                    mPaint.setColorFilter(mColorFilterArr[i]);
                }
                canvas.drawBitmap(mPointBmp, mMatrix, mPaint);
            }
            canvas.restore();
        }
    }

    private void drawMovablePointByRes(Canvas canvas, CirclePointConfig config)
    {
        if (config != null && mMovablePointBmp != null && !mMovablePointBmp.isRecycled())
        {
            int layer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mMatrix.reset();
            float x = mMovablePointX - config.mMovablePointWH / 2f;
            float y = mMovablePointY - config.mMovablePointWH / 2f;
            float scale = Math.min((float) config.mMovablePointWH / mMovablePointBmp.getWidth(), (float) config.mMovablePointWH / mMovablePointBmp.getHeight());
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate(x, y);
            canvas.drawBitmap(mMovablePointBmp, mMatrix, mPaint);
            switch (config.mMovablePointColorType)
            {
                case CirclePointConfig.PointColorType.fixed_one_color:
                {
                    canvas.drawColor(config.mMovablePointColor, PorterDuff.Mode.SRC_IN);
                    break;
                }

                case CirclePointConfig.PointColorType.gradient:
                {
                    canvas.drawColor(mMovablePointGradientColor, PorterDuff.Mode.SRC_IN);
                    break;
                }
            }
            canvas.restoreToCount(layer);
        }
    }

    private void drawPointBySelf(Canvas canvas, CirclePointConfig config)
    {
        if (config != null)
        {
            canvas.save();
            int viewH = getMeasuredHeight();
            int size = config.mPointSum;
            float x;
            float y = viewH / 2f + config.mPointsTranslationY;
            for (int i = 0; i < size; i++)
            {
                if (i == config.mZeroIndex) continue;

                x = config.mLeftMargin + config.mPointW * i + config.mDistanceBetweenPointAndPoint * i + config.mPointW / 2f;
                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                if (config.mPointColorArr != null && i < config.mPointColorArr.length)
                {
                    mPaint.setColor(config.mPointColorArr[i]);
                }
                canvas.drawCircle(x, y, config.mPointW / 2f, mPaint);
            }
            canvas.restore();
        }
    }

    private void drawZeroPointBySelf(Canvas canvas, CirclePointConfig config)
    {
        if (config != null)
        {
            canvas.save();
            int viewH = getMeasuredHeight();
            int size = config.mPointSum;
            float x;
            float y = viewH / 2f + config.mPointsTranslationY;
            for (int i = 0; i < size; i++)
            {
                if (i != config.mZeroIndex) continue;

                x = config.mLeftMargin + config.mPointW * i + config.mDistanceBetweenPointAndPoint * i + config.mPointW / 2f;
                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                mPaint.setStyle(Paint.Style.FILL);
                if (config.mPointColorArr != null && i < config.mPointColorArr.length)
                {
                    mPaint.setColor(config.mPointColorArr[i]);
                }
                canvas.drawCircle(x, y, config.mPointW / 2f, mPaint);
                break;
            }
            canvas.restore();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if (mInit)
        {
            mInit = false;
            PointF pointF = countMovablePointByValue(getConfig(), mCurrentValue);
            mMovablePointX = pointF.x;
            mMovablePointY = pointF.y;
        }
    }

    @Override
    public void onClear()
    {
        super.onClear();

        mZeroPointBmp = null;
        mPointBmp = null;
        mMovablePointBmp = null;
    }
}