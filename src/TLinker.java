import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * links objects together so they can interact. For example, this links pipes together so the player can teleport from one to the other
 * @author rweichler
 *
 */
public class TLinker extends TTool {
	
	Thing enlace;
	private Sprite preview = new Sprite("Imagenes/sprites/tools/enlace.png");
	
	public TLinker(){
		super();
		enlace = null;
		//isInWorld = false;
	}
	public BufferedImage preview(){
		return preview.getBuffer();
	}
	
	/**
	 * 
	 * @return the enlace that's in the queue, if there is none then null
	 */
	public Thing getLink(){
		return enlace;
	}
	
	public boolean colocarEnlace(Thing t){
		return enlace != null && enlace.colocarEnlace(t);
	}
	
	public void enContacto(Thing t){
		if(t.colocarEnlace(enlace)){
			if(enlace == null){
				enlace = t;
			}else{
				t.enlace(enlace);
				enlace.enlace(t);
			}
		}
	}
	
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		super.draw(g,o,heroe);
		if(enlace == null || !enlace.inPlayerView(heroe))return;
		int[] c = enlace.getDrawCoords(heroe);
		g.setColor(Color.YELLOW);
		g.fillRect(c[0],c[1],c[2],c[3]);
	}
	
}
