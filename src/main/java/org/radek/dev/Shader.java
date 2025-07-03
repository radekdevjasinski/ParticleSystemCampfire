package org.radek.dev;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Shader {
    public int particleShader;
    public int defaultShader;
    public int starShader;
    public Shader()
    {
        particleShader = createShaderProgram("resources/shaders/particle");
        defaultShader = createShaderProgram("resources/shaders/default");
        starShader = createShaderProgram("resources/shaders/star");
    }
    private static String loadShader(String path) {
        try {
            return new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(path)));
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }
    public static int createShaderProgram(String path) {
        String vertexShaderSource = loadShader(path + "Vertex.glsl");
        String fragmentShaderSource = loadShader(path + "Fragment.glsl");

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, vertexShaderSource);
        glCompileShader(vertexShader);
        checkCompileErrors(vertexShader, "VERTEX");

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, fragmentShaderSource);
        glCompileShader(fragmentShader);
        checkCompileErrors(fragmentShader, "FRAGMENT");

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        checkCompileErrors(program, "PROGRAM");

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);

        return program;
    }
    private static void checkCompileErrors(int shader, String type) {
        int success;
        if (type.equals("PROGRAM")) {
            success = glGetProgrami(shader, GL_LINK_STATUS);
            if (success == 0) {
                System.err.println("ERROR::SHADER::PROGRAM::LINKING_FAILED\n" + glGetProgramInfoLog(shader));
            }
        } else {
            success = glGetShaderi(shader, GL_COMPILE_STATUS);
            if (success == 0) {
                System.err.println("ERROR::SHADER::" + type + "::COMPILATION_FAILED\n" + glGetShaderInfoLog(shader));
            }
        }
    }
    public void useShader(int shader, float[] projection, float[] view, float[] model)
    {
        glUseProgram(shader);

        try (MemoryStack stack = stackPush()) {
            FloatBuffer fb = stack.mallocFloat(16);

            // projection
            int projLoc = glGetUniformLocation(shader, "projection");
            fb.put(projection).flip();
            glUniformMatrix4fv(projLoc, false, fb);

            // view
            fb.clear();
            int viewLoc = glGetUniformLocation(shader, "view");
            fb.put(view).flip();
            glUniformMatrix4fv(viewLoc, false, fb);

            // model
            fb.clear();
            int modelLoc = glGetUniformLocation(shader, "model");
            fb.put(model).flip();
            glUniformMatrix4fv(modelLoc, false, fb);
        }
    }
    public void cleanup()
    {
        glDeleteProgram(particleShader);
        glDeleteProgram(defaultShader);
        glDeleteProgram(starShader);
    }
    public void setUniformColor(int shaderProgram, Vector3f color) {
        int loc = glGetUniformLocation(shaderProgram, "uColor");
        glUniform3f(loc, color.x, color.y, color.z);
    }
    public void setUniformPosition(int shaderProgram, Vector3f position)
    {
        Matrix4f model = new Matrix4f().identity().translate(position);
        glUniformMatrix4fv(glGetUniformLocation(shaderProgram, "model"), false, model.get(new float[16]));
    }
    public void setUniformLight(int shaderProgram, PointLight pointLight)
    {
        int lightPosLoc = glGetUniformLocation(shaderProgram, "lightPosition");
        int lightColorLoc = glGetUniformLocation(shaderProgram, "lightColor");
        int intensityLoc = glGetUniformLocation(shaderProgram, "lightIntensity");
        int radiusLoc = glGetUniformLocation(shaderProgram, "lightRadius");

        // Pozycja ognia
        glUniform3f(lightPosLoc, pointLight.position.x, pointLight.position.y, pointLight.position.z);

        // Kolor ognia (pomarańczowy)
        glUniform3f(lightColorLoc, pointLight.color.x, pointLight.color.y, pointLight.color.z);

        // Intensywność i promień światła
        glUniform1f(intensityLoc, pointLight.intensity);
        glUniform1f(radiusLoc, pointLight.radius);

    }
}
