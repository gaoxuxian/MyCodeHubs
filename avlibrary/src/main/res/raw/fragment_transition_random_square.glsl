precision highp float;

uniform sampler2D vTextureBack;
uniform sampler2D vTextureFront;
uniform float progress;
uniform ivec2 size; // = ivec2(5, 5)
uniform float smoothness; // = 0.5

varying vec2 aCoordinate;

float rand (vec2 co) {
  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

void main(){
    vec2 uv = aCoordinate;
    float rate = smoothstep(0.2, 1., pow(progress, 0.5));
    float r = rand(floor(vec2(size) * uv));
    float m = smoothstep(0.0, -smoothness, r - (rate * (1.0 + smoothness)));
    gl_FragColor = mix(texture2D(vTextureFront, uv), texture2D(vTextureBack, uv), m);
}