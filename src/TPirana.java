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
public class TPirana extends TEnemigo{
	private static final String PATH = "Imagenes/sprites/tuberia/pirana/";
	private static final double VELOCIDAD_MOVIMIENTO = 0.75;
	private static final int TIEMPO_INACTIVIDAD = 1500/15;
	
	
	private boolean tieneHeroe;
	private boolean tieneTuberia;
	private boolean inofensiva;
	private double biteYCoord;
	private double tiempoInactividad;
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

	
	public TPirana(){
		this(0,0);
	}
	public TPirana(double x, double y){
		super(x,y,16*2,24*2);
		tieneHeroe = false;
		tieneTuberia = false;
		biteYCoord = -1;
		tiempoInactividad = 0;
		inofensiva = true;
	}
	

	public byte direccionMuerte(){
		if(inofensiva)
			return DESDE_NINGUNO;
		else
			return DE_TODASPARTES;
	}
	public void heroeContacto(Heroe heroe){
		//super.heroeContacto(heroe);
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
		tieneHeroe = true;
	}
	
	public void startBite(double y){
		biteYCoord = y;
	}
	
	public void think(){
		super.think();
		if(!tieneTuberia){
			matar();
			return;
		}

		if(biteYCoord != -1){
			//waiting to move up and now can
			if(pos.y < biteYCoord && vel.y == 0 && tiempoInactividad <= 0 && !tieneHeroe){
				vel.y = VELOCIDAD_MOVIMIENTO;
				inofensiva = false;
			//waiting to move down and now can
			}else if(pos.y >= biteYCoord && vel.y == 0 && tiempoInactividad <= 0){
				vel.y = -VELOCIDAD_MOVIMIENTO;
			//just finished moving up and now must idle
			}else if(pos.y > biteYCoord && vel.y > 0 && tiempoInactividad <= 0){
				tiempoInactividad = TIEMPO_INACTIVIDAD;
				pos.y = biteYCoord;
				vel.y = 0;
			//just finished moving down and must idle
			}else if(pos.y < biteYCoord - height && vel.y < 0 && tiempoInactividad <= 0){
				quitar();
			}
		}
		if(pos.y == biteYCoord - height && vel.y == 0 && tieneHeroe){
			tieneHeroe = false;
			tiempoInactividad = TIEMPO_INACTIVIDAD;
		}else if(tiempoInactividad > 0){
			tiempoInactividad -= JGameMaker.time();
		}
		
		
	}
	
	private void quitar(){
		tiempoInactividad = TIEMPO_INACTIVIDAD;
		pos.y = biteYCoord - height;
		vel.y = 0;
		inofensiva = true;
	}
	
	public boolean activarGravedad(){
		return muriendo();
	}
	
	
	public void heroeMatar(Heroe heroe){
		if(inofensiva)return;
		super.heroeMatar(heroe);
		if(heroe.tuberiando()){
			quitar();
			tieneHeroe = true;
		}
	}

	public void thingTouch(Thing t){
		if(t instanceof TTuberia){
			TTuberia tuberia = (TTuberia)t;
			if(tuberia.getPirana() == null){
				tuberia.addPirana(this);
			}
			if(tuberia.getPirana() == this){
				tieneTuberia = true;
				revive();
			}
		}
	}
	
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		if(tieneTuberia)return;
		super.draw(g,o,heroe);
	}
	
	/**
	 * parent tuberia calls this so this is drawn under it on the screen
	 */
	public void drawTuberia(Graphics g, ImageObserver o, Heroe heroe){
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
