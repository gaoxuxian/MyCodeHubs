precision mediump float;

uniform sampler2D vTexture;
uniform float vTime;
uniform float vFuzzyRang; // 模糊范围, 以直线为基准, 该value是模糊区域一半
uniform float vSpeed;
uniform float vDelay; // [0.1,0.2]
varying vec2 aCoordinate;

float funX(float x, float offset, float k)
{
    return k * x + offset;
}

void main()
{
    vec2 tCoordinate = aCoordinate;
    float tOffset = clamp((vTime * vSpeed - vDelay * 2.5), (-vDelay * 1.5), (1.0 + vFuzzyRang * 2.0)); // y = x ± b 、 y = -x ± b的偏移b

    // 通过x 求出y
    vec2 kArr = vec2(-1.0, 1.0);
    vec2 temp = tCoordinate - vec2(0.5, 0.5);
    float y1 = funX(temp.x, tOffset, kArr.y) + vFuzzyRang;
    float y3 = funX(temp.x, tOffset, kArr.x) + vFuzzyRang;
    float y2 = funX(temp.x, -tOffset, kArr.x) - vFuzzyRang;
    float y4 = funX(temp.x, -tOffset, kArr.y) - vFuzzyRang;

    // 对比四个轴的y, 确定max\min
    float maxY = min(y1, y3);
    float minY = max(y2, y4);

    vec4 tColor = vec4(0.0, 0.0, 0.0, 0.0);
    // 通过纹理坐标y 跟 max、min对比, 得到y的位置
    if (maxY > minY)
    {
        float y = clamp(temp.y, minY, maxY);
        float tApha = smoothstep(0.0, vFuzzyRang * 2.0, min(distance(maxY, y), distance(y, minY)));
        vec4 tempColor = texture2D(vTexture, tCoordinate);
        tColor.rgb = tempColor.rgb;
        tColor.a = tApha;
    }
    gl_FragColor = tColor;
}