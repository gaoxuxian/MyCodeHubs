precision mediump float;

uniform sampler2D vTexture;
varying vec2 aCoordinate;

void main() {
    vec4 tGray = texture2D(vTexture, vec2(aCoordinate.x/2.0, aCoordinate.y));
    vec4 tColor = texture2D(vTexture, vec2(aCoordinate.x/2.0 + 0.5, aCoordinate.y));
    gl_FragColor = vec4(tColor.rgb, tGray.r);
}