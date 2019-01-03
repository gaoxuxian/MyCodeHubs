precision highp float;

uniform sampler2D vTextureBack;
uniform sampler2D vTextureFront;
uniform float progress;
uniform float count; // = 10.0
uniform float smoothness; // = 0.5

varying vec2 aCoordinate;

void main() {
    float pr = smoothstep(-smoothness, 0.0, aCoordinate.x - progress * (1.0 + smoothness));
    float s = step(pr, fract(count * aCoordinate.x));

    vec4 frontColor = texture2D(vTextureFront, aCoordinate);
    vec4 backColor = texture2D(vTextureBack, aCoordinate);

    gl_FragColor = mix(frontColor, backColor, s);
}