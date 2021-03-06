import java.awt.geom.Point2D;

/**
 * Enemy in juego. Has the capability of killing a player.
 * @author Reed Weichler
 *
 */
public abstract class TEnemigo extends Thing {
	
	/**
	 * Creates a new TEnemigo with the coordinates (x,y) and the specified width and height.
	 */
	public TEnemigo(double x, double y, int width, int height){
		super(x,y,width,height);
	}
	/**
	 * Creatse a new TEnemigo width coordinates (0,0), width of 1 and height of 1.
	 */
	public TEnemigo(){
		this(0,0,1,1);
	}
	/**
	 * determines the direction(s) in which when touched, this will matar a player.
	 * @return the direction(s) in which when touched, this will matar a player.
	 */
	public byte direccionMuerte(){
		return DE_TODASPARTES;
	}
	/**
	 * Called when this is hit by a block from below. By default, it is asesinado.
	 * @param block the block that hit it
	 */
	public void golpeBloque(Thing block){
		matar(new Point2D.Double((pos.x - block.pos.x)/10, Math.random()*3 + 6));
	}
	/**
	 * called when a heroe touches this from a non-lethal direction, as indicated by direccionMuerte(). By default, this is asesinado.
	 * @param heroe
	 */
	public void heroeContacto(Heroe heroe){
		matar();
	}
	/**
	 * called when this touches a non-player and non-block entity
	 * @param t
	 */
	public void thingTouch(Thing t){
		
	}
	/**
	 * called when a heroe touches this from a lethal direction, as indicated by direccionMuerte(). By default, the heroe is asesinado.
	 * @param heroe
	 */
	public void heroeMatar(Heroe heroe){
		heroe.matar();
	}
	/**
	 * gives the player the ability for an extra saltar, usually called when this is hit from above
	 * @param heroe
	 */
	public void pisar(Heroe heroe){
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
		if(t instanceof TBloque && where == DESDE_ABAJO && t.vel.y > 1){
			golpeBloque(t);
		}else if(t instanceof Heroe){
			Heroe heroe = (Heroe)t;
			if((where & direccionMuerte()) > 0){
				heroeMatar(heroe);
			}else if(!heroe.vModoEstrella()){
				heroeContacto(heroe);
			}
		}
		thingTouch(t);
	}
	
	public boolean isStatic(){
		return false;
	}
	
}
