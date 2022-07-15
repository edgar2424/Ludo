package cliente.interfacejuego;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import cliente.reglas.GameFacade;
import cliente.interfaces.*;

public class ButtonsPanel extends JPanel implements ObservadorIF {
	private static final long serialVersionUID = 1L;

	private int tamanoPixel;
	private int x0=15*tamanoPixel;
	private JButton nuevoJuego = new JButton("Nuevo");
	private JButton cargar = new JButton("Cargar juego");
	private JButton guardar = new JButton("guardar");
	private JButton lancarDado = new JButton("Lanzar Dado");
	private JLabel jugadorActual = new JLabel();
	private JLabel dado = new JLabel();
	private JLabel estadoAtual = new JLabel();

	private GameFacade juegof;
	
	private static ButtonsPanel instance = null;
	public static ButtonsPanel GetButtonsPanel(){
		if(instance == null)
			instance = new ButtonsPanel();
		return instance;
	}
	
	private ButtonsPanel(){
		this.tamanoPixel = Main.tamanoPixel;
		juegof = GameFacade.GetjuegoFacade();
		juegof.add(this);
		SetButtons();
		SetLabels();
		Setdado();
		SetCurrentPlayerLabel();
		StartListeners();
		SetConfigurations();
		
	}
		
	private URL GetdadoImageByNumber(int n){
		switch(n){
		case 1:	return getClass().getResource("/cliente/images/1.png");
		case 3:	return getClass().getResource("/cliente/images/3.png");
		case 2:	return getClass().getResource("/cliente/images/2.png");
		case 4:	return getClass().getResource("/cliente/images/4.png");
		case 5:	return getClass().getResource("/cliente/images/5.png");
		case 6:	return getClass().getResource("/cliente/images/6.png");
		case 7:	return getClass().getResource("/cliente/images/6.png");
		}
		return null;
	}
	
	private void SetButtons(){
		int i=0;
		for(JButton jb : new JButton []{ nuevoJuego, cargar, guardar}){
			jb.setSize(4*tamanoPixel, tamanoPixel);
			jb.setLocation(x0+tamanoPixel/2, i+tamanoPixel);
			this.add(jb);
			i+=3*tamanoPixel/2;
		}		
		lancarDado.setSize(4*tamanoPixel, tamanoPixel);
		lancarDado.setLocation(x0+tamanoPixel/2, 19*tamanoPixel/2);
		this.add(lancarDado);
		
		lancarDado.setEnabled(false);
		guardar.setEnabled(false);
	}
	
	private void SetLabels(){
		JLabel jl = new JLabel("� Jugar:", SwingConstants.CENTER);
		jl.setFont(new Font("Mensajero nuevo",Font.BOLD,20) );
		jl.setSize(4*tamanoPixel, tamanoPixel);
		jl.setLocation(x0+tamanoPixel/2, 11*tamanoPixel/2);
		this.add(jl);
		
		estadoAtual.setHorizontalAlignment(JLabel.CENTER);
		estadoAtual.setVerticalAlignment(JLabel.TOP);
		estadoAtual.setFont(new Font("Mensajero nuevo",Font.PLAIN,15) );
		estadoAtual.setSize(5*tamanoPixel, 3*tamanoPixel);
		estadoAtual.setLocation(x0, 22*tamanoPixel/2);
		this.add(estadoAtual);
	}

	private void Setdado(){
		dado.setLocation(x0+2*tamanoPixel,16*tamanoPixel/2);
		this.add(dado);	
	}
	
	private void SetCurrentPlayerLabel(){
		jugadorActual = new JLabel();
		jugadorActual.setHorizontalAlignment(JLabel.CENTER);
		jugadorActual.setFont(new Font("Mensajero nuevo",Font.BOLD,20) );
		jugadorActual.setSize(4*tamanoPixel, tamanoPixel);
		jugadorActual.setLocation(x0+tamanoPixel/2, 13*tamanoPixel/2);
		this.add(jugadorActual);
	}
	
	private void StartListeners(){
		
		lancarDado.addActionListener( new ActionListener(){
			
			@Override
			public void actionPerformed(ActionEvent e)
			{
				Thread t = new Thread(new Runnable() { 
				public void run() { 			
					juegof.StartNewRound();
					juegof.Rolldado();			
					juegof.MovePiece();					
				}});
				t.start();
			}
			
		});
		
		nuevoJuego.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				juegof.StartGame();
				guardar.setEnabled(true);
				revalidate();
				repaint();
			}
        });
		
		guardar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				juegof.SaveGame();
			}
        });
		
		cargar.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				juegof.LoadGame();
				guardar.setEnabled(true);
				revalidate();
				repaint();
			}
        });
	}
	
	private void SetConfigurations() {
		this.setBackground(Color.lightGray);
		this.setLayout(null);
		this.setSize(5*tamanoPixel, 15*tamanoPixel);
		this.setLocation(15*tamanoPixel, 0);
		this.setVisible(true);
	}
	
	private void Refreshdado(int dadoValue){
		if(dadoValue<=7 && dadoValue >0){
			dado.setIcon(new ImageIcon(GetdadoImageByNumber(dadoValue)));
			try {
				Thread.sleep(90);
			} catch (InterruptedException e) {
			}
		}
		else
			dado.setIcon(new ImageIcon());
		dado.setSize(dado.getIcon().getIconWidth(),dado.getIcon().getIconHeight());
	}
	
	// OBSERVADOR DE juegoFACADE
	@Override
	public void notify(ObservadoIF observado) {
		int dadoValue = (int) observado.get(1);
		Refreshdado(dadoValue);
		
		Color foreground = (Color) observado.get(2);
		jugadorActual.setForeground(foreground);
		
		String currentPlayerText = (String) observado.get(3);
		jugadorActual.setText(currentPlayerText);
		
		String currentStateText = (String) observado.get(4);
		estadoAtual.setText(currentStateText);
		
		boolean lancarDadoEnabled = (boolean )observado.get(5);
		lancarDado.setEnabled(lancarDadoEnabled);
		
		revalidate();
		repaint();
	}


}
