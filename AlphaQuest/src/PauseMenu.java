
public class PauseMenu extends GUIButtons{

	public PauseMenu(Sprite sprite, Rectangle region, boolean fixed) {
		super(sprite, region, fixed);
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		
	}

	@Override
	public void update(Game game) {
		
	}

	@Override
	public boolean handleMouseClick(Rectangle mouseRectangle, Rectangle camera, int xZoom, int yZoom) {
		return false;
	}

	@Override
	public int getLayer() {
		return 0;
	}

	@Override
	public Rectangle getRectangle() {
		return null;
	}

	@Override
	public void activate() {

	}

}
