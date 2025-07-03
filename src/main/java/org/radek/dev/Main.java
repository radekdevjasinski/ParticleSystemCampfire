package org.radek.dev;

import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL32C.GL_PROGRAM_POINT_SIZE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private long window;
    private List<ParticleSystem> particleSystems = new ArrayList<>();
    private Shader shader;
    private Render render;
    private PointLight fireLight;
    private List<Vector3f> trees = new ArrayList<>();

    public static int WIDTH = 800, HEIGHT = 600;

    public void run() {
        init();
        loop();

        render.cleanup();
        shader.cleanup();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    private void init() {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        window = glfwCreateWindow(WIDTH, HEIGHT, "Campfire", NULL, NULL);
        if (window == NULL)
            throw new RuntimeException("Failed to create the GLFW window");

        glfwMakeContextCurrent(window);
        glfwSwapInterval(1);
        glfwShowWindow(window);

        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);


        ParticleSystem fireBase
                = new ParticleSystem(0.01f, 5, new Vector3f(0,0,0), 1.5f,
                new Vector4f(1, 0.404f, 0.122f,1), new Vector4f(0.1f,0.1f,0.1f,0),
                0.1f, 4f,1f, 15f, 0.01f, .075f, 0.01f);

        particleSystems.add(fireBase);

        ParticleSystem fireCenter
                = new ParticleSystem(0.01f, 5, new Vector3f(0,0,0), 1f,
                new Vector4f(1,1,1,1), new Vector4f(1f, 0.569f, 0.118f,0),
                0.1f, 1f,5f, 15f, 0.1f, .2f, 0);

        particleSystems.add(fireCenter);

        render = new Render();
        render.createParticlesBuffer();
        render.createGroundBuffer();
        render.createWoodBuffer();
        render.createTrunkBuffer();
        render.createCrownBuffer();
        render.createStarBuffer(100);

        shader = new Shader();

        GenerateTrees(100);

        // ognisko
        fireLight = new PointLight(new Vector3f(0f,1f,0f),new Vector3f(1.0f, 0.5f, 0.2f), 1.4f,100f);
    }

    private void loop() {
        glClearColor(0, 0, 0, 0);
        glEnable(GL_PROGRAM_POINT_SIZE);
        float fov = (float) Math.toRadians(45.0);
        float aspect = (float) Main.WIDTH / Main.HEIGHT;
        float zNear = 0.1f;
        float zFar = 250f;
        float[] projection = Transform.perspective(fov, aspect, zNear, zFar);

        float[] view = Transform.lookAt(
                0f, 10f, -30f,    // pozycja kamery
                0f, 0f, 180f,   // gdzie patrzy
                0f, 1f, 0f     // kierunek "góry"
        );

        float[] model = Transform.identityMatrix();
        float lastTime = 0;

        int frameCounter = 0;
        while (!glfwWindowShouldClose(window)) {
            glClearColor(0f, 0f, 0f, 1.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            float currentTime = (float)glfwGetTime();
            float deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            for(ParticleSystem ps : particleSystems)
            {
                ps.emitParticles(deltaTime);
                ps.updateParticles(deltaTime);

                shader.useShader(shader.particleShader, projection, view, model);

                render.updateParticlesBuffer(ps.particles);
                render.drawParticles(ps.particles);
            }


            shader.useShader(shader.defaultShader, projection, view, model);

            //światło
            frameCounter++;
            if (frameCounter >= 7) { // zmieniaj co 5 klatek
                frameCounter = 0;
                fireLight.intensity = ParticleSystem.randomFloat(0.7f, 1.5f);
            }
            shader.setUniformLight(shader.defaultShader, fireLight);


            shader.setUniformColor(shader.defaultShader, new Vector3f(0f, 0.36f, 0f));
            render.drawGround();


            shader.setUniformColor(shader.defaultShader, new Vector3f(0.4f, 0.2f, 0.1f));
            render.drawWood();


            DrawTrees();
            shader.useShader(shader.starShader, projection, view, model);
            shader.setUniformColor(shader.starShader, new Vector3f(1f, 1f, 1f));
            render.drawStars(100);

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }
    void GenerateTrees(int count) {
        float minDistance = 10f; // Minimalna odległość między drzewami

        int attempts = 0;
        int maxAttempts = count * 100; // Limit prób, żeby uniknąć nieskończonej pętli

        while (trees.size() < count && attempts < maxAttempts) {
            float randomX = ParticleSystem.randomFloat(-100f, 100f);
            float randomZ = ParticleSystem.randomFloat(20f, 200f);
            Vector3f newTree = new Vector3f(randomX, 0, randomZ);

            boolean tooClose = false;

            for (Vector3f tree : trees) {
                if (tree.distance(newTree) < minDistance) {
                    tooClose = true;
                    break;
                }
            }

            if (!tooClose) {
                trees.add(newTree);
            }

            attempts++;
        }

        if (trees.size() < count) {
            System.out.println("Nie udało się wygenerować wszystkich drzew – zbyt mało miejsca.");
        }
    }

    void DrawTrees()
    {
        for (Vector3f tree : trees) {
            shader.setUniformColor(shader.defaultShader, new Vector3f(0.4f, 0.2f, 0.1f));
            shader.setUniformPosition(shader.defaultShader, new Vector3f(tree.x, 0, tree.z));
            render.drawTrunk();
            shader.setUniformColor(shader.defaultShader, new Vector3f(0f, 1f, 0f));
            shader.setUniformPosition(shader.defaultShader, new Vector3f(tree.x, 0, tree.z));
            render.drawCrown();
        }
    }


    public static void main(String[] args) {
        new Main().run();
    }
}
