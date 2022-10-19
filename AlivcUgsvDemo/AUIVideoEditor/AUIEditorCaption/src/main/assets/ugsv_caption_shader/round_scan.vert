attribute vec3 a_position;
attribute vec2 a_texcoord;

uniform mat4 u_mvp;
uniform mat4 textureMatrix;

varying vec2 textureCoordinate;

void main()
{
    gl_Position = u_mvp * vec4(a_position, 1.0);
    textureCoordinate = (textureMatrix * vec4(a_texcoord.xy, 0.0, 1.0)).xy;
}