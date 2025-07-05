package org.radek.dev;

public class FPS {
    private long lastTime = System.nanoTime();
    private int frames = 0;

    // Licznik do średnich
    private double fpsSum = 0;
    private double frameTimeSum = 0;
    private int secondsCounted = 0;

    private final int AVG_SECONDS = 10; // Licz z ostatnich 10 sekund

    public void calculateFPS() {
        long currentTime = System.nanoTime();
        frames++;

        if (currentTime - lastTime >= 1_000_000_000L) { // 1 sekunda minęła
            int currentFPS = frames;
            double frameTime = 1000.0 / currentFPS; // ms na klatkę

            // Dodaj do sumy
            fpsSum += currentFPS;
            frameTimeSum += frameTime;
            secondsCounted++;

            // Wyświetl w konsoli
            System.out.println("FPS: " + currentFPS + " | Frame time: " + String.format("%.2f", frameTime) + " ms");

            // Co AVG_SECONDS sekund pokazujemy średnią
            if (secondsCounted >= AVG_SECONDS) {
                double avgFPS = fpsSum / secondsCounted;
                double avgFrameTime = frameTimeSum / secondsCounted;
                System.out.println("ŚREDNIE Z " + AVG_SECONDS + " s -> FPS: " + String.format("%.2f", avgFPS) +
                        " | Frame time: " + String.format("%.2f", avgFrameTime) + " ms");

                // Resetujemy licznik średnich
                fpsSum = 0;
                frameTimeSum = 0;
                secondsCounted = 0;
            }

            // Reset liczników
            frames = 0;
            lastTime = currentTime;
        }
    }

}
