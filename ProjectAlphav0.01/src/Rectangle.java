public class Rectangle {
	public int x,y,w,h;
	private int[] pixels;
	
	Rectangle(int x, int y, int w, int h){
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	Rectangle(){
		this(0,0,0,0);
	}
	
	public void generateGraphics(int color) {
		pixels = new int[w*h];
		for(int y = 0; y < h; y++) {
			for(int x = 0; x < w; x++) {
				pixels[x + y * w] = color;
			}
		}
	}
	
	public int[] getPixels() {
		if(pixels != null) {
			return pixels;
		} else {
			System.out.println("Attempted to retrive pixels from a Rectangle without generated graphics");
		}
		return null;
	}
}
