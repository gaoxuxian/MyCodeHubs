package java_ui.seekbar;

import android.content.Context;
import android.graphics.*;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;

/**
 * @author Gxx
 * Created by Gxx on 2018/10/22.
 */
public class HorLineSeekBar extends SemiFinishedSeekBar<HorLineConfig, ValueChangeListener<HorLineSeekBar>>
{
    private Bitmap mZeroPointBmp;
    private Bitmap mSetPointBmp;
    private Bitmap mPresetPointBmp;

    // 原点中心
    private float mZeroPointX;
    private float mZeroPointY;

    // 动点中心
    private float mSetPointX;
    private float mSetPointY;

    private LinearGradient mProgressBarGradientColor;
    private String mValueText;

    public HorLineSeekBar(Context context)
    {
        super(context);
    }

    /**
     * @param value 具体数值
     */
    @Override
    public void setSelectedValue(float value)
    {
        mLastValue = mCurrentValue;
        mCurrentValue = value;
        mValueText = countValueText(getConfig(), value);
    }

    @Override
    public void setConfig(HorLineConfig config)
    {
        super.setConfig(config);

        if (config != null)
        {
            mCurrentValue = config.mSelectedValue;
            mValueText = countValueText(config, mCurrentValue);

            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            if ((measuredWidth > 0 && measuredHeight > 0))
            {
                HorLineConfig.Progress progress = config.mProgress;
                if (progress != null && progress.mBgColorArr != null && progress.mBgColorArr.length > 1)
                {
                    initProgressBarGradientColor(measuredWidth, measuredHeight, config);
                }
            }

            if (config.mZero != null && config.mZero.mType == HorLineConfig.PointDrawType.resource)
            {
                mZeroPointBmp = BitmapFactory.decodeResource(getResources(), config.mZero.mBmpResId);
            }

            if (config.mSetPoint != null && config.mSetPoint.mType == HorLineConfig.PointDrawType.resource)
            {
                mSetPointBmp = BitmapFactory.decodeResource(getResources(), config.mSetPoint.mBmpResId);
            }
            
            if (config.mPresetPoint != null && config.mPresetPoint.mType == HorLineConfig.PointDrawType.resource)
            {
                mPresetPointBmp = BitmapFactory.decodeResource(getResources(), config.mPresetPoint.mBmpResId);
            }
        }
    }

    @Override
    public float getCurrentValue()
    {
        if (isConfigAvailable())
        {
            HorLineConfig config = getConfig();
            if (config.mDataType == HorLineConfig.DataType.type_float)
            {
                return mCurrentValue;
            }
            else if (config.mDataType == HorLineConfig.DataType.type_int)
            {
                return Math.round(mCurrentValue);
            }
        }
        return 0;
    }

    @Override
    public float getLastValue()
    {
        if (isConfigAvailable())
        {
            HorLineConfig config = getConfig();
            if (config.mDataType == HorLineConfig.DataType.type_float)
            {
                return mLastValue;
            }
            else if (config.mDataType == HorLineConfig.DataType.type_int)
            {
                return Math.round(mLastValue);
            }
        }
        return 0;
    }

    @Override
    protected void oddDown(MotionEvent event)
    {
        countValueByXY(getConfig(), event.getX());

        if (isValueChangeListenerAvailable())
        {
            getValueChangeListener().onValueChange(this, getCurrentValue(), getLastValue(), event);
        }

        update();
    }

    @Override
    protected void oddMove(MotionEvent event)
    {
        countValueByXY(getConfig(), event.getX());

        if (isValueChangeListenerAvailable())
        {
            getValueChangeListener().onValueChange(this, getCurrentValue(), getLastValue(), event);
        }

        update();
    }

    @Override
    protected void oddUp(MotionEvent event)
    {
        countValueByXY(getConfig(), event.getX());

        if (isValueChangeListenerAvailable())
        {
            getValueChangeListener().onValueChange(this, getCurrentValue(), getLastValue(), event);
        }

        if (isConfigAvailable())
        {
            HorLineConfig config = getConfig();
            if (config.mDataType == HorLineConfig.DataType.type_int)
            {
                mLastValue = getLastValue();
                mCurrentValue = getCurrentValue();
            }
        }

        update();
    }

    @Override
    public void onClear()
    {
        super.onClear();

        mZeroPointBmp = null;
        mSetPointBmp = null;
        mPresetPointBmp = null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);

