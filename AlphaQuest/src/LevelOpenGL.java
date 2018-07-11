
public class LevelOpenGL {

	private VertexArray background;
	
	public LevelOpenGL() {
		float[] vertices = new float[] {
				//Bottom left
				-10.0f, -10.0f * 9.0f / 16.0f, 0.0f,
				
				//Top left
				-10.0f, 10.0f * 9.0f / 16.0f, 0.0f,
				
				
				0.0f, 10.0f * 9.0f / 16.0f, 0.0f,
				
				
				0.0f, -10.0f * 9.0f / 16.0f, 0.0f
		};
		
		byte[] indices = new byte[] {
			0, 1, 2,
			2, 3, 0
		};
		
		//texture coordinates
		float[] tcs = new float[] {
			0, 1,
			0, 0,
			1, 0,
			1, 1
		};
		
		background = new VertexArray(vertices, indices, tcs);
	}
	
	public void render() {
		Shader.BACKGROUND.enable();
		background.render();
		Shader.BACKGROUND.disable();
	}

}
