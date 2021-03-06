attribute vec4 vPosition;
attribute vec2 vCoordinate;
uniform mat4 vMatrix;

uniform vec2 offsetCoordinate;
varying vec2 textureCoordinate[13];

void main() {

    gl_Position = vMatrix * vPosition;

	vec2 textureCoord = vCoordinate;

    textureCoordinate[0] = textureCoord.xy - 6.0 * offsetCoordinate;
    textureCoordinate[1] = textureCoord.xy - 5.0 * offsetCoordinate;
    textureCoordinate[2] = textureCoord.xy - 4.0 * offsetCoordinate;
    textureCoordinate[3] = textureCoord.xy - 3.0 * offsetCoordinate;
    textureCoordinate[4] = textureCoord.xy - 2.0 * offsetCoordinate;
    textureCoordinate[5] = textureCoord.xy - 1.0 * offsetCoordinate;
    textureCoordinate[6] = textureCoord.xy;
    textureCoordinate[7] = textureCoord.xy + 1.0 * offsetCoordinate;
    textureCoordinate[8] = textureCoord.xy + 2.0 * offsetCoordinate;
    textureCoordinate[9] = textureCoord.xy + 3.0 * offsetCoordinate;
    textureCoordinate[10] = textureCoord.xy + 4.0 * offsetCoordinate;
    textureCoordinate[11] = textureCoord.xy + 5.0 * offsetCoordinate;
    textureCoordinate[12] = textureCoord.xy + 6.0 * offsetCoordinate;
}