        if ((oldw != w || oldh != h) && isConfigAvailable())
        {
            initProgressBarGradientColor(w, h, getConfig());
        }
    }

    private void initProgressBarGradientColor(int viewW, int viewH, HorLineConfig config)
    {
        if (config != null && config.mProgress != null && config.mProgress.mBgColorArr != null && config.mProgress.mBgColorArr.length > 1)
        {
            float startX = config.mLeftMargin;
            float startY = viewH / 2 + config.mTranslationY;
            float endX = viewW - config.mLeftMargin;
            mProgressBarGradientColor = new LinearGradient(startX, startY, endX, startY, config.mProgress.mBgColorArr, null, Shader.TileMode.CLAMP);
        }
    }

    @Override
    protected void onDrawToCanvas(Canvas canvas)
    {
        if (isConfigAvailable())
        {
            HorLineConfig config = getConfig();

            // 画背景
            drawProgressBar(canvas, config);

            // 画原点
            if (config.mZero != null)
            {
                switch (config.mZero.mType)
                {
                    case HorLineConfig.PointDrawType.self:
                    {
                        drawZeroPointBySelf(canvas, config);
                        break;
                    }

                    case HorLineConfig.PointDrawType.resource:
                    {
                        drawZeroPointByRes(canvas, config);
                        break;
                    }
                }
            }

            // 画进度
            drawProgress(canvas, config);

            // 画预设点
            if (config.mPresetPoint != null)
            {
                switch (config.mPresetPoint.mType)
                {
                    case HorLineConfig.PointDrawType.self:
                    {
                        drawPresetPointBySelf(canvas, config);
                        break;
                    }

                    case HorLineConfig.PointDrawType.resource:
                    {
                        drawPresetPointByRes(canvas, config);
                        break;
                    }
                }
            }

            // 画动点
            if (config.mSetPoint != null)
            {
                switch (config.mSetPoint.mType)
                {
                    case HorLineConfig.PointDrawType.self:
                    {
                        drawSetPointBySelf(canvas, config);
                        break;
                    }

                    case HorLineConfig.PointDrawType.resource:
                    {
                        drawSetPointByRes(canvas, config);
                        break;
                    }
                }
            }

            // 画文字
            drawValueText(canvas, config);
        }
    }

    private void drawPresetPointByRes(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mShowPresetPoint && config.mPresetPoint != null && mPresetPointBmp != null && config.mZero != null)
        {
            HorLineConfig.PresetPoint point = config.mPresetPoint;
            HorLineConfig.ZeroPoint zero = config.mZero;

            int layer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            if (point.mColorType == HorLineConfig.PointColorType.gradient)
            {
                mPaint.setShader(mProgressBarGradientColor);
            }

            mMatrix.reset();
            float scale = Math.min((float) point.mBmpResW / mZeroPointBmp.getWidth(), (float) point.mRectH / mZeroPointBmp.getHeight());
            mMatrix.postScale(scale, scale);

            float percent = point.mPresetValue / config.mMaxValue;
            float x;
            float y = mZeroPointY;
            switch (zero.mLocation)
            {
                case HorLineConfig.ZeroLocation.middle:
                {
                    x = mZeroPointX + (getMeasuredWidth() / 2f - config.mLeftMargin) * percent;
                    break;
                }

                default:
                {
                    x = mZeroPointX + (getMeasuredWidth() - config.mLeftMargin * 2f) * percent;
                }
            }

            mMatrix.postTranslate(x - point.mBmpResW / 2f, y - point.mBmpResH / 2f);
            canvas.drawBitmap(mPresetPointBmp, mMatrix, mPaint);
            if (point.mColorType == HorLineConfig.PointColorType.fixed_one_color)
            {
                canvas.drawColor(point.mColor, PorterDuff.Mode.SRC_IN);
            }
            canvas.restoreToCount(layer);
        }
    }

    private void drawPresetPointBySelf(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mShowPresetPoint && config.mPresetPoint != null && config.mZero != null)
        {
            HorLineConfig.PresetPoint point = config.mPresetPoint;
            HorLineConfig.ZeroPoint zero = config.mZero;

            canvas.save();
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            switch (point.mColorType)
            {
                case HorLineConfig.PointColorType.fixed_one_color:
                {
                    mPaint.setColor(point.mColor);
                    break;
                }

                case HorLineConfig.PointColorType.gradient:
                {
                    mPaint.setShader(mProgressBarGradientColor);
                    break;
                }
            }

            float percent = point.mPresetValue / config.mMaxValue;
            float x;
            float y = mZeroPointY;
            switch (zero.mLocation)
            {
                case HorLineConfig.ZeroLocation.middle:
                {
                    x = mZeroPointX + (getMeasuredWidth() / 2f - config.mLeftMargin) * percent;
                    break;
                }

                default:
                {
                    x = mZeroPointX + (getMeasuredWidth() - config.mLeftMargin * 2f) * percent;
                }
            }

            switch (point.mShape)
            {
                case HorLineConfig.PointShape.circle:
                {
                    canvas.drawCircle(x, y, point.mCircleRadius, mPaint);
                    break;
                }

                case HorLineConfig.PointShape.rect:
                {
                    canvas.drawRect(x - point.mRectW / 2f, y - point.mRectH / 2f, x + point.mRectW / 2f, y + point.mRectH / 2f, mPaint);
                    break;
                }
            }
            canvas.restore();
        }
    }

    private void drawSetPointByRes(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mSetPoint != null && mSetPointBmp != null && !mSetPointBmp.isRecycled())
        {
            HorLineConfig.SetPoint point = config.mSetPoint;
            canvas.save();
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            switch (point.mType)
            {
                case HorLineConfig.PointColorType.fixed_one_color:
                {
                    mPaint.setColor(point.mColor);
                    break;
                }

                case HorLineConfig.PointColorType.gradient:
                {
                    mPaint.setShader(mProgressBarGradientColor);
                    break;
                }
            }
            mMatrix.reset();
            float x = mSetPointX - point.mPointWH / 2f;
            float y = mSetPointY - point.mPointWH / 2f;
            float scale = Math.min(point.mPointWH / mSetPointBmp.getWidth(), point.mPointWH / mSetPointBmp.getHeight());
            mMatrix.postScale(scale, scale);
            mMatrix.postTranslate(x, y);
            canvas.drawBitmap(mSetPointBmp, mMatrix, mPaint);
            canvas.restore();
        }
    }

    private void drawZeroPointByRes(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mZero != null && mZeroPointBmp != null)
        {
            HorLineConfig.ZeroPoint zero = config.mZero;

            int layer = canvas.saveLayer(null, null, Canvas.ALL_SAVE_FLAG);
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

            mMatrix.reset();
            float scale = Math.min((float) zero.mBmpResW / mZeroPointBmp.getWidth(), (float) zero.mRectH / mZeroPointBmp.getHeight());
            mMatrix.postScale(scale, scale);
            
            float x;
            float y = getMeasuredHeight() / 2f + config.mTranslationY;
            switch (zero.mLocation)
            {
                case HorLineConfig.ZeroLocation.middle:
                {
                    x = getMeasuredWidth() / 2f;
                    break;
                }

                default:
                {
                    x = config.mLeftMargin;
                }
            }
            mZeroPointX = x;
            mZeroPointY = y;
            mMatrix.postTranslate(x - zero.mBmpResW / 2f, y - zero.mBmpResH / 2f);
            canvas.drawBitmap(mZeroPointBmp, mMatrix, mPaint);
            canvas.drawColor(zero.mColor, PorterDuff.Mode.SRC_IN);
            canvas.restoreToCount(layer);
        }
    }

    private void drawZeroPointBySelf(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mZero != null)
        {
            HorLineConfig.ZeroPoint zero = config.mZero;

            canvas.save();
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mPaint.setColor(zero.mColor);

            float x;
            float y = getMeasuredHeight() / 2f + config.mTranslationY;

            switch (zero.mLocation)
            {
                case HorLineConfig.ZeroLocation.middle:
                {
                    x = getMeasuredWidth() / 2f;
                    break;
                }

                default:
                {
                    x = config.mLeftMargin;
                }
            }

            mZeroPointX = x;
            mZeroPointY = y;

            switch (zero.mShape)
            {
                case HorLineConfig.PointShape.circle:
                {
                    canvas.drawCircle(x, y, zero.mCircleRadius, mPaint);
                    break;
                }

                case HorLineConfig.PointShape.rect:
                {
                    canvas.drawRect(x - zero.mRectW / 2f, y - zero.mRectH / 2f, x + zero.mRectW / 2f, y + zero.mRectH / 2f, mPaint);
                    break;
                }
                case HorLineConfig.PointShape.rect_round:
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        canvas.drawRoundRect(x - zero.mRectW / 2f, y - zero.mRectH / 2f, x + zero.mRectW / 2f, y + zero.mRectH / 2f, zero.mRectRadius, zero.mRectRadius, mPaint);
                    } else {
                        canvas.drawRect(x - zero.mRectW / 2f, y - zero.mRectH / 2f, x + zero.mRectW / 2f, y + zero.mRectH / 2f, mPaint);
                    }
                    break;
                }
            }
            canvas.restore();
        }
    }

    private void drawValueText(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mValueText != null && config.mSetPoint != null)
        {
            HorLineConfig.ValueText valueText = config.mValueText;
            HorLineConfig.SetPoint setPoint = config.mSetPoint;
            canvas.save();

            if (config.mShowSelectedValue && !TextUtils.isEmpty(this.mValueText))
            {
                mPaint.reset();
                mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
                mPaint.setTextSize(valueText.mTextSize);
                mPaint.setColor(valueText.mColor);
                float textW = mPaint.measureText(this.mValueText, 0, this.mValueText.length());
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                float x = mSetPointX - textW / 2f;
                float y = mSetPointY - setPoint.mPointWH / 2f - valueText.mDistanceToSetPoint - fontMetrics.descent;
                canvas.drawText(this.mValueText, x, y, mPaint);
            }

            canvas.restore();
        }
    }

    private void drawSetPointBySelf(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mSetPoint != null)
        {
            HorLineConfig.SetPoint point = config.mSetPoint;
            canvas.save();
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            switch (point.mColorType)
            {
                case HorLineConfig.PointColorType.fixed_one_color:
                {
                    mPaint.setColor(point.mColor);
                    break;
                }

                case HorLineConfig.PointColorType.gradient:
                {
                    mPaint.setShader(mProgressBarGradientColor);
                    break;
                }
            }
            canvas.drawCircle(mSetPointX, mSetPointY, point.mPointWH / 2f, mPaint);
            canvas.restore();
        }
    }

    private void drawProgress(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mZero != null && config.mProgress != null)
        {
            canvas.save();
            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mPaint.setColor(config.mProgress.mColor);
            mPaint.setStrokeWidth(config.mProgress.mLineWidth);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            float startX = mZeroPointX;
            float startY = mZeroPointY;
            float endX;
            float percent = mCurrentValue / config.mMaxValue;
            switch (config.mZero.mLocation)
            {
                case HorLineConfig.ZeroLocation.middle:
                {
                    endX = mZeroPointX + (getMeasuredWidth() / 2f - config.mLeftMargin) * percent;
                    break;
                }

                default:
                {
                    endX = mZeroPointX + (getMeasuredWidth() - config.mLeftMargin * 2f) * percent;
                }
            }
            mSetPointX = endX;
            mSetPointY = startY;
            if (config.mShowProgress)
            {
                canvas.drawLine(startX, startY, endX, startY, mPaint);
            }
            canvas.restore();
        }
    }

    private void drawProgressBar(Canvas canvas, HorLineConfig config)
    {
        if (config != null && config.mProgress != null && config.mProgress.mBgColorArr != null)
        {
            HorLineConfig.Progress progress = config.mProgress;
            canvas.save();
            int measuredWidth = getMeasuredWidth();
            int measuredHeight = getMeasuredHeight();

            int lineWidth = measuredWidth - config.mLeftMargin * 2;
            float x = config.mLeftMargin;
            float y = measuredHeight / 2 + config.mTranslationY;

            int length = progress.mBgColorArr.length;

            mPaint.reset();
            mPaint.setFlags(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
            mPaint.setStrokeWidth(progress.mLineWidth);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            if (length == 1)
            {
                mPaint.setColor(progress.mBgColorArr[0]);
            }
            else if (length > 1)
            {
                mPaint.setShader(mProgressBarGradientColor);
            }
            canvas.drawLine(x, y, x + lineWidth, y, mPaint);
            canvas.restore();
        }
    }

    private void countValueByXY(HorLineConfig config, float x)
    {
        if (config != null)
        {
            float minX = config.mLeftMargin;
            float maxX = getMeasuredWidth() - config.mLeftMargin;
            float realW = getMeasuredWidth() - config.mLeftMargin * 2f;

            if (x < minX)
            {
                x = minX;
            }
            else if (x > maxX)
            {
                x = maxX;
            }

            float percent = (x - minX) / realW;

            if (config.mZero != null)
            {
                switch (config.mZero.mLocation)
                {
                    case HorLineConfig.ZeroLocation.middle:
                    {
                        percent = percent * 2f - 1f;
                        break;
                    }
                }
            }

            mLastValue = mCurrentValue;
            float value = config.mMaxValue * percent;
            int valueInt = (int) (value * 100);
            value = (float) valueInt / 10f;
            valueInt = Math.round(value);
            mCurrentValue = (float) valueInt / 10f;

            mValueText = countValueText(config, mCurrentValue);
        }
    }

    private String countValueText(HorLineConfig config, float value)
    {
        String out = null;
        if (config != null && config.mShowSelectedValue)
        {
            if (value > 0)
            {
                if (config.mShowValuePlusLogo)
                {
                    if (config.mDataType == HorLineConfig.DataType.type_int)
                    {
                        int round = Math.round(value);
                        if (round == 0){
                            out = "0";
                        }else {
                            out = "+" + Math.round(value);
                        }
                    }
                    else
                    {
                        out = "+" + value;
                    }
                }
                else
                {
                    if (config.mDataType == HorLineConfig.DataType.type_int)
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
                if (config.mDataType == HorLineConfig.DataType.type_int)
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
                if (config.mDataType == HorLineConfig.DataType.type_int)
                {
                    out = String.valueOf(0);
                }
                else
                {
                    out = String.valueOf(0.0);
                }

            }
        }
        return out;
    }
}
