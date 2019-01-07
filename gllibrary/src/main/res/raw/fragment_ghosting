precision mediump float;

uniform sampler2D vTexture;
uniform float iTime;
uniform float vOffset; // = 0.1
varying vec2 aCoordinate;

void main(){
    vec2 uv = aCoordinate;
    float d = (1.0 - abs(mod(iTime, 2.0) - 1.0)) * vOffset;
    vec4 c0 = texture2D(vTexture,uv);
    vec4 c1 = texture2D(vTexture,uv + vec2(d,0.0));
    vec4 c2 = texture2D(vTexture,uv - vec2(d,0.0));
    gl_FragColor = mix(mix(c0, c1, 0.5), c2, 1.0/3.0);
}