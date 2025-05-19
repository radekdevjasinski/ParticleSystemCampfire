#version 330 core

in float vLifePercent;
in vec4 vStartColor;
in vec4 vEndColor;

out vec4 fragColor;

void main() {
    fragColor = mix(vStartColor,vEndColor,vLifePercent);
}
