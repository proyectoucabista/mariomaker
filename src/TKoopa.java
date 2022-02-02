



import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;

/**
 * This represents a Koopa in game. It can be jumped on top of to turn it into a shell. The shell can be made to move quickly left or right if a Heroe jumps on it again.
 * @author Reed Weichler
 *
 */
public class TKoopa extends TEnemy{
	/**
	 * speed of the shell when jumped on top of
	 */
	public static final double VELOCIDAD_CAPARAZON = 6;
	private static final AePlayWave KICK = new AePlayWave("Sonidos/patada.wav");
	private boolean isShell;
	private boolean pieDerecho;
	private double ultimaX;
	private double backOpenTimer;
	private Point2D.Double livingVel;
	
	private static final int TIME_TO_BACK_OPEN = 400;
	
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
		livingVel = new Point2D.Double();
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
		isShell = false;
		ultimaX = 0;
		pieDerecho = true;
		backOpenTimer = 0;
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
		
		if(isShell){
			
			if(backOpenTimer < 150 && backOpenTimer != 0)
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
	
	public boolean isInanimate(){
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
		if(backOpenTimer > 0 && vel.x == 0){
			backOpenTimer-= JGameMaker.time();
			if(backOpenTimer <= 0){
				makeShell(false);
				vel.x = -1;
			}
		}
		super.think();
		if(!isShell){
			if(pos.x > ultimaX + 10 || pos.x < ultimaX - 10){
				pieDerecho = !pieDerecho;
				ultimaX = pos.x;
			}
		}
	}
	
	public byte killDirection(){
		if(!isShell || vel.x != 0){
			if(vel.x > 0){
				return FROM_RIGHT | FROM_BELOW;
			}else{
				return FROM_LEFT | FROM_BELOW;
			}
			
		}else{
			return FROM_NONE;
		}
	}
	
	public void blockHit(Thing block){
		vel.x = (pos.x - block.pos.x)/5;
		vel.y = Math.random()*3 + 6;
		makeShell(true);
		if(vel.x > 0 && vel.x < 2){
			vel.x = 2;
		}else if(vel.x < 0 && vel.x > -2){
			vel.x = -2;
		}
	}
	
	
	public void heroTouch(Heroe heroe){
		byte where = fromWhere(heroe);
		
		//shell is standing still
		if(isShell && vel.x == 0){
			//project self to wherever i please.
			if(heroe.pos.x + heroe.width/2 > pos.x + width/2){
				vel.x = -VELOCIDAD_CAPARAZON;
			}else{
				vel.x = VELOCIDAD_CAPARAZON;
			}
			KICK.start();
		}
		//stomping on walking koopa || stomping on moving shell
		else if(where == FROM_ABOVE && !isShell || isShell && vel.x != 0 && !(new Thing(heroe.posLast.x, heroe.posLast.y, heroe.width, heroe.height)).tocando(new Thing(this.posLast.x,this.posLast.y,width,height))){
			stomp(heroe);
			vel.x = 0;
			makeShell(true);
		}
		
	}
	
	
	public void thingTouch(Thing t){
		byte where = fromWhere(t);

		if((where & FROM_SIDE) > 0 && t instanceof TEnemy && isShell && vel.x != 0){//collided with a moving enemy to murderize painfully and not so nicily lololol
			
			double x = vel.x;
			if(livingVel != null)
				x = livingVel.x;
			KICK.start();
			
			
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
	
	public void makeShell(boolean shell){
		isShell = shell;
		if(shell){
			height = 28;
			backOpenTimer = TIME_TO_BACK_OPEN;
		}else{
			height = 46;
		}
	}
	
	public void matar(Point2D.Double vel){
		livingVel.setLocation(this.vel);
		super.matar(vel);
	}
	
	public boolean isFast(){
		return isShell && vel.x != 0;
	}
	
}