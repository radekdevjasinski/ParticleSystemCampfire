package org.radek.dev;

import org.joml.Vector3f;
import org.joml.Vector4f;

public class Particle {
    public Vector3f position;
    public Vector3f velocity;
    public float lifetime;
    public float age;
    public float startSize;
    public float size;
    public Vector4f startColor;
    public Vector4f endColor;


    public Particle(Vector3f position, Vector3f velocity, float lifetime, float startSize, Vector4f startColor, Vector4f endColor) {
        this.position = position;
        this.velocity = velocity;
        this.lifetime = lifetime;
        this.age = 0;
        this.startSize = startSize;
        this.size = this.startSize;
        this.startColor = startColor;
        this.endColor = endColor;
    }
    public float getLifePercent()
    {
        return age/lifetime;
    }
}