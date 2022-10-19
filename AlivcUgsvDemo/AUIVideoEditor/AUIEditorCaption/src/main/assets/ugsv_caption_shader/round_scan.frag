precision mediump float;
uniform sampler2D inputImageTexture;
uniform float uAlpha;
uniform bool premultiplied;
varying vec2 textureCoordinate;
uniform float normalizedElapseTime;
uniform float interpolatedFactor;

uniform highp vec4 texSize;

void main(void)
{
    vec2 v = textureCoordinate.yx - vec2(0.5, 0.5);
    float theta = (atan(-v.y * 0.5, -v.x) / 3.1415926 + 1.0) * 0.5;
    if (theta >= normalizedElapseTime)
    {
        gl_FragColor = texture2D(inputImageTexture, textureCoordinate);
    }
    else
    {
        gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    }
}
