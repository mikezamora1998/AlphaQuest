/**
 * Handles textButton functions, used to represent the normal text user interface.
 * @author Michael, David, Brandon
 * @deprecated This method has not yet been implemented.
 */
public class TextButton extends GUIButtons{
	
	private Game game;
	private int tileID;
	private boolean isSelected = false;
	
	public TextButton(Game game, int tileID, Sprite tileSprite, Rectangle rect) {
		super(tileSprite, rect, true);
		this.game = game;
		this.tileID = tileID;
		rect.generateGraphics(0x1CBBB4);
	}

	@Override
	public void render(RenderHandler renderer, int xZoom, int yZoom, Rectangle interfaceRect) {
		
		int sizeReduction = 1;
		renderer.renderRectangle(region, interfaceRect, 1, 1, fixed);
		renderer.renderSprite(sprite, 
								region.x + interfaceRect.x + (xZoom - (xZoom - sizeReduction)) * region.w / 2 / xZoom,
								region.y + interfaceRect.y + (yZoom - (yZoom - sizeReduction)) * region.h / 2 / yZoom, 
								xZoom - sizeReduction, 
								yZoom - sizeReduction, 
								fixed);
	}
	
	@Override
	public void update(Game game) {
		if(tileID != game.getSelectedTile()) {
			if(!isSelected) {
				region.generateGraphics(0x67FF3D);
				isSelected = true;
			}
		}else {
			if(isSelected) {
				region.generateGraphics(0x1CBBB4);
				isSelected = false;
				
			}
		}
	}
	
	public void activate() {
		game.changeTile(tileID);
	}
}
