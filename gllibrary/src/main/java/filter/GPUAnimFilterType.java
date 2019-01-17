package filter;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/17.
 */
public enum GPUAnimFilterType implements FilterType
{
    NONE(0),

    GHOSTING(1), // 重影
    ;
    private int mValue;

    GPUAnimFilterType(int value) {
        mValue = value;
    }

    public int getValue() {
        return mValue;
    }

    public static GPUAnimFilterType getType(int id) {
        if (id <= 0) {
            return NONE;
        }

        GPUAnimFilterType out = NONE;

        GPUAnimFilterType[] values = values();

        for (GPUAnimFilterType type : values) {
            if (type.getValue() == id) {
                out = type;
                break;
            }
        }

        return out;
    }
}
