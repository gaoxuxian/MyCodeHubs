package lib.gl.util;

import android.content.Context;

import lib.gl.filter.GPUAnimFilterType;
import lib.gl.filter.GPUFilterType;
import lib.gl.filter.GPUImageAnimFilter;
import lib.gl.filter.GPUImageFilter;
import lib.gl.filter.GPUImageTransitionFilter;
import lib.gl.filter.GPUTransitionFilterType;
import lib.gl.filter.common.BmpToTextureFilter;
import lib.gl.filter.common.DisplayImageFilter;
import lib.gl.filter.innovation.GhostingFilter;
import lib.gl.filter.transitions.CircleCropTransitionFilter;
import lib.gl.filter.transitions.ColorGhostingTransitionFilter;
import lib.gl.filter.transitions.ColorScanTransitionFilter;
import lib.gl.filter.transitions.ColorScanTransitionFilterV2;
import lib.gl.filter.transitions.ColourDistanceTransitionFilter;
import lib.gl.filter.transitions.FuzzyTransitionFilter;
import lib.gl.filter.transitions.FuzzyZoomTransitionFilter;
import lib.gl.filter.transitions.LuminanceMeltTransitionFilter;
import lib.gl.filter.transitions.PagingTransitionFilter;
import lib.gl.filter.transitions.ParticlesTransitionFilter;
import lib.gl.filter.transitions.PerlinTransitionFilter;
import lib.gl.filter.transitions.RandomSquaresTransitionFilter;
import lib.gl.filter.transitions.RotateTransitionFilter;
import lib.gl.filter.transitions.ShakeTransitionFilter;
import lib.gl.filter.transitions.SingleDragTransitionFilter;
import lib.gl.filter.transitions.SmoothnessTransitionFilter;
import lib.gl.filter.transitions.SpreadRoundTransitionFilter;
import lib.gl.filter.transitions.SquareAnimTransitionFilter;
import lib.gl.filter.transitions.TearTransitionFilter;
import lib.gl.filter.transitions.TranslationTransitionFilter;
import lib.gl.filter.transitions.ZoomTransitionFilter;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/17.
 */
public class FilterFactory
{
    public static GPUImageFilter createImageFilter(Context context, GPUFilterType type)
    {
        if (type != null)
        {
            switch (type)
            {
                case BITMAP_TRANSFORM_TEXTURE:
                    return new BmpToTextureFilter(context);

                case DISPLAY:
                    return new DisplayImageFilter(context);
            }
        }

        return null;
    }

    public static GPUImageTransitionFilter createTransitionFilter(Context context, GPUTransitionFilterType type)
    {
        if (type != null)
        {
            switch (type)
            {
                case ZOOM:
                    return new ZoomTransitionFilter(context);

                case SMOOTHNESS:
                    return new SmoothnessTransitionFilter(context);

                case TRANSLATION:
                    return new TranslationTransitionFilter(context);

                case SPREAD_ROUND:
                    return new SpreadRoundTransitionFilter(context);

                case SHAKE:
                    return new ShakeTransitionFilter(context);

                case PERLIN:
                    return new PerlinTransitionFilter(context);

                case PAGING:
                    return new PagingTransitionFilter(context);

                case FUZZY:
                    return new FuzzyTransitionFilter(context);

                case ROTATE:
                    return new RotateTransitionFilter(context);

                case CIRCLE_CROP:
                    return new CircleCropTransitionFilter(context);

                case LUMINANCE_MELT:
                    return new LuminanceMeltTransitionFilter(context);

                case COLOR_DISTANCE:
                    return new ColourDistanceTransitionFilter(context);

                case SQUARE_ANIM:
                    return new SquareAnimTransitionFilter(context);

                case COLOR_GHOSTING:
                    return new ColorGhostingTransitionFilter(context);

                case COLOR_SCAN:
                    return new ColorScanTransitionFilter(context);

                case COLOR_SCAN_V2:
                    return new ColorScanTransitionFilterV2(context);

                case PARTICLES:
                    return new ParticlesTransitionFilter(context);

                case TEAR:
                    return new TearTransitionFilter(context);

                case FUZZY_ZOOM:
                    return new FuzzyZoomTransitionFilter(context);

                case SINGLE_DRAG:
                    return new SingleDragTransitionFilter(context);

                case RANDOM_SQUARE:
                    return new RandomSquaresTransitionFilter(context);

                default:
                    return new GPUImageTransitionFilter(context);
            }
        }

        return null;
    }

    public static GPUImageAnimFilter createImageAnimFilter(Context context, GPUAnimFilterType type)
    {
        if (type != null)
        {
            switch (type)
            {
                case GHOSTING:
                    return new GhostingFilter(context);
            }
        }

        return null;
    }
}
