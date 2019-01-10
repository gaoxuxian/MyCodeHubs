package filter;


public enum GPUFilterType implements FilterType {
    NONE(0),
    OBLIQUE_PLUS(1), // y = x 分屏滤镜
    BITMAP_TRANSFORM_TEXTURE(2), // android 位图转纹理
    DISPLAY(3), // 显示

    TRANSITION_NONE(4),
    TRANSITION_ZOOM(5), // 转场-变焦
    TRANSITION_SMOOTHNESS(6), // 转场-矩形渐变平滑
    TRANSITION_TRANSLATION(7), // 转场-平移
    TRANSITION_SPREAD_ROUND(8), // 转场-扩散圆
    TRANSITION_SHAKE(9), // 转场-抖动+rgb分离
    TRANSITION_PERLIN(10), // 转场-佩尔林算法
    TRANSITION_PAGING(11), // 转场-翻页
    TRANSITION_FUZZY(12), // 转场-模糊
    TRANSITION_ROTATE(13), // 转场-旋转
    TRANSITION_CIRCLE_CROP(14), // 转场-可视范围以圆半径变化
    TRANSITION_LUMINANCE_MELT(15), // 转场-亮度融化
    TRANSITION_COLOR_DISTANCE(16), // 转场-色彩渐变

    GHOSTING(17), // 重影

    TRANSITION_SQUARE_ANIM(18), // 转场-方块动画
    TRANSITION_COLOR_GHOSTING(19), // 转场-色彩渐变 + 重影
    TRANSITION_COLOR_TRANSLATION(20), // 转场 - 色彩平移
    TRANSITION_COLOR_TRANSLATION_V2(21), // 转场 - 色彩平移v2
    TRANSITION_PARTICLES(22), // 转场 - 颗粒感
    TRANSITION_TEAR(23), // 转场 - 撕裂感
    TRANSITION_FUZZY_ZOOM(24), // 转场 - 模糊放大
    TRANSITION_SINGLE_DRAG(25), // 转场 - 单方向拉扯
    TRANSITION_RANDOM_SQUARE(26), // 转场 - 随机方块
    ;

    private int mValue;

    GPUFilterType(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static GPUFilterType getType(int id) {
        if (id <= 0) {
            return NONE;
        }

        GPUFilterType out = NONE;

        GPUFilterType[] values = values();

        for (GPUFilterType type : values) {
            if (type.getValue() == id) {
                out = type;
                break;
            }
        }

        return out;
    }
}
