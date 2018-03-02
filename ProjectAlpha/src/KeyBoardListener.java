import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyBoardListener implements KeyListener, FocusListener{

	public boolean[] keys = new boolean[120];
	
	public KeyBoardListener() {
		
	}

	@Override
	public void keyPressed(KeyEvent event) { 
		//prints key code
		//System.out.println(event.getKeyCode());
		
		int keyCode = event.getKeyCode();
		
		//checks if key pressed is in bounds of keys array
		if(keyCode < keys.length) {
			keys[keyCode] = true;
		}
	}
	
	@Override
	public void keyReleased(KeyEvent event) {
		
		int keyCode = event.getKeyCode();
		
		//checks if key pressed is in bounds of keys array
		if(keyCode < keys.length) {
			keys[keyCode] = false;
		}
	}
	
	@Override
	public void focusLost(FocusEvent event) {
		for(int i = 0; i < keys.length; i++) {
			keys[i] = false;
		}
	}
	
	@Override
	public void keyTyped(KeyEvent event) {
		//blank
	}
	
	@Override
	public void focusGained(FocusEvent event) {
		//blank
	}
	
	public boolean up() {
		return keys[KeyEvent.VK_W] || keys[KeyEvent.VK_UP];
	}
	
	public boolean down() {
		return keys[KeyEvent.VK_S] || keys[KeyEvent.VK_DOWN];
	}
	
	public boolean left() {
		return keys[KeyEvent.VK_A] || keys[KeyEvent.VK_LEFT];
	}

	public boolean right() {
		return keys[KeyEvent.VK_D] || keys[KeyEvent.VK_RIGHT];
	}
}