precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
const float intensity = 0.1; // = 0.1
const int passes = 5;
varying vec2 aCoordinate;

void main(){
    vec4 c1 = vec4(0.0);
    vec4 c2 = vec4(0.0);

    float disp = intensity*(0.5-distance(0.5, progress));
    for (int xi=0; xi <= passes; xi++)
    {
        float x = float(xi) / float(passes) - 0.5;
        for (int yi=0; yi <= passes; yi++)
        {
            float y = float(yi) / float(passes) - 0.5;
            vec2 v = vec2(x,y);
            c1 += texture2D(vTextureFront, aCoordinate + disp*v);
            c2 += texture2D(vTextureBack, aCoordinate + disp*v);
        }
    }
    c1 /= float((passes + 1)*(passes + 1));
    c2 /= float((passes + 1)*(passes + 1));
    gl_FragColor = mix(c1, c2, progress);
}