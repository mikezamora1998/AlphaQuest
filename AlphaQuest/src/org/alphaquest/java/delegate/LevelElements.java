package org.alphaquest.java.delegate;

import java.awt.image.BufferedImage;

import org.alphaquest.java.game.Map;
import org.alphaquest.java.game.Player;
import org.alphaquest.java.game.Sprite;
import org.alphaquest.java.math.Rectangle;

public interface LevelElements {

	public void startLevel();
	public void endLevel();
	public void updateLevel(GameObject[] o);
	public void renderLevel();

	public void handleKeyPressed(boolean[] keys);
	public void handleCTRL(boolean[] keys);
	public void leftClick(int x, int y);
	public void rightClick(int x, int y);
	public void changeTile(int tileID);
	public Sprite loadSprite(String path);
	
	public int getSelectedTile();
	public boolean hasLevelEnded();
	public BufferedImage[] getBackgroundImages();
	public Map getMap();
	public GameObject[] getObjects();
	public void setPauseOption(int tileID);
	public Player getPlayer();
	public Rectangle getRectangleBackground();
	public int getSelectedPauseOption();
}
