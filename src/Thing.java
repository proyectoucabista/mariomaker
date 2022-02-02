import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
/**
 * This represents an object that can be put in the juego.
 * @author Reed Weichler
 *
 */
public class Thing{

	public static final byte DESDE_NINGUNO = 1;

	public static final byte DESDE_ARRIBA = 2;

	public static final byte DESDE_ABAJO = 4;

	public static final byte DESDE_IZQUIERDA = 8;

	public static final byte DESDE_DERECHA = 16;
	
	public static final byte DE_TODASPARTES = DESDE_ARRIBA + DESDE_ABAJO + DESDE_IZQUIERDA + DESDE_DERECHA;
	
	public static final byte DE_LADO = DESDE_IZQUIERDA + DESDE_DERECHA;
	
	private Thing spawn = null;
	
	/**
	 * position
	 */
	public Point2D.Double pos;
	/**
	 * last position
	 */
	public Point2D.Double ultimaPos;
	/**
	 * velocity
	 */
	public Point2D.Double vel;
	/**
	 * acceleration
	 */
	public Point2D.Double acc;
	/**
	 * if this is cayendo to its death because of a hole in the floor
	 */
	public boolean cayendo;
	
	private Sprite sprite;
	
	
	public int width,height;
	private boolean asesinado,muriendo;
	
