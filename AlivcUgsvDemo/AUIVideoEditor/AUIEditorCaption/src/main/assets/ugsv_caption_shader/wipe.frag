precision mediump float;
uniform sampler2D inputImageTexture;
uniform float uAlpha;
uniform bool premultiplied;
varying vec2 textureCoordinate;
uniform float normalizedElapseTime;
uniform float interpolatedFactor;

float w = 0.2;
float g = 1.0;

//uniform int direction;
//uniform int wipeMode;

const int LEFT = 0;
const int TOP = 1;
const int RIGHT = 2;
const int BOTTOM = 3;

const int APPEAR = 0;
const int DISAPPEAR = 1;

void main()
{

    vec4 gamma = vec4(g, g, g, 1.0);

    vec4 color = texture2D(inputImageTexture, textureCoordinate);
    if(premultiplied && color.a > 0.0)
    {
        color.rgb /= color.a;
    }
    vec4 color0 = pow(color, gamma);
    float offset = interpolatedFactor;
    float correction = mix(w, -w, offset);

    float coord = textureCoordinate.x;

    int wipeMode = APPEAR;
    int direction = LEFT;

    if(direction == LEFT){
        coord = 1.0 - textureCoordinate.x;
    }else if(direction == RIGHT){
        coord = textureCoordinate.x;
    }else if(direction == TOP){
        coord = 1.0 - textureCoordinate.y;
    }else if(direction == BOTTOM){
        coord = textureCoordinate.y;
    }

    float choose = smoothstep(offset - w, offset + w, coord + correction); // clamped ramp

    float alpha = choose;
    if(wipeMode == APPEAR){
        alpha = 1.0 - choose;
    }else if(wipeMode == DISAPPEAR){
        alpha = choose;
    }
    gl_FragColor = vec4(color0.r,color0.g,color0.b,color0.a*alpha);
}