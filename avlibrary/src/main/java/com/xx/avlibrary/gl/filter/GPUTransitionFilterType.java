package com.xx.avlibrary.gl.filter;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/17.
 */
public enum GPUTransitionFilterType implements FilterType
{
    NONE(0),
    ZOOM(1), // 转场-变焦
    SMOOTHNESS(2), // 转场-矩形渐变平滑
    TRANSLATION(3), // 转场-平移
    SPREAD_ROUND(4), // 转场-扩散圆
    SHAKE(5), // 转场-抖动+rgb分离
    PERLIN(6), // 转场-佩尔林算法
    PAGING(7), // 转场-翻页
    FUZZY(8), // 转场-模糊
    SPIN(9), // 转场-自旋
    CIRCLE_CROP(10), // 转场-可视范围以圆半径变化
    LUMINANCE_MELT(11), // 转场-亮度融化
    COLOR_DISTANCE(12), // 转场-色彩渐变

    SQUARE_ANIM(13), // 转场-方块动画
    COLOR_GHOSTING(14), // 转场-色彩渐变 + 重影
    COLOR_SCAN(15), // 转场 - 色彩扫描
    COLOR_SCAN_V2(16), // 转场 - 色彩扫描v2
    PARTICLES(17), // 转场 - 颗粒感
    TEAR(18), // 转场 - 撕裂感
    FUZZY_ZOOM(19), // 转场 - 模糊放大
    SINGLE_DRAG(20), // 转场 - 单方向拉扯
    RANDOM_SQUARE(21), // 转场 - 随机方块
    ROTATE_WHITE(22), // 转场 - 旋转闪白

    ROTATE_ZOOM(23), // 转场 - 放大+旋转
    JUST_EXTEND(24), // 转场 - 纯缩放
    NOISE_BLUR_ZOOM(25), // 转场 - 虚化缩放

    MOTION_BLUR(26), // 转场 - 旋转运动模糊

    MOVE_X_RIGHT(27), // 转场 - 右移动
    MOVE_X_LEFT(28), // 转场 - 左移动
    MOVE_Y_UP(29), // 转场 - 上移动
    MOVE_Y_DOWN(30), // 转场 - 下移动

    MOTION_ZOOM_OUT_ZOOM_IN(31), // 转场 - 运动伸缩-先缩小后放大-整体是放大效果
    MOTION_ZOOM_IN_ZOOM_OUT(32), // 转场 - 运动伸缩-先放大后缩小-整体是缩小效果

    RADIAL_BLUR_ZOOM_OUT(33), // 转场 - 径向模糊缩小
    RADIAL_BLUR_ZOOM_IN(34), // 转场 - 径向模糊放大
    ;
    private int mValue;

    GPUTransitionFilterType(int value)
    {
        mValue = value;
    }

    public int getValue()
    {
        return mValue;
    }

    public static GPUTransitionFilterType getType(int id)
    {
        if (id <= 0)
        {
            return NONE;
        }

        GPUTransitionFilterType out = NONE;

        GPUTransitionFilterType[] values = values();

        for (GPUTransitionFilterType type : values)
        {
            if (type.getValue() == id)
            {
                out = type;
                break;
            }
        }

        return out;
    }
}
