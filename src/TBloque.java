



import java.awt.*;
import java.awt.image.*;

public class TBloque extends TGridded{

	public static final int WIDTH = 32;
	private double oldY,oldX;
	private byte cabezaso;
	private byte image;
	private TItem item;
	private boolean apareciendo;
	private boolean seMueveCuandoGolpea;
	
	public static final String BLOCK_PATH = "Imagenes/sprites/bloque/";
	
	private Sprite[] BLOQUE = {
			new Sprite(BLOCK_PATH+"ladrillo.png"),
			new Sprite(BLOCK_PATH+"b_activado.gif"),
			new Sprite(BLOCK_PATH+"b_desactivado.gif"),
			new Sprite(BLOCK_PATH+"piso.png"),
			new Sprite(BLOCK_PATH+"pilar.png"),
			new Sprite(BLOCK_PATH+"hongo/left.png"),
			new Sprite(BLOCK_PATH+"hongo/mid.png"),
			new Sprite(BLOCK_PATH+"hongo/right.png"),
			new Sprite(BLOCK_PATH+"hongo/top.png"),
			new Sprite(BLOCK_PATH+"hongo/bottom.png"),
	};
	
	public static final byte
		LADRILLO_MARRON = 0,
		LADRILLOS = 0,
		BLOQUE_PREGUNTA = 1,
		BLOQUE_PREGUNTA_DESACTIVADO = 2,
		SUELO = 3,
		PILAR = 4,
		HONGO_IZQUIERDO = 5,
		HONGO_MEDIO = 6,
		HONGO_DERECHO = 7,
		HONGO_ARRIBA = 8,
		HONGO_ABAJO = 9;

	private static final Color[] aboveGround = {
		new Color(200,76,12),
		new Color(252,188,176)
	};	
	private static final Color[] underGround = {
		new Color(0,128,135),
		new Color(149,252,240)
		
	};
	
	public TBloque(double x, double y, byte image){
		this(x,y,image,null,false);
	}
	
	public TBloque(double x, double y, byte image, TItem item){
		this(x,y,image,item,true);
	}
	
	public TBloque(byte image){
		this(0,0,image,null,false);
	}
	
	public TBloque(){
		this(PILAR);
	}
	/**
	 * Creates a TBloque with the specified x and y coordinates, image, contained item, and whether or not if it moves when it's cabezaso.
	 * @param x
	 * @param y
	 * @param image
	 * @param item
	 * @param seMueveCuandoGolpea
	 */
	public TBloque(double x, double y, byte image, TItem item, boolean seMueveCuandoGolpea){
		super(x,y,WIDTH,WIDTH);
		this.seMueveCuandoGolpea = seMueveCuandoGolpea;
		init();
		apareciendo = false;
		cabezaso = DESDE_NINGUNO;
		this.image = image;
		this.item = item;
	}
	
	public void makeSpriteUnderground(){
		for(Sprite s: BLOQUE){
			s.replaceColors(aboveGround,underGround);
		}
		//System.out.println(BLOQUE[image]);
	}
	
	public void init(){
		super.init();
		oldX = pos.x;
		oldY = pos.y;
	}
	public void init(Serializer s){
		image = (byte)s.ints[super.numInts()];
		seMueveCuandoGolpea = s.bools[super.numBools()];
		Class c = s.classes[super.numClasses()];
		if(c != null){
			try {
				item = (TItem)(c.newInstance());
			} catch (Exception e) {
				item = null;
				e.printStackTrace();
			}
		}
		super.init(s);
	}
	public Serializer serialize(){
		Serializer s = super.serialize();
		s.ints[super.numInts()] = image;
		s.bools[super.numBools()] = seMueveCuandoGolpea;
		if(item != null){
			s.classes[super.numClasses()] = item.getClass();
		}
		return s;
	}
	public int numInts(){return super.numInts() + 1;}
	public int numBools(){return super.numBools() + 1;}
	public int numClasses(){return super.numClasses() + 1;}
	
