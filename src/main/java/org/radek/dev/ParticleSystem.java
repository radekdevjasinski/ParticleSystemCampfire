package org.radek.dev;

import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    public static int MAXPARTICLES = 1000;
    public float spawnInterval;
    public int spawnNumber;
    public Vector3f emitterPosition;
    public float emitterSize;
    public Vector4f startColor;
    public Vector4f endColor;
    public float minLife;
    public float maxLife;
    public float minSize;
    public float maxSize;
    public float minYVel;
    public float maxYVel;
    public float sideVel;

    public List<Particle> particles = new ArrayList<>();
    float spawnTimer = 0;

    public ParticleSystem(float spawnInterval, int spawnNumber, Vector3f emitterPosition, float emitterSize, Vector4f startColor, Vector4f endColor,
                          float minLife, float maxLife, float minSize, float maxSize, float minYVel, float maxYVel, float sideVel) {
        this.spawnInterval = spawnInterval;
        this.spawnNumber = spawnNumber;
        this.emitterPosition = emitterPosition;
        this.emitterSize = emitterSize;
        this.startColor = startColor;
        this.endColor = endColor;
        this.minLife = minLife;
        this.maxLife = maxLife;
        this.minSize = minSize;
        this.maxSize = maxSize;
        this.minYVel = minYVel;
        this.maxYVel = maxYVel;
        this.sideVel = sideVel;
    }

    public Particle generateParticle()
    {
        Vector3f randomPos = new Vector3f(randomFloat(-emitterSize,emitterSize),0,randomFloat(-emitterSize,emitterSize));
        Vector3f randomVel = new Vector3f(randomFloat(-sideVel,sideVel),randomFloat(minYVel,maxYVel),randomFloat(-sideVel,sideVel));
        float randomLife = randomFloat(minLife,maxLife);
        float randomSize = randomFloat(minSize,maxSize);
        return new Particle(addVector(randomPos, emitterPosition),randomVel,randomLife,randomSize,startColor, endColor);
    }

    public void emitParticles(float deltaTime)
    {
        spawnTimer += deltaTime;
        if (spawnTimer >= spawnInterval)
        {
            spawnTimer -= spawnInterval;
            if (particles.size() < MAXPARTICLES)
            {
                for (int i = 0; i < spawnNumber; i++)
                {
                    particles.add(generateParticle());
                }
            }
        }
    }

    public void updateParticles(float deltaTime) {
        for (Particle particle : particles) {
            particle.position.add(particle.velocity);
            particle.age += deltaTime;

        }
        particles.removeIf(p -> p.age >= p.lifetime);
    }
    float randomFloat(float min, float max)
    {
        Random r = new Random();
        return min + r.nextFloat() * (max - min);
    }
    Vector3f addVector(Vector3f a, Vector3f b)
    {
        return new Vector3f(a.x + b.x, a.y + b.y,a.z + b.z);
    }
}
