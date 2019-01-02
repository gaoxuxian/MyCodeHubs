precision highp float;

uniform sampler2D vTextureBack;
uniform sampler2D vTextureFront;
uniform float progress;
uniform float zoom_quickness;

varying vec2 aCoordinate;

vec2 zoom(vec2 uv, float amount){
    return .5 + (uv - .5) * (1. - amount);
}

float quick(float quickness){
    return clamp(quickness, .2, 1.);
}

void main() {
    vec4 frontColor = texture2D(vTextureFront, zoom(aCoordinate, smoothstep(0., quick(zoom_quickness), progress)));
    vec4 backColor = texture2D(vTextureBack, aCoordinate);
    gl_FragColor = mix(frontColor, backColor, smoothstep(quick(zoom_quickness)-.2, 1., progress));
}