	/*public void setPos(double x, double y){
		super.setPos(x,y);
		oldX = x;
		oldY = y;
		System.out.println(oldX);
	}*/
	
	public BufferedImage preview(){
		return BLOQUE[image].getBuffer();
	}

	public void enContacto(Thing t){
		if(!t.isStatic() && seMueveCuandoGolpea && vel.x == 0 && vel.y == 0 && cabezaso == DESDE_NINGUNO && !apareciendo){
			
			//System.out.println(pos.x + " " + oldX);
			
			byte where = fromWhere(t);
			
			if(where == DESDE_ABAJO && t instanceof Heroe){
				cabezaso(where);
				vel.y = 3;
			}else{
				if( (where == DESDE_IZQUIERDA || where == DESDE_DERECHA) && t.vRapido() ){
					cabezaso(where);
					if(where == DESDE_IZQUIERDA){
						vel.x = 3;
					}else{
						vel.x = -3;
					}
				}
			}
		}
		super.enContacto(t);
	}
	/**
	 * Called when a player or a juego object hits the block (for apareciendo TItems)
	 * @param where
	 * Where it cabezaso.
	 */
	public void cabezaso(byte where){
		cabezaso = where;
		//oldX = pos.x;
		//oldY = pos.y;
	}
	
	private void spawnItem(){
		addSpawn(item);
		item = null;
		apareciendo = false;
		seMueveCuandoGolpea = false;
	}
	
	private void beginSpawn(){
		if(item == null)return;
		apareciendo = true;
		item.setPos(pos);
		image = BLOQUE_PREGUNTA_DESACTIVADO;
	}
	
	
	public void think(){
		super.think();
		
		if(apareciendo){
			item.think();
			//System.out.println(item.pos.y + " > " + pos.y + " + " + height);
			if(item.pos.y > pos.y + height){
				item.onBlockExit();
				spawnItem();
			}
		}
		
		if(pos.y > oldY + WIDTH/2 && cabezaso == DESDE_ABAJO && vel.y > 0){
			vel.y = -3;
		}else if(pos.y < oldY && cabezaso == DESDE_ABAJO && vel.y < 0){
			vel.y = 0;
			cabezaso = DESDE_NINGUNO;
			pos.y = oldY;
			beginSpawn();
		}else if(pos.x > oldX + WIDTH/2 && cabezaso == DESDE_IZQUIERDA && vel.x > 0){
			vel.x = -3;
		}else if(pos.x < oldX && cabezaso == DESDE_IZQUIERDA && vel.x < 0){
			vel.x = 0;
			cabezaso = DESDE_NINGUNO;
			pos.x = oldX;
			beginSpawn();
		}else if(pos.x < oldX - WIDTH/2 && cabezaso == DESDE_DERECHA && vel.x < 0){
			vel.x = 3;
		}else if(pos.x > oldX && cabezaso == DESDE_DERECHA && vel.x > 0){
			vel.x = 0;
			cabezaso = DESDE_NINGUNO;
			pos.x = oldX;
			beginSpawn();
		}
	}
	
	public boolean isStatic(){
		return !seMueveCuandoGolpea;
	}
	
	/**
	 * determines if this can contain a new TItem
	 * @return true if this can contain a new TItem, false if not
	 */
	public boolean canAcceptItem(){
		return item == null && (seMueveCuandoGolpea || image == TBloque.BLOQUE_PREGUNTA_DESACTIVADO);
	}
	
	/**
	 * makes this contain a new Item
	 * @param item the TItem to contain
	 */
	public void addItem(TItem item){
		if(image == BLOQUE_PREGUNTA_DESACTIVADO){
			image = BLOQUE_PREGUNTA;
			seMueveCuandoGolpea = true;
		}
		this.item = item;
	}	
	
	public BufferedImage figureOutDrawImage(){
		return BLOQUE[image].getBuffer();
	}
	
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		if(inPlayerView(heroe) && apareciendo)
			item.draw(g,o,heroe);
		super.draw(g,o,heroe);
	}

}