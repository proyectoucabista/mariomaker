import java.awt.geom.Point2D;

/**
 * Enemy in juego. Has the capability of killing a player.
 * @author Reed Weichler
 *
 */
public abstract class TEnemy extends Thing {
	
	/**
	 * Creates a new TEnemy with the coordinates (x,y) and the specified width and height.
	 */
	public TEnemy(double x, double y, int width, int height){
		super(x,y,width,height);
	}
	/**
	 * Creatse a new TEnemy width coordinates (0,0), width of 1 and height of 1.
	 */
	public TEnemy(){
		this(0,0,1,1);
	}
	/**
	 * determines the direction(s) in which when touched, this will matar a player.
	 * @return the direction(s) in which when touched, this will matar a player.
	 */
	public byte killDirection(){
		return FROM_EVERYWHERE;
	}
	/**
	 * Called when this is hit by a block from below. By default, it is killed.
	 * @param block the block that hit it
	 */
	public void blockHit(Thing block){
		matar(new Point2D.Double((pos.x - block.pos.x)/10, Math.random()*3 + 6));
	}
	/**
	 * called when a heroe touches this from a non-lethal direction, as indicated by killDirection(). By default, this is killed.
	 * @param heroe
	 */
	public void heroTouch(Heroe heroe){
		matar();
	}
	/**
	 * called when this touches a non-player and non-block entity
	 * @param t
	 */
	public void thingTouch(Thing t){
		
	}
	/**
	 * called when a heroe touches this from a lethal direction, as indicated by killDirection(). By default, the heroe is killed.
	 * @param heroe
	 */
	public void heroKill(Heroe heroe){
		heroe.matar();
	}
	/**
	 * gives the player the ability for an extra saltar, usually called when this is hit from above
	 * @param heroe
	 */
	public void stomp(Heroe heroe){
		heroe.setPos(heroe.pos.x, pos.y+height + 1);
		if(heroe.saltoAbajo()){
			heroe.vel.y = 0;
			heroe.saltar(true,false);
		}else{
			heroe.vel.y = 7;
		}
	}
	
	public void enContacto(Thing t){
		if(muriendo())return;
		byte where = fromWhere(t);
		if(t instanceof TBlock && where == FROM_BELOW && t.vel.y > 1){
			blockHit(t);
		}else if(t instanceof Heroe){
			Heroe heroe = (Heroe)t;
			if((where & killDirection()) > 0){
				heroKill(heroe);
			}else if(!heroe.vModoEstrella()){
				heroTouch(heroe);
			}
		}
		thingTouch(t);
	}
	
	public boolean isStatic(){
		return false;
	}
	
}
