#version 330 core

layout(location = 0) in vec3 inPosition;
layout(location = 1) in float inSize;
layout(location = 2) in vec4 inStartColor;
layout(location = 3) in vec4 inEndColor;
layout(location = 4) in float inLifePercent;

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out float vLifePercent;
out vec4 vStartColor;
out vec4 vEndColor;

void main() {
    gl_Position = projection * view * model * vec4(inPosition, 1.0);
    gl_PointSize = inSize;

    vLifePercent = inLifePercent;
    vStartColor = inStartColor;
    vEndColor = inEndColor;

}
