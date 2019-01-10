precision highp float;

uniform sampler2D vTextureBack;
uniform sampler2D vTextureFront;
uniform float progress;
uniform float vStrength; // 0.6

varying vec2 aCoordinate;

const float PI = 3.141592653589793;

float rand (vec2 co) {
  return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float Sinusoidal_easeInOut(in float begin, in float change, in float duration, in float time) {
    return -change / 2.0 * (cos(PI * time / duration) - 1.0) + begin;
}

void main(){
    vec2 uv = aCoordinate;
    float rate = pow(progress, 2.5);
    vec2 coordinate = uv - vec2(.5);
    float offset = rand(coordinate);
    float strength = Sinusoidal_easeInOut(0., vStrength, 1., rate);
    float total = 0.0;
    vec4 color = vec4(0.0);
    for (float t = 0.0; t <= 50.0; t++) {
        float percent = (t + offset) / 20.0;
        float weight = 8.0 * (percent - percent * percent);
        color += texture2D(vTextureFront, uv - coordinate * percent * strength) * weight;
        total += weight;
    }
    gl_FragColor = mix(color / total, texture2D(vTextureBack, uv), smoothstep(0.5, 1., rate));
}