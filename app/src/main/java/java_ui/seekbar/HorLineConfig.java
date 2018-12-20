package java_ui.seekbar;

import android.graphics.Color;

public class HorLineConfig implements IConfig
{
    /*
    For example:

        IConfig config = SeekBarConfigFactory.createConfig(SeekBarConfigFactory.ConfigType.horizontal_line);
        if (config instanceof HorLineConfig)
        {
            HorLineConfig.ZeroPoint zeroPoint = new HorLineConfig.ZeroPoint();
            zeroPoint.mLocation = HorLineConfig.ZeroLocation.start;
            zeroPoint.mType = HorLineConfig.PointDrawType.self;
            zeroPoint.mColor = Color.WHITE;
            zeroPoint.mShape = HorLineConfig.PointShape.none;
            // zeroPoint.mCircleRadius = PercentUtil.WidthPxxToPercent(6);

            HorLineConfig.SetPoint setPoint = new HorLineConfig.SetPoint();
            setPoint.mColorType = HorLineConfig.PointColorType.gradient;
            setPoint.mColor = Color.WHITE;
            setPoint.mPointWH = PercentUtil.WidthPxxToPercent(50);
            setPoint.mType = HorLineConfig.PointDrawType.self;

            HorLineConfig.PresetPoint presetPoint = new HorLineConfig.PresetPoint();
            presetPoint.mColorType = HorLineConfig.PointColorType.fixed_one_color;
            presetPoint.mColor = Color.WHITE;
            presetPoint.mType = HorLineConfig.PointDrawType.self;
            presetPoint.mShape = HorLineConfig.PointShape.none;
            presetPoint.mRectW = PercentUtil.WidthPxxToPercent(4);
            presetPoint.mRectH = PercentUtil.HeightPxxToPercent(20);
            presetPoint.mPresetValue = 7;

            HorLineConfig.Progress progress = new HorLineConfig.Progress();
            progress.mBgColorArr = new int[]{0xffe8251b, 0xffffb5b4, 0xffe30c4c};
            // progress.mBgColorArr = new int[]{Color.GRAY};
            progress.mColor = Color.WHITE;
            progress.mLineWidth = PercentUtil.HeightPxxToPercent(5);

            HorLineConfig.ValueText valueText = new HorLineConfig.ValueText();
            valueText.mDistanceToSetPoint = PercentUtil.HeightPxxToPercent(10);
            valueText.mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            valueText.mColor = Color.WHITE;

            ((HorLineConfig) config).mZero = zeroPoint;
            ((HorLineConfig) config).mSetPoint = setPoint;
            ((HorLineConfig) config).mPresetPoint = presetPoint;
            ((HorLineConfig) config).mProgress = progress;
            ((HorLineConfig) config).mValueText = valueText;

            ((HorLineConfig) config).mMinValue = -10;
            ((HorLineConfig) config).mMaxValue = 10;
            ((HorLineConfig) config).mSelectedValue = 4;
            ((HorLineConfig) config).mDataType = HorLineConfig.DataType.type_float;
            ((HorLineConfig) config).mLeftMargin = PercentUtil.WidthPxxToPercent(100);
            ((HorLineConfig) config).mTranslationY = PercentUtil.WidthPxxToPercent(0);
            ((HorLineConfig) config).mShowProgress = false;
            ((HorLineConfig) config).mShowSelectedValue = true;
            ((HorLineConfig) config).mShowValuePlusLogo = false;
        }
     */

    public @interface PointShape
    {
        int none = 0;
        int circle = 1;
        int rect = 2;
        int rect_round = 3;
    }

    public @interface ZeroLocation
    {
        int start = 0;
        int middle = 1;
    }

    HorLineConfig()
    {

    }

    // =================================== 必须参数 =============================== //
    public float mMinValue = 0;

    public float mMaxValue = 100;

    public ZeroPoint mZero; // 零点

    public SetPoint mSetPoint; // 调节点

    public PresetPoint mPresetPoint; // 预设点, 为null相当于没有预设

    public Progress mProgress; // 进度

    public ValueText mValueText; // 数值文案

    /**
     * 选中的数值, 具体数据, 与 {@link HorLineConfig#mDataType} 有关
     */
    public float mSelectedValue;

    /**
     * 选中的数据的类型 {@link DataType}
     */
    public int mDataType;

    public int mLeftMargin; // 与左边缘的距离

    // =================================== 非必须参数 ============================= //

    public boolean mShowSelectedValue; // 是否显示选中的数值文案

    public boolean mShowValuePlusLogo; // 如果 数值文案 > 0 ，是否显示 + 号

    public float mTranslationY; // 默认是居中view 画，+往下偏，-往上偏

    public boolean mShowProgress = true; // 是否显示进度

    public boolean mShowPresetPoint = true; //是否显示默认点

