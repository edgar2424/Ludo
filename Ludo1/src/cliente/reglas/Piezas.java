package cliente.reglas;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import cliente.interfaces.ObservadoIF;
import cliente.interfaces.ObservadorIF;

public class Piezas implements ObservadoIF{

	private int[][] red;
	private int[][] blue;
	private int[][] green;
	private int[][] yellow;
	private Map<Integer, Integer[]> coordinatesDictionary;
	private List<ObservadorIF> lst = new ArrayList<ObservadorIF>();
	
	int[][] GetRedpiezas(){return red;}
	int[][] GetBluepiezas(){return blue;}
	int[][] GetGreenpiezas(){return green;}
	int[][] GetYellowpiezas(){return yellow;}
	
	void SetRedpiezas(int[][] red){ 
		this.red = red;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	void SetBluepiezas(int[][] blue){ 
		this.blue = blue; 
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	void SetGreenpiezas(int[][] green){ 
		this.green = green; 
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	void SetYellowpiezas(int[][] yellow){ 
		this.yellow = yellow;
		ListIterator<ObservadorIF> li = lst.listIterator();
		while(li.hasNext())
			li.next().notify(this);
	}
	
	private static Piezas instance = null;
	public static Piezas Getpiezas(){
		if(instance == null)
			instance = new Piezas();
		return instance;
	}
	
    Piezas(){
    	coordinatesDictionary = new HashMap<Integer, Integer[]>();
    	SetDictionary();
    }
	
    int [][][] GetAll(){
    	int[][][] all = new int[4][4][2];
    	all[0] = GetRedpiezas();
    	all[1] = GetBluepiezas();
    	all[2] = GetGreenpiezas();
    	all[3] = GetYellowpiezas();
    	return all;
    }
    
    public void SetAll(int[][][] allpiezas) {
		SetRedpiezas(allpiezas[0]);
		SetBluepiezas(allpiezas[1]);
		SetGreenpiezas(allpiezas[2]);
		SetYellowpiezas(allpiezas[3]);		
	}
    
    int Decode(int[] place){
		 for (Entry<Integer, Integer[]> entry : coordinatesDictionary.entrySet()) 
		 {
			 Integer [] value = entry.getValue();
			 int[] toInt = new int[value.length];
			 int count = 0;
			 for(Integer v : value)
				 toInt[count++] = v.intValue();
		        if ( Arrays.equals(place,toInt)) 
		            return entry.getKey();
		 }
		 return 0;
	}
	
	int[] Encode(int place) {
		int temp []= new int[2];
		temp[0]=coordinatesDictionary.get(place)[0];
		temp[1]= coordinatesDictionary.get(place)[1];
		return temp;
	}
	
	int[] GetDecodedpiezasPlacesByColor(String color){
		Game juego = Game.Getjuego();
		int [] decodedpiezas=new int[4];
		int[][] encodedpiezas = GetEncodedpiezasFromColor(color);
		int count=0;
		for(int[] piece :encodedpiezas ){
			if(juego.IsInStart(piece, color))
				decodedpiezas[count] = -1;
			else if(juego.IsInEnd(piece, color))
				decodedpiezas[count] = 0;
			else
				decodedpiezas[count] = Decode(piece);
			count++;
		}		
		return decodedpiezas;
	}
	
	int[][] GetEncodedpiezasFromColor(String color) {
		switch(color){
		case "red":
			return GetRedpiezas();
		case "blue":
			return GetBluepiezas();
		case "green":
			return GetGreenpiezas();
		case "yellow":
			return GetYellowpiezas();
		}
		return null;
	}

	void Movepiezas(int[][] newCoordinates,String color) {
		switch(color){
		case "red":
			SetRedpiezas(newCoordinates);
			break;
		case "blue":
			SetBluepiezas(newCoordinates);
			break;
		case "green":
			SetGreenpiezas(newCoordinates);
			break;
		case "yellow":
			SetYellowpiezas(newCoordinates);
			break;
		}
	}
	
	private void SetDictionary(){
		Integer temps[][] = new Integer[][]{
				new  Integer[]{6,0},new Integer[]{7,0}, new Integer[]{8,0}, new Integer[]{8,1}, new Integer[]{8,2} ,
				new  Integer[]{8,3},new Integer[]{8,4}, new Integer[]{8,5}, new Integer[]{9,6}, new Integer[]{10,6},
				new Integer[]{11,6},new Integer[]{12,6},new Integer[]{13,6},new Integer[]{14,6},new Integer[]{14,7},
				new Integer[]{14,8},new Integer[]{13,8},new Integer[]{12,8},new Integer[]{11,8},new Integer[]{10,8},
				new Integer[]{9,8},	new Integer[]{8,9},	new Integer[]{8,10},new Integer[]{8,11},new Integer[]{8,12},
				new Integer[]{8,13},new Integer[]{8,14},new Integer[]{7,14},new Integer[]{6,14},new Integer[]{6,13},
				new Integer[]{6,12},new Integer[]{6,11},new Integer[]{6,10},new Integer[]{6,9} ,new Integer[]{5,8} ,
				new Integer[]{4,8} ,new Integer[]{3,8} ,new Integer[]{2,8} ,new Integer[]{1,8} ,new Integer[]{0,8} ,
				new Integer[]{0,7} ,new Integer[]{0,6} ,new Integer[]{1,6} ,new Integer[]{2,6} ,new Integer[]{3,6} ,
				new Integer[]{4,6} ,new Integer[]{5,6} ,new Integer[]{6,5} ,new Integer[]{6,4} ,new Integer[]{6,3} ,
				new Integer[]{6,2} ,new Integer[]{6,1} 
		};
		int count = 1;
		for(Integer[] temp : temps)
			coordinatesDictionary.put(count++, temp);
		
		Integer finalroute[][] = new Integer[][]{
			new Integer[]{1,7},new Integer[]{2,7},new Integer[]{3,7},new Integer[]{4,7},new Integer[]{5,7},//red
			new Integer[]{7,13},new Integer[]{7,12},new Integer[]{7,11},new Integer[]{7,10},new Integer[]{7,9},//blue
			new Integer[]{7,1},new Integer[]{7,2},new Integer[]{7,3},new Integer[]{7,4},new Integer[]{7,5},//green
			new Integer[]{13,7},new Integer[]{12,7},new Integer[]{11,7},new Integer[]{10,7},new Integer[]{9,7},//yellow
		};
		
		count =100;
		for(Integer[] temp : finalroute)
			coordinatesDictionary.put(count++, temp);
	}
	
	//OBSERVADO POR juegoFACADE
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
		// TODO Auto-generated method stub
		return null;
	}
	

	
	
	
}
