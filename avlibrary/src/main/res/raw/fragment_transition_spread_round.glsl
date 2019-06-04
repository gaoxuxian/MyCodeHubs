precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
uniform float dots; // = 20.0
uniform vec2 center; // = (0., 0.)

varying vec2 aCoordinate;

void main(){
    bool nextImage = distance(fract(aCoordinate * dots), vec2(.5, .5)) < (progress / distance(aCoordinate, center));
    vec4 frontColor = texture2D(vTextureFront, aCoordinate);
    vec4 backColor = texture2D(vTextureBack, aCoordinate);
    gl_FragColor = nextImage ? backColor : frontColor;
}