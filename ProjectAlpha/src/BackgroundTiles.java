import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class BackgroundTiles {
	
	
	private ArrayList<Tile> tilesList = new ArrayList<Tile>();
	
	//This will only work assuming the sprites in the spriteSheet have been loaded
	public BackgroundTiles(Game game, File tilesFile) {
		
		try {
			Scanner scanner = new Scanner(tilesFile);
			while(scanner.hasNextLine()){
				//read each line and create a tile
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					String[] splitString = line.split("-");
					String tileName = splitString[0];
					Sprite tileSprite = game.loadSprite("assets/Tiles/" + splitString[1]);
					Tile tile = new Tile(tileName, tileSprite);
					tilesList.add(tile);
					System.out.println(tileName);
				}
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void renderTile(int tileID, RenderHandler render, int xPosition, int yPosition, int xZoom, int yZoom) {
		if(tileID >= 0 && tilesList.size() > tileID) {
			render.renderSprite(tilesList.get(tileID).sprite, xPosition, yPosition, xZoom, yZoom, false);
		}else {
			System.out.println("TileID " + tileID + " is not within range " + tilesList.size() + ".");
		}
	}
	
	public int size() {
		return tilesList.size();
	}
	
	public Sprite[] getSprite() {
		Sprite[] sprites = new Sprite[size()];
		
		for(int i = 0; i < sprites.length; i++) {
			sprites[i] = tilesList.get(i).sprite;
		}
		
		return sprites;
	}
	
	public Sprite getSprite(int tileID) {
		if(tileID >= 0 && tileID < tilesList.size()) {
			return tilesList.get(tileID).sprite;
		}
		return null;
	}
	
	class Tile{
		public String tileName;
		public Sprite sprite;
		
		public Tile(String tileName, Sprite sprite) {
			this.tileName = tileName;
			this.sprite = sprite;
			
		}
	}
	//Tiles.txt format
	//TileName-SpriteX, SpriteY
}
