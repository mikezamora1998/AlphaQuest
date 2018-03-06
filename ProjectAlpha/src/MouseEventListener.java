import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class MouseEventListener implements MouseListener, MouseMotionListener {

	private Game game;
	
	public MouseEventListener(Game game) {
		this.game = game;
	}

	@Override
	public void mouseClicked(MouseEvent event) {
		
	}
	
	@Override
	public void mouseDragged(MouseEvent event) {

	}

	@Override
	public void mouseEntered(MouseEvent event) {
		
	}

	@Override
	public void mouseExited(MouseEvent event) {
		
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		//Prints x and y position of mouse
		//System.out.println(event.getX() + ", "+ event.getY());
		
		if(event.getButton() == MouseEvent.BUTTON1) {
			game.leftClick(event.getX(), event.getY());
		}
		if(event.getButton() == MouseEvent.BUTTON3) {
			game.rightClick(event.getX(), event.getY());
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		
	}
}
