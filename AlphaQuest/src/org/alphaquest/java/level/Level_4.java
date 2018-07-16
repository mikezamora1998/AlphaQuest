package org.alphaquest.java.level;

import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import org.alphaquest.java.Toolkit.Sound;
import org.alphaquest.java.delegate.GameObject;
import org.alphaquest.java.delegate.LevelDeligate;
import org.alphaquest.java.game.AnimatedSprite;
import org.alphaquest.java.game.Map;
import org.alphaquest.java.game.Player;
import org.alphaquest.java.game.Sprite;
import org.alphaquest.java.game.SpriteSheet;
import org.alphaquest.java.game.Tiles;
import org.alphaquest.java.gui.GUI;
import org.alphaquest.java.gui.GUIButtons;
import org.alphaquest.java.gui.SDKButton;
import org.alphaquest.java.math.Rectangle;

public class Level_4 extends LevelDeligate{

	private BufferedImage[] bgLayer;
	private Rectangle background;
	
	private int TILESIZE = 16;
	private SpriteSheet sheet;
	private SpriteSheet playerSheet;
	
	private Map map;
	
	private Tiles tiles;

	private Player player;

	private int selectedTileID;
	private int selectedLayer;
	
	private boolean isEnded;
	
	public Level_4(){
		renderer = game.getRenderer();
	}
	
	@Override
	public void setupLevel() {
		
		background = new Rectangle(0,0,8000,1500);
		
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
		map = new Map(new File(tk.filePathString("/Map.txt")), tiles, this, game);
		
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
		
		//Load Objects
		objects = new GameObject[2];
		player =  new Player(playerAnimations, xZoom, yZoom);
		objects[0] = player;
		objects[1] = gui;

	}

	@Override
	public void startLevel() {
		Sound.backGround.play();
	}

	@Override
	public void updateLevel(GameObject[] o) {
		for(int i = 0; i < o.length; i++) {
			o[i].update(game);
		}
	}

	@Override
	public void renderLevel() {
		int bgX = 0;
		int bgY = 0;

		renderer.renderImage(bgLayer[0], bgX, bgY, 2, 2, true);
		
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
		
		map.render(renderer, objects, xZoom, yZoom);
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
	public void handleKeyPressed(boolean[] keys) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleCTRL(boolean[] keys) {
		if(keys[KeyEvent.VK_S]) {
			map.saveMap();
		}
		if(keys[KeyEvent.VK_Q]) {
			close();
		}
	}

	@Override
	public void leftClick(int x, int y) {
		Rectangle mouseRectangle = new Rectangle(x, y, 1, 1);
		
		boolean stopChecking = false;
		
		for(int i = 0; i < objects.length; i++) {
			if(!stopChecking) {
				stopChecking = objects[i].handleMouseClick(mouseRectangle, renderer.getCamera(), xZoom, yZoom);
			}
		}
		
		if(!stopChecking) {
			//Divide by tile size default is 16
			x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
			y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
			//(x, y, TileID)
			map.setTile(selectedLayer, x, y, selectedTileID);
		}
	}

	@Override
	public void rightClick(int x, int y) {
		//Divide by tile size default is 16
		x = (int) Math.floor((x + renderer.getCamera().x)/(16.0 * xZoom));
		y = (int) Math.floor((y + renderer.getCamera().y)/(16.0 * yZoom));
		map.removeTile(selectedLayer, x, y);
	}

	@Override
	public void changeTile(int tileID) {
		selectedTileID = tileID;
	}

	@Override
	public Sprite loadSprite(String path) {
		return new Sprite(tk.loadImage(path));
	}

	@Override
	public int getSelectedTile() {
		return selectedTileID;
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
	public Map getMap() {
		return map;
	}

	@Override
	public Player getPlayer() {
		return player;
	}

	@Override
	public Rectangle getRectangleBackground() {
		return background;
	}

	@Override
	public GameObject[] getObjects() {
		return objects;
	}

	@Override
	public GameObject[] getPauseObjects() {
		return null;
	}

	@Override
	public int getSelectedPauseOption() {
		return selectedPauseOption;
	}

	@Override
	public void setPauseOption(int tileID) {
		selectedPauseOption = tileID;

		if(selectedPauseOption == 4)
			close();
	}

}
