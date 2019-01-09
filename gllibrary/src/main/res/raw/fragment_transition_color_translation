precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
varying vec2 aCoordinate;

void main(){
    vec2 uv = aCoordinate - vec2(.5);

    // first step width 20%
    float first_step = 1. - step(0.4, progress);
    float first_progress = smoothstep(0., 0.4, progress) * first_step;
    float first_sx = -0.8 + 1.3 * first_progress;
    float intensity = step(1., (1. - step(first_sx, uv.x)) + step(first_sx + 0.2, uv.x)) * first_step;

    // second step width 60%
    float second_step = (1. - step(0.8, progress)) * (1. - first_step);
    float second_progress = smoothstep(0.4, 0.8, progress) * second_step;
    float second_sx = -1.2 + 1.8 * second_progress;
    intensity += step(1., (1. - step(second_sx, uv.x)) + step(second_sx + 0.6, uv.x)) * second_step;

    // third step width 100%
    float third_step = step(0.8, progress) * (1. - second_step);
    float third_progress = smoothstep(0.8, 1., progress) * third_step;
    float third_sx = -1.5 + 2. * third_progress;
    intensity += step(1., step(third_sx + 1., uv.x)) * third_step;

    vec4 frontColor = texture2D(vTextureFront, aCoordinate);
    vec4 backColor = texture2D(vTextureBack, aCoordinate);
    float grayColor = backColor.r * .3 + backColor.g * .59 + backColor.b * .11;
    vec4 backColorFinal = vec4(progress * backColor.r + (1. - progress) * grayColor,
                               progress * backColor.g + (1. - progress) * grayColor,
                               progress * backColor.b + (1. - progress) * grayColor, backColor.a);

    gl_FragColor = mix(frontColor, backColorFinal, 1. - intensity);
}