


import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * This class holds all the Screens to be displayed in the juego and also switches to/from them when necessary. When creating a new screen it sets it's ScreenController to the same as this one, so to change anything in ScreenManager just call the methods in the ScreenController and it should work.
 * @author Reed Weichler
 *
 */
public class ScreenManager implements MouseListener, MouseMotionListener,KeyListener{
	
	private GameScreen juego;
	private Pantalla menu;
	
	private boolean pausado;
	private ScreenController controller;
	
	/**
	 * position of mouse on the screen
	 */
	public static Point mouse = new Point();
	
	/*public static Server server;
	public static Client client;
	
	public static Connector connection;*/

	private ArrayList<Integer> teclaPresionada;
	
	
	private boolean thinked = true;
	
	/**
	 * creates a new ScreenManager with a ScreenController with a FileOpener as specified
	 * @param opener the FileOpener that will be put inside the created ScreenController
	 */
	public ScreenManager(FileOpener opener){
		teclaPresionada = new ArrayList<Integer>();
		controller = new ScreenController(this,opener);
		renew();
	}
	
	/**
	 * refreshes everything to where it's like you just opened the program
	 */
	public void renew(){
		juego = null;
		menu = new MainScreen();
		menu.controller = controller;
		pausado = true;
		
		AePlayWave.fondoMusica = new AePlayWave("Sonidos/fondo.wav");
               
                AePlayWave.fondoMusica.start(); // inicia musica principal del juego
		
	}
	
	/**
	 * corrects syncronization with drawing and thinking
	 * @return true if did think, false if not
	 */
	public boolean didThink(){
		return thinked;
	}
	
	/**
	 * draws the menu Pantalla and juego GameScreen
	 * @param g the Graphics to be drawn to
	 */
	public void draw(Graphics g){
		//System.out.println("Draw");
		((Graphics2D)g).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		

		if(!pausado && juego != null){
			juego.draw(g);
		}else{
			if(menu instanceof PauseScreen)
				juego.draw(g);
			menu.draw(g);
		}
		thinked = false;
		/*if(inGame){
			main.draw(g, this);
			if(connected){
				g.setFont(CHAT_BOLD);
				g.setColor(Color.WHITE);
				if(typing)
					g.drawString("You: " + typedString, 5,getHeight()-6);
				else
					g.drawString("Press ~ to chat!", 5,getHeight()-6);
			}
		}*/
	}
	
	/**
	 * starts nivel editor mode (new file)
	 * @param marioColor the color the Heroe should be
	 */
	public void nivelEditor(int marioColor){
		juego = new NivelEditor();
		menu = new PauseScreen(true);
		juego.controller = controller;
		menu.controller = controller;
		juego.init(marioColor);
		
	}
	
	/**
	 * starts nivel editor mode (open file)
	 * @param marioColor the color the Heroe should be
	 * @param f the file to be opened which contains the nivel data
	 */
	public void nivelEditor(int marioColor, File f){
		juego = new NivelEditor();
		boolean init;
		try{
			init = juego.init(marioColor, f);
		}catch(Exception ex){
			if(menu instanceof MainScreen)
				((MainScreen)menu).cargaFallida();
			juego = null;
			return;
		}
		if(!init){
			juego = null;
			return;
		}
		pausado = false;
		menu = new PauseScreen(true);
		juego.controller = controller;
		menu.controller = controller;
		
	}
	
	/**
	 * saves the nivel, if any to file
	 * @param f the file to be written to
	 * @return true if successfully written, false if not
	 */
	public boolean guardarJuego(File f){
		if(juego != null){
			return juego.guardarJuego(f);
		}
		return false;
	}
	

	/**
	 * starts single player mode
	 * @param marioColor the color the Heroe should be
	 * @param f the file to be opened and read
	 */
	public void unJugador(int marioColor, File f){
		if(juego == null || !(juego instanceof UnJugador))
			juego = new UnJugador();
		boolean init;
		juego.cargando = true;
		try{
			init = juego.init(marioColor, f);
		}catch(Exception ex){
			if(menu instanceof MainScreen)
				((MainScreen)menu).cargaFallida();
			juego = null;
			return;
		}
		if(!init){
			juego = null;
			return;
		}
		pausado = false;
		menu = new PauseScreen(false);
		juego.controller = controller;
		menu.controller = controller;
	}
	
	/**
	 * called every frame, updates think in the GameScreen and menu Pantalla.
	 */
	public void think(){
		//System.out.println("Think");
		if(!pausado){
			juego.think();
		}else{
			menu.think();
		}
		thinked = true;
	}
	
	/**
	 * pauses/unpauses the juego, if one is in session
	 * @param pause if true, the juego pauses. if false, it unpauses
	 */
	public void pause(boolean pause){
		pausado = pause;
	}
	
	/**
	 * called when a key is pressed
	 * @param event the KeyEvent from keyPressed/keyReleased
	 * @param pressed is true if the key was pressed down, is false if not
	 */
	public void key(KeyEvent event, boolean pressed){
		if(!pausado){
			juego.key(event, pressed);
		}else{
			menu.key(event, pressed);
		}
	}

	/**
	 * called when a mouse button is pressed
	 * @param event the MouseEvent from mousePressed/mouseReleased
	 * @param pressed is true if the mouse was pressed down, is false if not
	 */
	public void mouse(MouseEvent event, boolean pressed){
		if(pausado){
			menu.mouse(event, pressed);
		}else{
			juego.mouse(event,pressed);
		}
	}
	
	public void keyPressed(KeyEvent event){
		int code = event.getKeyCode();
		if(teclaPresionada.size() != 0 && teclaPresionada.get(teclaPresionada.size()-1).equals(code) || teclaPresionada.indexOf(code) != -1)
			return;
		teclaPresionada.add(code);
		key(event,true);
	}
	public void keyReleased(KeyEvent event){
		int code = event.getKeyCode();
		int index = teclaPresionada.indexOf(code);
		if(index != -1)
			teclaPresionada.remove(index);
		key(event,false);
	}
	
	/*private int findKey(int value, int low, int high) {
		if (high < low)
			return -1;
		if(low == high){
			if(teclaPresionada.get(low) == value){
				return low;
			}else{
				return -1;
			}
		}
		int mid = low + (high - low) / 2;
		if (teclaPresionada.get(mid) > value)
			return findKey(value, low, mid-1);
		else if (teclaPresionada.get(mid) < value)
			return findKey(value, mid+1, high);
		return mid; // found
	}*/
	
	public void keyTyped(KeyEvent event){}
	
	public void mouseClicked(MouseEvent event){}
	public void mousePressed(MouseEvent event){
		mouse(event,true);
	}
	public void mouseReleased(MouseEvent event){
		mouse(event,false);
	}
	public void mouseExited(MouseEvent event){}
	public void mouseEntered(MouseEvent event){}
	
	public void mouseMoved(MouseEvent event){
		mouse.setLocation(event.getX(),event.getY());	
	}
	public void mouseDragged(MouseEvent event){
		mouseMoved(event);
	}
	
}