precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
uniform float iTime;
uniform float vOffset;
uniform float power; // = 2
varying vec2 aCoordinate;

void main(){
    float fd = (1.0 - abs(mod(iTime, 2.0) - 1.0)) * vOffset;
    vec4 fc0 = texture2D(vTextureFront,aCoordinate);
    vec4 fc1 = texture2D(vTextureFront,aCoordinate + vec2(fd,0.0));
    vec4 fc2 = texture2D(vTextureFront,aCoordinate - vec2(fd,0.0));
    vec4 frontColor = mix(mix(fc0, fc1, 0.5), fc2, 1.0/3.0);

    float bd = (1.0 - abs(mod(iTime, 2.0) - 1.0)) * vOffset;
    vec4 bc0 = texture2D(vTextureBack,aCoordinate);
    vec4 bc1 = texture2D(vTextureBack,aCoordinate + vec2(bd,0.0));
    vec4 bc2 = texture2D(vTextureBack,aCoordinate - vec2(bd,0.0));
    vec4 backColor = mix(mix(bc0, bc1, 0.5), bc2, 1.0/3.0);

    float m = step(distance(frontColor, backColor), smoothstep(0.224, 0.866, progress));

    gl_FragColor = mix(mix(frontColor, backColor, m), backColor, pow(progress, power));
}