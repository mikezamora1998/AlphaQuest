
public class Player implements GameObject{

	private Rectangle playerRectangle;
	private Rectangle collisionCheckRectangle;
	private final int xCollisionOffset = 14;
	private final int yCollisionOffset = 20;
	private int layer = 0;
	private boolean yCollision;
	private boolean xCollision;
	boolean onGround;
	private int minY = 886;
	private int newMinY;
	
	//controls the speed of the player
	//private int speed = 16;
	private Sprite sprite;
	private AnimatedSprite animatedSprite= null;
	//0 = right, 1 = left, 2 = up, 3 = down
	private int direction = 0;
	
	//platformer movement
	private int speed = 5;
	private int jumpSpeed = 25;
	private Vector2 velocity = new Vector2();
	private float gravtity = 1.5f;
	
	private int backgroundHeight = 1280;
	private int playerHeight = 26;
	private int playerWidth = 20;
	
	private int xZoom;
	private int yZoom;
	
	
	public Player(Sprite sprite, int xZoom, int yZoom) {
		this.sprite = sprite;
		this.xZoom = xZoom;
		this.yZoom = yZoom;
		
		if(sprite instanceof AnimatedSprite) {
			animatedSprite = (AnimatedSprite) sprite;
		}
		updateDirection();
		playerRectangle = new Rectangle(0, backgroundHeight - (playerHeight * xZoom), playerWidth, playerHeight);// equation sets player to bottom of the screen
		playerRectangle.generateGraphics(3, 0xff923459);
		collisionCheckRectangle = new Rectangle(0, 0, 10*xZoom, 15*yZoom);
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

		xCollision = false;
		yCollision = false;
		onGround = false;
		boolean didMove = false;
		int newDirection = direction;
		
		collisionCheckRectangle.x = playerRectangle.x;
		collisionCheckRectangle.y = playerRectangle.y;
		
		//if(!(playerRectangle.y == newY))
			if(playerRectangle.y < minY) {
				velocity.y += gravtity;
			}else {
				velocity.y = 0;
				playerRectangle.y = minY;
				velocity.x = 0;
			}
			
		onGround = playerRectangle.y == minY;
		
		//boolean onGround = playerRectangle.y == minY;
		
		if(keyListener.left()) {
			//playerRectangle.x -= speed;
			newDirection = 1;
			didMove = true;
			velocity.x = -speed;
		
			collisionCheckRectangle.x -= speed;
		
		}if(keyListener.right()) {
			//playerRectangle.x += speed;
			newDirection = 0;
			didMove = true;
			velocity.x = speed;
		
			collisionCheckRectangle.x += speed;
			
		}if(keyListener.up()) {
			//playerRectangle.y -= speed;
			//newDirection = 2;
		
			if(onGround) {
				velocity.y -= jumpSpeed;
			}
			didMove = true;
		}if(keyListener.down()) {
			//playerRectangle.y += speed;
			//newDirection = 3;
			//didMove = true;
		}
		
		if(newDirection != direction) {
			direction = newDirection;
			updateDirection();
		}
		
		if(onGround) {
			newMinY = minY;
		}
		//System.out.println("OnGround : " + onGround);
		
		if(!didMove) {
			animatedSprite.reset();
		}
		if(didMove) {
			animatedSprite.update(game);
		}
		
		collisionCheckRectangle.x += xCollisionOffset;
		collisionCheckRectangle.y += yCollisionOffset;

		Rectangle axisCheck = new Rectangle(collisionCheckRectangle.x, playerRectangle.y + yCollisionOffset, collisionCheckRectangle.w, collisionCheckRectangle.h);

		//Check the X axis
		if(!game.getMap().checkCollision(axisCheck, layer, game.getXZoom(), game.getYZoom()) && 
			!game.getMap().checkCollision(axisCheck, layer + 1, game.getXZoom(), game.getYZoom())) {
			playerRectangle.x = collisionCheckRectangle.x - xCollisionOffset;
			
			xCollision = false;
		}else {
			xCollision = true;
		}
		//System.out.println("X collision : " + xCollision);

		axisCheck.x = playerRectangle.x + xCollisionOffset;
		axisCheck.y = collisionCheckRectangle.y;
		axisCheck.w = collisionCheckRectangle.w;
		axisCheck.h = collisionCheckRectangle.h;
		//axisCheck = new Rectangle(playerRectangle.x, collisionCheckRectangle.y, collisionCheckRectangle.w, collisionCheckRectangle.h);

		//Check the Y axis
		if(!game.getMap().checkCollision(axisCheck, layer, game.getXZoom(), game.getYZoom()) && 
			!game.getMap().checkCollision(axisCheck, layer + 1, game.getXZoom(), game.getYZoom())) {
			
			//if(yCollision == false && onGround == false) {
			
				playerRectangle.y = collisionCheckRectangle.y - yCollisionOffset;
			
			
			System.out.println("Player.y : " + playerRectangle.y);
			yCollision = false;
		}else {
			playerRectangle.y = collisionCheckRectangle.y - yCollisionOffset;
			yCollision = true;
			
		}
		
		System.out.println("Y collision : " + yCollision);
		//System.out.println("MinY : " + minY);
		//System.out.println("NewY : " + newMinY);
		
		Background background = game.getGameBackground();
		if(!xCollision) {
			int newX = (int) (playerRectangle.x + velocity.x);
			if(newX < 0) {
				newX = 0;
	 		}
			if(newX > background.getWidth() - playerRectangle.w*game.xZoom) {
				newX = background.getWidth() - playerRectangle.w*game.xZoom;
			}
			playerRectangle.x = newX;
			System.out.println("New X : " + newX);
		}
		
		int newY;
		if(yCollision) {
			newY = playerRectangle.y -15;
		}else {
			newY = (int) (playerRectangle.y + velocity.y);
			
		}
			if(newY < 0) {
				newY = 0;
			}
			if(newY > background.getHeight() - playerRectangle.h*game.yZoom) {
				newY = background.getHeight() - playerRectangle.h*game.yZoom;
			}
			if(onGround && newY != minY) {
				newY = minY - 1;
			}
			//if(yCollision == true)
			playerRectangle.y = newY;
			System.out.println("New Y : " + newY);
		//}else {
			if(yCollision) {
			//int newY = playerRectangle.y;
			//playerRectangle.y = newY;
			//}
		}
		
		updateCamera(background, game.getRenderer().getCamera());
		
	}
	
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		return false;
	}

	public void updateCamera(Background background, Rectangle camera) {
		int x = playerRectangle.x - (camera.w / 2);
		if(x < 0) {
			x = 0;
		}
		if(x + camera.w > background.getWidth()) {
			x = background.getWidth() - camera.w;
		}
		camera.x = x;
		
		int y = playerRectangle.y - (camera.h /2);
		if(y < 0){
			y = 0;
		}
		if(y + camera.h > background.getHeight()) {
			y = background.getHeight() - camera.h;
		}
		
		camera.y = y;
		//camera.x = playerRectangle.x - (camera.w / 2);
		//camera.y = playerRectangle.y - (camera.h / 2);
		
	}
	
	public int getLayer() {
		return layer;
	}

	public Rectangle getRectangle() {
		return playerRectangle;
	}
}
