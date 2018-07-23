package org.alphaquest.java.functions;

import java.util.ArrayList;

import org.alphaquest.java.Game;
import org.alphaquest.java.Toolkit.KeyBoardListener;
import org.alphaquest.java.Toolkit.Sound;
import org.alphaquest.java.delegate.GameObject;
import org.alphaquest.java.functions.Map.MappedTile;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.math.Vector2;
import org.alphaquest.java.render.RenderHandler;

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
	private Rectangle axisCheck = new Rectangle();
	Rectangle background;
	private Sprite sprite;
	private AnimatedSprite animatedSprite = null;
	
	private boolean onGround;
	
	private final int xCollisionOffset = 14;
	private final int yCollisionOffset = 20;
	private final int MINIMUM_Y = (19*16*3) - 26;
	private int minimumY = MINIMUM_Y;
	private int layer = 0;

	// 0 = right, 1 = left, 2 = up, 3 = down
	private int direction = 0;
	private int playerHeight = 26;
	private int playerWidth = 20;

	// controls the speed of the player
	private int speed = 10;
	private int jumpSpeed = 25;
	private float gravtity = 1.5f;
	private Vector2 velocity = new Vector2();
	
	Collision collision;
	boolean collisionLeft = false;
	boolean collisionRight = false;
	boolean collisionTop = false;
	boolean collisionBottom = false;

	Rectangle tileRect = new Rectangle();
	
	public Player(Sprite sprite, int xZoom, int yZoom) {
		this.sprite = sprite;

		if (sprite instanceof AnimatedSprite) {
			animatedSprite = (AnimatedSprite) sprite;
		}
		updateDirection();
		// equation sets player to bottom of the screen
		playerRectangle = new Rectangle(75, 0 /*backgroundHeight - (playerHeight * xZoom)*/, playerWidth, playerHeight);
		playerRectangle.generateGraphics(3, 0xff923459);
		collisionCheckRectangle = new Rectangle(0, 0, playerWidth, playerHeight);
		collisionCheckRectangle.generateGraphics(3, 0xff923123);
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
			renderer.renderRectangle(tileRect, xZoom, yZoom, false);
		} else if (sprite != null) {
			renderer.renderSprite(sprite, playerRectangle.x, playerRectangle.y, xZoom, yZoom, false);
		} else {
			renderer.renderRectangle(playerRectangle, xZoom, yZoom, false);
			renderer.renderRectangle(collisionCheckRectangle, xZoom, yZoom, false);
			renderer.renderRectangle(tileRect, xZoom, yZoom, false);
			
			/*renderer.renderRectangle(leftCheck, xZoom, yZoom, false);
			renderer.renderRectangle(rightCheck, xZoom, yZoom, false);
			renderer.renderRectangle(topCheck, xZoom, yZoom, false);
			renderer.renderRectangle(bottomCheck, xZoom, yZoom, false);*/
		}
	}

	@Override
	public void update(Game game) {
		KeyBoardListener keyListener = game.getKeyListener();
		background = game.getRectangleBackground();
		
		collisionCheckRectangle = playerRectangle;
		
		
		
		//System.out.println("Player Y: " + playerRectangle.y + "   VS   Min Y:" + minimumY);
		onGround = playerRectangle.y == minimumY;
		
		boolean didMove = false;
		int newDirection = direction;

		if (collisionCheckRectangle.y < minimumY && !collisionBottom)
			velocity.y += gravtity;
		else if (!collisionBottom) {
			velocity.y = 0;
			collisionCheckRectangle.y = minimumY;
			velocity.x = 0;
		}else if (collisionBottom) {
			velocity.y = 0;
			velocity.x = 0;
		}

		//System.out.println("OnGround: " + onGround);
		
		if (keyListener.left()) {
			// playerRectangle.x -= speed;
			newDirection = 1;
			didMove = true;

			velocity.x = -speed;

			//collisionCheckRectangle.x -= speed;
		}
		if (keyListener.right()) {
			// playerRectangle.x += speed;
			newDirection = 0;
			didMove = true;
				
			velocity.x = speed;

			//collisionCheckRectangle.x += speed;
		}
		if (keyListener.up()) {
			// playerRectangle.y -= speed;
			// newDirection = 2;
			if (onGround) {
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
		
		collisionCheckRectangle.x = (int) (collisionCheckRectangle.x + velocity.x);
		collisionCheckRectangle.y = (int) (collisionCheckRectangle.y + velocity.y);
		
		checkCollision(game, collisionCheckRectangle);
		
		validatePlayerInBounds(game);
		
		System.out.println("Player X pos: " + playerRectangle.x);
		System.out.println("Player Y pos: " + playerRectangle.y + "\n");
		
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

	public void validatePlayerInBounds(Game game) {
		int tileSize = 16;
		
		if(!collisionLeft) {
			//playerRectangle.x = collisionCheckRectangle.x;
		}else {
			
		}
		
		if(!collisionRight) {
			//playerRectangle.x = collisionCheckRectangle.x;
		}else {
			
		}
		
		if(!collisionTop) {
			//playerRectangle.y = collisionCheckRectangle.y;
		}else {
			
		}
		
		if(!collisionBottom) {
			MappedTile tile = game.getMap().getCollisionTile(Collision.BOTTOM);
			if(tile != null) {
				
				boolean lt = true, rt = true;
				
				MappedTile left1 = game.getMap().getTile(0, tile.x -1, tile.y);
				if(left1 != null) {
					int id = game.getMap().getTileSet().collisionType(left1.id);
					if(id != 0 && id != 1) {
						lt = false;
					}
				}
				
				MappedTile right1 = game.getMap().getTile(0, tile.x + 1, tile.y);
				if(right1 != null) {
					int id = game.getMap().getTileSet().collisionType(right1.id);
					if(id != 0 && id != 1) {
						rt = false;
					}
				}
				
				if(left1 != null && lt) {
					
					if((tile.x * game.xZoom * tileSize) > playerRectangle.x + (playerRectangle.w * game.xZoom)) {
						System.out.println("Set Collision Tile Left1");
						game.getMap().setCollisionTile(left1, Collision.BOTTOM);
					}
				}else {
					if((tile.x * game.xZoom * tileSize) > playerRectangle.x + (playerRectangle.w * game.xZoom)) {
						minimumY = MINIMUM_Y;
					}
				}
			
				
				if(right1 != null && rt) {
					if((tile.x * game.xZoom * tileSize) + (game.xZoom * tileSize) < playerRectangle.x) {
						System.out.println("Set Collision Tile Right1");
						game.getMap().setCollisionTile(right1, Collision.BOTTOM);
					}
				}else {
					if((tile.x * game.xZoom * tileSize) + (game.xZoom * tileSize) < playerRectangle.x) {
						minimumY = MINIMUM_Y;
					}
				}
				
				tileRect  = new Rectangle((tile.x * game.xZoom * tileSize), (tile.y * game.xZoom * tileSize), tileSize, tileSize);
				tileRect.generateGraphics(0xff42f47d);
			}else {
				playerRectangle.y = collisionCheckRectangle.y;
				if(playerRectangle.y == minimumY) {
					minimumY = MINIMUM_Y;
				}
			}
		}else {
			MappedTile tile = game.getMap().getCollisionTile(Collision.BOTTOM);
			
			tileRect  = new Rectangle((tile.x * game.xZoom * tileSize), (tile.y * game.xZoom * tileSize), tileSize, tileSize);
			tileRect.generateGraphics(0xff42f47d);
			
			minimumY = (tile.y * game.yZoom * tileSize) - (playerRectangle.h * game.yZoom);
			playerRectangle.y = minimumY;
		}
		
		if (playerRectangle.y < 0)
			playerRectangle.y = 0;
		
		if (playerRectangle.y > background.h - playerRectangle.h * game.yZoom)
			playerRectangle.y = background.h - playerRectangle.h * game.yZoom;

		if (playerRectangle.x < 0)
			playerRectangle.x = 0;

		if (playerRectangle.x > background.w - playerRectangle.w * game.xZoom)
			playerRectangle.x = background.w - playerRectangle.w * game.xZoom;
		
	}
	
	public void checkCollision(Game game, Rectangle axisCheck) {

		Collision playerLeft = game.getMap().checkCollision(axisCheck, layer, game.xZoom, game.yZoom, Collision.LEFT);

		switch (playerLeft){	
        case LEFT:
        	collisionLeft = true;
        	System.out.println("Collision Left: " + collisionLeft);
            break;
		default:
			collisionLeft = false;
			break;
		}
		
		Collision playerRight = game.getMap().checkCollision(axisCheck, layer, game.xZoom, game.yZoom, Collision.RIGHT);
		
		switch (playerRight){	
        case RIGHT:
        	collisionRight = true;
        	System.out.println("Collision Right: " + collisionRight);
            break;
		default:
			collisionRight = false;
			break;
		}
		
		Collision playerTop = game.getMap().checkCollision(axisCheck, layer, game.xZoom, game.yZoom, Collision.TOP);
		
		switch (playerTop){	
        case TOP:
        	collisionTop = true;
        	System.out.println("Collision Top: " + collisionTop);
            break;
		default:
			collisionTop = false;
			break;
		}
		
		Collision playerBottom = game.getMap().checkCollision(axisCheck, layer, game.xZoom, game.yZoom, Collision.BOTTOM);
		
		switch (playerBottom){	
        case BOTTOM:
        	collisionBottom = true;
        	System.out.println("Collision Bottom: " + collisionBottom);
            break;
		default:
			collisionBottom = false;
			break;
		}
	}
	
	public int getLayer() {
		return layer;
	}

	public Rectangle getRectangle() {
		return playerRectangle;
	}
}