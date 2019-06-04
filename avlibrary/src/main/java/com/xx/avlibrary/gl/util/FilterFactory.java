package com.xx.avlibrary.gl.util;

import android.content.Context;
import com.xx.avlibrary.gl.filter.*;
import com.xx.avlibrary.gl.filter.common.BmpToTextureFilter;
import com.xx.avlibrary.gl.filter.common.DisplayImageFilter;
import com.xx.avlibrary.gl.filter.innovation.GhostingFilter;
import com.xx.avlibrary.gl.filter.transitions.*;

/**
 * @author Gxx
 * Created by Gxx on 2019/1/17.
 */
public class FilterFactory {
    public static GPUImageFilter createImageFilter(Context context, GPUFilterType type) {
        if (type != null) {
            switch (type) {
                case BITMAP_TRANSFORM_TEXTURE:
                    return new BmpToTextureFilter(context);

                case DISPLAY:
                    return new DisplayImageFilter(context);
            }
        }

        return null;
    }

    public static GPUImageTransitionFilter createTransitionFilter(Context context, GPUTransitionFilterType type) {
        if (type != null) {
            switch (type) {
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

    public static GPUImageAnimFilter createImageAnimFilter(Context context, GPUAnimFilterType type) {
        if (type != null) {
            switch (type) {
                case GHOSTING:
                    return new GhostingFilter(context);
            }
        }

        return null;
    }
}
