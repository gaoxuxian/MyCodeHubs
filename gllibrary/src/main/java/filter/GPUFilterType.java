package filter;


public enum GPUFilterType implements FilterType {
    NONE(0),
    OBLIQUE_PLUS(1), // y = x 分屏滤镜
    BITMAP_TRANSFORM_TEXTURE(2), // android 位图转纹理
    DISPLAY(3), // 显示
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
