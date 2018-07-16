package org.alphaquest.java.level;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import org.alphaquest.java.Game;
import org.alphaquest.java.delegate.GameObject;
import org.alphaquest.java.Toolkit.Sound;
import org.alphaquest.java.Toolkit.ToolKit;
import org.alphaquest.java.delegate.LevelElements;
import org.alphaquest.java.delegate.LevelDeligate;
import org.alphaquest.java.game.AnimatedSprite;
import org.alphaquest.java.game.Map;
import org.alphaquest.java.game.Player;
import org.alphaquest.java.game.Sprite;
import org.alphaquest.java.game.SpriteSheet;
import org.alphaquest.java.game.Tiles;
import org.alphaquest.java.gui.GUI;
import org.alphaquest.java.gui.GUIButtons;
import org.alphaquest.java.gui.PauseButton;
import org.alphaquest.java.gui.SDKButton;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.render.RenderHandler;

public class Level_2 extends LevelDeligate{
	
	private Game game;
	private RenderHandler renderer;
	
	private int xZoom;
	private int yZoom;
	private int screenWidth;
	private int screenHeight;
	
	private BufferedImage[] bgLayer;
	private Rectangle background;
	
	private int TILESIZE = 16;
	private SpriteSheet sheet;
	private SpriteSheet textSheet;
	private SpriteSheet playerSheet;
	
	private Map map;
	
	private Tiles tiles;
	private Tiles startTiles;

	private GUI pauseButton;
	private Rectangle box;
	
	private Player player;
	
	private GameObject[] objects;
	private GameObject[] pauseObjects;

	private int selectedTileID;
	private int selectedLayer;
	private int selectedPauseOption;
	
	private boolean pause;
	private boolean isEnded;

	public Level_2(Game game) {
		this.game = game;
		this.xZoom = game.xZoom;
		this.yZoom = game.yZoom;
		this.screenWidth = game.screenWidth;
		this.screenHeight = game.screenHeight;
		this.renderer = game.getRenderer();
	}

	@Override
	public void setupLevel() {
		pause = false;
		
		background = new Rectangle(0,0,8000,1500);
		
		ToolKit tk = new ToolKit();
		bgLayer = new BufferedImage[4];
		bgLayer[0] = tk.loadImage("/background C layer1.png");
		bgLayer[1] = tk.loadImage("/background C layer2 p1.png");
		bgLayer[2] = tk.loadImage("/background C layer2 p2.png");
		bgLayer[3] = tk.loadImage("/background C layer2 p3.png");
		
		//size of the blocks in the sprite sheet. (x, y) 16px by 16px default
		BufferedImage sheetImage = tk.loadImage("/Tiles1.png");
		sheet = new SpriteSheet(sheetImage);
		sheet.loadSprites(TILESIZE, TILESIZE);
		
		BufferedImage textSheetImage = tk.loadImage("/font sheet.png");
		textSheet = new SpriteSheet(textSheetImage);
		textSheet.loadSprites(20, 20);
		
		BufferedImage playerSheetImage = tk.loadImage("/Player.png");
		playerSheet = new SpriteSheet(playerSheetImage);
		playerSheet.loadSprites(20, 26);
		
		//Load Tiles
		tiles = new Tiles(new File(tk.filePathString("/Tiles.txt")), sheet);
		LevelElements level = this;
		map = new Map(new File(tk.filePathString("/Map.txt")), tiles, level, game);
		startTiles = new Tiles(new File(tk.filePathString("/StartTiles.txt")), textSheet);

		
		//player animation sprites
		AnimatedSprite playerAnimations = new AnimatedSprite(playerSheet, 5);
			
		//load SDK GUI
		GUIButtons[] buttons = new GUIButtons[tiles.size()];
		Sprite[] tileSprites = tiles.getSprite();
		int guiSpacing = 5;
		for(int i = 0; i < buttons.length; i++) {
			//Rectangle tileRectangle = new Rectangle(0, i * (TILESIZE * xZoom + guiSpacing), TILESIZE * xZoom, TILESIZE * yZoom);
			Rectangle tileRectangle = new Rectangle(0, i * (tileSprites[i].getWidth() * xZoom + guiSpacing), tileSprites[i].getWidth() * xZoom, tileSprites[i].getHeight() * yZoom);
			buttons[i] = new SDKButton(game, i,tileSprites[i], tileRectangle);
		}
		
		GUI gui = new GUI(buttons, 5, 5, true);
		
		GUIButtons[] pauseButtons = new GUIButtons[startTiles.size()]; //array length = number of options in start menu
		Sprite[] pauseTileSprites = startTiles.getSprite();
		Sprite[] startTileSprites = startTiles.getSprite();
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
			pauseButtons[i] = new PauseButton(game, i, pauseTileSprites[i], pauseTileRectangle);
		}
		pauseButton = new GUI(pauseButtons, 5, 5, true);
		
