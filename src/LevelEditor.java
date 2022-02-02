



import java.awt.*;
import java.util.Vector;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * The Level Editor part of the juego. It contains a SpawnScreen that is used to create and manipulate Things in the juego.
 * @author rweichler
 *
 */
public class LevelEditor extends GameScreen{
	//private CreativeBox box;
	private SpawnScreen pantallaSpawn;
	private boolean mouseAbajo;
	private Vector<TGridded> arrastreSpawn;
	private boolean congelar;
	
	public LevelEditor(){
		super();
		pantallaSpawn = new SpawnScreen(heroe);
		mouseAbajo = false;
		arrastreSpawn = null;
		congelar = false;
	}
	
	/*public void resetHero(int marioColor){
		heroe.setSpriteColor(marioColor);
		resetHero();
	}*/
	public void reiniciar(){
		heroe.init();
		heroe.setMuertePos();
		heroe.startInvulnerable();
		setSpawn();
	}
	
	public boolean init(int marioColor, File f) throws Exception{
		boolean b = super.init(marioColor, f);
		congelar = true;
		return b;
	}
	
	public void init(int marioColor){
		super.init(marioColor);
		congelar = false;
	}


	public void draw(Graphics g) {
		lobbyActual().draw(g, null, heroe, pantallaSpawn);
		//box.draw(g,heroe);
		pantallaSpawn.draw(g);
		
	}
	public void key(KeyEvent e, boolean pressed) {
		super.key(e,pressed);
		pantallaSpawn.key(e,pressed);
		
	}

	public void mouse(MouseEvent e, boolean down) {
		pantallaSpawn.mouse(e,down);
		if(pantallaSpawn.shouldToggleLevel()){
			lobbyIndex++;
			if(lobbyIndex == lobbys.size()){
				lobbyIndex = 0;
			}
			heroe.init();
			heroe.startInvulnerable();
		}else if(pantallaSpawn.shouldToggleFreeze()){
			congelar = !congelar;
		}
		if(down){
			mouseAbajo = true;
			//box.start(e.getX(), e.getY());
			Thing spawn = pantallaSpawn.getSpawn();
			if(spawn != null){
				lobbyActual().add(spawn);
				if(spawn instanceof TGridded){
					arrastreSpawn = new Vector<TGridded>();
					arrastreSpawn.add((TGridded)spawn);
				}
			}
		}else{
			mouseAbajo = false;
			arrastreSpawn = null;
		}
		
	}

	public void think() {
		super.think();
		pantallaSpawn.think();
		lobbyActual().think(heroe,true,congelar);
		Class spawn = lobbyActual().debeRemoverSpawnOtrosLobbys();
		if(spawn != null){
			for(Lobby lobby: lobbys){
				if(lobby != lobbyActual()){
					lobby.removerSpawns(spawn);
				}
			}
		}
		
		if(mouseAbajo && arrastreSpawn != null && arrastreSpawn.size() > 0 && pantallaSpawn.pasarSpawn() != null && pantallaSpawn.pasarSpawn() instanceof TGridded){
			TGridded peek = (TGridded)pantallaSpawn.pasarSpawn();
			boolean touchingSomething = false;
			for(TGridded grid: arrastreSpawn){
				if(peek.representation().contains(grid.representation())){
					touchingSomething = true;
					break;
				}
			}
			if(!touchingSomething){
				arrastreSpawn.add(peek);
				lobbyActual().add(pantallaSpawn.getSpawn());
			}
		}
		if(mouseAbajo && pantallaSpawn.pasarSpawn() != null && pantallaSpawn.pasarSpawn() instanceof TRemover){
			lobbyActual().add(pantallaSpawn.pasarSpawn());
		}
		
	}
}
