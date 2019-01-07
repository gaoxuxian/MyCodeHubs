precision mediump float;

uniform sampler2D vTexture;
uniform float iTime; // [0,1]
varying vec2 aCoordinate;

void main(){
    vec4 color = texture2D(vTexture,aCoordinate);
    gl_FragColor = vec4(color.rgb, iTime);
}