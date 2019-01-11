precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
varying vec2 aCoordinate;

void main(){

    vec2 uv = aCoordinate;

    // first step width 50%
    float rate = pow(progress, 2.2);
    float first_step = 1. - step(0.7, rate);
    float first_rate = smoothstep(0., 0.7, rate) * first_step;
    float first_sx = -.6 + 1.6 * first_rate;

    float show_front_color = 1. * first_step;
    float show_front_gray_color = 0. * first_step;
    float intensity = step(1., (1. - step(first_sx, uv.x)) + step(first_sx + 0.6, uv.x)) * first_step;

    // second step width 40%
    float second_step = (1. - step(1., rate)) * (1. - first_step);
    float second_rate = smoothstep(0.7, 1., rate) * second_step;
    float second_sx = -0.4 + 1.4 * second_rate;

    show_front_gray_color += step(second_sx, uv.x) * second_step;;
    show_front_color += step(second_sx + .4, uv.x) * second_step;
    intensity += (1. - step(second_sx, uv.x)) * second_step;

    // third step
    float third_step = step(1., rate) * (1. - second_step);
    show_front_color += 0. * third_step;
    show_front_gray_color += 0. * third_step;
    intensity += 1. * third_step;

    // color
    vec4 frontColor = texture2D(vTextureFront, aCoordinate);
    vec4 backColor = texture2D(vTextureBack, aCoordinate);

    float inte = smoothstep(0.2, 0.6, rate);
    float grayF = frontColor.r * .3 + frontColor.g * .59 + frontColor.b * .11;
    vec4 frontGrayColor = vec4(mix(frontColor.r, grayF, inte),mix(frontColor.g, grayF, inte),mix(frontColor.b, grayF, inte), frontColor.a);

    float inteB = smoothstep(0.1, 0.5, rate);
    float grayB = backColor.r * .3 + backColor.g * .59 + backColor.b * .11;
    vec4 backGrayColor = vec4(mix(grayB, backColor.r, inteB), mix(grayB, backColor.g, inteB), mix(grayB, backColor.b, inteB), backColor.a);

    gl_FragColor = mix(mix(frontGrayColor, backGrayColor, 1. - show_front_color), mix(frontGrayColor, backGrayColor, 1. - show_front_gray_color), 1. - intensity);

}