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
