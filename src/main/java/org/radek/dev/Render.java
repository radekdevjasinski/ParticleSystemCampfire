package org.radek.dev;

import org.joml.Vector3f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;

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
    int[] starBuffers = new int[2]; // VAO + VBO

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
        // 4 wierzchołki, każdy ma pozycję i normalną (0, 1, 0)
        float[] groundVertices = {
                // Pozycja                // Normalna
                -250.0f, 0.0f, -250.0f,    0.0f, 1.0f, 0.0f,
                250.0f, 0.0f, -250.0f,    0.0f, 1.0f, 0.0f,
                250.0f, 0.0f,  250.0f,    0.0f, 1.0f, 0.0f,
                -250.0f, 0.0f,  250.0f,    0.0f, 1.0f, 0.0f
        };

        groundBuffers[0] = glGenVertexArrays();
        groundBuffers[1] = glGenBuffers();

        glBindVertexArray(groundBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, groundBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(groundVertices.length);
        buffer.put(groundVertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        int stride = 6 * Float.BYTES;

        // Pozycja (location = 0)
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, stride, 0);

        // Normalna (location = 1)
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, stride, 3 * Float.BYTES);

        glBindVertexArray(0);
    }


    public void createWoodBuffer() {
        float width = 10f;
        float length = 2.0f;
        float posY = 0.1f;

        // Normalne: skierowane w górę (0, 1, 0)
        float[] up = {0f, 1f, 0f};

        // 2 prostokąty po 6 wierzchołków (2x po 3 trójkąty)
        float[] woodVertices = new float[6 * 6 * 2]; // 6 wierzchołków * (3 pos + 3 norm) * 2 prostokąty

        int i = 0;

        // Pierwszy prostokąt (na osi X)
        float[][] verts1 = {
                {-length/2, posY, -width/2},
                { length/2, posY, -width/2},
                { length/2, posY,  width/2},
                {-length/2, posY, -width/2},
                { length/2, posY,  width/2},
                {-length/2, posY,  width/2},
        };
        for (float[] v : verts1) {
            woodVertices[i++] = v[0];
            woodVertices[i++] = v[1];
            woodVertices[i++] = v[2];
            woodVertices[i++] = up[0];
            woodVertices[i++] = up[1];
            woodVertices[i++] = up[2];
        }

        // Drugi prostokąt (na osi Z)
        float[][] verts2 = {
                {-width/2, posY, -length/2},
                { width/2, posY, -length/2},
                { width/2, posY,  length/2},
                {-width/2, posY, -length/2},
                { width/2, posY,  length/2},
                {-width/2, posY,  length/2},
        };
        for (float[] v : verts2) {
            woodVertices[i++] = v[0];
            woodVertices[i++] = v[1];
            woodVertices[i++] = v[2];
            woodVertices[i++] = up[0];
            woodVertices[i++] = up[1];
            woodVertices[i++] = up[2];
        }

        woodBuffers[0] = glGenVertexArrays();
        woodBuffers[1] = glGenBuffers();

        glBindVertexArray(woodBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, woodBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(woodVertices.length);
        buffer.put(woodVertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0); // Pozycja
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);

        glEnableVertexAttribArray(1); // Normalna
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        glBindVertexArray(0);
    }


    public void createTrunkBuffer() {
        float width = 3f;
        float height = 15f;

        float[] frontNormal = {0f, 0f, -1f};

        float[][] verts = {
                {-width / 2, 0.0f, -width / 2},
                { width / 2, 0.0f, -width / 2},
                { width / 2, height, -width / 2},
                {-width / 2, 0.0f, -width / 2},
                { width / 2, height, -width / 2},
                {-width / 2, height, -width / 2},
        };

        float[] fullData = new float[6 * 6]; // 6 wierzchołków * (3 pos + 3 norm)

        int i = 0;
        for (float[] v : verts) {
            fullData[i++] = v[0];
            fullData[i++] = v[1];
            fullData[i++] = v[2];
            fullData[i++] = frontNormal[0];
            fullData[i++] = frontNormal[1];
            fullData[i++] = frontNormal[2];
        }

        trunkBuffers[0] = glGenVertexArrays();
        trunkBuffers[1] = glGenBuffers();

        glBindVertexArray(trunkBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, trunkBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(fullData.length);
        buffer.put(fullData).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0); // Pozycja
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);

        glEnableVertexAttribArray(1); // Normalna
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        glBindVertexArray(0);
    }


    public void drawTrunk() {
        glBindVertexArray(trunkBuffers[0]);
        glDrawArrays(GL_TRIANGLES, 0, 6);
        glBindVertexArray(0);
    }
    public void createCrownBuffer() {
        float radius = 7f;
        float height = 25f;

        // Wierzchołki trójkąta
        Vector3f top = new Vector3f(0.0f, height, 0.0f);
        Vector3f left = new Vector3f(-radius, 10f, -radius);
        Vector3f right = new Vector3f(radius, 10f, -radius);

        // Obliczenie wektora normalnego
        Vector3f edge1 = new Vector3f();
        Vector3f edge2 = new Vector3f();
        Vector3f normal = new Vector3f();

        right.sub(top, edge1);
        left.sub(top, edge2);
        edge1.cross(edge2, normal).normalize();


        // Każdy wierzchołek: 3 pos + 3 normal = 6 wartości
        float[] vertices = {
                top.x, top.y, top.z, normal.x, normal.y, normal.z,
                left.x, left.y, left.z, normal.x, normal.y, normal.z,
                right.x, right.y, right.z, normal.x, normal.y, normal.z,
        };

        crownBuffers[0] = glGenVertexArrays();
        crownBuffers[1] = glGenBuffers();

        glBindVertexArray(crownBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, crownBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(vertices.length);
        buffer.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        // Atrybuty: pozycja (location = 0), normalna (location = 1)
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 6 * Float.BYTES, 0);

        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 3, GL_FLOAT, false, 6 * Float.BYTES, 3 * Float.BYTES);

        glBindVertexArray(0);
    }


    public void drawCrown() {
        glBindVertexArray(crownBuffers[0]);
        glDrawArrays(GL_TRIANGLES, 0, 3);
        glBindVertexArray(0);
    }


    public void drawGround() {
        glBindVertexArray(groundBuffers[0]);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 4);  // rysujesz 4 wierzchołki
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
    public void createStarBuffer(int count) {
        Random random = new Random();
        float[] stars = new float[count * 3]; // tylko pozycje XYZ

        // Wygeneruj losowe pozycje gwiazd
        for (int i = 0; i < count; i++) {
            float x = ParticleSystem.randomFloat(-150f, 150f);
            float y = ParticleSystem.randomFloat(25f, 100f);  // wyżej nad ziemią
            float z = 200f;

            stars[i * 3] = x;
            stars[i * 3 + 1] = y;
            stars[i * 3 + 2] = z;
        }

        starBuffers[0] = glGenVertexArrays();
        starBuffers[1] = glGenBuffers();

        glBindVertexArray(starBuffers[0]);
        glBindBuffer(GL_ARRAY_BUFFER, starBuffers[1]);

        FloatBuffer buffer = MemoryUtil.memAllocFloat(stars.length);
        buffer.put(stars).flip();
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW);
        MemoryUtil.memFree(buffer);

        glEnableVertexAttribArray(0); // tylko pozycja
        glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);

        glBindVertexArray(0);
    }
    public void drawStars(int starCount) {
        glBindVertexArray(starBuffers[0]);
        glDrawArrays(GL_POINTS, 0, starCount);
        glBindVertexArray(0);
    }

}
