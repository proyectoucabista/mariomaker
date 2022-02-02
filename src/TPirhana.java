import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/**
 * The little chomper dude that is found in pipes. They cannot be found anywhere else. If it does not have a parent tuberia then it kills itelf.
 * @author Reed Weichler
 *
 */
public class TPirhana extends TEnemy{
	private static final String PATH = "Imagenes/sprites/tuberia/pirana/";
	private static final double MOVE_VELOCITY = 0.75;
	private static final int IDLE_TIME = 1500/15;
	
	
	private boolean hasHero;
	private boolean hasPipe;
	private boolean harmless;
	private double biteYCoord;
	private double idleTime;
	private Sprite[] IMAGEN = {
			new Sprite(PATH+"open.png"),
			new Sprite(PATH+"closed.png"),
			new Sprite(PATH+"preview.png"),
	};
	private static final Color[] aboveGround = {
		Color.WHITE,
		new Color(255,165,66),
		new Color(0,173,0),
		
	};
	private static final Color[] underGround = {
		new Color(240,208,176),
		new Color(228,92,16),
		new Color(0,136,136),
	};

	
	public TPirhana(){
		this(0,0);
	}
	public TPirhana(double x, double y){
		super(x,y,16*2,24*2);
		hasHero = false;
		hasPipe = false;
		biteYCoord = -1;
		idleTime = 0;
		harmless = true;
	}
	

	public byte killDirection(){
		if(harmless)
			return FROM_NONE;
		else
			return FROM_EVERYWHERE;
	}
	public void heroTouch(Heroe heroe){
		//super.heroTouch(heroe);
	}
	
	public BufferedImage figureOutDrawImage(){
		Sprite image = IMAGEN[(System.currentTimeMillis() % 400 > 200)?1:0];
		if(muriendo()){
			return image.flipY();
		}else{
			return image.getBuffer();
		}
	}
	/**
	 * called when a heroe is tocando the parent TTuberia
	 */
	public void warnHero(){
		hasHero = true;
	}
	
	public void startBite(double y){
		biteYCoord = y;
	}
	
	public void think(){
		super.think();
		if(!hasPipe){
			matar();
			return;
		}

		if(biteYCoord != -1){
			//waiting to move up and now can
			if(pos.y < biteYCoord && vel.y == 0 && idleTime <= 0 && !hasHero){
				vel.y = MOVE_VELOCITY;
				harmless = false;
			//waiting to move down and now can
			}else if(pos.y >= biteYCoord && vel.y == 0 && idleTime <= 0){
				vel.y = -MOVE_VELOCITY;
			//just finished moving up and now must idle
			}else if(pos.y > biteYCoord && vel.y > 0 && idleTime <= 0){
				idleTime = IDLE_TIME;
				pos.y = biteYCoord;
				vel.y = 0;
			//just finished moving down and must idle
			}else if(pos.y < biteYCoord - height && vel.y < 0 && idleTime <= 0){
				hide();
			}
		}
		if(pos.y == biteYCoord - height && vel.y == 0 && hasHero){
			hasHero = false;
			idleTime = IDLE_TIME;
		}else if(idleTime > 0){
			idleTime -= JGameMaker.time();
		}
		
		
	}
	
	private void hide(){
		idleTime = IDLE_TIME;
		pos.y = biteYCoord - height;
		vel.y = 0;
		harmless = true;
	}
	
	public boolean activarGravedad(){
		return muriendo();
	}
	
	
	public void heroKill(Heroe heroe){
		if(harmless)return;
		super.heroKill(heroe);
		if(heroe.tuberiando()){
			hide();
			hasHero = true;
		}
	}

	public void thingTouch(Thing t){
		if(t instanceof TTuberia){
			TTuberia tuberia = (TTuberia)t;
			if(tuberia.getPirhana() == null){
				tuberia.addPirhana(this);
			}
			if(tuberia.getPirhana() == this){
				hasPipe = true;
				revive();
			}
		}
	}
	
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		if(hasPipe)return;
		super.draw(g,o,heroe);
	}
	
	/**
	 * parent tuberia calls this so this is drawn under it on the screen
	 */
	public void drawPipe(Graphics g, ImageObserver o, Heroe heroe){
		super.draw(g,o,heroe);
	}
	
	public void makeSpriteUnderground(){
		for(Sprite s: IMAGEN){
			s.replaceColors(aboveGround,underGround);
		}
		//System.out.println(KOOPA[0]);
	}
	
	public BufferedImage preview(){
		return IMAGEN[2].getBuffer();
	}
}
