precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
varying vec2 aCoordinate;

void main(){

    vec2 block = floor(aCoordinate.xy / vec2(16.));
    vec2 uv_noise = block / vec2(64.);
    uv_noise += floor(vec2(progress) * vec2(1200.0, 3500.0)) / vec2(64.);
    vec2 dist = progress > 0.0 ? (fract(uv_noise) - 0.5) * 0.1 * smoothstep(0.15, 0.9, 1.0 - progress) : vec2(0.0);
    vec2 red = aCoordinate + dist * 0.2;
    vec2 green = aCoordinate + dist * .3;
    vec2 blue = aCoordinate + dist * .5;

    float colorR = mix(texture2D(vTextureFront, red), texture2D(vTextureBack, red), progress).r;
    float colorG = mix(texture2D(vTextureFront, green), texture2D(vTextureBack, green), progress).g;
    float colorB = mix(texture2D(vTextureFront, blue), texture2D(vTextureBack, blue), progress).b;

    gl_FragColor = vec4(colorR, colorG, colorB, 1.0);
}