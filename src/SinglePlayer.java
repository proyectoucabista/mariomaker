import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
/**
 * This class actually plays through the levels that are created with LevelEditor.
 * @author Reed Weichler
 */
public class SinglePlayer extends GameScreen{

	TextButton rect,rect2;
	
	private boolean won;
	
	private boolean shouldLoadLevel;
	
	private static final Image HAS_GANADO = new Sprite("Imagenes/marioganador.gif").getImage();
	
	private double wonTimer;
	
	public SinglePlayer(){
		super();
		rect = new TextButton("HIIIIIIII",JGameMaker.FONT_GRANDE, 10,10, java.awt.Color.WHITE);
		rect2 = new TextButton("FULLY WORK YET",JGameMaker.FONT_GRANDE, 10, 12 + rect.getHeight(), java.awt.Color.WHITE);
	}

	public void init(int marioImage){
		super.init(marioImage);
		won = false;
		shouldLoadLevel = false;
		wonTimer = 0;
	}
	
	public void draw(Graphics g) {
		super.draw(g);
		if(loading)return;
		rect.draw(g);
		rect2.draw(g);
		if(won)
			g.drawImage(HAS_GANADO, (JGameMaker.screenWidth - HAS_GANADO.getWidth(null))/2, (JGameMaker.screenHeight - HAS_GANADO.getHeight(null))/2, null);
	}
	public void think() {
		super.think();
		currentRoom().think(hero,false);
		if(hero.won() && !won){
			won = true;
			wonTimer = 2500/15.0;
		}
		if(shouldLoadLevel){
			shouldLoadLevel = false;
			controller.nextLevel();

			loading = false;
		}
		if(loading){
			shouldLoadLevel = true;
		}
		if(won && wonTimer > 0){
			wonTimer -= JGameMaker.time();
			if(wonTimer <= 0){
				loading = true;
				won = false;
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
		if(hero.isDead() && !won){
			controller.pause(true);
		}else{
			setSpawn();
		}
	}
	
	

}
