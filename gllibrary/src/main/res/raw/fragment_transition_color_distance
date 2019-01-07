precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
uniform float power; // = 5.0
varying vec2 aCoordinate;

void main(){
    vec4 frontColor = texture2D(vTextureFront, aCoordinate);
    vec4 backColor = texture2D(vTextureBack, aCoordinate);
    float m = step(distance(frontColor, backColor), progress);

    gl_FragColor = mix(mix(frontColor, backColor, m), backColor, pow(progress, power));
}