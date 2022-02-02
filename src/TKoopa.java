



import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * This represents a Koopa in juego. It can be jumped on top of to turn it into a shell. The shell can be made to move quickly left or right if a Heroe jumps on it again.
 * @author Reed Weichler
 *
 */
public class TKoopa extends TEnemy{
	/**
	 * speed of the shell when jumped on top of
	 */
	public static final double VELOCIDAD_CAPARAZON = 6;
	private static final AePlayWave PATADA = new AePlayWave("Sonidos/patada.wav");
	private boolean vCaparazon;
	private boolean pieDerecho;
	private double ultimaX;
	private double tiempoVolverAbrir;
	private Point2D.Double viviendoVel;
	
	private static final int TIEMPO_VOLVER_ABRIR = 400;
	
	private static final String KOOPA_SPRITE_PATH = "Imagenes/sprites/koopa/";
	
	private Sprite[] KOOPA = {
		new Sprite(KOOPA_SPRITE_PATH+"right_walk.gif"),
		new Sprite(KOOPA_SPRITE_PATH+"left_walk.gif"),
		new Sprite(KOOPA_SPRITE_PATH+"shell.gif"),
		new Sprite(KOOPA_SPRITE_PATH+"shell_opening.gif"),
		
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
	public TKoopa(double x, double y, int w, int h){
		super(x,y,w,h);
		viviendoVel = new Point2D.Double();
	}
	public TKoopa(double x, double y){
		this(x,y,32,46);
	}
	public TKoopa(){
		this(0,0);
	}
	
	public void init(){
		super.init();
		vel.x = -1;
		vCaparazon = false;
		ultimaX = 0;
		pieDerecho = true;
		tiempoVolverAbrir = 0;
	}
	

	
	public void makeSpriteUnderground(){
		for(Sprite s: KOOPA){
			s.replaceColors(aboveGround,underGround);
		}
		//System.out.println(KOOPA[0]);
	}
	
	public BufferedImage preview(){
		return KOOPA[0].getBuffer();
	}
	
	public BufferedImage figureOutDrawImage(){
		Sprite img;
		
		if(muriendo()){
			img = KOOPA[2];
			return img.flipY();
		}
		
		if(vCaparazon){
			
			if(tiempoVolverAbrir < 150 && tiempoVolverAbrir != 0)
				img = KOOPA[3];
			else
				img = KOOPA[2];
			
		}else{
			if(pieDerecho)
				img = KOOPA[0];
			else
				img = KOOPA[1];
		}
		
		
		if(vel.x > 0)
			return img.flipX();
		return img.getBuffer();
	}
	
	public boolean vInanimado(){
		return false;
	}

	public boolean activarGravedad(){return true;}
	public boolean isEnemy(){return true;}
	
	public void think(){
		if(muriendo()){
			height = 28;
			super.think();
			return;
		}
		if(tiempoVolverAbrir > 0 && vel.x == 0){
			tiempoVolverAbrir-= JGameMaker.time();
			if(tiempoVolverAbrir <= 0){
				crearCaparazon(false);
				vel.x = -1;
			}
		}
		super.think();
		if(!vCaparazon){
			if(pos.x > ultimaX + 10 || pos.x < ultimaX - 10){
				pieDerecho = !pieDerecho;
				ultimaX = pos.x;
			}
		}
	}
	
	public byte direccionMuerte(){
		if(!vCaparazon || vel.x != 0){
			if(vel.x > 0){
				return DESDE_DERECHA | DESDE_ABAJO;
			}else{
				return DESDE_IZQUIERDA | DESDE_ABAJO;
			}
			
		}else{
			return DESDE_NINGUNO;
		}
	}
	
	public void golpeBloque(Thing block){
		vel.x = (pos.x - block.pos.x)/5;
		vel.y = Math.random()*3 + 6;
		crearCaparazon(true);
		if(vel.x > 0 && vel.x < 2){
			vel.x = 2;
		}else if(vel.x < 0 && vel.x > -2){
			vel.x = -2;
		}
	}
	
	
	public void heroeContacto(Heroe heroe){
		byte where = fromWhere(heroe);
		
		//shell is standing still
		if(vCaparazon && vel.x == 0){
			//project self to wherever i please.
			if(heroe.pos.x + heroe.width/2 > pos.x + width/2){
				vel.x = -VELOCIDAD_CAPARAZON;
			}else{
				vel.x = VELOCIDAD_CAPARAZON;
			}
			PATADA.start();
		}
		//stomping on walking koopa || stomping on moving shell
		else if(where == DESDE_ARRIBA && !vCaparazon || vCaparazon && vel.x != 0 && !(new Thing(heroe.ultimaPos.x, heroe.ultimaPos.y, heroe.width, heroe.height)).tocando(new Thing(this.ultimaPos.x,this.ultimaPos.y,width,height))){
			stomp(heroe);
			vel.x = 0;
			crearCaparazon(true);
		}
		
	}
	
	
	public void thingTouch(Thing t){
		byte where = fromWhere(t);

		if((where & DE_LADO) > 0 && t instanceof TEnemy && vCaparazon && vel.x != 0){//collided with a moving enemy to murderize painfully and not so nicily lololol
			
			double x = vel.x;
			if(viviendoVel != null)
				x = viviendoVel.x;
			PATADA.start();
			
			
			t.matar(new Point2D.Double(vel.x*2, Math.random()*16+3));
			
			
		}
	}
	
	public Serializer serialize(){
		int temp = height;
		height = 46;
		Serializer s = super.serialize();
		height = temp;
		return s;
	}
	
	public void crearCaparazon(boolean shell){
		vCaparazon = shell;
		if(shell){
			height = 28;
			tiempoVolverAbrir = TIEMPO_VOLVER_ABRIR;
		}else{
			height = 46;
		}
	}
	
	public void matar(Point2D.Double vel){
		viviendoVel.setLocation(this.vel);
		super.matar(vel);
	}
	
	public boolean vRapido(){
		return vCaparazon && vel.x != 0;
	}
	
}