


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;





public class MainScreen extends Pantalla {
	
	///private boolean choosingMultiplayer = false;
	
	private Lobby lobby;
	
	//private String ip = null; //ip you're trying to connect to. when null, you're not trying to connect to a server, when initialized, this means you're trying to connect to a server.
	private int marioSeleccionado;
	
	private final BufferedImage[] MARIO_COLORS = {
		null,
		null,
		null,
		null,
		null,
		null
	};
	
	private TextButton	editorButton,
						nuevoJuego,
						cargarJuego,
						botonUnico,
						titulo,
						errorCargar,
						salir;
	
	private static final int ESPACIO_ARRIBA = 150, ESPACIO_ENTRE_TITULOS = 2;
	
	private static final Font FONT_TITULO = new Font("Courier", Font.PLAIN, 100);
	
	private boolean editorSeleccionado, cargaFallida;
	
	public MainScreen(){
		editorSeleccionado = false;
		cargaFallida = false;
		for(int i = 0; i < MARIO_COLORS.length; i++){
			Heroe h = new Heroe();
			h.setSpriteColor(i);
			MARIO_COLORS[i] = (h.IMAGEN[0]).getBuffer();
		}
		marioSeleccionado = (int)(Math.random()*6);
		boolean underground = Math.random() > 0.5;
		lobby = new Lobby(underground, -1);
		lobby.add(new TGoomba(500,0,32,32));
		TKoopa koopa = new TKoopa(3000,0);
		lobby.add(koopa);
		koopa.crearCaparazon(true);
		koopa.vel.x = -TKoopa.VELOCIDAD_CAPARAZON;
		lobby.add(new TBlock(32*5,32*4, TBlock.BLOQUE_PREGUNTA));
		lobby.add(new TBlock(32*4,32*2, TBlock.LADRILLOS));
		lobby.add(new TBlock(32*3,32*2, TBlock.LADRILLOS));
		lobby.add(new TBlock(32*5,32*2, TBlock.LADRILLOS));
		lobby.add(new TBlock(32*6,32*2, TBlock.BLOQUE_PREGUNTA_DESACTIVADO));
		lobby.add(new TBlock(32*7,32*2, TBlock.LADRILLOS));
		TTuberia tuberia = new TTuberia();
		tuberia.setPos(-32*7, 32*5);
		TPirhana pirana = new TPirhana();
		lobby.add(tuberia);
		lobby.add(pirana);
		tuberia.addPirhana(pirana);
		lobby.add(new TBlock(-32*5, 0, TBlock.LADRILLOS, null, true));
		
		nuevoJuego = new TextButton("NUEVO JUEGO", JGameMaker.FONT_GRANDE);
		cargarJuego = new TextButton("CARGAR JUEGO", JGameMaker.FONT_GRANDE);
		editorButton = new TextButton("CREAR MAPA", JGameMaker.FONT_GRANDE);
		botonUnico = new TextButton("UN SOLO JUGADOR", JGameMaker.FONT_GRANDE);
		errorCargar = new TextButton("ERROR AL CARGAR JUEGO", JGameMaker.FONT_GRANDE, TextButton.TITULO);
		//multiButton = new TextRect("ONLINE", JGameMaker.FONT_GRANDE);
		titulo = new TextButton("JGameMaker", FONT_TITULO, Color.WHITE);
		salir = new TextButton("SALIR", JGameMaker.FONT_GRANDE);
		int height = editorButton.getHeight();
		
		titulo.setPos(0, ESPACIO_ARRIBA - titulo.getHeight() - 50);
		editorButton.setPos(0, ESPACIO_ARRIBA);
		nuevoJuego.setPos(0, ESPACIO_ARRIBA);
		cargarJuego.setPos(nuevoJuego.getWidth() + ESPACIO_ENTRE_TITULOS*20, ESPACIO_ARRIBA);
		botonUnico.setPos(0, ESPACIO_ARRIBA + height + ESPACIO_ENTRE_TITULOS);
		//multiButton.setPos(0, ESPACIO_ARRIBA + (height + ESPACIO_ENTRE_TITULOS)*2);
		errorCargar.setPos(0,ESPACIO_ARRIBA + (height + ESPACIO_ENTRE_TITULOS) * 2);
		salir.setPos(0, ESPACIO_ARRIBA + (height + ESPACIO_ENTRE_TITULOS)*4);
	}
	
	public void cargaFallida(){
		cargaFallida = true;
		editorSeleccionado = false;
	}
	
	public void draw(Graphics g) {
		lobby.draw(g,null,null);
    	
    	titulo.draw(g);
    	
		if(!editorSeleccionado){
	    	editorButton.draw(g);
		}else{
			cargarJuego.draw(g);
			nuevoJuego.draw(g);
		}
		if(cargaFallida){
			errorCargar.draw(g);
		}
	    botonUnico.draw(g);
	    salir.draw(g);
	    
	    
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
		for(int i = 0; i < 6; i++){
			int x = i, y= 0;
			if(i > 2){
				x -= 3;
				y = 1;
			}

			if(i == marioSeleccionado)
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			//draw the mario itself
			g.drawImage(
				MARIO_COLORS[i],
				428 + x*128,
				100 + y*80,
				80,
				80,
				null//(java.awt.image.ImageObserver)this
			);

			if(i == marioSeleccionado)
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
		}
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));

	}

	public void key(KeyEvent e, boolean down) {
		int code = e.getKeyCode();
		//char c = e.getKeyChar();
		
		if(!down) return; //continue only if you're pressing the key down
		
		
		
		
		//if(ip == null){
		if(code == KeyEvent.VK_ENTER || code == KeyEvent.VK_SPACE || code == KeyEvent.VK_ESCAPE){
			controller.nivelEditor(marioSeleccionado);
		}
		return; //continue only if you're typing the IP of the server to connect to
		//}
		
	
	}

	public void mouse(MouseEvent e, boolean down) {
		if(down)return;
		int x = e.getX(), y = e.getY();
		if(salir.contains(x,y)){
			System.exit(0);
		}
		if(!editorSeleccionado){
			if(editorButton.contains(x,y)){
				//controller.cargarJuego(marioSeleccionado);
				editorSeleccionado = true;
			}
		}else{
			if(cargarJuego.contains(x,y)){
				cargaFallida = false;
				controller.cargarNivelEditor(marioSeleccionado);
			}else if(nuevoJuego.contains(x,y)){
				controller.nivelEditor(marioSeleccionado);
			}
		}
		if(botonUnico.contains(x,y)){
			cargaFallida = false;
			controller.unJugador(marioSeleccionado);
		}
		
		
		for(int i = 0; i < 6; i++){
			int xpos = i, ypos= 0;
			if(i > 2){
				xpos -= 3;
				ypos = 1;
			}
			if(x > 428 + xpos*128 && x < 508 + xpos*128 &&
				y > 100 + ypos*80 && y < 180 + ypos*80){
				marioSeleccionado = i;
				break;
			}
		}

	}

	public void think() {
		
		lobby.think(null,true);

	}

}
