precision mediump float;

uniform sampler2D vTexture;
uniform float vFactor;
varying vec2 aCoordinate;

float funy(float y){
    return (y + 0.0) / 1.25;
}

void main(){
    vec2 tCoordinate = aCoordinate;
    vec4 tColor = texture2D(vTexture,tCoordinate);
    float x = tCoordinate.x - 0.5;
    float y = tCoordinate.y - 0.5;

    float tAlpha = smoothstep(-0.26, 0.26, (funy(y) + vFactor - x));
    gl_FragColor = vec4(tColor.r, tColor.g, tColor.b, tAlpha);
}