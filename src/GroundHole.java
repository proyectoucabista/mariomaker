import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * Represents a hole in the ground that Things in the game can fall through and die.
 * @author Reed Weichler
 *
 */
public class GroundHole extends TColoredBlock {
	
	private static final BufferedImage preview = new Sprite("Imagenes/sprites/tools/agujero.png").getBuffer();
	
	private byte adjacentSkies;
	
	public GroundHole(){
		super(Fondo.COLOR_NUBES, FROM_NONE);
		adjacentSkies = FROM_NONE;
		height = height*3/2;
	}
	public void makeSpriteUnderground(){
		setColor(Fondo.UNDERGROUND_COLOR);
	}
	public void init(){
		init(Fondo.COLOR_NUBES,FROM_NONE);
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
	
	
	private boolean hasAdjacentSky(byte direction){
		if(direction == FROM_NONE)return false;
		return (adjacentSkies & direction) != 0;
	}
	
	public void addAdjacent(TGridded other){
		super.addAdjacent(other);
		byte direction = getDirection(other);
		if(direction == FROM_NONE)return;
		if(!hasAdjacentSky(direction)){
			adjacentSkies += direction;
		}
	}
	
	

	public void removeAdjacent(TGridded other){
		super.removeAdjacent(other);
		byte direction = getDirection(other);
		if(direction == FROM_NONE)return;
		if(hasAdjacentSky(direction)){
			adjacentSkies -= direction;
		}
	}
	public void enContacto(Thing t){
		if(t instanceof TGridded && !(t instanceof GroundHole || t instanceof TBGBlock) && getGridPos().equals(((TGridded)t).getGridPos())){
			matar();
		}
		if(!t.activarGravedad()) return;
		if((t.pos.x >= pos.x || hasAdjacentSky(FROM_LEFT)) && (t.pos.x + t.width <= pos.x + width || hasAdjacentSky(FROM_RIGHT))){
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
