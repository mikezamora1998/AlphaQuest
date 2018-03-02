
public interface GameObject {
	//called every time possible
	public void render(RenderHandler renderer, int xZoom, int yZoom);
	
	//called at 60fps
	public void update(Game game);
}