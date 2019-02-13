precision highp float;

uniform sampler2D vTexture;
uniform float intensity;// default 0.236638
const int passes = 9;

varying vec2 aCoordinate;

void main() {
    vec4 tempColor = vec4(0.0);

    for (int xi = 0; xi <= passes; xi++) {
        float x = (float(xi) / float(passes) - 0.5) * 0.1; // -0.5是左右偏移

        for (int yi = 0; yi <= passes; yi++) {
            float y = (float(yi) / float(passes) - 0.5) * 0.1; // -0.5是左右偏移
            tempColor += texture2D(vTexture, aCoordinate + intensity * vec2(x, y));
        }
    }

    gl_FragColor = tempColor / float((passes + 1) * (passes + 1));
}