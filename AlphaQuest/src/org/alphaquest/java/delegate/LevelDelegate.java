package org.alphaquest.java.delegate;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import org.alphaquest.java.Game;
import org.alphaquest.java.Toolkit.ToolKit;
import org.alphaquest.java.functions.Sprite;
import org.alphaquest.java.functions.SpriteSheet;
import org.alphaquest.java.functions.Tiles;
import org.alphaquest.java.gui.GUI;
import org.alphaquest.java.gui.GUIButtons;
import org.alphaquest.java.gui.PauseButton;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.render.RenderHandler;

public abstract class LevelDelegate implements LevelElements, LoadingDelegate {
	
	private GUI pauseButton;
	private GameObject[] pauseObjects;
	public int selectedPauseOption;
	
	public ToolKit tk;
	private Tiles startTiles;
	public SpriteSheet textSheet;
	private Rectangle box;
	
	public Game game;
	public RenderHandler renderer;
	
	public int xZoom;
	public int yZoom;
	public int screenWidth;
	public int screenHeight;
	
	public boolean pause = false;
	
	public LevelDelegate() {
		
	}
	
	public void setup(Game game) {
		this.xZoom = game.xZoom;
		this.yZoom = game.yZoom;
		this.screenWidth = game.screenWidth;
		this.screenHeight = game.screenHeight;
		
		
		renderer = game.getRenderer();
		tk = new ToolKit();
		
		BufferedImage textSheetImage = tk.loadImage("/font sheet.png");
		textSheet = new SpriteSheet(textSheetImage);
		textSheet.loadSprites(20, 20);
		
		startTiles = new Tiles(new File(tk.filePathString("/StartTiles.txt")), textSheet);
		
		int guiSpacing = 5;
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
		
		pauseObjects = new GameObject[1];
		pauseObjects[0] = pauseButton;
		
		
		setup();
	}
	
	public void close() {
		System.exit(0);
	}
	
	public void handleEsc(boolean[] keys) {
		if(keys[KeyEvent.VK_ESCAPE] && !pause) {
			pause = true;
		}else {
			pause = false;
		}
	}
	
	public void handleLeftClick(int x, int y) {
		
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		boolean stopChecking = false;
		
		if(pause) {
			if(!stopChecking) {
				stopChecking = pauseObjects[0].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
			}
		}else
			leftClick(x,y);
	}
	
	public void handleRightClick(int x, int y) {
		if(!pause)
			rightClick(x,y);
	}
	
	
	public void render() {
		renderLevel();
		
		if(pause)
			pauseObjects[0].render(renderer, xZoom, yZoom);
	}
	
	public void update() {
		
		GameObject[] o = null;
		if(!pause)
			o = getObjects();
		else
			o = pauseObjects;
		
		
		if(o != null)
			updateLevel(o);
		else
			System.err.println("NO OBJECTS TO UPDATE!");
	}
	
	public void defaultPauseOptions(int tileID) {
		selectedPauseOption = tileID;

		if(selectedPauseOption == 4)
			close();
	}
}
