package cliente.interfacejuego;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import cliente.reglas.GameFacade;

public class PanelMouseListener	implements MouseListener {

	private static PanelMouseListener instance = null;
	private GameFacade juegof;
	
	public PanelMouseListener(){
		juegof = GameFacade.GetjuegoFacade();
	}
	
	public static PanelMouseListener GetMouseListener(){
		if(instance == null)
			instance = new PanelMouseListener();
		return instance;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		juegof.MouseClicked(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
  
}