package org.alphaquest.java.level;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.Timer;

import org.alphaquest.java.Game;
import org.alphaquest.java.delegate.GameObject;
import org.alphaquest.java.Toolkit.KeyBoardListener;
import org.alphaquest.java.Toolkit.MouseEventListener;
import org.alphaquest.java.Toolkit.Sound;
import org.alphaquest.java.Toolkit.ToolKit;
import org.alphaquest.java.delegate.LevelElements;
import org.alphaquest.java.delegate.LevelDeligate;
import org.alphaquest.java.game.Map;
import org.alphaquest.java.game.Player;
import org.alphaquest.java.game.Sprite;
import org.alphaquest.java.game.SpriteSheet;
import org.alphaquest.java.game.Tiles;
import org.alphaquest.java.gui.GUI;
import org.alphaquest.java.gui.GUIButtons;
import org.alphaquest.java.gui.SDKButton;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.render.RenderHandler;

public class StartScreen extends LevelDeligate{

	private Game game;
	
	private BufferedImage[] bgLayer;

	private SpriteSheet textSheet;

	private Tiles startTiles;

	private GameObject[] objects;

	private int selectedTileID;

	private KeyBoardListener keyListener;

	private MouseEventListener mouseListener;

	private Map map;

	private Rectangle background;

	private int xZoom;

	private int yZoom;

	private int screenWidth;

	private int screenHeight;

	private RenderHandler renderer;

	private boolean gameStart;

	private boolean forward;

	private boolean isEnded;
	
	public StartScreen(Game game) {
		this.game = game;
		this.xZoom = game.xZoom;
		this.yZoom = game.yZoom;
		this.screenWidth = game.screenWidth;
		this.screenHeight = game.screenHeight;
		this.mouseListener = game.getMouseListener();
		this.keyListener = game.getKeyListener();
		this.renderer = game.getRenderer();
	}

	@Override
	public void setupLevel() {
		forward = false;
		isEnded = false;
		
		background = new Rectangle(0,0,8000,1500);
		//Opening music
		Timer timer = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Sound.opening.play();
			}
		});
		timer.setRepeats(false); // Only execute once
		timer.start();
		
		ToolKit tk = new ToolKit();
		bgLayer = new BufferedImage[4];
		bgLayer[0] = tk.loadImage("/background C layer1.png");
		bgLayer[1] = tk.loadImage("/background C layer2 p1.png");
		bgLayer[2] = tk.loadImage("/background C layer2 p2.png");
		bgLayer[3] = tk.loadImage("/background C layer2 p3.png");
		
		//load sprite sheets
		BufferedImage textSheetImage = tk.loadImage("/font sheet.png");
		textSheet = new SpriteSheet(textSheetImage);
		textSheet.loadSprites(20, 20);
		
		//Load Tiles
		startTiles = new Tiles(new File(tk.filePathString("/StartTiles.txt")), textSheet);

		GUIButtons[] startButtons = new GUIButtons[startTiles.size()];
		Sprite[] startTileSprites = startTiles.getSprite();
		int guiSpacing = 5;
		for(int i = 0; i < startButtons.length; i++) {
			Rectangle startTileRectangle = new Rectangle((i * (startTileSprites[i].getWidth() * xZoom + guiSpacing)) + 50, 100, startTileSprites[i].getWidth() * xZoom, startTileSprites[i].getHeight() * yZoom);
			startButtons[i] = new SDKButton(game, i,startTileSprites[i], startTileRectangle);
		}
		GUI startButton = new GUI(startButtons, 5, 5, true);
		
		objects = new GameObject[1];
		objects[0] = startButton;
	}
	
	@Override
	public void startLevel() {}
	
	@Override
	public Sprite loadSprite(String path) {
		return new Sprite(new ToolKit().loadImage(path));
	}

	@Override
	public void handleKeyPressed(boolean[] keys) {}

	@Override
	public void handleCTRL(boolean[] keys) {
		if(keys[KeyEvent.VK_Q]) {
			System.exit(0);
		}
	}

	@Override
	public void handleEsc(boolean[] keys) {}

	@Override
	public void leftClick(int x, int y) {
		
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		
		gameStart = objects[0].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
		
		Sound.start.play();
		Sound.opening.stop();
		Timer timer = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				endLevel();
			}
		});
		timer.setRepeats(false); // Only execute once
		timer.start();
	}

	@Override
	public void rightClick(int x, int y) {}

	@Override
	public void changeTile(int tileID) {}

	@Override
	public int getSelectedTile() {return selectedTileID;}

	@Override
	public Map getMap() {return map;}

	@Override
	public Rectangle getRectangleBackground() {return background;}

	@Override
	public boolean hasLevelEnded() {return isEnded;}

	@Override
	public BufferedImage[] getBackgroundImages() {return bgLayer;}

	@Override
	public GameObject[] getObjects() {return objects;}

	@Override
	public GameObject[] getPauseObjects() {return null;}

	@Override
	public int getSelectedPauseOption() {return 0;}

	@Override
	public void setPauseOption(int tileID) {}

	@Override
	public void endLevel() {
		if(!isEnded) {
			game.setLevel(1);
			isEnded = true;
		}
	}

	@Override
	public Player getPlayer() {
		return null;
	}

	@Override
	public void updateLevel(GameObject[] o) {
		for(int i = 0; i < objects.length; i++)
			objects[i].update(game);
	}

	@Override
	public void renderLevel() {

		int bgX = 0;
		int bgY = 0;
		
		renderer.renderImage(bgLayer[0], bgX, bgY, 2, 2, true);
		
		if(renderer.getCamera().x<2860)
			renderer.renderImage(bgLayer[1], bgX, bgY, 2, 2, false);

		bgX = bgLayer[1].getWidth()*2;
		if(renderer.getCamera().x + 950 >860 && renderer.getCamera().x<4820)
			renderer.renderImage(bgLayer[2], bgX, bgY, 2, 2, false);
		//System.out.println(level[currentLevel].getObjects());
		
		objects[0].render(renderer, xZoom, yZoom);
		
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
}
