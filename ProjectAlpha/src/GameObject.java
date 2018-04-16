
public interface GameObject {
	//called every time possible
	public void render(RenderHandler renderer, int xZoom, int yZoom);
	
	//called at 60fps
	public void update(Game game);
	
	//Call whenever mouse is clicked on canvas.
	//return true to stop checking clicks
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom);

	//TODO collision
	public int getLayer();
	
	//TODO collision
	public Rectangle getRectangle();
}