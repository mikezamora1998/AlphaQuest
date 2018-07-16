package org.alphaquest.java.delegate;

import org.alphaquest.java.Game;
import org.alphaquest.java.math.Rectangle;
import org.alphaquest.java.render.RenderHandler;

/**
 * Game object interface that is used to be able to call on all game object quickly and easily.
 * @author Michael, David, Brandon
 */
public interface GameObject {
	//called every time possible
	public void render(RenderHandler renderer, int xZoom, int yZoom);
	
	//called at 60fps
	public void update(Game game);
	
	//Call whenever mouse is clicked on canvas.
	//return true to stop checking clicks
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom);

	public int getLayer();
	
	public Rectangle getRectangle();
}