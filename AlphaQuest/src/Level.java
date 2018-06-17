import java.awt.image.BufferedImage;

public interface Level {

	public void setupLevel();
	public void startLevel();
	public void updateLevel();
	public void renderLevel();
	public void endLevel();

	public void handleKeyPressed(boolean[] keys);
	public void handleCTRL(boolean[] keys);
	public void handleEsc(boolean[] keys);
	public void leftClick(int x, int y);
	public void rightClick(int x, int y);
	public void changeTile(int tileID);
	public Sprite loadSprite(String path);
	
	public int getSelectedTile();
	public boolean hasLevelEnded();
	public BufferedImage[] getBackgroundImages();
	public Map getMap();
	public Player getPlayer();
	public Rectangle getRectangleBackground();
	public GameObject[] getObjects();
	public GameObject[] getPauseObjects();
	public int getSelectedPauseOption();
	public void setPauseOption(int tileID);
}
