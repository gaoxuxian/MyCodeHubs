precision highp float;

#define CIRCLE_CENTER vec2(.5);

uniform sampler2D vTexture;
uniform float vTextureWidth;
uniform float vTextureHeight;
uniform vec4 maskColor;
varying vec2 aCoordinate;

// 等比圆
float findGeometricCircle(vec2 p) {
    float width = vTextureWidth;
    float height = vTextureHeight;
    vec2 ratio = vec2(1., height / width);
    vec2 dx = p - CIRCLE_CENTER;
    float dist = length(dx * ratio);
    float radius = min(width, height) * .5 / max(width, height);
    return step(radius, dist);
}

// 非等比圆
float findNonGeometricCircle(vec2 p) {
    vec2 dx = p - CIRCLE_CENTER;
    return step(.5, length(dx));
}

void main(){
    float circleArea = findGeometricCircle(aCoordinate);
    vec4 textureColor = texture2D(vTexture, aCoordinate);
    gl_FragColor = mix(textureColor, maskColor, circleArea);
}