#version 330 core

in vec3 fragPosition;
in vec3 normal;

out vec4 fragColor;

uniform vec3 uColor;      // Kolor materiału
uniform vec3 lightPosition;  // Pozycja ogniska
uniform vec3 lightColor;     // Kolor ognia (np. pomarańcz)
uniform float lightIntensity;
uniform float lightRadius;

void main() {
    // Normalizacja
    vec3 N = normalize(normal);
    vec3 L = lightPosition - fragPosition;
    float distance = length(L);
    L = normalize(L);

    // Rozproszone światło (Lambert)
    float diff = max(dot(N, L), 0.0);

    // Attenuacja (wygaszenie światła z odległością)
    float attenuation = clamp(1.0 - distance / lightRadius, 0.0, 1.0);

    // Finalne oświetlenie
    vec3 lighting = lightColor * diff * lightIntensity * attenuation;

    // Finalny kolor – baza + światło
    vec3 ambient = uColor * 0.1; // 10% stałego światła
    vec3 color = ambient + lighting;
    fragColor = vec4(color, 1.0);
}
