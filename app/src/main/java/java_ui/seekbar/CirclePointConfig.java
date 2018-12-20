package java_ui.seekbar;

import android.graphics.Color;

public class CirclePointConfig implements IConfig
{
    /*
    For example:

        IConfig config = SeekBarConfigFactory.createConfig(SeekBarConfigFactory.ConfigType.circle_point);
        if (config instanceof CirclePointConfig)
        {
            // 必须参数
            ((CirclePointConfig) config).mPointSum = 11;
            ((CirclePointConfig) config).mZeroIndex = 5;
            ((CirclePointConfig) config).mSelectedValue = 3;
            ((CirclePointConfig) config).mDataType = CirclePointConfig.DataType.type_float;

            ((CirclePointConfig) config).mPointDrawType = CirclePointConfig.PointDrawType.resource;
            ((CirclePointConfig) config).mZeroPointDrawType = CirclePointConfig.PointDrawType.resource;
            ((CirclePointConfig) config).mMovableDrawType = CirclePointConfig.PointDrawType.resource;

            ((CirclePointConfig) config).mPointW = PixelPercentUtil.WidthPxxToPercent(22);
            ((CirclePointConfig) config).mMovablePointWH = PixelPercentUtil.WidthPxxToPercent(54);

            ((CirclePointConfig) config).mDistanceBetweenPointAndPoint = PixelPercentUtil.WidthPxxToPercent(72);
            ((CirclePointConfig) config).mLeftMargin = PixelPercentUtil.WidthPxxToPercent(59);

            // 非必须参数
            ((CirclePointConfig) config).mPointBmpResId = R.drawable.ic_rate_nor;
            ((CirclePointConfig) config).mZeroPointBmpResId = R.drawable.ic_rate_nor_original_double;
            ((CirclePointConfig) config).mMovableBmpResId = R.drawable.ic_rate_sel;

            ((CirclePointConfig) config).mMovablePointColorType = CirclePointConfig.PointColorType.gradient;
            ((CirclePointConfig) config).mMovablePointColor = Color.WHITE;
            ((CirclePointConfig) config).mPointColorArr = new int[]{
            0xffdd1611, 0xffe23a35, 0xffeb706a, 0xfff4a39d, 0xfffbd1cb,  Color.WHITE, 0xfffde4e9, 0xfff5bacc, 0xffee9bb5, 0xffdd4070, 0xffd4114d};

            ((CirclePointConfig) config).mShowValuePlusLogo = true;
            ((CirclePointConfig) config).mShowSelectedValue = true;
            ((CirclePointConfig) config).mValueTextColor = Color.WHITE;
            ((CirclePointConfig) config).mValueTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
            ((CirclePointConfig) config).mDistanceBetweenPointAndValue = PixelPercentUtil.HeightPxxToPercent(10);

            ((CirclePointConfig) config).mPointsTranslationY = 0;

            new CirclePointSeekBar(getContext()).setConfig((CirclePointConfig) config);;
        }
     */

    CirclePointConfig()
    {

    }

    // =================================== 必须参数 =============================== //
    public int mPointSum; // 点的总数

    public int mZeroIndex; // 原点下标，从 0 开始

    /**
     * 选中的数值, 并非下标，具体数据, 与 {@link CirclePointConfig#mDataType} 有关
     */
    public float mSelectedValue;

    /**
     * 选中的数据的类型 {@link DataType}
     */
    public int mDataType;

    public int mZeroPointDrawType; // 原点的画法

    public int mPointDrawType; // 点的画法

    public int mMovableDrawType; // 可操作点的画法

    public int mPointW; // 点的大小, 如果是画图片, 建议原点和其他点的宽度一致, 图片高度会根据宽度适配, 如果是根据参数画，这个是直径

    public int mMovablePointWH; // 可操作的点的大小

    public float mDistanceBetweenPointAndPoint; // 点与点的距离

    public int mLeftMargin; // 第一个点与左边缘的距离

    // =================================== 非必须参数 ============================= //

    public int mZeroPointBmpResId; // 原点的图片资源

    public int mPointBmpResId; // 点的图片资源

    public int mMovableBmpResId; // 可操作的图片资源

    /**
     * 点的颜色, 如果是画图片，非必须，如果是自己按参数画，必须
     */
    public int[] mPointColorArr;

    /**
     * 可操作的点的颜色类型 {@link PointColorType}
     * <p>
     * 若类型是 {@link PointColorType#gradient}, {@link CirclePointConfig#mMovablePointColor} 将没有意义
     * <p>
     * 会根据 {@link CirclePointConfig#mPointColorArr} 和 实际操作过程中，选中的位置判断渐变色
     */
    public int mMovablePointColorType = PointColorType.fixed_one_color;

    /**
     * 可操作的点的颜色, 该参数与 {@link CirclePointConfig#mMovablePointColorType} 有关
     */
    public int mMovablePointColor = Color.WHITE;

    public boolean mShowSelectedValue; // 是否显示选中的数值文案

    public boolean mShowValuePlusLogo; // 如果 数值文案 > 0 ，是否显示 + 号

    public float mDistanceBetweenPointAndValue = 10; // 点与数值间的距离

    public float mValueTextSize = 10;

    public int mValueTextColor = Color.WHITE;

    public float mPointsTranslationY; // 默认点是居中view 画，+往下偏，-往上偏

    public CirclePointConfig cloneTo()
    {
        CirclePointConfig config = new CirclePointConfig();

        config.mPointSum = this.mPointSum;
        config.mZeroIndex = this.mZeroIndex;
        config.mSelectedValue = this.mSelectedValue;
        config.mDataType = this.mDataType;

        config.mPointDrawType = this.mPointDrawType;
        config.mZeroPointDrawType = this.mZeroPointDrawType;
        config.mMovableDrawType = this.mMovableDrawType;

        config.mPointW = this.mPointW;
        config.mMovablePointWH = this.mMovablePointWH;

        config.mDistanceBetweenPointAndPoint = this.mDistanceBetweenPointAndPoint;
        config.mLeftMargin = this.mLeftMargin;

        // 非必须参数
        config.mPointBmpResId = this.mPointBmpResId;
        config.mZeroPointBmpResId = this.mZeroPointBmpResId;
        config.mMovableBmpResId = this.mMovableBmpResId;

        config.mMovablePointColorType = this.mMovablePointColorType;
        config.mMovablePointColor = this.mMovablePointColor;
        config.mPointColorArr = this.mPointColorArr;

        config.mShowValuePlusLogo = this.mShowValuePlusLogo;
        config.mShowSelectedValue = this.mShowSelectedValue;
        config.mValueTextColor = this.mValueTextColor;
        config.mValueTextSize = this.mValueTextColor;
        config.mDistanceBetweenPointAndValue = this.mDistanceBetweenPointAndValue;

        config.mPointsTranslationY = this.mPointsTranslationY;

        return config;
    }
}
