package filter.innovation;

import android.content.Context;

import filter.GPUFilterType;
import filter.GPUImageFilter;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/29.
 */
public class ZoomFilter extends GPUImageFilter
{
    public ZoomFilter(Context context)
    {
        super(context);
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return null;
    }
}
