



import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.*;

/**
 * It's a Goomba. It kills a player if it touches it from the side or bottom. If the heroe hits it on top, it is asesinado.
 * @author Reed Weichler
 *
 */
public class TGoomba extends TEnemy{
	
	private boolean gotHit;
	private boolean pieDerecho;
	private double time2Dai;
	private double ultimaX;
	
	private static final int DIE_TIME = 20;
	
	private static final AePlayWave STOMP = new AePlayWave("Sonidos/pisar.wav");
	private static final String GOOMBA_SPRITE_PATH = "Imagenes/sprites/goomba/";
	
	private Sprite[] GOOMBA = {
		new Sprite(GOOMBA_SPRITE_PATH+"caminar.gif",32,32),
		new Sprite(GOOMBA_SPRITE_PATH+"muerte.gif",32,32),
		
	};
	

	private static final Color[] aboveGround = {
		new Color(231,90,16),
		new Color(247,214,181),
		Color.BLACK,
	};	
	private static final Color[] underGround = {
		new Color(0,136,136),
		new Color(0,255,255),
		new Color(0,64,88),
	};
	
	
	public TGoomba(){
		this(0,0);
	}
	
	public TGoomba(double x, double y){
		this(x,y,32,32);
	}
	
	public TGoomba(double x, double y, int width, int height){
		super(x,y,width,height);
		gotHit = false;
		ultimaX = 0;
		pieDerecho = true;
	}
	
	public void init(){
		super.init();
		vel.x = -1;
	}
	
	public void makeSpriteUnderground(){
		for(Sprite s: GOOMBA){
			s.replaceColors(aboveGround,underGround);
		}
	}
	
	public BufferedImage preview(){
		return GOOMBA[0].getBuffer();
	}
	
	public BufferedImage figureOutDrawImage(){
		
		Sprite img;
		
		if(gotHit){
			img = GOOMBA[1];
		}else{
			img = GOOMBA[0];
		}
		if(muriendo() && !gotHit){
			if(pieDerecho)
				return img.flipXY();
			else
				return img.flipY();
		}else{
			if(pieDerecho)
				return img.flipX();
			else
				return img.getBuffer();
		}
	}
	
	public boolean vInanimado(){
		return false;
	}
	
	public boolean activarGravedad(){return true;}
	
	public void think(){
		if(gotHit){
			time2Dai -= JGameMaker.time();
			if(time2Dai < 1)
				matar();
		}else{
			super.think();
			if(pos.x > ultimaX + 10 || pos.x < ultimaX - 10){
				pieDerecho = !pieDerecho;
				ultimaX = pos.x;
			}
		}
	}
	public byte direccionMuerte(){
		return DESDE_ABAJO | DE_LADO;
	}
	
	public void enContacto(Thing t){
		if(gotHit) return;
		super.enContacto(t);
	}
	public void heroeContacto(Heroe heroe){
		if(heroe.vel.y < -1){
			STOMP.start();
			gotHit();
			stomp(heroe);
		}
	}
	
	private void gotHit(){
		gotHit = true;
		time2Dai = DIE_TIME;
	}
	
	
}