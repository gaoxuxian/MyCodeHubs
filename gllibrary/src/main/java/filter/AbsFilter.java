package filter;

/**
 * @author Gxx
 * Created by Gxx on 2018/12/24.
 */
public interface AbsFilter<T extends FilterType>
{
    T getFilterType();
}