	/**
	 * returns an array of values that is used to determine where this should be drawn on the screen
	 * @param heroe
	 * @return {x coord, y coord, width, height}
	 */
	public int[] getDrawCoords(Heroe heroe){
		int[] r = new int[4];
		r[0] = JGameMaker.scaleW(JGameMaker.screenWidth-(heroe.viewX()-pos.x));
		//r[1] = (int)(Global.H - Global.NIVEL_SUELO - (h + pos.y) + 0.5);
		r[1] = JGameMaker.scaleH(heroe.viewY()-pos.y-height);
		r[2] = JGameMaker.scaleW(width);
		r[3] = JGameMaker.scaleH(height);
		return r;
	}
	/**
	 * returns the in-juego image
	 * @return in-juego image
	 */
	public BufferedImage figureOutDrawImage(){
		return sprite.getBuffer();
	}
	/**
	 * draws this to g
	 * @param g
	 * @param o
	 * @param heroe
	 */
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		if(inPlayerView(heroe)){
			g.setColor(Color.WHITE);
			int[] c = getDrawCoords(heroe);
			
	
			g.drawImage(figureOutDrawImage(),c[0],c[1],c[2],c[3], o);
		}
	}
	/**
	 * determines whether or not this is in the view of the Heroe specified
	 * @param heroe
	 * @return true if this can be seen on screen, false if not
	 */
	public boolean inPlayerView(Heroe heroe){
		return JGameMaker.screenWidth-heroe.viewX()+pos.x+width > 0 && JGameMaker.screenWidth-heroe.viewX()+pos.x < JGameMaker.screenWidth
		&& heroe.viewY()-pos.y > 0 && heroe.viewY()-pos.y-height < JGameMaker.screenHeight;
	}
	/**
	 * Creates a new Thing with coordinates (x,y) and the  specified width and height
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param width width
	 * @param height height
	 */
	public Thing(double x, double y, int width, int height){
		pos = new Point2D.Double(x,y);
		sprite = new Sprite(width,height);
		ultimaPos = new Point2D.Double(x,y);
		this.width=width;
		this.height=height;
		asesinado = false;
		cayendo = false;
		init();
	}
	/**
	 * creates a new Thing with coordinates (0,0), width of 1 and height of 1
	 */
	public Thing(){
		this(0,0,1,1);
	}
	/**
	 * converts this to a Serializer so it can be written to a file
	 * @return
	 */
	public Serializer serialize(){
		Serializer s = new Serializer(this.getClass());
		
		s.ints = new int[numInts()];
		s.doubles = new double[numDoubles()];
		s.bools = new boolean[numBools()];
		s.classes = new Class[numClasses()];
		
		s.ints[0] = width;
		s.ints[1] = height;
		
		s.doubles[0] = pos.x;
		s.doubles[1] = pos.y;
		
		return s;
	}
	/**
	 * returns number of ints that this Serializer representation should contain
	 * @return number of ints that this Serializer representation should contain
	 */
	public int numInts(){return 2;}
	/**
	 * returns number of doubles that this Serializer representation should contain
	 * @return number of doubles that this Serializer representation should contain
	 */
	public int numDoubles(){return 2;}
	/**
	 * returns number of booleans that this Serializer representation should contain
	 * @return number of booleans that this Serializer representation should contain
	 */
	public int numBools(){return 0;}
	/**
	 * returns number of classes that this Serializer representation should contain
	 * @return number of classes that this Serializer representation should contain
	 */
	public int numClasses(){return 0;}
	
	/**
	 * initializes fields based on the configuration of s
	 * @param s
	 */
	public void init(Serializer s){
		setPos(s.doubles[0],s.doubles[1]);
		width = s.ints[0];
		height = s.ints[1];
		init();
	}
	/**
	 * called when added to the room and to the spawn menu, intializes fields
	 */
	public void init(){
		if(vel == null)
			vel = new Point2D.Double();
		if(acc == null)
			acc = new Point2D.Double();
		updatePosLast();
	}
	/**
	 * adds t to spawn queue
	 * @param t
	 */
	public void addSpawn(Thing t){
		spawn = t;
	}
	/**
	 * removes the item from the spawn queue from this and returns it
	 * @return a Thing that should be added to the room. If there is none, null
	 */
	public Thing getSpawn(){
		Thing temp = spawn;
		spawn = null;
		return temp;
	}
	/**
	 * writes the last position to memory
	 */
	public void updatePosLast(){
		ultimaPos.setLocation(pos);
	}
	/**
	 * returns true if this is moving through the air as a result of matar(double x, double y), false if not
	 * @return true if this is moving through the air as a result of matar(double x, double y), false if not
	 */
	public boolean muriendo(){
		/*boolean r = getDying;
		getDying = muriendo;
		return r;*/
		return muriendo;
	}
	/**
	 * determines if t is tocando this
	 * @param t
	 * @return true if this is tocando t
	 */
	public boolean tocando(Thing t){
		if(t instanceof TBound){
			return t.tocando(this);
		}
		if(muriendo() || t.muriendo())return false;
		double
			x1 = pos.x,
			y1 = pos.y,
			x2 = t.pos.x,
			y2 = t.pos.y;
		double
			w1 = width + x1,
			h1 = height + y1,
			w2 = t.width + x2,
			h2 = t.height + y2;
		
		if(
			((
				w1 >= x2 &&
				h1 >= y2 + 5 &&
				x1 <= w2 &&
				y1 <= h2
			)||(
				w2 >= x1 &&
				h2 >= y1 &&
				x2 <=  w1 &&
				y2 <= h1
			))
				
		){
			//S//ystem.out.println(getClass() + " " + fromWhere(t));
			return true;
		}

		//tubing (for low framerates)
		return false;
	}
	/**
	 * changes the in-juego image of this Thing to the underground theme, if it has one
	 */
	public void makeSpriteUnderground(){
		
	}
	/**
	 * called when this Thing touches another Thing
	 * @param t the other Thing
	 */
	public void enContacto(Thing t){
		if(!t.isStatic()){
			
			byte where = fromWhere(t);
			
			if( where == DESDE_ARRIBA ){
				t.setPos(t.pos.x,pos.y + height);
				if(t.vel.y < 0)
					t.bumpY();
				if(t.acc.y < 0)
					t.acc.y = 0;
			}else if( where == DESDE_ABAJO ){
				t.setPos(t.pos.x,pos.y - t.height);
				if(t.vel.y > 0)
					t.bumpY();
				if(t.acc.y > 0)
					t.acc.y = 0;
			}else if( where == DESDE_IZQUIERDA ){
				t.setPos(pos.x - t.width,t.pos.y);
				t.bumpX();
				if(t.acc.x > 0)
					t.acc.y = 0;
			}else if( where == DESDE_DERECHA ){
				t.setPos(pos.x + width,t.pos.y);
				t.bumpX();
				if(t.acc.x < 0)
					t.acc.x = 0;
			}
		}
	}
	
	/**
	 * determines where a Thing came from when a collision occurs
	 * @param t the other Thing
	 * @return the direction where t came from, if it can't be determined, returns Thing.DESDE_NINGUNO
	 */
	public byte fromWhere(Thing t){
		
		//they moved into us
		if( t.ultimaPos.y + 2 >= pos.y + height && t.vel.y <= 0 ){
			return DESDE_ARRIBA;
		}else if( t.ultimaPos.y + t.height <= pos.y && t.vel.y > 0 ){
			return DESDE_ABAJO;
		}else if( t.ultimaPos.x + t.width <= pos.x && t.vel.x > 0 ){
			return DESDE_IZQUIERDA;
		}else if( t.ultimaPos.x >= pos.x + width && t.vel.x < 0 ){
			return DESDE_DERECHA;
		}
		
		//we moved into them
		if( t.pos.y >= ultimaPos.y + height && vel.y > 0 ){
			return DESDE_ARRIBA;
		}else if( t.pos.y + t.height <= ultimaPos.y && vel.y < 0 ){
			return DESDE_ABAJO;
		}else if( t.pos.x + t.width <= ultimaPos.x && vel.x < 0 ){
			return DESDE_IZQUIERDA;
		}else if( t.pos.x >= ultimaPos.x + width && vel.x > 0 ){
			return DESDE_DERECHA;
		}
		
		//both at same time
		if( t.pos.y <= pos.y + height && t.pos.y+10 >= pos.y && vel.y > t.vel.y && vel.y != 0 && t.vel.y != 0 ){
			return DESDE_ARRIBA;
		}else if( t.pos.y + t.height >= pos.y && t.pos.y + t.height <= pos.y + height && vel.y <  t.vel.y && vel.y != 0 && t.vel.y != 0 ){
			return DESDE_ABAJO;
		}else if( t.pos.x + t.width >= pos.x && t.pos.x <= pos.x && vel.x != 0 && t.vel.x != 0 ){
			return DESDE_IZQUIERDA;
		}else if( t.pos.x <= pos.x + width && t.pos.x >= pos.x && vel.x != 0 && t.vel.x != 0 ){
			return DESDE_DERECHA;
		}
		
		return DESDE_NINGUNO;
	}
	/**
	 * queues the parent Room, if any, to remove this
	 */
	public void matar(){
		asesinado = true;
	}
	/**
	 * queues the room to not remove this Thing if previously told to do so
	 */
	public void revive(){
		asesinado = false;
	}
	/**
	 * sets the velocity of the object to a value, once it is off screen then it is removed from the room
	 * @param vel the velocity that it will move
	 */
	public void matar(Point2D.Double vel){
		muriendo = true;
		this.vel.setLocation(vel);
	}
	/**
	 * links this Thing to another (pipes, etc.)
	 * @param other Thing to link to
	 */
	public void link(Thing other){
		
	}
	/**
	 * determines whether or not this Thing can link with other
	 * @param other the Thing that is checked against
	 * @return true if it can, false if it cannot
	 */
	public boolean canLink(Thing other){
		return false;
	}
	/**
	 * returns image that is to be displayed in the spawn menu
	 * @return image that is to be displayed in the spawn menu
	 */
	public BufferedImage preview(){
		return null;
	}
	/**
	 * returns whether or not this should be removed from its parent Room
	 * @return whether or not this should be removed from its parent Room
	 */
	public boolean asesinado(){
		return asesinado;
	}
	
	/**
	 * called every frame, updates velocity, position, etc
	 */
	public void think(){
		updatePosLast();
		pos.setLocation(pos.x+vel.x*JGameMaker.time(), pos.y+vel.y*JGameMaker.time());
		vel.setLocation(vel.x + acc.x*JGameMaker.time(), vel.y + acc.y*JGameMaker.time());
		if(activarGravedad()){
			if(pos.y+JGameMaker.NIVEL_SUELO < 0){
				matar();
				cayendo = false;
			}else if(pos.y > 0 || muriendo || cayendo){
				acc.y = -JGameMaker.GRAVEDAD;
			}else if(activarGravedad()){
				pos.y = 0;
				bumpY();
			}
		}
	}
	/**
	 * sets the current position
	 */
	public void setPos(double x, double y){
		pos.setLocation(x,y);
	}
	/**
	 * sets the current position
	 */
	public void setPos(Point2D.Double pos){
		setPos(pos.x,pos.y);
	}
	/**
	 * called when this Thing bumps into another Thing, changes X velocity
	 */
	public void bumpX(){
		vel.x *= -1;
	}
	/**
	 * called when this Thing bumps into another Thing, chanes Y velocity
	 */
	public void bumpY(){//if this Thing bumps into something, this is what the vector is multiplied by
		vel.y = 0;
	}
	/**
	 * determines if this has the capability to move
	 * @return true if this Thing moves, false if it does not
	 */
	public boolean isStatic(){
		return false;
	}
	/**
	 * determines if this is fast enough to matar TEnemies
	 * @return true if this Thing moves fast enough to matar TEnemies, false if it does not
	 */
	public boolean vRapido(){
		return false;
	}
	
	/**
	 * sets the position when it is in the spawn menu
	 * @param x coord of mouse
	 * @param y coord of mouse
	 */
	public void setSpawnPos(double x, double y){
		setPos(x,y - height);
	}
	/**
	 * determines if this should be affected by gravity
	 * @return true if gravity should effect this Thing, false if it should not
	 */
	public boolean activarGravedad(){return false;}
	
}