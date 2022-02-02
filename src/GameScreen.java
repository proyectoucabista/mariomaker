import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Vector;

/**
 * Draws any current game going on. It has a Hero and an array of Rooms that can be drawn and interacted with.
 * @author Reed Weichler
 *
 */
public class GameScreen extends Pantalla{
	/**
	 * The Rooms in the game
	 */
	public Vector<Lobby> lobbys;
	/**
	 * The Hero to be displayed and interact with the Things in each Lobby
	 */
	public Hero hero;
	/**
	 * the index of the current Lobby in lobbys
	 */
	public int roomIndex;
	
	/**
	 * true if the another level is loading, false if not
	 */
	public boolean loading;
	
	private TextButton loadingLabel;
	
	public GameScreen(){
		lobbys = new Vector<Lobby>();
		hero = new Hero();
		loadingLabel = new TextButton("LOADING", JGameMaker.FONT_GRANDE, Color.WHITE);
		loadingLabel.setPos((JGameMaker.screenWidth - loadingLabel.getWidth())/2, (JGameMaker.screenHeight - loadingLabel.getHeight())/2);
		loading = false;
	}
	/**
	 * Returns the current lobby the Hero is in
	 * @return the current lobby the Hero is in
	 */
	public Lobby currentRoom(){
		return lobbys.get(roomIndex);
	}
	
	public void draw(Graphics g) {
		if(loading){
			g.setColor(Color.BLACK);
			g.fillRect(0,0,JGameMaker.WIDTH,JGameMaker.HEIGHT);
			loadingLabel.draw(g);
		}else{
			currentRoom().draw(g, null, hero);
		}
		
	}
	
	/**
	 * makes the Hero set its position to wherever a TSpawn is
	 */
	public void setSpawn(){
		for(int i = 0; i < lobbys.size(); i++){
			if(lobbys.get(i).setSpawn(hero)){
				roomIndex = i;
				break;
			}
		}
	}
	
	/**
	 * Initializes the game by loading a file
	 * @param f file to be opened
	 * @return true if successful, false if not
	 * @throws Exception if could not be initialized
	 */
	public boolean init(File f) throws Exception{
		return init(-1, f);
	}
	
	/**
	 * Initializes the game by loading a file and changing the color of the Hero
	 * @param marioColor the color of the Hero
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
	 * saves the current level to a File
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
	 * initializes the game (no opening of files)
	 */
	public void init(){
		hero.init();
		lobbys = new Vector<Lobby>();
		Lobby overworld = new Lobby(false, lobbys.size());
		lobbys.add(overworld);
		Lobby underground = new Lobby(true, lobbys.size());
		lobbys.add(underground);

		roomIndex = 0;
		reiniciar();
		loading = false;
                if(AePlayWave.fondoMusica != null){
                  AePlayWave.fondoMusica.finalizarMusica();// quitar musica del lobby si se ha jugado una partida  
                }
                 
                AePlayWave.fondoMusica = new AePlayWave("Sonidos/fondo2.wav");
                AePlayWave.fondoMusica.start(); // iniciar musica del Lobby

	}
	/**
	 * initializes the game (no opening of files) while setting the color of the Hero
	 * @param marioColor the color of the Hero
	 */
	public void init(int marioColor) {
		//ObjectOutputStream obj_out;
		init();
		hero.setSpriteColor(marioColor);
	}
	public void key(KeyEvent e, boolean pressed){
		if(loading)return;
		int code = e.getKeyCode();
		if(code == KeyEvent.VK_ESCAPE && pressed){
			controller.pause(true);
		}else if(code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT){
			if(pressed){
				hero.move(true);
			}else{
				hero.stop(true);
			}
		}else if(code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT){
			if(pressed){
				hero.move(false);
			}else{
				hero.stop(false);
			}
		}else if(code == KeyEvent.VK_W || code == KeyEvent.VK_UP || code == KeyEvent.VK_SPACE){
			hero.saltar(pressed);
		}else if(code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN){
			hero.crouch(pressed);
		}
	}
	/**
	 * Called after initialization and when the Hero dies
	 */
	public void reiniciar(){
		
	}
	
	public void mouse(MouseEvent e, boolean pressed){
		
	}
	
	public void think(){
		if(hero.piped()){
			//hero.pipeLocations.get(roomIndex).setLocation(hero.pos);
			roomIndex = hero.getRoomAndSetNewPosition();
			//if(roomIndex == 0){
			//	roomIndex = 1;
			//}else{
			//	roomIndex = 0;
			//}
			//hero.setPos(hero.pipeLocations.get(roomIndex));
			//hero.vel = new Point2D.Double();
		}
		if(hero.isDead()){
			reiniciar();
		}
	}
}
