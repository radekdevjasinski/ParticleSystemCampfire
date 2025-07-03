package org.radek.dev;

import org.joml.Vector3f;

public class PointLight {
    public Vector3f position;
    public Vector3f color;
    public float intensity;
    public float radius;

    public PointLight(Vector3f position, Vector3f color, float intensity, float radius) {
        this.position = position;
        this.color = color;
        this.intensity = intensity;
        this.radius = radius;
    }
}
