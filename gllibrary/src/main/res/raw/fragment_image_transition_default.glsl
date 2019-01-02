precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
varying vec2 aCoordinate;

void main(){
    vec4 frontColor = texture2D(vTextureFront, aCoordinate);
    vec4 backColor = texture2D(vTextureBack, aCoordinate);
    gl_FragColor = mix(frontColor, backColor, smoothstep(0., 1., progress));
}