package org.alphaquest.java.Toolkit;

import java.applet.Applet;
import java.applet.AudioClip;
import java.net.MalformedURLException;
import java.net.URL;

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
	private String assetsPath = "/assets";
	
    Sound(String path, int type){
    	this.type = type;
    	try {
    		System.out.println("Sound: " + path.replace("/", "").replace(".wav", "") + ", Type: " + type);
    		
    		//TODO: Use this for EXE Testing
    		clip = Applet.newAudioClip(filePathURL(path));
    		
    		//TODO: Use this for eclipse testing
    		//clip = Applet.newAudioClip(Sound.class.getResource(path));
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    public URL filePathURL(String path) {
		String dir = getClass().getResource("/" + getClass().getName().replaceAll("\\.", "/") + ".class").toString();
		
		if(!dir.contains(".exe"))
			return Sound.class.getResource(path);
			
		dir = dir.substring(4).replaceFirst("/[^/]+\\.exe!.*$", "/");
		dir = dir + assetsPath  + path;
        try {
            return new URL(dir);
        } catch (MalformedURLException e) {}
        return null;
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