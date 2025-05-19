package org.radek.dev;

public class Transform {
    public static float[] perspective(float fov, float aspect, float zNear, float zFar) {
        float tanHalfFov = (float)Math.tan(fov / 2.0f);

        float[] result = new float[16];
        result[0] = 1.0f / (aspect * tanHalfFov);
        result[5] = 1.0f / tanHalfFov;
        result[10] = -(zFar + zNear) / (zFar - zNear);
        result[11] = -1.0f;
        result[14] = -(2.0f * zFar * zNear) / (zFar - zNear);
        return result;
    }
    public static float[] identityMatrix() {
        float[] m = new float[16];
        m[0] = m[5] = m[10] = m[15] = 1f;
        return m;
    }

    public static float[] lookAt(float eyeX, float eyeY, float eyeZ,
                           float centerX, float centerY, float centerZ,
                           float upX, float upY, float upZ) {
        float[] f = normalize(centerX - eyeX, centerY - eyeY, centerZ - eyeZ);
        float[] up = normalize(upX, upY, upZ);
        float[] s = cross(f, up);
        float[] u = cross(s, f);

        float[] result = identityMatrix();
        result[0] = s[0];
        result[1] = u[0];
        result[2] = -f[0];

        result[4] = s[1];
        result[5] = u[1];
        result[6] = -f[1];

        result[8] = s[2];
        result[9] = u[2];
        result[10] = -f[2];

        result[12] = -dot(s, eyeX, eyeY, eyeZ);
        result[13] = -dot(u, eyeX, eyeY, eyeZ);
        result[14] = dot(f, eyeX, eyeY, eyeZ);

        return result;
    }

    public static float[] normalize(float x, float y, float z) {
        float len = (float)Math.sqrt(x*x + y*y + z*z);
        return new float[]{x / len, y / len, z / len};
    }

    public static float[] cross(float[] a, float[] b) {
        return new float[]{
                a[1]*b[2] - a[2]*b[1],
                a[2]*b[0] - a[0]*b[2],
                a[0]*b[1] - a[1]*b[0]
        };
    }

    public static float dot(float[] a, float x, float y, float z) {
        return a[0]*x + a[1]*y + a[2]*z;
    }
}
