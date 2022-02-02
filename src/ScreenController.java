import java.io.File;

/**
 * This class controls the ScreenManager. Every Pantalla has one of these.
 * @author Reed Weichler
 *
 */
public class ScreenController {
	private ScreenManager pantallaPanel;
	private FileOpener opener;
	private File nivel;
	private File nivelActual;
	private int marioImagen;
	/**
	 * Creates a new instancia of ScreenController, controlling manager and using opener as the file opener.
	 * @param manager the ScreenManager to be controlled
	 * @param opener the FileOpener to be used when opening files
	 */
	public ScreenController(ScreenManager manager, FileOpener opener){
		this.pantallaPanel = manager;
		this.opener = opener;
		nivel = null;
		marioImagen = -1;
	}
	/**
	 * pauses / unpauses the juego
	 * @param pause true if wants to pause, false if wants to unpause
	 */
	public void pause(boolean pause){
		pantallaPanel.pause(pause);
	}
	/**
	 * creates a new LevelEditor
	 * @param marioImagen the color of the Heroe
	 */
	public void nivelEditor(int marioImagen){
		pantallaPanel.nivelEditor(marioImagen);
		pause(false);
	}
	
	/**
	 * creates a new UnJugador
	 * @param marioImagen the color of the Heroe
	 */
	public void unJugador(int marioImagen){
		nivel = opener.openFile();
		nivelActual = nivel;
		this.marioImagen = marioImagen;
		pantallaPanel.unJugador(marioImagen, nivel);
	}
	
	/**
	 * called when RESET LEVEL is pressed in the PauseScreen in UnJugador. It resets the current nivel back to what it originally was so it can be played through again
	 */
	public void reiniciarUnJugador(){
		pantallaPanel.unJugador(marioImagen, nivelActual);
	}
	
	/**
	 * returns to the main menu
	 */
	public void menuPrincipal(){
		pantallaPanel.renew();
	}
	
	/**
	 * If in nivel editor mode, prompts the user to select a file and saves the nivel to that file
	 * @return true if juego was saved, false if not
	 */
	public boolean guardarJuego(){
		return pantallaPanel.guardarJuego(opener.saveFile());
	}
	
	/**
	 * Prompts the user to select a file and opens the Level editor to edit that file
	 * @param marioImagen the color of the Heroe
	 */
	public void cargarNivelEditor(int marioImagen){
		pantallaPanel.nivelEditor(marioImagen,opener.openFile());
	}
	
	/**
	 * called when in UnJugador the Heroe reaches the goal. Tries to open the next nivel using the .wcfg. If it cannot be found then it returns to the main menu
	 */
	public void proximoNivel(){
		
		String lvl = nivel.getPath();
		String path, filename;
		{
			String[] split = lvl.split("/");
			filename = split[split.length - 1];
			if(split.length > 1){
				StringBuffer buffer = new StringBuffer();
				for(int i = 0; i < split.length - 1; i++){
					buffer.append(split[i]);
					buffer.append('/');
				}
				path = buffer.toString();
			}else{
				path = "";
			}
		}
		String file = path + filename.substring(0,filename.length() - "jgamemaker".length()) + "wcfg";
		boolean loaded = false;
		if(opener.readFile(file)){
			String line;
			while((line = opener.readLine()) != null){
				if(line.equals(nivelActual.getName())){
					line = opener.readLine();
					if(line == null){
						break;
					}
					try {
						File fcLevel = new File(path + line);
						if(!fcLevel.getPath().equals(nivelActual.getPath()) && opener.readFile(fcLevel)){	
							nivelActual = fcLevel;
							pantallaPanel.unJugador(marioImagen, nivelActual);
							loaded = true;
							break;
						}else{
							continue;
						}
					} catch (Exception e) {
						System.err.println(e.getMessage());
						loaded = false;
						break;
					}
				}
			}
		}
		if(!loaded){
			menuPrincipal();
		}
		
	}
}
