



import java.awt.image.*;
/**
 * When equipped, the player becomes invulnerable for a short period of time and flashes seizure-inducing colors.
 * @author Reed Weichler
 *
 */
public class TEstrella extends TItem{
	private Sprite IMAGEN = new Sprite("Imagenes/sprites/estrella.gif");
	
	
	public void giveItem(Heroe h){
		h.startStar();
	}
	
	public BufferedImage preview(){
		return IMAGEN.getBuffer();
	}
	
	public BufferedImage figureOutDrawImage(){
		return IMAGEN.getBuffer();
	}
	
	public void onBlockExit(){
		super.onBlockExit();
		vel.x = 3;
		vel.y = Math.abs(vel.x*2);
	}
	
	public void bumpY(){
		if(vel.y > 0){
			vel.y = -10;
		}else{
			vel.y = 10;
		}
	}
}