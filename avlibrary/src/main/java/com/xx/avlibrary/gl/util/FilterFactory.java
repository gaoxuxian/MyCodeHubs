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

                case SPIN:
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

                case ROTATE_WHITE:
                    return new RotatingWhiteTransitionFilter(context);

                case ROTATE_ZOOM:
                    return new RotationTransitionFilter(context);

                case JUST_EXTEND:
                    return new ExtendTransitionFilter(context);

                case NOISE_BLUR_ZOOM:
                    return new NoiseBlurZoomTransitionFilter(context);

                case MOTION_BLUR:
                    return new MotionBlurTransitionFilter(context);

                case MOVE_X_RIGHT:
                    return new MoveTransitionFilter(context, 1, 0);

                case MOVE_X_LEFT:
                    return new MoveTransitionFilter(context, -1,0);

                case MOVE_Y_UP:
                    return new MoveTransitionFilter(context, 0, 1);

                case MOVE_Y_DOWN:
                    return new MoveTransitionFilter(context, 0, -1);

                case MOTION_ZOOM_OUT_ZOOM_IN:
                    return new MotionZoomTransitionFilter(context, 1);

                case MOTION_ZOOM_IN_ZOOM_OUT:
                    return new MotionZoomTransitionFilter(context, -1);

                case RADIAL_BLUR_ZOOM_OUT:
                    return new RadialBlurTransitionFilter(context, 1);

                case RADIAL_BLUR_ZOOM_IN:
                    return new RadialBlurTransitionFilter(context, -1);

                case ROTATE_ZOOM_V2:
                    return new RotationTransitionFilterV2(context);

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
