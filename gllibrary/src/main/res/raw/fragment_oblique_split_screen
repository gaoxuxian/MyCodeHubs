precision mediump float;

uniform sampler2D vTexture;
uniform float vFactor; // y = kx + b 的k
uniform float vOffset; // y = kx + b 的b
uniform float vFlip; // 纹理坐标跟顶点坐标是不是上下翻转关系, 1是，-1不是
uniform float vFuzzyRang; // 模糊范围

varying vec2 aCoordinate;

float funy(float y, float factor, float offset){
    return (y - (-vFlip * offset)) / (-vFlip * factor);
}

void main(){
    vec2 tCoordinate = aCoordinate;
    vec4 tColor = texture2D(vTexture,tCoordinate);
    vec2 center = tCoordinate - vec2(0.5, 0.5);
    float tAlpha = smoothstep(-vFuzzyRang, vFuzzyRang, (funy(center.y, vFactor, vOffset) - center.x));
    gl_FragColor = vec4(tColor.r, tColor.g, tColor.b, tAlpha);
}