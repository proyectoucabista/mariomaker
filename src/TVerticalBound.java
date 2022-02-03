import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * An infinitely wide and 32-unit tall structure that impermeable by all in-juego objects. Seen as orange in the Level editor.
 * @author Reed Weichler
 *
 */
public class TVerticalBound extends TLimite {

	private static final Sprite PREVIEW = new Sprite("Imagenes/sprites/tools/vbound.png");

	public BufferedImage preview(){
		return PREVIEW.getBuffer();
	}
	public void think(){
		boolean muerte = this.asesinado();
		super.think();
		if(!muerte){
			revive();
		}
	}
	
	public boolean tocando(Thing t){

		if(t.muriendo())return false;
		double
			y1 = pos.y,
			y2 = t.pos.y;
		double
			h1 = height + y1,
			h2 = t.height + y2;
		
		if(
			((
				h1 >= y2 &&
				y1 <= h2
			)||(
				h2 >= y1 &&
				y2 <= h1
			))
				
		){
			return true;
		}
		return false;
	}
	
	public byte fromWhere(Thing t){
		if(pos.y + height <= t.ultimaPos.y && t.vel.y < 0){
			return DESDE_ARRIBA;
		}else if(pos.y >= t.ultimaPos.y + t.height && t.vel.y > 0){
			return DESDE_ABAJO;
		}
		return DESDE_NINGUNO;
	}
	
	public int[] getDrawCoords(Heroe heroe){
		int[] c = super.getDrawCoords(heroe);
		c[0] = 0;
		c[2] = JGameMaker.screenWidth;
		return c;
	}
	

	public boolean inPlayerView(Heroe heroe){
		return heroe.viewY()-pos.y > 0 && heroe.viewY()-pos.y-height < JGameMaker.screenHeight;
	}
}
