/**
 * Determines which button is active in the GUI.
 * @author Michael, David, Brandon
 */
public abstract class GUIButtons implements GameObject{

	protected Sprite sprite;
	protected Rectangle region;
	protected boolean fixed;
	
	public GUIButtons(Sprite sprite, Rectangle region, boolean fixed) {
		this.sprite = sprite;
		this.region = region;
		this.fixed = fixed;
	}

	public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
		renderer.renderSprite(sprite, region.x + interfaceRect.x, region.y + interfaceRect.y, xZoom, yZoom, fixed);
	}
	
	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		
	}

	@Override
	public void update(Game game) {	
		
	}

	@Override
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		
		if(mouseRectangle.intersects(region)){
			activate();
			return true;
		}
		return false;
	}

	public abstract void activate();
	
	public int getLayer() {
		return Integer.MAX_VALUE;
	}

	public Rectangle getRectangle() {
		return region;
	}
}
