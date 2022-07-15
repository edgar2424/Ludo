package cliente.reglas;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

import cliente.interfacejuego.Main;
import cliente.interfaces.ObservadoIF;
import cliente.interfaces.ObservadorIF;

public class Game implements ObservadoIF{
	
	private Piezas piezasclass;
	private GetByColor getByColor;
	private List<ObservadorIF> lst = new ArrayList<ObservadorIF>();
	
	private int currentPlayer;
	int GetCurrentPlayer(){return currentPlayer;}
    void SetCurrentPlayer(int current){
		currentPlayer = current;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private int currentState;
	int GetCurrentState(){return currentState;}
	void SetCurrentState(int current){
		currentState = current;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private boolean waitingForPlayer = false;
	boolean IsWaitingForPlayer(){ return waitingForPlayer; }
	void SetWaitingForPlayer(boolean waiting){
		waitingForPlayer = waiting;
	}
	
	private int dadoValue;
	int GetdadoValue(){return dadoValue;}
	void SetdadoValue(int value){
		dadoValue = value;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private Random random = new Random();
	private int changeCurrentStateTo;
	private int valueNeededToLeaveStartPlace = 6;
	private int roundsPlayed = 0;
	
	private static Game instance = null;
	public static Game Getjuego(){
		if(instance == null)
			instance = new Game();
		return instance;
	}
	
	private Game(){
		piezasclass = Piezas.Getpiezas();
		getByColor = GetByColor.GetGetByColor();
	}
	
	void StartGamepiezas(){
		piezasclass.SetRedpiezas(getByColor.getStartRed());
		piezasclass.SetBluepiezas(getByColor.getStartBlue());
		piezasclass.SetGreenpiezas(getByColor.getStartGreen());
		piezasclass.SetYellowpiezas(getByColor.getStartYellow());
	}
		
	void StartNewRound(){
		SetCurrentState(6);
	}
	
	void StartGame(){
		StartGamepiezas();
		SetCurrentPlayer(0);
		SetCurrentState(0);
		SetdadoValue(-1);
	}
	
	void Rolldado(){
		for(int i= 1; i<7;i++)
			SetdadoValue(i);
		
		SetdadoValue(random.nextInt(6)+1);
				
		if(GetdadoValue() == 6 && !this.IsThereAPieceInStart(this.GetCurrentPlayersColor()))
		{	
			SetdadoValue(7);
			changeCurrentStateTo = 4;
		}
	}
	
	void MouseClicked(MouseEvent e) {
		if(IsWaitingForPlayer()){
			int[] piezaselected = piezaselected(e);
			if(piezaselected != null){
				boolean moved = TryMoving(piezaselected);
				if(moved){
					SetWaitingForPlayer(changeCurrentStateTo != 5 ? false : true);
					if(!IsWaitingForPlayer() )
						NextPlayer();
					else
						MovePiece();
					return;
				}
			}
			SetCurrentState(3);
			return;
		}
	}
		
	void MovePiece(){
		SetWaitingForPlayer(false);
		String color = GetCurrentPlayersColor();
		if(GetdadoValue() == 6 && this.roundsPlayed ==2 )
				ReturnLastPieceMovedToStart(color);
		else
			ChooseMovementAndMovePiece(color);
	}
		
	boolean IsInStart(int[] piece,String color) {
		int[][] startpiezas = getByColor.GetStartPlacesByColor(color);
		for(int [] startpiece : startpiezas)
		if(Arrays.equals(piece,startpiece))
			return true;
		return false;
	}
	
	boolean IsInEnd(int[] piece, String color) {
		int[] endplace = getByColor.GetCoordinatesOfEndPlaceByColor(color);
		if(Arrays.equals(piece,endplace))
			return true;
		return false;
	}
	
	private void NextPlayer(){
		try {
			Thread.sleep(800);
		} catch (InterruptedException e) {
		}
		
		changeCurrentStateTo = 0;
		if(GetdadoValue() == 6 && GetCurrentState() != 2 && roundsPlayed < 2) 
			roundsPlayed++;
		else{
			SetCurrentPlayer(GetCurrentPlayer() < 3 ? GetCurrentPlayer()+1 : 0);
			roundsPlayed =0;
		}
		SetdadoValue(0);
		SetCurrentState(0);
		GameFacade.GetjuegoFacade().SetLancarDadoEnabled(true);
	}
		
	private int[] piezaselected(MouseEvent e){
		int x = e.getX();
		int y = e.getY();
		int p = Main.tamanoPixel;
		String color = GetCurrentPlayersColor();
		int[][] piezas = piezasclass.GetEncodedpiezasFromColor(color);
		for(int[] piece : piezas )
		{
			int[] pieceboundary = new int[] {piece[0]*p + p, piece[1]*p + p};
			if( piece[0]*p < x && x < pieceboundary[0])
				if(piece[1]*p < y && y < pieceboundary[1])
					return piece;
		}
		return null;
	}
	
	private boolean TryMoving(int[] piezaselected) {
		String color = GetCurrentPlayersColor();
		int decodedPiece;
		if(IsInStart(piezaselected, color))
			decodedPiece = -1;
		else
			decodedPiece = piezasclass.Decode(piezaselected);
		
		//START
		if(decodedPiece == -1 ){
			if(!AreThereTwopiezasInStartHouse(color)
				&& GetdadoValue() == valueNeededToLeaveStartPlace){
				MovePieceFromStartPlaceToStartHouse(color);
				return true;
			}
			else
				return false;
		}
		
		//FINAL
		if(decodedPiece >=100) {// jugar en la recta final
			if(GetdadoValue() + decodedPiece == getByColor.GetFirstHouseOfFinalRouteByColor(color) + 5){
				MoveToEndPlace(decodedPiece, color);
				return true;
			}
			else if(GetdadoValue() + decodedPiece < getByColor.GetFirstHouseOfFinalRouteByColor(color) + 5){
				MovePieceFromCurrentToNew(color, decodedPiece, GetdadoValue() + decodedPiece);
				return true;
			}
			else 
				return false;
		}
		
		int finalHouse = getByColor.GetFinalHouseByColor(color);
		
		if(EntersFinalRoute(decodedPiece, color))//Pieza decodificada <= Casa final
		{
			int housesToWalkInFinalRoute = GetdadoValue() - (finalHouse - decodedPiece);
			if(housesToWalkInFinalRoute > 6)
				return false;
			else if(housesToWalkInFinalRoute == 0)
				MovePieceFromCurrentToNew(color, decodedPiece, finalHouse);			
			else if(housesToWalkInFinalRoute == 6)
				MoveToEndPlace(decodedPiece,color);
			else{
				int firstofRoute = getByColor.GetFirstHouseOfFinalRouteByColor(color);
				int newhouse = firstofRoute + housesToWalkInFinalRoute - 1;
				MovePieceFromCurrentToNew(color, decodedPiece, newhouse);
			}
			return true;
		}
		
		
		//NORMAL
		int newHouse = decodedPiece + GetdadoValue() <= 52 ? decodedPiece + GetdadoValue() : decodedPiece + GetdadoValue() - 52;
		if(!IsBlocked(decodedPiece, color)){
			MovePieceFromCurrentToNew(color, decodedPiece, newHouse);
			boolean captured = CapturePieceIfThereIsAPieceFromAnotherColorInHouse(color, newHouse );
			if(captured)
			{
				SetdadoValue(6);
                            
				SetCurrentState(5);
				changeCurrentStateTo = 5;
			}
			else
				changeCurrentStateTo = 0;
			return true;
		}
		return false;
	}
	
	private boolean EntersFinalRoute(int decodedPiece, String color) {
		int newHouse = decodedPiece + GetdadoValue() <= 52 ? decodedPiece + GetdadoValue() : decodedPiece + GetdadoValue() - 52;
		int finalHouse = getByColor.GetFinalHouseByColor(color);
		
		if(decodedPiece + GetdadoValue() <= 52){
			for(int i = decodedPiece; i < decodedPiece + GetdadoValue(); i++)
				if(i == finalHouse)
					return true;
		}
		else{
			for(int i = decodedPiece; i <= 52; i++)
				if(i == finalHouse)
					return true;
			for(int i= 1; i <=newHouse; i++)
				if(i == finalHouse)
					return true;
		}
		
		return false;
	}
	private void MoveToEndPlace(int decodedPiece, String color) {
		MovePieceFromCurrentToNew(color, decodedPiece, 0);
		if(AllpiezasAreEnded(color)){
			GameFacade.GetjuegoFacade().EndGame();
		}
			
	}
	
	private boolean CapturePieceIfThereIsAPieceFromAnotherColorInHouse(String color, int piece) {
		if(IsInShelter(piece))
			return false;
		String [] otherColors = GetAllOtherColors(color);
		for(String othercolor : otherColors){
			int[] otherpiezas = piezasclass.GetDecodedpiezasPlacesByColor(othercolor);
			for(int otherpiece : otherpiezas)
				if(otherpiece == piece){
					this.MovePieceFromCurrentToNew(othercolor, otherpiece, -1);
					return true;
				}
		}
		return false;
	}
	
	private void ReturnLastPieceMovedToStart(String color) {
		int lastmoved = getByColor.GetLastPieceMovedByColor(color);
		if(lastmoved <100)
			this.MovePieceFromCurrentToNew(color, lastmoved, -1);
		NextPlayer();
	}
	
	private void ChooseMovementAndMovePiece(String color) {
		if(CanMoveAutomatically(color)){
			MovePieceFromStartPlaceToStartHouse(color);
			NextPlayer();
		}
		else if(CanMove(color))
			WaitForPlayerMovement(color);
		else{
			PlayerCantMove();
			NextPlayer();
		}
	}
	
	private boolean CanMoveAutomatically(String color){
		return AreAllpiezasInStart(color) &&
				!AreThereTwopiezasInStartHouse(color)
				&& GetdadoValue() == valueNeededToLeaveStartPlace;
	}
	
	private void MovePieceFromStartPlaceToStartHouse(String color) {
			int startHouse = getByColor.GetStartHouseByColor(color);
			MovePieceFromCurrentToNew(color, -1, startHouse);
	}
	
	private boolean CanMove(String color) {
		if(IsThereAPieceInStart(color) && 
				GetdadoValue() == valueNeededToLeaveStartPlace &&
				!AreThereTwopiezasInStartHouse(color))
			return true; //puede mover una pieza desde el lugar de inicio
		else if(IsThereAPieceInFinalRouteThatCanMove(color))
			return true; //puede mover una pieza en la ruta final
		else if(!AreAllpiezasInStart(color) && IsThereAPieceOnNormalHousesNotBlocked( color) )
			return true;//al menos una pieza en el tablero y no bloqueada por barrera o dos piezas en la casa de destino
		else if(AnyPieceEntersFinalRouteAndCanMove(color))
			return true;
		return false;
	}

	private boolean AnyPieceEntersFinalRouteAndCanMove(String color) {
		int finalHouse = getByColor.GetFinalHouseByColor(color);
		int[] piezasPlaces = piezasclass.GetDecodedpiezasPlacesByColor(color);
		for(int piece : piezasPlaces){
			if(EntersFinalRoute(piece, color)){
				int housesToWalkInFinalRoute = GetdadoValue() - (finalHouse - piece);
				if(housesToWalkInFinalRoute <= 6)
					return true;
			}
		}
		return false;
		
	}
	private boolean IsThereAPieceInFinalRouteThatCanMove(String color) {
		int[] piezasPlaces = piezasclass.GetDecodedpiezasPlacesByColor(color);
		for(int piece : piezasPlaces){
			if(piece > 100)
				if(GetdadoValue() + piece <= getByColor.GetFirstHouseOfFinalRouteByColor(color) + 5)
					return true;
		}
		return false;
	}
	private boolean IsThereAPieceOnNormalHousesNotBlocked( String color) {
		int[] piezasPlaces = piezasclass.GetDecodedpiezasPlacesByColor(color);
		for(int piece : piezasPlaces){
			if(piece < 100 && piece != -1)
				if(!IsBlocked(piece, color))
					return true;
		}
		return false;
	}
	
    private boolean IsBlocked(int piece, String color) {
		int newHouse = piece + GetdadoValue() <= 52 ? piece + GetdadoValue() : piece + GetdadoValue() - 52;
		if(AlreadyTwopiezasInDestinationHouse(color, newHouse))
			return true;
		else if(IsThereABarrier(color, piece, newHouse))
			return true;
		return false;
	}
	
    private boolean IsThereABarrier(String color, int piece, int newHouse){
    	int otherpiezas[][] = GetAllOtherpiezas(color);
		ArrayList<Integer> PlacesWherepiezasFormBarrier = new ArrayList<Integer>();
			for(int[] otherpiece : otherpiezas){
				for (int j=0;j<otherpiece.length;j++)
				  for (int k=j+1;k<otherpiece.length;k++)
				    if (k!=j && otherpiece[k] == otherpiece[j] && otherpiece[j] != -1)
				    	PlacesWherepiezasFormBarrier.add(otherpiece[j]);
			}
			
			if(piece + GetdadoValue() < 100 ){ 
				for(int barrier : PlacesWherepiezasFormBarrier)
					if(piece + GetdadoValue() <= 52 ){
						if(piece < barrier && barrier <= piece+ GetdadoValue())
							return true;
					}
					else
						if((piece < barrier && barrier <= 52) || (52< barrier && barrier < newHouse))
							return true;
			}
			return false;
    }
    
	private boolean IsInShelter(int piece){
		return piece == 52 || piece == 13 || piece == 26 || piece == 39
				||  piece == 4|| piece == 17|| piece == 30|| piece == 43;
	}
	
	private int[][] GetAllOtherpiezas(String color){
		String  otherColors[] = GetAllOtherColors(color);
		int count = 0;
		int  otherpiezas[][] = new int[3][4];
		for(String coulor : otherColors){
			int piezas[] = piezasclass.GetDecodedpiezasPlacesByColor(coulor);	
			otherpiezas[count++] = piezas;
		}
		return otherpiezas;
	}
	
	private String[] GetAllOtherColors(String color){
		String  allcolors [] = new String[]{ "red", "blue", "green", "yellow"};
		String  otherColors[] = new String [3];
		
		int count = 0;
		for(String coulor : allcolors)
			if(!coulor.equals(color))
				otherColors[count++] = coulor;
		return otherColors;
	}
	
	String GetPoints(){
		String  allcolors [] = new String[]{ "red", "blue", "green", "yellow"};
	    int[] distssum = new int [4];
		int count2 =0;
	    for(String color : allcolors)
		{ 	
			int[] dists = new int[4];
			int [] piezas = piezasclass.GetDecodedpiezasPlacesByColor(color);
			int count = 0;
			for(int piece : piezas)
			{
				if(piece == 0)
					dists[count]= 0;
				else if(piece == -1)
					dists[count] = 52;
				else if(piece >100)
					dists[count] = 3;
				else
				{
					int walked = GetWalkedDistance(color, piece);
					dists[count] = 52 - walked;
				}

				count++;
			}
			int distsum=0;
			for(int dist : dists)
				distsum+=dist;
			distssum[count2++] = distsum;
		}
		String newstr =  "\n"+ "amarillo" + " puntuacion: " + distssum[0]+ "\n"+
				"azul"  + " puntuacion: " + distssum[1]+ "\n"+
				"verde"  + " puntuacion: " + distssum[2]+ "\n"+
				"amarelo"  + " puntuacion: " + distssum[3];
		
		return newstr;
	}
	
	private int GetWalkedDistance(String color, int piece) {
		int finalhouse = getByColor.GetFinalHouseByColor(color);
		int starthouse = getByColor.GetStartHouseByColor(color);
		switch(color )
		{
		case "red":
			return piece <= finalhouse ? 9 + piece : piece - starthouse;
		case "blue":
			return piece <= finalhouse ? 22 +piece : piece - starthouse;
		case "green":
			return piece <= finalhouse ? 52 + piece : piece -  starthouse;
		case "yellow":
			return piece <= finalhouse ? 13 + piece : piece - starthouse;
		}
		return 0;
	}
	private boolean AlreadyTwopiezasInDestinationHouse(String color, int destination){
		if(IsInShelter(destination)){
			if(AlreadyTwopiezasOfAnyColorInDestinationHouse(destination))
				return true;
		}
		else if(AlreadyTwopiezasOfPlayerInDestinationHouse(color, destination))
			return true;
		return false;
	}
	
	private boolean AlreadyTwopiezasOfAnyColorInDestinationHouse(int destination){
		int count =0;
		int[] allpiezas = GetAllpiezas();
		for(int piece : allpiezas)
			if(piece == destination)
				count++;
		return count >= 2;
	}
	
	private boolean AlreadyTwopiezasOfPlayerInDestinationHouse(String color, int destination){
		int [] piezas = piezasclass.GetDecodedpiezasPlacesByColor(color);
		int count =0;
		for(int piece : piezas)
			if(piece == destination)
				count ++;
		return count >= 2;
	}
	
	private void WaitForPlayerMovement(String color) {
		SetCurrentState(changeCurrentStateTo !=  0 ? changeCurrentStateTo : 1);
		SetWaitingForPlayer(true);
	}
	
	private void PlayerCantMove() {
		SetCurrentState(2);
	}
	
	private boolean AreThereTwopiezasInStartHouse(String color) {
		int startHouse = getByColor.GetStartHouseByColor(color);
		int[] piezasPlaces = this.GetAllpiezas();
		int count=0;
		for(int piece : piezasPlaces)
			if(piece == startHouse)
				count++;
		return count >= 2 ? true: false;
	}
	
	private boolean IsThereAPieceInStart(String color) {
		int[] piezasPlaces = piezasclass.GetDecodedpiezasPlacesByColor(color);
		for(int piece : piezasPlaces)
			if(piece == -1)
				return true;
		return false;
	}
	
	//-1 : start, 0 : final, 100 a 118 final route
	private void MovePieceFromCurrentToNew(String color, int currentPlace, int newPlace) {
		int[][] piezas = piezasclass.GetEncodedpiezasFromColor(color);
		int[][] newCoordinates = GetNewCoordinates(color, currentPlace, newPlace, piezas);
		getByColor.StoreLastPieceMoved(color, newPlace);
		piezasclass.Movepiezas(newCoordinates, color);
	}
		
	private int[] GetAllpiezas(){
		int [] red = piezasclass.GetDecodedpiezasPlacesByColor("red");
		int [] blue = piezasclass.GetDecodedpiezasPlacesByColor("blue");
		int [] yellow = piezasclass.GetDecodedpiezasPlacesByColor("yellow");
		int [] green = piezasclass.GetDecodedpiezasPlacesByColor("green");
		int [] all = new int [red.length + blue.length + yellow.length + green.length];
		int count =0;
		for(int[] piezas : new int [][]{red, blue, green, yellow }){
			for(int piece : piezas)
			{
				all[count++] = piece;
			}
		}
		return all;
	}
		
	private int[][] GetNewCoordinates(String color, int currentPlace, int newPlace, int[][] piezas) {
		int[][] newCoordinates;
		if(currentPlace == -1)
			newCoordinates = SetNewCoordinatesForPieceInStart(piezasclass.Encode(newPlace), piezas,color);
		else if(newPlace == -1)
			newCoordinates = SetNewCoordinatesForPieceMovingToStart(piezasclass.Encode(currentPlace), piezas, color);
		else if(newPlace == 0)
			newCoordinates = SetNewCoordinatesForPieceInEnd(piezasclass.Encode(currentPlace), piezas, color);
		else
			newCoordinates = SetNewCoordinates(piezasclass.Encode(currentPlace), piezasclass.Encode(newPlace),piezas);
		return newCoordinates;
	}
	
	private int[][] SetNewCoordinatesForPieceInEnd(int[] currentPlace, int[][] piezas, String color) {
		for(int i=0;i <piezas.length; i++)
			if(Arrays.equals(piezas[i],currentPlace)){
				piezas[i]= getByColor.GetCoordinatesOfEndPlaceByColor(color);
				break;
			}
		return piezas;
	}
			
	private int[][] SetNewCoordinatesForPieceInStart(int[] newPlace, int[][] piezas, String color) {
		for(int i=0;i <piezas.length; i++)
			if(IsInStart(piezas[i], color)){
				piezas[i]=newPlace;
				break;
			}
		return piezas;
	}

	private int[][] SetNewCoordinatesForPieceMovingToStart(int[] currentPlace, int[][] piezas, String color) {
		for(int i=0;i <piezas.length; i++)
			if(Arrays.equals(piezas[i], currentPlace)){
				piezas[i]=GetCoordinatesOfFirstStartPlaceWithoutAPiece(color,piezas);
				break;
			}
		return piezas;
	}
	
	private int[] GetCoordinatesOfFirstStartPlaceWithoutAPiece(String color, int[][] piezas) {
		int[][] start = getByColor.GetStartPlacesByColor(color);
		boolean flag;
		for(int j=0;j<start.length;j++){
			flag = true;
			for(int i=0;i <piezas.length; i++)
				if(Arrays.equals(start[j], piezas[i]))
					flag=false;
			if(flag)
				return start[j];
		}
		return null;
	}

	private int[][] SetNewCoordinates(int[] currentPlace, int[] newPlace,int[][] piezas) {
		for(int i=0;i <piezas.length; i++)
			if(Arrays.equals(piezas[i], currentPlace)){
				piezas[i]=newPlace;
				break;
			}
		return piezas;
	}
	
	private String GetCurrentPlayersColor() {
		switch(GetCurrentPlayer()){
		case 0:
			return "red";
		case 1:
			return "green";
		case 2:
			return "yellow";
		case 3:
			return "blue";
		}
		return null;
	}

	private boolean AreAllpiezasInStart(String color) {
		int [] piezasPlaces = piezasclass.GetDecodedpiezasPlacesByColor(color);
		for(int piece : piezasPlaces)
			if(piece != -1)
				return false;
		return true;
	}
	
	private boolean AllpiezasAreEnded(String color) {
		int [] piezasPlaces = piezasclass.GetDecodedpiezasPlacesByColor(color);
		for(int piece : piezasPlaces)
			if(piece != 0)
				return false;
		return true;
	}
	
	//OBSERVADO POR juegoFACEDE
	//OBSERVADO PELO juegoFACADE
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
		return 0;
	}
	
	
}