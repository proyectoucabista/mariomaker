import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
/**
 * This class actually plays through the levels that are created with LevelEditor.
 * @author Reed Weichler
 */
public class SinglePlayer extends GameScreen{

	TextButton rect,rect2;
	
	private boolean ganador;
	
	private boolean shouldLoadLevel;
	
	private static final Image HAS_GANADO = new Sprite("Imagenes/marioganador.gif").getImage();
	
	private double tiempoCelebracion;
	
	public SinglePlayer(){
		super();
		rect = new TextButton("CUIDA TU AMBIENTE",JGameMaker.FONT_GRANDE, 10,10, java.awt.Color.WHITE);
		rect2 = new TextButton("RECICLA LOS MATERIALES REUTILIZABLES",JGameMaker.FONT_GRANDE, 10, 12 + rect.getHeight(), java.awt.Color.WHITE);
	}

	public void init(int marioImage){
		super.init(marioImage);
		ganador = false;
		shouldLoadLevel = false;
		tiempoCelebracion = 0;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		if(loading)return;
		rect.draw(g);
		rect2.draw(g);
		if(ganador)
			g.drawImage(HAS_GANADO, (JGameMaker.screenWidth - HAS_GANADO.getWidth(null))/2, (JGameMaker.screenHeight - HAS_GANADO.getHeight(null))/2, null);
	}
	public void think() {
		super.think();
		currentRoom().think(hero,false);
		if(hero.ganador() && !ganador){
			ganador = true;
			tiempoCelebracion = 6500/15.0;
                        AePlayWave.fondoMusica.finalizarMusica(); // acabar musica de fondo
                        
                        AePlayWave.fondoMusica = new AePlayWave("Sonidos/ganador.wav"); 
                        AePlayWave.fondoMusica.start(); // colocar musica de muerte de fondo
		}
		if(shouldLoadLevel){
			shouldLoadLevel = false;
			controller.nextLevel();

			loading = false;
		}
		if(loading){
			shouldLoadLevel = true;
		}
		if(ganador && tiempoCelebracion > 0){
			tiempoCelebracion -= JGameMaker.time();
			if(tiempoCelebracion <= 0){
				loading = true;
				ganador = false;
			}
		}
	}
	
//	public void mouse(MouseEvent e, boolean pressed){
//		if(e.getButton() == MouseEvent.BUTTON1 && pressed){
//		}
//	}

	public boolean saveGame(File f) {
		return false;
	}
	
	public void reset() {
		if(hero == null)return;
		if(hero.isDead() && !ganador){
			controller.pause(true);
		}else{
			setSpawn();
		}
	}
	
	

}
