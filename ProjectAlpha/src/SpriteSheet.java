import java.awt.image.BufferedImage;

/**
 * Handles SpriteSheet functions.
 * <p>
 * @see <b>{@literal Constructor: }</b>
 * <p> 
 * {@link #SpriteSheet(BufferedImage sheetImage)}
 * <p>
 * <b>{@literal Methods: }</b>
 * <p>
 * {@link #loadSprites(int, int)}
 * <p>
 * {@link #getSprite(int, int)}
 * <p>
 * {@link #getPixels()}
 * <p>
 * {@link #getImage()}
 * <p>
 * <b>{@literal Public Variables: }</b>
 * <p>
 * <b>final int</b> {@link #SIZEX}
 * <p>
 * <b>final int</b> {@link #SIZEY}
 * <p>
 * @author Michael, David, Brandon
 */
public class SpriteSheet {
	
	/**
	 * Integer array of pixels
	 * @see #getPixels()
	 */
	private int[] pixels;
	
	/**
	 * BufferedImage = SpriteSheet Image.
	 * @see #getImage()
	 */
	private BufferedImage image;
	
	/**
	 * Width of Sprites X.
	 */
	private int spriteSizeX;
	
	/**
	 * Size <b>X</b> (Width) of SpriteSheet.
	 * <p>
	 * Final can only be set once.
	 * @see SpriteSheet
	 */
	public final int SIZEX;
	
	/**
	 * Size <b>Y</b> (Height) of SpriteSheet.
	 * <p>
	 * Final can only be set once.
	 * @see SpriteSheet
	 */
	public final int SIZEY;
	
	/**
	 * Sprite Array of all loaded Sprites.
	 * @see #loadSprites(int spriteSizeX, int spriteSizeY)
	 */
	private Sprite[] loadedSprites = null;
	
	/**
	 * Boolean, false by default, if true Sprites are loaded.
	 * @see #loadSprites(int spriteSizeX, int spriteSizeY)
	 */
	public boolean spritesLoaded = false;
	
	/**
	 * Constructor for SpriteSheet.
	 * <p>
	 * Accepts a BufferedImage Object.
	 * @param sheetImage BufferedImage
	 */
	public SpriteSheet(BufferedImage sheetImage) {
		image = sheetImage;
		SIZEX = sheetImage.getWidth();
		SIZEY = sheetImage.getHeight();
		
		pixels = new int[SIZEX*SIZEY];
		pixels = sheetImage.getRGB(0, 0, SIZEX, SIZEY, pixels, 0, SIZEX);
	}
	
	/**
	 * Loads all Sprites from SpriteSheet into the loadedSprites array.
	 * <p>
	 * Sets {@link #spritesLoaded} = true
	 * @param spriteSizeX <b>int</b>
	 * @param spriteSizeY <b>int</b>
	 */
	public void loadSprites(int spriteSizeX, int spriteSizeY) {
		
		this.spriteSizeX = spriteSizeX;
		loadedSprites = new Sprite[(SIZEX / spriteSizeX) * (SIZEY / spriteSizeY)];

		int spriteID = 0;
		for(int y = 0; y <SIZEY; y += spriteSizeY) {
			for(int x = 0; x< SIZEX; x += spriteSizeX) {
				
				loadedSprites[spriteID] = new Sprite(this, x, y, spriteSizeX, spriteSizeY);
				spriteID++;
			}
		}
		
		spritesLoaded = true;
	}
	
	/**
	 * Gets Sprite at a specific <b>X</b> and <b>Y</b> position in the SpriteSheet.
	 * @param x <b>int</b>
	 * @param y <b>int</b>
	 * @return <b>loadedSprites[spriteId]</b>	
	 * <p>
	 * (or <b>null</b> , if an error is encountered)
	 */
	public Sprite getSprite(int x, int y) {
		if(spritesLoaded) {
			int spriteID = x + y * (SIZEX / spriteSizeX);
			
			if(spriteID < loadedSprites.length) {
				return loadedSprites[spriteID];
			}else {
				System.out.println("SpriteID of " + spriteID + " is out of the range with a length of " + loadedSprites.length + ".");
			}
		}else {
			System.out.print("SpriteSheet could not get a sprite with no loaded sprites.");
		}
		
		return null;
	}
	
	public Sprite[] getLoadedSprites() {
		return loadedSprites;
	}
	
	/**
	 * Gets pixels array.
	 * @return <b>pixels int[]</b>
	 * @see #pixels
	 */
	public int[] getPixels() {
		return pixels;
	}
	
	/**
	 * Gets BufferedImage image.
	 * @return <b>BufferedImage image</b>
	 * @see #image
	 */
	public BufferedImage getImage() {
		return image;
	}
}
