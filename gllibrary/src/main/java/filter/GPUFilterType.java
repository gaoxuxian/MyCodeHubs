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
