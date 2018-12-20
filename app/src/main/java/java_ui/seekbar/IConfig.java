package java_ui.seekbar;

public interface IConfig extends Cloneable
{
    @interface DataType
    {
        int type_int = 0;
        int type_float = 1;
    }

    @interface PointDrawType
    {
        int self = 0; // 自己控制参数画
        int resource = 1; // 画图
    }

    @interface PointColorType
    {
        int fixed_one_color = 0; // 固定一种颜色
        int gradient = 1; // 渐变色
    }
}
