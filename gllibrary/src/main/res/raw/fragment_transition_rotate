precision mediump float;

uniform sampler2D vTextureFront;
uniform sampler2D vTextureBack;
uniform float progress;
varying vec2 aCoordinate;

void main(){
    float radius = 1.0;
    vec2 uv = aCoordinate;
    uv -= vec2( 0.5, 0.5 );
    float dist = length(uv);

    if (dist < radius)
    {
    	float percent = (radius - dist) / radius;
    	float A = (progress <= 0.5 ) ? mix( 0.0, 1.0, progress/0.5) : mix(1.0, 0.0, (progress-0.5)/0.5);
    	float theta = percent * percent * A * 8.0 * 3.14159;
    	float sin_value = sin(theta);
    	float cos_value = cos(theta);
    	uv = vec2(dot(uv, vec2(cos_value, -sin_value)), dot(uv, vec2(sin_value, cos_value)));
    }
    uv += vec2( 0.5, 0.5 );

    vec4 front_color = texture2D(vTextureFront, uv);
    vec4 back_color = texture2D(vTextureBack, uv);

    gl_FragColor = mix(front_color, back_color, progress);
}