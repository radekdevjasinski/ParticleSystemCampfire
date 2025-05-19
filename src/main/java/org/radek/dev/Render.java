package org.radek.dev;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_TRIANGLE_FAN;
import static org.lwjgl.opengl.GL20.glBufferSubData;
import static org.lwjgl.opengl.GL20.glDeleteBuffers;
import static org.lwjgl.opengl.GL30.*;

public class Render {
    int[] particleBuffers = new int[2];
    int[] groundBuffers = new int[2]; // [0] = VAO, [1] = VBO
    int[] woodBuffers = new int[2];
    int[] trunkBuffers = new int[2];
    int[] crownBuffers = new int[2];


    static int PARTICLESSTRIDE = (3+1+4+4+1);

    public void updateParticlesBuffer(List<Particle> particles) {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(particles.size() * PARTICLESSTRIDE);
        for (Particle particle : particles) {
            buffer
                    .put(particle.position.x).put(particle.position.y).put(particle.position.z) //x3
                    .put(particle.size) //x1
                    .put(particle.startColor.x).put(particle.startColor.y).put(particle.startColor.z).put(particle.startColor.w) //x4
                    .put(particle.endColor.x).put(particle.endColor.y).put(particle.endColor.z).put(particle.endColor.w) //x4
                    .put(particle.getLifePercent()); //x1
        }
        buffer.flip();

        glBindBuffer(GL_ARRAY_BUFFER, particleBuffers[1]);
        glBufferSubData(GL_ARRAY_BUFFER, 0, buffer);
        MemoryUtil.memFree(buffer);
    }

    public void createParticlesBuffer() {
        particleBuffers[0] = glGenVertexArrays();
        particleBuffers[1] = glGenBuffers();

        glBindVertexArray(particleBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, particleBuffers[1]);
        glBufferData(GL_ARRAY_BUFFER, (long) ParticleSystem.MAXPARTICLES * PARTICLESSTRIDE * Float.BYTES, GL_DYNAMIC_DRAW);

        glEnableVertexAttribArray(0);// position (vec3)
        glVertexAttribPointer(0, 3, GL_FLOAT, false, PARTICLESSTRIDE * Float.BYTES, 0);

        glEnableVertexAttribArray(1); // size (float)
        glVertexAttribPointer(1, 1, GL_FLOAT, false, PARTICLESSTRIDE * Float.BYTES, 3 * Float.BYTES);

        glEnableVertexAttribArray(2); // start color vec4
        glVertexAttribPointer(2, 4, GL_FLOAT, false, PARTICLESSTRIDE * Float.BYTES, 4 * Float.BYTES);

        glEnableVertexAttribArray(3); // end color vec4
        glVertexAttribPointer(3, 4, GL_FLOAT, false, PARTICLESSTRIDE * Float.BYTES, 8 * Float.BYTES);

        glEnableVertexAttribArray(4); // age
        glVertexAttribPointer(4, 1, GL_FLOAT, false, PARTICLESSTRIDE * Float.BYTES, 12 * Float.BYTES);
        glBindVertexArray(0);
    }

    public void drawParticles(List<Particle> particles) {
        glBindVertexArray(particleBuffers[0]);
        glDrawArrays(GL_POINTS, 0, particles.size());
    }

    public void createGroundBuffer() {
        float[] groundVertices = {
                -100.0f, 0.0f, -100.0f,
                100.0f, 0.0f, -100.0f,
                100.0f, 0.0f,  100.0f,
                -100.0f, 0.0f,  100.0f
        };

        groundBuffers[0] = glGenVertexArrays();
        groundBuffers[1] = glGenBuffers();

        glBindVertexArray(groundBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, groundBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(groundVertices.length);
        buffer.put(groundVertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        glBindVertexArray(0);
    }

    public void createWoodBuffer()
    {
        float width = 10f;
        float length = 2.0f;
        float posY = .1f;

        float[] woodVertices = {
                // Pierwszy prostokąt (X-owy)
                -length/2, posY, -width/2,
                length/2, posY, -width/2,
                length/2, posY,  width/2,
                -length/2, posY, -width/2,
                length/2, posY,  width/2,
                -length/2, posY,  width/2,

                // Drugi prostokąt (Z-owy)
                -width/2, posY, -length/2,
                width/2, posY, -length/2,
                width/2, posY,  length/2,
                -width/2, posY, -length/2,
                width/2, posY,  length/2,
                -width/2, posY,  length/2,
        };

        woodBuffers[0] = glGenVertexArrays();
        woodBuffers[1] = glGenBuffers();

        glBindVertexArray(woodBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, woodBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(woodVertices.length);
        buffer.put(woodVertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        glBindVertexArray(0);
    }

    public void createTrunkBuffer() {
        float width = 2f;
        float height = 10f;

        float[] vertices = {
                -width / 2, 0.0f, -width / 2,
                width / 2, 0.0f, -width / 2,
                width / 2, height, -width / 2,
                -width / 2, 0.0f, -width / 2,
                width / 2, height, -width / 2,
                -width / 2, height, -width / 2,
        };

        trunkBuffers[0] = glGenVertexArrays();
        trunkBuffers[1] = glGenBuffers();

        glBindVertexArray(trunkBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, trunkBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        glBindVertexArray(0);
    }

    public void drawTrunk() {
        glBindVertexArray(trunkBuffers[0]);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }

    public void createCrownBuffer() {
        float radius = 5f;
        float height = 15f;

        float[] vertices = {
                0.0f, height, 0.0f,              // góra
                -radius, 5f, -radius,           // dolne lewe
                radius, 5f, -radius,           // dolne prawe
        };

        crownBuffers[0] = glGenVertexArrays();
        crownBuffers[1] = glGenBuffers();

        glBindVertexArray(crownBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, crownBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        glBindVertexArray(0);
    }

    public void drawCrown() {
        glBindVertexArray(crownBuffers[0]);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0);
    }


    public void drawGround() {
        glBindVertexArray(groundBuffers[0]);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);
        glBindVertexArray(0);
    }
    public void drawWood() {
        glBindVertexArray(woodBuffers[0]);
        glDrawArrays(GL_TRIANGLES, 0, 12);
        glBindVertexArray(0);
    }

    public void cleanup() {
        glDeleteVertexArrays(particleBuffers[0]);
        glDeleteBuffers(particleBuffers[1]);

        glDeleteVertexArrays(groundBuffers[0]);
        glDeleteBuffers(groundBuffers[1]);

        glDeleteVertexArrays(woodBuffers[0]);
        glDeleteBuffers(woodBuffers[1]);
    }
}