		//TODO: Implement Later
				//textTiles = new Tiles(new File("assets/TextTiles.txt"), textSheet);
				//textMap = new Map(new File("assets/TextMap.txt"), textTiles);

				//System.out.println(screenWidth);
				//TODO: Escape test buttons
				
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
		player =  new Player(playerAnimations, xZoom, yZoom);
		objects[0] = player;
		objects[1] = gui;
		
		pauseObjects = new GameObject[1];
		//pauseObjects = new GameObject[objects.length+1];
		/*for(int i = 0; i < objects.length; i++) {
			pauseObjects[i] = objects[i];
		}*/
		pauseObjects[0] = pauseButton;
	}
	
	@Override
	public void startLevel() {
		Sound.backGround.play();
	}
	
	public void updateLevel() {
		if(!pause) {
			for(int i = 0; i < objects.length; i++) {
				objects[i].update(game);
			}
		}else {
			for(int i = 0; i < pauseObjects.length; i++)
				pauseObjects[i].update(game);
		}
	}

	@Override
	public void renderLevel() {

		int bgX = 0;
		int bgY = 0;

		//renderer.renderImage(bgLayer[0], bgX, bgY, 2, 2, true);
		
		if(player.getRectangle().x<2860)
			renderer.renderImage(bgLayer[1], bgX, bgY, 2, 2, false);

		bgX = bgLayer[1].getWidth()*2;
		if(player.getRectangle().x>860 && player.getRectangle().x<4820)
			renderer.renderImage(bgLayer[2], bgX, bgY, 2, 2, false);
		
		bgX = bgLayer[1].getWidth()*2*2;
		if(player.getRectangle().x>2820 && player.getRectangle().x<6720)
			renderer.renderImage(bgLayer[3], bgX, bgY, 2, 2, false);
		
		bgX = bgLayer[1].getWidth()*2*3;
		if(player.getRectangle().x>4620 && player.getRectangle().x<8620)
			renderer.renderImage(bgLayer[1], bgX, bgY, 2, 2, false);
		
		bgX = bgLayer[1].getWidth()*2*4;
		if(player.getRectangle().x>6720)
			renderer.renderImage(bgLayer[2], bgX, bgY, 2, 2, false);

			/*box.generateGraphics(20, 0xFFFFFFFF);
			renderer.renderRectangle(box, xZoom, yZoom, true);*/
			
		map.render(renderer, objects, xZoom, yZoom);
		
		if(pause)
			pauseObjects[0].render(renderer, xZoom, yZoom);
		
	}
	
	@Override
	public void endLevel() {
		if(!isEnded) {
			Sound.end.play();
			game.setLevel(1);
			isEnded = true;
		}
	}
	
	@Override
	public Sprite loadSprite(String path) {
		return new Sprite(new ToolKit().loadImage(path));
	}

	@Override
	public void handleKeyPressed(boolean[] keys) {
		
		
	}

	@Override
	public void handleCTRL(boolean[] keys) {
		
		if(keys[KeyEvent.VK_S]) {
			map.saveMap();
		}
		if(keys[KeyEvent.VK_Q]) {
			System.exit(0);
		}
	}

	@Override
	public void handleEsc(boolean[] keys) {
		
		if(keys[KeyEvent.VK_ESCAPE] && !pause) {
			pause = true;
		}else {
			pause = false;
		}
	}

	@Override
	public void leftClick(int x, int y) {
		
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		
			boolean stopChecking = false;
			
			if(pause) {
				if(!stopChecking) {
					stopChecking = pauseObjects[0].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
				}
				
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
		
	}

	@Override
	public void rightClick(int x, int y) {
		
		//Divide by tile size default is 16
		if(!pause) {
			x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
			y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
			map.removeTile(selectedLayer, x, y);
		}
	}

	@Override
	public void changeTile(int tileID) {
		
		selectedTileID = tileID;
	}

	@Override
	public int getSelectedTile() {
		
		return selectedTileID;
	}

	@Override
	public Map getMap() {
		
		return map;
	}

	@Override
	public Rectangle getRectangleBackground() {
		return background;
	}

	@Override
	public boolean hasLevelEnded() {
		return isEnded;
	}

	@Override
	public BufferedImage[] getBackgroundImages() {
		return bgLayer;
	}

	@Override
	public GameObject[] getObjects() {
		return objects;
	}

	@Override
	public GameObject[] getPauseObjects() {
		return pauseObjects;
	}

	@Override
	public int getSelectedPauseOption() {
		return selectedPauseOption;
	}

	@Override
	public void setPauseOption(int tileID) {
		selectedPauseOption = tileID;
		
		//if(selectedOption == 1)
			//pause = false;
		if(selectedPauseOption == 4)
			System.exit(0);
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public void updateLevel(GameObject[] o) {
		// TODO Auto-generated method stub
		
	}
}
