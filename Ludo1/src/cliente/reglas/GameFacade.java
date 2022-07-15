package cliente.reglas;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.*;

import javax.swing.JOptionPane;

import cliente.interfaces.*;

public class GameFacade implements ObservadoIF, ObservadorIF{

	Game juego;
	JuegoGuardado juegoGuardado;
	Piezas piezas;
	private List<ObservadorIF> lst = new ArrayList<ObservadorIF>();
	boolean lancarDadoEnabled;
	
	private static GameFacade instance = null;
	public static GameFacade GetjuegoFacade(){
		if(instance == null)
			instance = new GameFacade();
		return instance;
	}	
	
	GameFacade(){
		juego = Game.Getjuego();
		juegoGuardado = juegoGuardado.GetJuegoGuardado();
		piezas = Piezas.Getpiezas();
		
		juego.add(this);
		piezas.add(this);
	}
	
	public void StartNewRound(){
		SetLancarDadoEnabled(false);
		juego.StartNewRound();
	}
	
	public void SetLancarDadoEnabled(boolean trueorfalse){
		lancarDadoEnabled = trueorfalse;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
		li.next().notify(this);
	}
	
	public void StartGame(){
		SetLancarDadoEnabled(true);
		juego.StartGame();
	}
	
	public void Rolldado(){
		juego.Rolldado();
	}
	
	public void MovePiece(){
		juego.MovePiece();
	}
	
	public int GetdadoValue(){
		return juego.GetdadoValue();
	}
	
	public void MouseClicked(MouseEvent e){
		juego.MouseClicked(e);
	}
	
	public Color GetCurrentPlayerForeground(){
		switch (juego.GetCurrentPlayer()){
			case 0:	return new Color(220,20,60);
			case 1:	return new Color (60,179,113);
			case 2:	return new Color(255,215,0);
			case 3:	return new Color(100,149,237);
		}return null;
	}
	
	public String GetCurrentPlayerText(){
		switch (juego.GetCurrentPlayer()){
			case 0:	return "rojo";
			case 1: return "Verde";
			case 2: return "Amarrilo";
			case 3:	return "Azul";
		}	return null;
	}
	
	public String GetCurrentStateText(){
		switch (juego.GetCurrentState()){
			case 6: return "";
			case 0:	return "<html>Esperando<br> lanzamento.</html>";
			case 1:	return "<html>Escoja jugada<br> clickando en<br>la pieza deseada.</html>";
			case 2:	return "<html>No hay<br> jugadas<br> posibles.</html>";
			case 3:	return "<html> Esa Jugada<br> no  es<br> posible, <br>escoja otra.";
			case 4:	return "<html> Ande 7 casas<br> (Dado = 6 e<br> no existe<br> piezas en casa<br> inicial).</html>";
			case 5:	return "<html> Pieza comida.<br> Ande 20<br> casillas.</html>";
		}return null;
	}
	
	public void SaveGame(){
		juegoGuardado.SaveGame();
		JOptionPane.showMessageDialog(null, "Juego guardado");
	}

	public void LoadGame(){
		juegoGuardado.LoadGame();
		JOptionPane.showMessageDialog(null, "Juego cargado");
	}
	
	//OBSERVADO POR EL PANEL DE BOTONES Y BOARDPANEL
	@Override
	public void add(ObservadorIF observador) {
		lst.add(observador);
	}

	@Override
	public void remove(ObservadorIF observador) {
		lst.remove(observador);
	}

	@Override
	public Object get(int i) {
		if(i == 1)
			return juego.GetdadoValue();
		if(i == 2)
			return GetCurrentPlayerForeground();
		if(i == 3)
			return GetCurrentPlayerText();
		if(i == 4)
			return GetCurrentStateText();
		if(i == 5)
			return lancarDadoEnabled;
		if(i == 6)
			return piezas.GetAll();
		return 0;
	}
	
	void EndGame(){
		JOptionPane.showMessageDialog(null, "Fin de juego\n Ganador: " + GetCurrentPlayerText()+
				"\n" + juego.GetPoints());
		System.exit(1);
	}


	//OBSERVADOR DE juego E piezas
	@Override
	public void notify(ObservadoIF observado) {
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
}
