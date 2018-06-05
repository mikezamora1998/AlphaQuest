import java.awt.Container;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.lang.Runnable;
import java.lang.Thread;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.Timer;
import javax.imageio.ImageIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
	GraphicsDevice[] devices;
	//alpha color			0xFF FF00DC	
	public static int alpha = 0xFFFF00DC;
	
	private RenderHandler renderer;
	private SpriteSheet sheet;
	private SpriteSheet textSheet;
	private SpriteSheet playerSheet;

	private Tiles tiles;
	//private Tiles textTiles;
	private Map map;
	//private Map textMap;
	private Rectangle background = new Rectangle(0,0,8000,1500);
	
	private GameObject[] objects;
	private KeyBoardListener keyListener = new KeyBoardListener(this);
	private Player player;
	private MouseEventListener mouseListener = new MouseEventListener(this);
	
	//Zooms in on an image
	public final int xZoom = 3;
	public final int yZoom = 3;
	
	private int TILESIZE = 16;
	private int selectedTileID = 0;
	private int selectedLayer = 0;

	private BufferedImage bgLayer1;
	private BufferedImage bgLayer2;
	private BufferedImage bgLayer3;
	private BufferedImage bgLayer4;

	private GameObject[] startObjects;
	private boolean gameStart;
	private boolean forward = true;
	private Tiles startTiles;

	private boolean pause = false;

	private GUI pauseButton;
	private GameObject[] pauseObjects;
	
	private String assetsPath = "/assets";

	private int screenWidth;

	private int screenHeight;
	private Rectangle box;
	private int selectedOption = 0;
	private Tiles textTiles;
	
	public Game(GraphicsDevice device) {
		super(device.getDefaultConfiguration());
		System.setProperty("sun.java2d.xrender", "true");
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        devices = env.getScreenDevices();

        begin(devices[0], getContentPane()); 
		
		//prints the file path to assets folder
		//System.out.println("GrassTile.png location. = " + Game.class.getResource("assets/GrassTile.png"));
		//System.out.println("Tiles.txt location. = " + Game.class.getResource("assets/Tiles.txt"));
		System.out.println("Main class Location: " + Game.class.getClassLoader().getResource("").getPath());
		gameStart = true;
		
		//Opening music
		Timer timer = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Sound.opening.play();
			}
		});
		timer.setRepeats(false); // Only execute once
		timer.start();
		
		bgLayer1 = loadImage("/background C layer1.png");
		bgLayer2 = loadImage("/background C layer2 p1.png");
		bgLayer3 = loadImage("/background C layer2 p2.png");
		bgLayer4 = loadImage("/background C layer2 p3.png");
		
		//size of the blocks in the sprite sheet. (x, y) 16px by 16px default
		BufferedImage sheetImage = loadImage("/Tiles1.png");
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(TILESIZE, TILESIZE);
		
		BufferedImage textSheetImage = loadImage("/font sheet.png");
		textSheet = new SpriteSheet(textSheetImage);
		textSheet.loadSprites(20, 20);
		
		BufferedImage playerSheetImage = loadImage("/Player.png");
		playerSheet = new SpriteSheet(playerSheetImage);
		playerSheet.loadSprites(20, 26);

		//Load Tiles
		//TODO: Use these for EXE Testing
		//tiles = new Tiles(new File(filePathString("/Tiles.txt")), sheet);
		//startTiles = new Tiles(new File(filePathString("/StartTiles.txt")), textSheet);
		//map = new Map(new File(filePathString("/Map.txt")), tiles);
			
		//TODO: Use these for eclipse testing
		startTiles = new Tiles(new File("assets/StartTiles.txt"), textSheet);
		tiles = new Tiles(new File("assets/Tiles.txt"), sheet);
		map = new Map(new File("assets/Map.txt"), tiles);
		
		//TODO: Implement Later
		textTiles = new Tiles(new File("assets/TextTiles.txt"), textSheet);
		//textMap = new Map(new File("assets/TextMap.txt"), textTiles);
		
		//player animation sprites
		AnimatedSprite playerAnimations = new AnimatedSprite(playerSheet, 5);
		
		//load SDK GUI
		GUIButtons[] buttons = new GUIButtons[tiles.size()];
		Sprite[] tileSprites = tiles.getSprite();
		int guiSpacing = 5;
		for(int i = 0; i < buttons.length; i++) {
			//Rectangle tileRectangle = new Rectangle(0, i * (TILESIZE * xZoom + guiSpacing), TILESIZE * xZoom, TILESIZE * yZoom);
			Rectangle tileRectangle = new Rectangle(0, i * (tileSprites[i].getWidth() * xZoom + guiSpacing), tileSprites[i].getWidth() * xZoom, tileSprites[i].getHeight() * yZoom);
			buttons[i] = new SDKButton(this, i,tileSprites[i], tileRectangle);
		}
		
		GUI gui = new GUI(buttons, 5, 5, true);
		
		
		GUIButtons[] startButtons = new GUIButtons[startTiles.size()];
		Sprite[] startTileSprites = startTiles.getSprite();
		for(int i = 0; i < startButtons.length; i++) {
			Rectangle startTileRectangle = new Rectangle((i * (startTileSprites[i].getWidth() * xZoom + guiSpacing)) + 50, 100, startTileSprites[i].getWidth() * xZoom, startTileSprites[i].getHeight() * yZoom);
			startButtons[i] = new SDKButton(this, i,startTileSprites[i], startTileRectangle);
		}
		GUI startButton = new GUI(startButtons, 5, 5, true);
		
		//System.out.println(screenWidth);
		//TODO: Escape test buttons
		GUIButtons[] pauseButtons = new GUIButtons[startTiles.size()]; //array length = number of options in start menu
		Sprite[] pauseTileSprites = startTiles.getSprite();
		for(int i = 0; i < pauseButtons.length; i++) {
			int rw = 100;
			int rh = 200;
			int rx = (screenWidth/2) - (rw * xZoom /2);
			int ry = (screenHeight/2) - (rh * yZoom /2);
			box = new Rectangle(rx, ry, rw, rh);
			
			int x = (box.x + (box.w * xZoom/2) - (pauseTileSprites[i].getWidth() * xZoom/2));
			int y = (box.y + (pauseTileSprites[i].getHeight() * yZoom/2) + i * ((pauseTileSprites[i].getHeight() + guiSpacing)  * yZoom));
			int w = startTileSprites[i].getWidth() * xZoom;
			int h = pauseTileSprites[i].getHeight() * yZoom;
			
			//System.out.println("X: " + x + ", Y: " + y + ", W: " + w + ", H:" + h);
			Rectangle pauseTileRectangle = new Rectangle(x, y, w, h);
			pauseButtons[i] = new PauseButton(this, i, pauseTileSprites[i], pauseTileRectangle);
		}
		pauseButton = new GUI(pauseButtons, 5, 5, true);
		
		/*GUIButtons[] textButtons = new GUIButtons[textTiles.size()];
		Sprite[] textTileSprites = textTiles.getSprite();
		for(int i = 0; i < startButtons.length; i++) {
			Rectangle textTileRectangle;
			if(i < 18) {
				textTileRectangle = new Rectangle((i * (textTileSprites[i].getWidth() * xZoom + guiSpacing)) + 50, 0, textTileSprites[i].getWidth() * xZoom, textTileSprites[i].getHeight() * yZoom);
			}else if(i < 36) {
				textTileRectangle = new Rectangle(((i-18) * (textTileSprites[i].getWidth() * xZoom + guiSpacing)) + 50, (textTileSprites[i].getWidth() * xZoom + guiSpacing), textTileSprites[i].getWidth() * xZoom, textTileSprites[i].getHeight() * yZoom);
			}else {
				textTileRectangle = new Rectangle(((i-36) * (textTileSprites[i].getWidth() * xZoom + guiSpacing)) + 50, (textTileSprites[i].getWidth() * xZoom + guiSpacing) * 2, textTileSprites[i].getWidth() * xZoom, textTileSprites[i].getHeight() * yZoom);
			}
			textButtons[i] = new TextButton(this, i,textTileSprites[i], textTileRectangle);
		}
		GUI textGUI = new GUI(textButtons, 5, 5, true);*/

		//Load Objects
		objects = new GameObject[2];
		player = new Player(playerAnimations, xZoom, yZoom);
		objects[0] = player;
		objects[1] = gui;
		
		startObjects = new GameObject[1];
		startObjects[0] = startButton;
		
		pauseObjects = new GameObject[1];
		//pauseObjects = new GameObject[objects.length+1];
		/*for(int i = 0; i < objects.length; i++) {
			pauseObjects[i] = objects[i];
		}*/
		pauseObjects[0] = pauseButton;
	}

	public void update() {
		if(!gameStart) {
			if(pause)
				//for(int i = 0; i < pauseObjects.length; i++)
					pauseObjects[0].update(this);
			else
				for(int i = 0; i < objects.length; i++)
					objects[i].update(this);
		}else {
			startObjects[0].update(this);
		}
	}
	
	public BufferedImage loadImage(String path) {
		try {
			//TODO: Use these for EXE Testing
			//BufferedImage loadedImage = ImageIO.read(filePathURL(path));
			
			//TODO: Use these for eclipse testing
			BufferedImage loadedImage = ImageIO.read(Game.class.getResource(path));
			
			BufferedImage formattedImage = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_INT_RGB);
			formattedImage.getGraphics().drawImage(loadedImage ,0 ,0 , null);
			
			return formattedImage;
		}catch(IOException exception){
			exception.printStackTrace();
			return null;
		}
	}
	
	public URL filePathURL(String path) {
		String dir = getClass().getResource("/" + getClass().getName().replaceAll("\\.", "/") + ".class").toString();
		dir = dir.substring(4).replaceFirst("/[^/]+\\.exe!.*$", "/");
		dir = dir + assetsPath + path;
        try {
            return new URL(dir);
        } catch (MalformedURLException e) {}
        return null;
	}
	
	public String filePathString(String path) {
		String url = getClass().getResource("/" + getClass().getName().replaceAll("\\.", "/") + ".class").toString();
        url = url.substring(4).replaceFirst("/[^/]+\\.exe!.*$", "/");
        url = url + assetsPath + path;
        try {
            File dir = new File(new URL(url).toURI());
            url = dir.getAbsolutePath();
        } catch (MalformedURLException e) {
            url = null;
        } catch (URISyntaxException e) {
            url = null;
        }
        return url;
	}
	
	public Sprite loadSprite(String path) {
		return new Sprite(loadImage(path));
	}
	
	public void handleCTRL(boolean[] keys) {
		if(keys[KeyEvent.VK_S]) {
			map.saveMap();
		}
		if(keys[KeyEvent.VK_Q]) {
			System.exit(0);
		}
	}
	
	public void handleEsc(boolean[] keys) {
		if(keys[KeyEvent.VK_ESCAPE] && !gameStart && !pause) {
			pause = true;
		}else {
			pause = false;
		}
	}
	
	public void leftClick(int x, int y) {
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		if(!gameStart) {
			boolean stopChecking = false;
			
			if(pause) {
				if(!stopChecking) {
					stopChecking = pauseObjects[0].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
				}
				System.out.println(pause);
			}else
				for(int i = 0; i < objects.length; i++) {
					if(!stopChecking) {
						stopChecking = objects[i].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
					}
				}
			
			if(!stopChecking) {
				if(pause) {
					
				}else {
					//Divide by tile size default is 16
					x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
					y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
					//(x, y, TileID)
					map.setTile(selectedLayer, x, y, selectedTileID);
				}
			}
		}else {
			gameStart = startObjects[0].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
			Sound.start.play();
			Sound.opening.stop();
			Timer timer = new Timer(1500, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					changeStart(false);
					Sound.backGround.play();
				}
			});
			timer.setRepeats(false); // Only execute once
			timer.start();
		}
	}
	
	public void rightClick(int x, int y) {
		//Divide by tile size default is 16
		if(!pause)
		if(!gameStart) {
			x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
			y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
			map.removeTile(selectedLayer, x, y);
		}
	}
	
	public void changeTile(int tileID) {
		selectedTileID = tileID;
	}
	
	public int getSelectedTile() {
		return selectedTileID;
	}
	
	public void render() {
		BufferStrategy bufferStrategy = getBufferStrategy();
		Graphics graphics = bufferStrategy.getDrawGraphics();
		super.paint(graphics);
		
		//renders in linear order. Newest will be rendered over older
		//renders background images
		int bgX = 0;
		int bgY = 0;

		renderer.renderImage(bgLayer1, bgX, bgY, 2, 2, true);
		
		if(!gameStart) {
			
			
			if(player.getRectangle().x<2860)
				renderer.renderImage(bgLayer2, bgX, bgY, 2, 2, false);

			bgX = bgLayer2.getWidth()*2;
			if(player.getRectangle().x>860 && player.getRectangle().x<4820)
				renderer.renderImage(bgLayer3, bgX, bgY, 2, 2, false);
			
			bgX = bgLayer2.getWidth()*2*2;
			if(player.getRectangle().x>2820 && player.getRectangle().x<6720)
				renderer.renderImage(bgLayer4, bgX, bgY, 2, 2, false);
			
			bgX = bgLayer2.getWidth()*2*3;
			if(player.getRectangle().x>4620 && player.getRectangle().x<8620)
				renderer.renderImage(bgLayer2, bgX, bgY, 2, 2, false);
			
			bgX = bgLayer2.getWidth()*2*4;
			if(player.getRectangle().x>6720)
				renderer.renderImage(bgLayer3, bgX, bgY, 2, 2, false);
			
			if(pause) {
				box.generateGraphics(20, 0xFFFFFFFF);
				renderer.renderRectangle(box, xZoom, yZoom, true);
				
				map.render(renderer, pauseObjects, xZoom, yZoom);
				
			}else
				map.render(renderer, objects, xZoom, yZoom);
			
		}else {
			
			if(renderer.getCamera().x<2860)
				renderer.renderImage(bgLayer2, bgX, bgY, 2, 2, false);

			bgX = bgLayer2.getWidth()*2;
			if(renderer.getCamera().x + 950 >860 && renderer.getCamera().x<4820)
				renderer.renderImage(bgLayer3, bgX, bgY, 2, 2, false);
			
			//textMap.render(renderer, objects, xZoom, yZoom);
			startObjects[0].render(renderer, xZoom, yZoom);
			
			//panning motion
			if(renderer.getCamera().x < 1900 && forward)
				renderer.getCamera().x +=1;
			else if(!forward && renderer.getCamera().x == 0) 
				forward = true;
			else {
				renderer.getCamera().x -=1;
				forward = false;
			}
		}
			
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
				update();
				changeInSeconds--;
			}

			render();
			lastTime = now;
		}
	}
	
	public void begin(GraphicsDevice device, Container c) {
		setContentPane(c);
		boolean isFullScreen = false;

        isFullScreen = device.isFullScreenSupported();
        setUndecorated(isFullScreen);
        setResizable(!isFullScreen);
        if (isFullScreen) {
            // Full-screen mode
            device.setFullScreenWindow(this);
            validate();
        } else {
            // Windowed mode
            pack();
            setVisible(true);
        }
        
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
		System.out.println("Screen Width: " + getWidth() + ", Screen Height: " + getHeight());
		renderer = new RenderHandler(getWidth(), getHeight());
    }
	
	public static void main(String[] args) {
        Game game = new Game(GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[0]);
        //Creates "game" object
		Thread gameThread = new Thread(game);
		gameThread.start();
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
		return map;
	}
	
	public Rectangle getRectangleBackground() {
		return background;
	}
	
	public int getXZoom() {
		return xZoom;
	}
	
	public int getYZoom() {
		return yZoom;
	}

	public void changeStart(boolean start) {
		gameStart = start;
	}

	public void changeOption(int tileID) {
		selectedOption = tileID;
		
		//if(selectedOption == 1)
			//pause = false;
		if(selectedOption == 4)
			System.exit(0);
	}

	public int getSelectedOption() {
		return selectedOption;
	}
}
