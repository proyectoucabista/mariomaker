
/**
 * This is an Item that can be spawned in juego, can be equipped to a player and can be put inside TBlocks.
 * @author rweichler
 *
 */
public abstract class TItem extends Thing{
	
	private boolean exited;
	
	public TItem(int width, int height){
		this(0,0,width,height);
	}
	
	/**
	 * Creates a new TItem with a width of 32 and height of 32
	 */
	public TItem(){
		this(0,0,32,32);
	}
	
	public TItem(double x, double y, int width, int height){
		super(x,y,width,height);
		exited = false;
	}
	
	public void init(){
		super.init();
		if(!muriendo())
			vel.y = 1;
	}
	
	
	public void enContacto(Thing t){
		if(t instanceof Heroe){
			giveItem((Heroe)t);
			matar();
		}
	}
	/**
	 * called when a Heroe touches this
	 * @param heroe the Heroe that touched this
	 */
	public abstract void giveItem(Heroe heroe);
	
	/*public boolean tocando(Thing t){
		if(!exited) return false;
		return super.tocando(t);
	}*/
	
	public boolean activarGravedad(){
		return exited;
	}
	
	/**
	 * called when this exits its parent TBlock
	 */
	public void onBlockExit(){
		exited = true;
		if(!muriendo()){
			vel.y = 0;
			vel.x = 0;
		}
	}
	
	/*public void draw(Graphics2D g, ImageObserver o, Heroe heroe){
		//if(exitingBlock)
		//	block.draw(g,o,heroe);
	}*/
}