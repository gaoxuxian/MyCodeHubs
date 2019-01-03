precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
uniform vec2 direction; // = vec2(0.0, 1.0)

varying vec2 aCoordinate;

void main(){
    vec2 p = aCoordinate + progress * sign(direction);
    vec2 f = fract(p);

    vec4 frontColor = texture2D(vTextureFront, f);
    vec4 backColor = texture2D(vTextureBack, f);

    gl_FragColor = mix(frontColor, backColor, step(0.0, p.y) * step(p.y, 1.0) * step(0.0, p.x) * step(p.x, 1.0));
}