import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwGetPrimaryMonitor;
import static org.lwjgl.glfw.GLFW.glfwGetVideoMode;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.glfw.GLFW.glfwSetWindowPos;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.opengl.GL11.*;


import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

/**
 * Creates a new GameTread that will run the game.
 * <p>
 * @see <b>{@literal Constructor: }</b>
 * <p> 
 * {@link #Game()}
 * <p>
 * <b>{@literal Methods: }</b>
 * <p>
 * {@link #backgroundMusic(String path)}
 * <p>
 * {@link #changeTile(int tileID)}
 * <p>
 * {@link #getKeyListener()}
 * <p>
 * {@link #getMap()}
 * <p>
 * {@link #getMouseListener()}
 * <p>
 * {@link #getRectangleBackground()}
 * <p>
 * {@link #getRenderer()}
 * <p>
 * {@link #getSelectedTile()}
 * <p>
 * {@link #getXZoom()}
 * <p>
 * {@link #getYZoom()}
 * <p>
 * {@link #handleCTRL(boolean[] keys)}
 * <p>
 * {@link #leftClick(int x, int y)}
 * <p>
 * {@link #loadImage(String path)}
 * <p>
 * {@link #loadSprite(String path)}
 * <p>
 * {@link #render()}
 * <p>
 * {@link #rightClick(int x, int y)}
 * <p>
 * {@link #run()}
 * <p>
 * {@link #update()}
 * <p>
 * <b>{@literal Public Variables: }</b>
 * <p>
 * <b>final int</b> {@link #xZoom}
 * <p>
 * <b>final int</b> {@link #yZoom}
 * <p>
 * @author Brandon Carlsen, David Lichliter, Michael Zamora
 */
@SuppressWarnings("serial")
public class Game implements Runnable {
	//alpha color			0xFF FF00DC	
	public static int alpha = 0xFFFF00DC;
	
	private RenderHandler renderer;

	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private MouseEventListener mouseListener = new MouseEventListener(this);
	
	//Zooms in on an image
	public final int xZoom = 3;
	public final int yZoom = 3;
	public int screenWidth;
	public int screenHeight;
	
	private Level[] level;
	private int currentLevel;
	
	private long window;
	
	public boolean running = false;

	private boolean hasWindow = false;
	
	private Thread gameThread;
	
	private LevelOpenGL levelOpenGL;
	
	private int i = 0;
	
	public static void main(String[] args) {
        new Game().start();
        //Creates "game" object
	}
	
	public void start() {
		running = true;
		
		gameThread = new Thread(this, "Game Thread");
		gameThread.start();
	}
	
	public void onStart() {
		level = new Level[4];
        level[0] = new StartScreen(this);
        level[1] = new Level_1(this);
        level[2] = new Level_2(this);
        level[3] = new Level_3(this);
        
		currentLevel = 0;
		
		for(int i = 0; i < level.length; i++) {
			System.out.println("Level: " + i);
			level[i].setupLevel();
		}
	}

	public void update() {
		//level[currentLevel].updateLevel();
		
		glfwPollEvents();
		if(Input.keys[GLFW_KEY_SPACE]) {
			System.out.println("Pressed: SPACE");
		}
	}
	
	public void render() {
		/*BufferStrategy bufferStrategy = getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);*/
		
		//renders in linear order. Newest will be rendered over older
		//level[currentLevel].renderLevel();
		
		//renderer.render();
		//renderer.clear();
		
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		
		if (i % 2 == 1) {
		levelOpenGL.render();
		i=0;
		System.out.println("frame: " + i);
		}else {
			i++;
			System.out.println("frame: " + i);
		}
		
		int error = glGetError();
		if(error != GL_NO_ERROR) {
			System.out.println(error);
		}
		
		if(hasWindow)
			glfwSwapBuffers(window);
	}

	public void run() {
		configureOpenGL();
		
		long lastTime = System.nanoTime(); //long 2^63
		double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
		double changeInSeconds = 0;

		while(running) {
			long now = System.nanoTime();

			changeInSeconds += (now - lastTime) / nanoSecondConversion;
			while(changeInSeconds >= 1) {
				update();
				changeInSeconds--;
			}

			render();
			lastTime = now;
			if(hasWindow)
			if(glfwWindowShouldClose(window)){
		        running = false;
		    }
		}
	}

