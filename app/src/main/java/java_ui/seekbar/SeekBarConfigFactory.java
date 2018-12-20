package java_ui.seekbar;

public class SeekBarConfigFactory
{
    public @interface ConfigType
    {
        int horizontal_line = 0;
        int circle_point = 1;
    }

    /**
     *
     * @param type {@link ConfigType}
     */
    public static IConfig createConfig(@ConfigType int type)
    {
        switch (type)
        {
            case ConfigType.circle_point:
            {
                return new CirclePointConfig();
            }

            case ConfigType.horizontal_line:
            {
                return new HorLineConfig();
            }

            default:
            {
                return new HorLineConfig();
            }
        }
    }
}
