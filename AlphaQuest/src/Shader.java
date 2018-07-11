
import static org.lwjgl.opengl.GL20.*;

import java.util.HashMap;
import java.util.Map;

public class Shader {

	public static final int VERTEX_ATTRIB = 0;
	public static final int TCOORD_ATTRIB = 0;
	
	public static Shader BACKGROUND;
	
	private final int _ID;
	
	private Map<String, Integer> locationCache = new HashMap<String, Integer>();
	
	public Shader(String vertex, String fragment) {
		_ID = ShaderUtilities.load(vertex, fragment);
	}
	
	public static void loadAll() {
		BACKGROUND = new Shader("shaders/background.vert","shaders/background.frag");
	}
	
	public int getUniform(String name) {
		
		if(locationCache.containsKey(name)) {
			return locationCache.get(name);
		}
		
		int result = glGetUniformLocation(_ID, name);
		
		if(result == -1) {
			System.err.println("Could not find uniform variable '" + name + "' !");
		}else {
			locationCache.put(name, result);
		}
		
		return result;
	}
	
	//Integer
	public void setUnifrom1i(String name, int value) {
		glUniform1i(getUniform(name), value);
	}
	
	//Float
	public void setUnifrom1f(String name, int value) {
		glUniform1f(getUniform(name), value);
	}
	
	//Two float
	public void setUnifrom2f(String name, float x, float y) {
		glUniform2f(getUniform(name), x, y);
	}
	
	//Three float
	public void setUnifrom3f(String name, Vector3f vector) {
		glUniform3f(getUniform(name), vector.x, vector.y, vector.z);
	}
	
	public void setUniformMat4f(String name, Matrix4f matrix) {
		//Transpose is true when set to 'row major' false for 'column major'
		glUniformMatrix4fv(getUniform(name), false, matrix.toFloatBuffer());
	}

	public void enable() {
		//TODO: check if _ID is == -1 (causes the instance to crash)
		glUseProgram(_ID);
	}
	
	public void disable() {
		glUseProgram(0);
	}
}
