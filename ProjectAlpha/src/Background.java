import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class Background {
	
	private Sprite background;
	private Sprite platforms;
	private BackgroundTiles tileSet;
	private File backgroundFile;
	
	private ArrayList<BackgroundTile> backgroundTile = new ArrayList<BackgroundTile>();
	
	private HashMap<Integer, String> comments = new HashMap<Integer, String>();
	
	public Background(Sprite background, Sprite platforms, BackgroundTiles tileSet, File backgroundFile) {
		this.background = background;
		this.platforms = platforms;
		this.tileSet = tileSet;
		this.backgroundFile = backgroundFile;
		
		try {
			Scanner scanner = new Scanner(backgroundFile);
			int currentLine = 0;
			
			while(scanner.hasNextLine()){
				
				//read each line and create a tile
				String line = scanner.nextLine();
				if(!line.startsWith("//")) {
					
					String[] splitString = line.split(",");
					
					if(splitString.length >= 3) {
						BackgroundTile mappedTile = new BackgroundTile(Integer.parseInt(splitString[0]), Integer.parseInt(splitString[1]), Integer.parseInt(splitString[2]));
						backgroundTile.add(mappedTile);
					}
				}else {
					comments.put(currentLine, line);
				}
				currentLine++;
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	public void setTile(int tileX, int tileY, int tileID, int xZoom, int yZoom) {
		Sprite sprite = tileSet.getSprite(tileID);
		backgroundTile.add(new BackgroundTile(tileID, tileX - sprite.getWidth()/2*xZoom, tileY - sprite.getHeight()/2*yZoom));
	}

	public void removeTile(int tileX, int tileY, int xZoom, int yZoom) {
		
		Rectangle mouse = new Rectangle(tileX, tileY, 1, 1);
		
		for(int i = backgroundTile.size(); i > 0; i--) {
			BackgroundTile mappedTile = backgroundTile.get(i);
			Sprite sprite = tileSet.getSprite(mappedTile.id);
			Rectangle mappedTileRect = new Rectangle(mappedTile.x, mappedTile.y, sprite.getWidth() * xZoom, sprite.getHeight()* yZoom);
			if(mouse.intersects(mappedTileRect)) {
				backgroundTile.remove(i);
				return;
			}
		}
	}
	
	public void saveMap() {
		try {
			int currentLine = 0;
			if(backgroundFile.exists()) {
				backgroundFile.delete();
			}
			backgroundFile.createNewFile();
			
			PrintWriter printWriter = new PrintWriter(backgroundFile);
			
			for(int i = 0; i < backgroundTile.size(); i++) {
				if(comments.containsKey(currentLine)) {
					printWriter.println(comments.get(currentLine));
				}
				
				BackgroundTile tile = backgroundTile.get(i);
				printWriter.println(tile.id + "," + tile.x + "," + tile.y);
				
				currentLine++;
			}
			
			printWriter.close();
		}catch(java.io.IOException e) {
			e.printStackTrace();
		}
	}
	
	public void render(RenderHandler renderer, int xZoom, int yZoom) {
		Rectangle camera = renderer.getCamera();
		renderer.renderSprite(background, 0, 0, camera.w, camera.h, 1, 1, true, camera.x, camera.y);
		
		//renders background platforms not used at the moment
		//renderer.renderSprite(platforms, 0, 0, camera.w, camera.h, 1, 1, true, camera.x, camera.y);
	
		for(int tileIndex = 0; tileIndex < backgroundTile.size(); tileIndex++) {
			BackgroundTile mappedTile = backgroundTile.get(tileIndex);
			
			tileSet.renderTile(mappedTile.id, renderer, mappedTile.x, mappedTile.y, xZoom, yZoom);
		}
	}
	
	public int getWidth() {
		return background.getWidth();
	}
	
	public int getHeight() {
		return background.getHeight();
	}
	
class BackgroundTile{
		
		public int id;
		
		public int x;
		
		public int y;
		
		public BackgroundTile(int id, int x, int y) {
			this.id = id;
			this.x = x;
			this.y = y;
		}
	}
}

