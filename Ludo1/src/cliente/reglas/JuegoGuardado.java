package cliente.reglas;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JuegoGuardado {

	private Piezas piezas;
	private Game juego;
	
	private static JuegoGuardado instance = null;
	public static JuegoGuardado GetJuegoGuardado(){
		if(instance == null)
			instance = new JuegoGuardado();
		return instance;
	}
	
	public void SaveGame(){
		piezas = Piezas.Getpiezas();
		juego = Game.Getjuego();
		
		FileWriter outputStream = null;
		try{
			outputStream = new FileWriter("savefile.txt");
			outputStream.write(juego.GetCurrentPlayer());
			outputStream.write(juego.GetCurrentState());
			outputStream.write(juego.IsWaitingForPlayer() == true ? 1:0);
			outputStream.write(juego.GetdadoValue());
			
			for(int [][] allpiezas : piezas.GetAll()){
				for(int [] piezas : allpiezas)
					for(int piece : piezas)
						outputStream.write(piece);
			}
		}catch(Exception e){
			System.exit(0);
		} finally {
			if(outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.exit(1);
				}
		}
	}
	
	
	public void LoadGame(){
		piezas = Piezas.Getpiezas();
		juego = Game.Getjuego();
		
		FileReader inputStream = null;
		try{
			
			inputStream = new FileReader("savefile.txt");
			juego.SetCurrentPlayer(inputStream.read());
			juego.SetCurrentState(inputStream.read());
			juego.SetWaitingForPlayer(inputStream.read() == 1);
			juego.SetdadoValue(inputStream.read());
			int [][][] allpiezas = new int [4][4][2];
			for(int i = 0; i< allpiezas.length;i++){
				for(int j = 0; j< allpiezas[i].length;j++)
					for(int k =0; k < allpiezas[i][j].length;k++)
						allpiezas[i][j][k] = inputStream.read();;
			}
			piezas.SetAll(allpiezas);
		}catch(Exception e){
			System.exit(1);
		}finally{
			if(inputStream != null)
				try{
					inputStream.close();
				}catch(Exception e){
					System.exit(3);
				}
		}
		
	}
	
}