    public HorLineConfig cloneTo()
    {
        HorLineConfig config = new HorLineConfig();

        config.mMaxValue = this.mMaxValue;
        config.mMinValue = this.mMinValue;
        config.mSelectedValue = this.mSelectedValue;
        config.mDataType = this.mDataType;
        config.mLeftMargin = this.mLeftMargin;

        if (this.mZero != null)
        {
            config.mZero = this.mZero.cloneTo();
        }

        if (this.mSetPoint != null)
        {
            config.mSetPoint = this.mSetPoint.cloneTo();
        }

        if (this.mPresetPoint != null)
        {
            config.mPresetPoint = this.mPresetPoint.cloneTo();
        }

        if (this.mProgress != null)
        {
            config.mProgress = this.mProgress.cloneTo();
        }

        if (this.mValueText != null)
        {
            config.mValueText = this.mValueText.cloneTo();
        }

        // 非必须参数
        config.mShowValuePlusLogo = this.mShowValuePlusLogo;
        config.mShowSelectedValue = this.mShowSelectedValue;
        config.mTranslationY = this.mTranslationY;
        config.mShowProgress = this.mShowProgress;
        config.mShowPresetPoint = this.mShowPresetPoint;

        return config;
    }

    public static class ZeroPoint
    {
        /**
         * 零位置, {@link ZeroLocation}
         */
        public int mLocation = ZeroLocation.start;

        public int mType = PointDrawType.self; // 画法

        public int mBmpResId; // 图片资源

        public int mShape = PointShape.none;// 形状

        public int mColor = Color.WHITE;

        // 如果是画圆
        public float mCircleRadius;

        // 如果是画矩形
        public int mRectW;
        public int mRectH;

        // 如果是画矩形
        public float mRectRadius;

        // 如果是资源文件
        public int mBmpResW;
        public int mBmpResH;

        public ZeroPoint cloneTo()
        {
            ZeroPoint out = new ZeroPoint();

            out.mLocation = this.mLocation;
            out.mType = this.mType;
            out.mBmpResId = this.mBmpResId;
            out.mShape = this.mShape;
            out.mColor = this.mColor;
            out.mCircleRadius = this.mCircleRadius;
            out.mRectRadius = this.mRectRadius;
            out.mRectW = this.mRectW;
            out.mRectH = this.mRectH;
            out.mBmpResW = this.mBmpResW;
            out.mBmpResH = this.mBmpResH;

            return out;
        }
    }

    public static class SetPoint
    {
        public int mType = PointDrawType.self; // 画法

        public int mBmpResId;

        /**
         * 可操作的点的颜色, 该参数与 {@link SetPoint#mColorType} 有关
         */
        public int mColor;

        public float mPointWH;

        /**
         * 可操作的点的颜色类型 {@link PointColorType}
         * <p>
         * 若类型是 {@link PointColorType#gradient}, {@link SetPoint#mColor} 将没有意义
         * <p>
         * 会根据 {@link Progress#mBgColorArr} 和 实际操作过程中，选中的位置判断渐变色
         */
        public int mColorType = PointColorType.fixed_one_color;

        public SetPoint cloneTo()
        {
            SetPoint out = new SetPoint();

            out.mType = this.mType;
            out.mBmpResId = this.mBmpResId;
            out.mColor = this.mColor;
            out.mColorType = this.mColorType;
            out.mPointWH = this.mPointWH;

            return out;
        }
    }

    public static class PresetPoint
    {
        public int mType = PointDrawType.self; // 画法

        public int mBmpResId;

        public float mPresetValue; // 预设值

        public int mShape = PointShape.none;// 形状

        /**
         * 可操作的点的颜色, 该参数与 {@link PresetPoint#mColorType} 有关
         */
        public int mColor;

        // 如果是画圆
        public float mCircleRadius;

        // 如果是画矩形
        public int mRectW;
        public int mRectH;

        // 如果是资源文件
        public int mBmpResW;
        public int mBmpResH;

        /**
         * 可操作的点的颜色类型 {@link PointColorType}
         * <p>
         * 若类型是 {@link PointColorType#gradient}, {@link PresetPoint#mColor} 将没有意义
         * <p>
         * 会根据 {@link Progress#mBgColorArr} 和 实际操作过程中，选中的位置判断渐变色
         */
        public int mColorType = PointColorType.fixed_one_color;

        public PresetPoint cloneTo()
        {
            PresetPoint out = new PresetPoint();

            out.mType = this.mType;
            out.mBmpResId = this.mBmpResId;
            out.mPresetValue = this.mPresetValue;
            out.mColor = this.mColor;
            out.mColorType = this.mColorType;
            out.mCircleRadius = this.mCircleRadius;
            out.mRectW = this.mRectW;
            out.mRectH = this.mRectH;
            out.mBmpResW = this.mBmpResW;
            out.mBmpResH = this.mBmpResH;

            return out;
        }
    }

    public static class Progress
    {
        /**
         * 进度条底色, 会按数量在进度条上以百分比的区域绘制颜色
         */
        public int[] mBgColorArr = new int[]{Color.GRAY};

        public int mLineWidth = 10;

        // 进度颜色
        public int mColor = Color.WHITE;

        public Progress cloneTo()
        {
            Progress out = new Progress();

            out.mBgColorArr = this.mBgColorArr;
            out.mLineWidth = this.mLineWidth;
            out.mColor = this.mColor;

            return out;
        }
    }

    public static class ValueText
    {
        public float mDistanceToSetPoint = 10; // 点与数值间的距离

        public float mTextSize = 10;

        public int mColor = Color.WHITE;

        public ValueText cloneTo()
        {
            ValueText out = new ValueText();

            out.mDistanceToSetPoint = this.mDistanceToSetPoint;
            out.mTextSize = this.mTextSize;
            out.mColor = this.mColor;

            return out;
        }
    }
}
