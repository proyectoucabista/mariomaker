import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

/**
 * Draws any current juego going on. It has a Heroe and an array of Rooms that can be drawn and interacted with.
 * @author Reed Weichler
 *
 */
public class GameScreen extends Pantalla{
	/**
	 * The Rooms in the juego
	 */
	public Vector<Lobby> lobbys;
	/**
	 * The Heroe to be displayed and interact with the Things in each Lobby
	 */
	public Heroe heroe;
	/**
	 * the index of the current Lobby in lobbys
	 */
	public int lobbyIndex;
	
	/**
	 * true if the another nivel is cargando, false if not
	 */
	public boolean cargando;
	
	private TextoBoton etiquetaCarga;
	
	public GameScreen(){
		lobbys = new Vector<Lobby>();
		heroe = new Heroe();
		etiquetaCarga = new TextoBoton("CARGANDO", JGameMaker.FONT_GRANDE, Color.WHITE);
		etiquetaCarga.setPos((JGameMaker.screenWidth - etiquetaCarga.getWidth())/2, (JGameMaker.screenHeight - etiquetaCarga.getHeight())/2);
		cargando = false;
	}
	/**
	 * Returns the current lobby the Heroe is in
	 * @return the current lobby the Heroe is in
	 */
	public Lobby lobbyActual(){
		return lobbys.get(lobbyIndex);
	}
	
	public void draw(Graphics g) {
		if(cargando){
			g.setColor(Color.BLACK);
			g.fillRect(0,0,JGameMaker.WIDTH,JGameMaker.HEIGHT);
			etiquetaCarga.draw(g);
		}else{
			lobbyActual().draw(g, null, heroe);
		}
		
	}
	
	/**
	 * makes the Heroe set its position to wherever a TSpawn is
	 */
	public void setSpawn(){
		for(int i = 0; i < lobbys.size(); i++){
			if(lobbys.get(i).setSpawn(heroe)){
				lobbyIndex = i;
				break;
			}
		}
	}
	
	/**
	 * Initializes the juego by cargando a file
	 * @param f file to be opened
	 * @return true if successful, false if not
	 * @throws Exception if could not be initialized
	 */
	public boolean init(File f) throws Exception{
		return init(-1, f);
	}
	
	/**
	 * Initializes the juego by cargando a file and changing the color of the Heroe
	 * @param marioColor the color of the Heroe
	 * @param f file to be opened
	 * @return true if successful, false if not
	 * @throws Exception if could not be initialized
	 */
	public boolean init(int marioColor, File f) throws Exception{
		if(marioColor != -1){
			init(marioColor);
		}else{
			init();
		}
		if(f == null)return false;
		Vector<Vector<Serializer>> serializers;
		Object o = Serializer.fromFile(f);
		if(o == null || !(o instanceof Vector))return false;
		serializers = (Vector<Vector<Serializer>>)o;
		Vector<Serializer> s1 = serializers.get(0);
		Vector<Serializer> s2 = serializers.get(1);
		for(Serializer s: s1){
			Thing t = (Thing)s.getInstance().newInstance();
			t.init(s);
			lobbys.get(0).add(t, false);
			
		}
		for(Serializer s: s2){
			Thing t = (Thing)s.getInstance().newInstance();
			t.init(s);
			lobbys.get(1).add(t, false);
		}
		reiniciar();
		JGameMaker.actualizarTiempo();
		return true;
	}
	
	/**
	 * saves the current nivel to a File
	 * @param f File to be written to
	 * @return true if successful, false if unsuccessful
	 */
	public boolean guardarJuego(File f){
		Vector<Vector<Serializer>> serializers = new Vector<Vector<Serializer>>();
		serializers.add(lobbys.get(0).serialize());
		serializers.add(lobbys.get(1).serialize());
		return Serializer.toFile(f, serializers);
	}
	/**
	 * initializes the juego (no opening of files)
	 */
	public void init(){
		heroe.init();
		lobbys = new Vector<Lobby>();
		Lobby overworld = new Lobby(false, lobbys.size());
		lobbys.add(overworld);
		Lobby underground = new Lobby(true, lobbys.size());
		lobbys.add(underground);

		lobbyIndex = 0;
		reiniciar();
		cargando = false;
                if(AePlayWave.fondoMusica != null){
                  AePlayWave.fondoMusica.finalizarMusica();// quitar musica del lobby si se ha jugado una partida  
                }
                 
                AePlayWave.fondoMusica = new AePlayWave("Sonidos/fondo2.wav");
                AePlayWave.fondoMusica.start(); // iniciar musica del Lobby

	}
	/**
	 * initializes the juego (no opening of files) while setting the color of the Heroe
	 * @param marioColor the color of the Heroe
	 */
	public void init(int marioColor) {
		//ObjectOutputStream obj_out;
		init();
		heroe.setSpriteColor(marioColor);
	}
	public void key(KeyEvent e, boolean pressed){
		if(cargando)return;
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_ESCAPE && pressed){
			controller.pause(true);
		}else if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
			if(pressed){
				heroe.move(true);
			}else{
				heroe.stop(true);
			}
		}else if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
			if(pressed){
				heroe.move(false);
			}else{
				heroe.stop(false);
			}
		}else if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE){
			heroe.saltar(pressed);
		}else if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
			heroe.crouch(pressed);
		}
	}
	/**
	 * Called after initialization and when the Heroe dies
	 */
	public void reiniciar(){
		
	}
	
	public void mouse(MouseEvent e, boolean pressed){
		
	}
	
	public void think(){
		if(heroe.tuberiado()){
			//heroe.pipeLocations.get(lobbyIndex).setLocation(heroe.pos);
			lobbyIndex = heroe.getRoomAndSetNewPosition();
			//if(lobbyIndex == 0){
			//	lobbyIndex = 1;
			//}else{
			//	lobbyIndex = 0;
			//}
			//heroe.setPos(heroe.pipeLocations.get(lobbyIndex));
			//heroe.vel = new Point2D.Double();
		}
		if(heroe.vMuerte()){
			reiniciar();
		}
	}
}
