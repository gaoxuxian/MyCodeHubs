precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
uniform float ratio;
uniform vec4 bgColor;
varying vec2 aCoordinate;

void main(){
    vec2 ratio2 = vec2(1.0, 1.0 / ratio);
    float s = pow(2.0 * abs(progress - 0.5), 3.0);
//    float dist = pow(length((aCoordinate - 0.5) * ratio2), 2.); // 求对应点相对于原点的距离-平方(圆半径)
    float dist = dot((aCoordinate - 0.5) * ratio2, (aCoordinate - 0.5) * ratio2); // 等效上面公式
    float factor = dist < s ? 0. : 1.;

    gl_FragColor = mix(progress < 0.5 ? texture2D(vTextureFront, aCoordinate) : texture2D(vTextureBack, aCoordinate),
                       bgColor,
                       factor);
}