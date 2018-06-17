/**
 * Handles player movements and collision.
 * <p>
 * @see <b>{@literal Constructor: }</b>
 * <p> 
 * {@link #}
 * <p>
 * <b>{@literal Methods: }</b>
 * <p>
 * {@link #}
 * @author Michael, David, Brandon
 */
public class Player implements GameObject {

	private Rectangle playerRectangle;
	private Rectangle collisionCheckRectangle;
	Rectangle background;
	private Sprite sprite;
	private AnimatedSprite animatedSprite = null;
	
	private boolean yCollision;
	private boolean xCollision;
	private boolean onGround;
	
	private final int xCollisionOffset = 14;
	private final int yCollisionOffset = 20;
	private final int minimumY = (19*16*3) - 26;
	private int minY = minimumY;
	private int layer = 0;
	private int xZoom;
	private int yZoom;
	
	private int backgroundHeight = 1500;
	// 0 = right, 1 = left, 2 = up, 3 = down
	private int direction = 0;
	private int playerHeight = 26;
	private int playerWidth = 20;

	// controls the speed of the player
	private int speed = 5;
	private int jumpSpeed = 25;
	private float gravtity = 1.5f;
	private Vector2 velocity = new Vector2();

	public Player(Sprite sprite, int xZoom, int yZoom) {
		this.sprite = sprite;
		this.xZoom = xZoom;
		this.yZoom = yZoom;

		if (sprite instanceof AnimatedSprite) {
			animatedSprite = (AnimatedSprite) sprite;
		}
		updateDirection();
		// equation sets player to bottom of the screen
		playerRectangle = new Rectangle(75, 0 /*backgroundHeight - (playerHeight * xZoom)*/, playerWidth, playerHeight);
		playerRectangle.generateGraphics(3, 0xff923459);
		collisionCheckRectangle = new Rectangle(0, 0, 10 * xZoom, 15 * yZoom);
	}

	private void updateDirection() {
		if (animatedSprite != null) {
			animatedSprite.setAnimationRange(direction * 8, (direction * 8) + 7);
		}
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {

		if (animatedSprite != null) {
			renderer.renderSprite(animatedSprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		} else if (sprite != null) {
			renderer.renderSprite(sprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		} else {
			renderer.renderRectangle(playerRectangle, xZoom, yZoom, false);
		}
	}

	@Override
	public void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();
		background = game.getRectangleBackground();
		
		xCollision = false;
		yCollision = false;
		onGround = false;
		boolean didMove = false;
		int newDirection = direction;

		collisionCheckRectangle.x = playerRectangle.x;
		collisionCheckRectangle.y = playerRectangle.y;

		if (playerRectangle.y < minY)
			velocity.y += gravtity;
		else {
			velocity.y = 0;
			playerRectangle.y = minY;
			velocity.x = 0;
		}

		onGround = playerRectangle.y == minY;

		if (keyListener.left()) {
			// playerRectangle.x -= speed;
			newDirection = 1;
			didMove = true;
			velocity.x = -speed;

			collisionCheckRectangle.x -= speed;
		}
		if (keyListener.right()) {
			// playerRectangle.x += speed;
			newDirection = 0;
			didMove = true;
			velocity.x = speed;

			collisionCheckRectangle.x += speed;
		}
		if (keyListener.up()) {
			// playerRectangle.y -= speed;
			// newDirection = 2;
			if (onGround || yCollision) {
				velocity.y -= jumpSpeed;
				Sound.jump.play();
			}
			
			didMove = true;
		}
		if (keyListener.down()) {
			// playerRectangle.y += speed;
			// newDirection = 3;
			// didMove = true;
		}

		collisionCheckRectangle.x += xCollisionOffset;
		collisionCheckRectangle.y += yCollisionOffset;

		Rectangle axisCheck = new Rectangle(collisionCheckRectangle.x, playerRectangle.y + yCollisionOffset,
				collisionCheckRectangle.w, collisionCheckRectangle.h);

		// Check the X axis
		if (!game.getMap().checkCollision(axisCheck, layer, game.xZoom, game.yZoom)
				&& !game.getMap().checkCollision(axisCheck, layer + 1, game.xZoom, game.yZoom)) {

			playerRectangle.x = collisionCheckRectangle.x - xCollisionOffset;

			xCollision = false;
		} else
			xCollision = true;

		axisCheck.x = playerRectangle.x + xCollisionOffset;
		axisCheck.y = collisionCheckRectangle.y + yCollisionOffset;
		axisCheck.w = collisionCheckRectangle.w;
		axisCheck.h = collisionCheckRectangle.h;

		// Check the Y axis
		if (!game.getMap().checkCollision(axisCheck, layer, game.xZoom, game.yZoom)
				&& !game.getMap().checkCollision(axisCheck, layer + 1, game.xZoom, game.yZoom))
			yCollision = false;
			//playerRectangle.y = collisionCheckRectangle.y - yCollisionOffset;
		else
			yCollision = true;


		if (!xCollision) {
			int newX = (int) (playerRectangle.x + velocity.x);

			if (newX < 0)
				newX = 0;

			if (newX > background.w - playerRectangle.w * game.xZoom)
				newX = background.w - playerRectangle.w * game.xZoom;

			playerRectangle.x = newX;

		} //else if (xCollision && playerRectangle.y < minY)
			//playerRectangle.y += 1;

		int newY;

		if (yCollision) {
			newY = playerRectangle.y;
			minY = newY;
			if (keyListener.up())
				playerRectangle.y -= speed;
		} else
			newY = (int) (playerRectangle.y + velocity.y);

		if (newY < 0)
			newY = 0;
		if (newY > background.h - playerRectangle.h * game.yZoom)
			newY = background.h - playerRectangle.h * game.yZoom;
		//if (onGround && newY != minY)
			//newY = minY - 1;

		
		if (!yCollision) {
			playerRectangle.y = newY;
			minY = minimumY;
		} else
			minY = newY;
		
		if (newDirection != direction) {
			direction = newDirection;
			updateDirection();
		}

		if (!didMove)
			animatedSprite.reset();
		if (didMove)
			animatedSprite.update(game);

		updateCamera(background, game.getRenderer().getCamera());
	}

	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		return false;
	}

	public void updateCamera(Rectangle background, Rectangle camera) {
		int x = playerRectangle.x - (camera.w / 2);
		if (x < 0)
			x = 0;
		if (x + camera.w > background.w)
			x = background.w - camera.w;
		
		camera.x = x;

		int y = playerRectangle.y - (camera.h / 2) - 250;
		if (y < 0)
			y = 0;
		if (y + camera.h > background.h)
			y = background.h - camera.h;
		//System.out.println("B: " + camera.y);
		camera.y = y;
		//System.out.println("A: " + camera.y);
		// camera.x = playerRectangle.x - (camera.w / 2);
		// camera.y = playerRectangle.y - (camera.h / 2);
	}

	public int getLayer() {
		return layer;
	}

	public Rectangle getRectangle() {
		return playerRectangle;
	}
}