
public class Player implements GameObject{

	private Rectangle playerRectangle;
	//controls the speed of the player
	private int speed = 16;
	private Sprite sprite;
	private AnimatedSprite animatedSprite= null;
	//0 = right, 1 = left, 2 = up, 3 = down
	private int direction = 0;
	
	public Player(Sprite sprite) {
		this.sprite = sprite;
		if(sprite instanceof AnimatedSprite) {
			animatedSprite = (AnimatedSprite) sprite;
		}
		updateDirection();
		playerRectangle = new Rectangle(32, 16, 16, 16);
		playerRectangle.generateGraphics(3, 0xff923459);
	}
	
	private void updateDirection() {
		if(animatedSprite != null) {
			animatedSprite.setAnimationRange(direction * 8, (direction * 8) + 7);
		}
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		
		if(animatedSprite != null) {
			renderer.renderSprite(animatedSprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		}else if(sprite != null){
			renderer.renderSprite(sprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		}else {
			renderer.renderRectangle(playerRectangle, xZoom, yZoom, false);
		}
	}

	@Override
	public void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();

		boolean didMove = false;
		int newDirection = direction;
		
		if(keyListener.left()) {
			playerRectangle.x -= speed;
			newDirection = 1;
			didMove = true;
		}if(keyListener.right()) {
			playerRectangle.x += speed;
			newDirection = 0;
			didMove = true;
		}if(keyListener.up()) {
			playerRectangle.y -= speed;
			newDirection = 2;
			didMove = true;
		}if(keyListener.down()) {
			playerRectangle.y += speed;
			newDirection = 3;
			didMove = true;
		}
		
		if(newDirection != direction) {
			direction = newDirection;
			updateDirection();
		}
		updateCamera(game.getRenderer().getCamera());
		
		if(!didMove) {
			animatedSprite.reset();
		}
		if(didMove) {
			animatedSprite.update(game);
		}
	}
	
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		return false;
	}

	public void updateCamera(Rectangle camera) {
		camera.x = playerRectangle.x - (camera.w / 2);
		camera.y = playerRectangle.y - (camera.h / 2);
		
	}
}
