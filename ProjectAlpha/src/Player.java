
public class Player implements GameObject{

	Rectangle playerRectangle;
	//controls the speed of the player
	int speed = 30;
	
	
	public Player() {
		playerRectangle = new Rectangle(32, 16, 16, 32);
		playerRectangle.generateGraphics(3, 0xff00ff00);
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		renderer.renderRectangle(playerRectangle, xZoom, yZoom);
	}

	@Override
	public void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();
		
		if(keyListener.up()) {
			playerRectangle.y -= speed;
		}if(keyListener.down()) {
			playerRectangle.y += speed;
		}if(keyListener.left()) {
			playerRectangle.x -= speed;
		}if(keyListener.right()) {
			playerRectangle.x += speed;
		}
	}

}
