precision mediump float;
uniform sampler2D inputImageTexture;
uniform float uAlpha;
uniform bool premultiplied;
varying vec2 textureCoordinate;
uniform float normalizedElapseTime;
uniform float interpolatedFactor;

uniform vec3 param;

void main()
{

    float f = 1.5;  //频率
    float v = 3.0;  //速度
    float pi = 3.14159265359;
    vec2 uv;

    if (param.x < 0.1)
    {
        uv = vec2(textureCoordinate.x, textureCoordinate.y + 0.3 * sin(f * pi * textureCoordinate.x - 2.0 * pi * normalizedElapseTime));
    }
    else
    {
        uv = vec2(textureCoordinate.x + param.z * sin(f * pi * textureCoordinate.y + 2.0 * pi * normalizedElapseTime), textureCoordinate.y);
    }

    gl_FragColor = texture2D(inputImageTexture, uv);
}