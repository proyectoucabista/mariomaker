import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Represents a hole in the ground that Things in the juego can fall through and die.
 * @author Reed Weichler
 *
 */
public class AgujeroTierra extends TBloquesColores {
	
	private static final BufferedImage preview = new Sprite("Imagenes/sprites/tools/agujero.png").getBuffer();
	
	private byte nubesAdyacentes;
	
	public AgujeroTierra(){
		super(Fondo.COLOR_NUBES, DESDE_NINGUNO);
		nubesAdyacentes = DESDE_NINGUNO;
		height = height*3/2;
	}
	public void makeSpriteUnderground(){
		setColor(Fondo.UNDERGROUND_COLOR);
	}
	public void init(){
		init(Fondo.COLOR_NUBES,DESDE_NINGUNO);
		revive();
	}
	public void think(){
		super.think();
		if(getGridPos().y != -1){
			setGridPos(getGridPos().x, -1);
		}
		
	}
	public BufferedImage preview(){
		return preview;
	}
	
	
	private boolean tieneNubesAdyacentes(byte direction){
		if(direction == DESDE_NINGUNO)return false;
		return (nubesAdyacentes & direction) != 0;
	}
	
	public void addAdyacente(TGridded other){
		super.addAdyacente(other);
		byte direction = getDirection(other);
		if(direction == DESDE_NINGUNO)return;
		if(!tieneNubesAdyacentes(direction)){
			nubesAdyacentes += direction;
		}
	}
	
	

	public void removerAdyacente(TGridded other){
		super.removerAdyacente(other);
		byte direction = getDirection(other);
		if(direction == DESDE_NINGUNO)return;
		if(tieneNubesAdyacentes(direction)){
			nubesAdyacentes -= direction;
		}
	}
	public void enContacto(Thing t){
		if(t instanceof TGridded && !(t instanceof AgujeroTierra || t instanceof TBGBloque) && getGridPos().equals(((TGridded)t).getGridPos())){
			matar();
		}
		if(!t.activarGravedad()) return;
		if((t.pos.x >= pos.x || tieneNubesAdyacentes(DESDE_IZQUIERDA)) && (t.pos.x + t.width <= pos.x + width || tieneNubesAdyacentes(DESDE_DERECHA))){
			t.cayendo = true;
		}else{
			if(t.pos.y >= pos.y + height*2/3 - 10){
				t.cayendo = false;
			}
		}
	}
	public int[] getDrawCoords(Heroe heroe){
		int[] c = super.getDrawCoords(heroe);
		c[3] = JGameMaker.screenHeight - c[1];
		return c;
	}
	
	
}
