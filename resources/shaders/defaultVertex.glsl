#version 330 core

layout(location = 0) in vec3 position;
layout(location = 1) in vec3 inNormal; // Dodaj normalę

uniform mat4 projection;
uniform mat4 view;
uniform mat4 model;

out vec3 fragPosition;
out vec3 normal;

void main() {
    vec4 worldPos = model * vec4(position, 1.0);
    fragPosition = worldPos.xyz;

    mat3 normalMatrix = transpose(inverse(mat3(model)));
    normal = normalize(normalMatrix * inNormal); // poprawnie transformowana normalna

    gl_Position = projection * view * worldPos;
}
