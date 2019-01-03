package filter.transitions;

import android.content.Context;

import filter.GPUFilterType;
import filter.GPUImageTransitionFilter;

/**
 * 矩形平滑转场
 * @author Gxx
 * Created by Gxx on 2019/1/2.
 */
public class SmoothnessTransitionFilter extends GPUImageTransitionFilter
{
    public SmoothnessTransitionFilter(Context context)
    {
        super(context);
    }

    @Override
    public GPUFilterType getFilterType()
    {
        return null;
    }
}
