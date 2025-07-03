#version 330 core

out vec4 fragColor;

uniform vec3 uColor; // np. biały: vec3(1.0, 1.0, 1.0)

void main() {
    fragColor = vec4(uColor, 1.0);
}
