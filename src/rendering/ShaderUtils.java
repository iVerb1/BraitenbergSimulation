package rendering;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;

/**
 * Created by s113427 on 8-3-2015.
 */
public class ShaderUtils {

    public static int loadShaderProgram(int... shaderPrograms) {
        int shaderProgram = glCreateProgram();

        for (int p : shaderPrograms) {
            glAttachShader(shaderProgram, p);
        }

        glLinkProgram(shaderProgram);
        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program wasn't linked correctly.");
            System.err.println(glGetProgramInfoLog(shaderProgram, 1024));
            return -1;
        }

        for (int p : shaderPrograms) {
            glDeleteShader(p);
        }

        return shaderProgram;
    }

    public static int loadShader(String location, int shaderType) {
        int shader = glCreateShader(shaderType);
        StringBuilder shaderSource = new StringBuilder();
        BufferedReader shaderFileReader = null;

        try {
            shaderFileReader = new BufferedReader(new FileReader(location));
            String line;
            while ((line = shaderFileReader.readLine()) != null) {
                shaderSource.append(line).append('\n');
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
        finally {
            if (shaderFileReader != null) {
                try {
                    shaderFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        glShaderSource(shader, shaderSource);
        glCompileShader(shader);
        if (glGetShaderi(shader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Shader wasn't able to be compiled correctly. Error log:");
            System.err.println(glGetShaderInfoLog(shader, 1024));
        }

        return shader;
    }

}
