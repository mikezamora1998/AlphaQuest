import java.awt.image.BufferedImage;
//4:54
public class AnimatedSprite extends Sprite implements GameObject{

	private Sprite[] sprites;
	private int currentSprite = 0;
	private int speed;
	private int counter;
	
	//speed represents how many frames pass until the sprite changes
	public AnimatedSprite(BufferedImage[] images, int speed) {
		sprites = new Sprite[images.length];
		this.speed = speed;
		
		for(int i = 0; i < images.length; i++) {
			sprites[i] = new Sprite(images[i]);
		}
	}

	//Render is dealt specifically with the Layer class
	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		
	}

	@Override
	public void update(Game game) {
		counter++;
		if(counter >= speed) {
			counter = 0;
			incrementSprite();
		}
	}
	
	public void incrementSprite() {
		currentSprite++;
		if(currentSprite >= sprites.length) {
			currentSprite = 0;
		}
	}

	public int getWidth() {
		return sprites[currentSprite].getWidth();
	}
	
	public int getHeight() {
		return sprites[currentSprite].getHeight();
	}

	public int[] getPixels() {
		return sprites[currentSprite].getPixels();
	}
}