	public void handleCTRL(boolean[] keys) {
		level[currentLevel].handleCTRL(keys);
	}

	public void handleEsc(boolean[] keys) {
		level[currentLevel].handleEsc(keys);
	}

	public void leftClick(int x, int y) {
		level[currentLevel].leftClick(x, y);
	}

	public void rightClick(int x, int y) {
		level[currentLevel].rightClick(x, y);
	}

	public void changeTile(int tileID) {
		level[currentLevel].changeTile(tileID);
	}
	
	public void setLevel(int incrementor) {
		if(currentLevel + incrementor < level.length) {
			System.out.println("Level change from level " + currentLevel + ", to level" + currentLevel + incrementor);
			currentLevel += incrementor;
			level[currentLevel].startLevel();
		}else
			System.out.print("Attempted Level change from Level "+ currentLevel + ", to level" + currentLevel + incrementor);
	}
	
	public void setPauseOption(int tileID) {
		level[currentLevel].setPauseOption(tileID);
	}
	
	public void configureOpenGL() {
		if(!glfwInit()){
		      // Throw an error.
		      System.err.println("GLFW initialization failed!");
		}
		
		screenWidth = 1280;//glfwGetVideoMode(glfwGetPrimaryMonitor()).width();
		screenHeight = 720;//glfwGetVideoMode(glfwGetPrimaryMonitor()).height();
		System.out.println("Screen Width: " + screenWidth + ", Screen Height: " + screenHeight);
		//renderer = new RenderHandler(screenWidth, screenHeight);
		
	    window = glfwCreateWindow(screenWidth, screenHeight, "Alpha Quest", NULL, NULL);

	    // This code performs the appropriate checks to ensure that the
	    // window was successfully created. 
	    // If not then it prints an error to the console
	    if(window == NULL){
	      // Throw an Error
	      System.err.println("Could not create our Window!");
	      return;
	    }else {
	    	hasWindow = true;
	    }
	    
	    // creates a bytebuffer object 'vidmode' which then queries 
	    // to see what the primary monitor is. 
	    GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
	    
	    glfwSetKeyCallback(window, new Input());
	    // Sets the initial position of our game window. 
	    glfwSetWindowPos(window, (vidmode.width() - screenWidth) /2, (vidmode.height() - screenHeight) /2);
	    // Sets the context of GLFW, this is vital for our program to work.
	    glfwMakeContextCurrent(window);
	    // finally shows our created window in all it's glory.
	    glfwShowWindow(window);
	    
	    GL.createCapabilities();
	    
	    Shader.loadAll();
	    
	    //projection matrix for a 16 by 9 monitor
	    Shader.BACKGROUND.enable();
	    Matrix4f pr_matrix = Matrix4f.orthographic(-10.0f, 10.0f, -10.0f * 9.0f / 16.0f, 10.0f * 9.0f / 16.0f, -1.0f, 1.0f);
	    Shader.BACKGROUND.setUniformMat4f("pr_matrix", pr_matrix);
	    Shader.BACKGROUND.disable();
	    
	    levelOpenGL = new LevelOpenGL();
	    
	    //tests
	    //glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
	    //glEnable(GL_DEPTH_TEST);
	    System.out.println("OPENGL Version : " + glGetString(GL_VERSION));
    }
	
	public KeyBoardListener getKeyListener() {
		return keyListener;
	}
	
	public MouseEventListener getMouseListener() {
		return mouseListener;
	}
	
	public RenderHandler getRenderer() {
		return renderer;
	}

	public Map getMap(){
		return level[currentLevel].getMap();
	}
	
	public Rectangle getRectangleBackground() {
		return level[currentLevel].getRectangleBackground();
	}

	public int getSelectedPauseOption() {
		return level[currentLevel].getSelectedPauseOption();
	}
	
	public int getSelectedTile() {
		return level[currentLevel].getSelectedTile();
	}

	public String[] saveMap() {
		FileChooser c = new FileChooser();
		String[] s = new String[2];
		s[0] = c.getFileName();
		s[1] = c.getFilePath();
		return s;
	}
}
