package org.alphaquest.java;

import java.awt.Container;
import java.awt.image.BufferStrategy;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.lang.Runnable;
import java.lang.Thread;
import javax.swing.JFrame;

import org.alphaquest.java.Toolkit.FileChooser;
import org.alphaquest.java.Toolkit.KeyBoardListener;
import org.alphaquest.java.Toolkit.MouseEventListener;
import org.alphaquest.java.delegate.LevelDelegate;
import org.alphaquest.java.functions.Map;
import org.alphaquest.java.level.Level_1;
import org.alphaquest.java.level.Level_2;
import org.alphaquest.java.level.StartScreen;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.render.RenderHandler;

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
public class Game extends JFrame implements Runnable {
	//alpha color			0xFF FF00DC	
	public static int alpha = 0xFFFF00DC;
	
	private RenderHandler renderer;

	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private MouseEventListener mouseListener = new MouseEventListener(this);
	
	public final int xZoom = 3;
	public final int yZoom = 3;
	public int screenWidth;
	public int screenHeight;
	
	private LevelDelegate[] level;
	private int currentLevel;
	
	public static void main(String[] args) {
        Game game = new Game();
        //Creates "game" object
		Thread gameThread = new Thread(game);
		gameThread.start();
	}
	
	public Game() {
		super(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0].getDefaultConfiguration());
        configureJFrame(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0], getContentPane()); 

        level = new LevelDelegate[3];
        level[0] = new StartScreen(this);
        level[1] = new Level_1(this);
        level[2] = new Level_2(this);
        
		currentLevel = 0;
		
		setLevel(currentLevel);
	}

	public void update() {
		level[currentLevel].update();
	}
	
	/**
	 * renders in linear order. Newest will be rendered over older
	 */
	public void render() {
		BufferStrategy bufferStrategy = getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);

		level[currentLevel].render();
		
		renderer.render(graphics);
		graphics.dispose();
		bufferStrategy.show();
		renderer.clear();
	}

	public void run() {
		long lastTime = System.nanoTime(); //long 2^63
		double nanoSecondConversion = 1000000000.0 / 60; //60 frames per second
		double changeInSeconds = 0;
		while(true) {
			long now = System.nanoTime();
			changeInSeconds += (now - lastTime) / nanoSecondConversion;
			while(changeInSeconds >= 1) {
				//checks if the level has loaded before updating the objects
				if(level[currentLevel].isFinishedLoading()) update();
				changeInSeconds--;
			}
			//checks if the level has loaded before rendering the objects
			if(level[currentLevel].isFinishedLoading()) render();
			lastTime = now;
		}
	}

	public void handleCTRL(boolean[] keys) {
		level[currentLevel].handleCTRL(keys);
	}

	public void handleEsc(boolean[] keys) {
		level[currentLevel].handleEsc(keys);
	}

	public void leftClick(int x, int y) {
		level[currentLevel].handleLeftClick(x, y);
	}

	public void rightClick(int x, int y) {
		level[currentLevel].handleRightClick(x, y);
	}

	public void changeTile(int tileID) {
		level[currentLevel].changeTile(tileID);
	}
	
	public void setLevel(int incrementor) {
		if(currentLevel + incrementor < level.length) {
			currentLevel += incrementor;
			
			System.out.println("\nSetting up Level: " + currentLevel);
			level[currentLevel].setup(this);
			
			while(true)
				if(level[currentLevel].isFinishedLoading())
					break;
				
			System.out.println("Strating Level: " + currentLevel + "\n");
			level[currentLevel].startLevel();
		}else
			System.err.println("ATTEMPTED TO SET LEVEL OUT OF BOUNDS!");
		
	}
	
	public void setPauseOption(int tileID) {
		level[currentLevel].setPauseOption(tileID);
	}
	
	public void configureJFrame(GraphicsDevice device, Container c) {
		System.setProperty("sun.java2d.xrender", "true");
		setContentPane(c);
		boolean isFullScreen = false;

       /* isFullScreen = device.isFullScreenSupported();
        setUndecorated(isFullScreen);
        setResizable(!isFullScreen);
        if (isFullScreen) {
            // Full-screen mode
            device.setFullScreenWindow(this);
            validate();
        } else {*/
            // Windowed mode
			setBounds(0, 0, 1600, 900);
            //pack();
            setVisible(true);
        //}
        
		//Make our program shutdown when we exit out.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//Add Listeners
		addKeyListener(keyListener);
		addFocusListener(keyListener);
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);

		requestFocus();
		//Create our object for buffer strategy.
		createBufferStrategy(2);
		
		screenWidth = getWidth();
		screenHeight = getHeight();
		System.out.println("Screen Width: " + screenWidth + ", Screen Height: " + screenHeight);
		renderer = new RenderHandler(screenWidth, screenHeight);
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
		setAutoRequestFocus(true);
		String[] s = new String[2];
		s[0] = c.getFileName();
		s[1] = c.getFilePath();
		return s;
	}
}
