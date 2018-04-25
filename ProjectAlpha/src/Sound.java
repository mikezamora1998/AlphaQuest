import java.applet.Applet;
import java.applet.AudioClip;

/**
 * Handles Sound functions, stores static objects of sound files that can be called at any time.
 * @author Michael, David, Brandon
 */
public class Sound {
	
	public static final Sound backGround = new Sound("/dz.wav", 0);
	public static final Sound opening = new Sound("/Opening.wav", 0);
	public static final Sound jump = new Sound("/jump.wav", 1);
	public static final Sound start = new Sound("/start.wav", 1);
	public static final Sound end = new Sound("/end.wav", 1);
	
	private AudioClip clip;
	private int type;
	
    Sound(String path, int type){
    	this.type = type;
    	try {
    		//System.out.println(Sound.class.getResource(path));
    		clip = Applet.newAudioClip(Sound.class.getResource(path));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public void play() {
    	try {
    		new Thread() {
    			public void run() {
    				if(type == 1)
    					clip.play();
    				
    				if(type == 0)
    					clip.loop();
    			}
    		}.start();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public void stop() {
    	clip.stop();
    }
}