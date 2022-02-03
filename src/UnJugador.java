import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
/**
 * This class actually plays through the levels that are created with NivelEditor.
 * @author Reed Weichler
 */
public class UnJugador extends GameScreen{

	TextoBoton mensaje1,mensaje2;
	
	private boolean ganador;
	
	private boolean debeCargarNivel;
	
	private static final Image HAS_GANADO = new Sprite("Imagenes/marioganador.gif").getImage();
	
	private double tiempoCelebracion;
	
	public UnJugador(){
		super();
		mensaje1 = new TextoBoton("CUIDA TU AMBIENTE",JGameMaker.FONT_GRANDE, 10,10, java.awt.Color.WHITE);
		mensaje2 = new TextoBoton("RECICLA LOS MATERIALES REUTILIZABLES",JGameMaker.FONT_GRANDE, 10, 12 + mensaje1.getHeight(), java.awt.Color.WHITE);
	}

	public void init(int marioImagen){
		super.init(marioImagen);
		ganador = false;
		debeCargarNivel = false;
		tiempoCelebracion = 0;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		if(cargando)return;
		mensaje1.draw(g);
		mensaje2.draw(g);
		if(ganador)
			g.drawImage(HAS_GANADO, (JGameMaker.screenWidth - HAS_GANADO.getWidth(null))/2, (JGameMaker.screenHeight - HAS_GANADO.getHeight(null))/2, null);
	}
	public void think() {
		super.think();
		lobbyActual().think(heroe,false);
		if(heroe.ganador() && !ganador){
			ganador = true;
			tiempoCelebracion = 6500/15.0;
                        AePlayWave.fondoMusica.finalizarMusica(); // acabar musica de fondo
                        
                        AePlayWave.fondoMusica = new AePlayWave("Sonidos/ganador.wav"); 
                        AePlayWave.fondoMusica.start(); // colocar musica de muerte de fondo
		}
		if(debeCargarNivel){
			debeCargarNivel = false;
			controller.proximoNivel();

			cargando = false;
		}
		if(cargando){
			debeCargarNivel = true;
		}
		if(ganador && tiempoCelebracion > 0){
			tiempoCelebracion -= JGameMaker.time();
			if(tiempoCelebracion <= 0){
				cargando = true;
				ganador = false;
			}
		}
	}
	


	public boolean guardarJuego(File f) {
		return false;
	}
	
	public void reiniciar() {
		if(heroe == null)return;
		if(heroe.vMuerte() && !ganador){
			controller.pause(true);
		}else{
			setSpawn();
		}
	}
	
	

}
