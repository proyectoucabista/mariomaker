import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * Represents a Gridded Thing that has Color. It can either be a corner piece, a side piece or a middle piece.
 * @author rweichler
 *
 */
public class TBloquesColores extends TGridded {
	
	/**
	 * The type of TBloquesColores the PantallaSpawn representation is
	 */
	public static byte direction = DESDE_ARRIBA + DESDE_IZQUIERDA;
	
	/**
	 * The index of the Color in the array of preset colors in the PantallaSpawn
	 */
	public static int colorIndex = 0;
	
	/**
	 * The color of the block when displayed in the PantallaSpawn
	 */
	public static Color menuColor = Color.WHITE;
	
	private static final Color[] COLORS = {
		Color.WHITE,
		Color.RED,
		Color.MAGENTA,
		Color.GREEN,
		Color.BLUE,
		Color.YELLOW,
		new Color(96,51,17),
		Color.CYAN,
		Color.GRAY,
		new Color(1,1,1),
	};
	
	private static Sprite[] CORNERS	= {
		new Sprite("Imagenes/sprites/coloredblock/TLcorner.png"),
		new Sprite("Imagenes/sprites/coloredblock/Tedge.png"),
		new Sprite("Imagenes/sprites/coloredblock/Ledge.png"),
		new Sprite("Imagenes/sprites/coloredblock/center.png"),
	};
	
	
	private Sprite[] corners;
	private byte dir;
	private Color color;
	
	/**
	 * Creates a new TBloquesColores with the color and type of that that is previewed in the PantallaSpawn
	 */
	public TBloquesColores(){
		this(colorIndex, direction);
	}
	/**
	 * Creates a new TBloquesColores with the index color specified but the type of that is previewed in the PantallaSpawn
	 * @param color Index of colored in preset array to be made
	 */
	public TBloquesColores(int color){
		this(color,direction);
	}
	
	/**
	 * Creates a new TBloquesColores with the color of that that is previewed in the PantallaSpawn but with the type specified
	 * @param dir type of block
	 */
	public TBloquesColores(byte dir){
		this(colorIndex, dir);
	}
	/**
	 * Creates a new TBloquesColores with the index color specified and the type specified
	 * @param index
	 * @param dir
	 */
	public TBloquesColores(int index, byte dir){
		this(COLORS[index], dir);
	}
	
	/**
	 * Creates a new TBloquesColores with the color specified and the type specified
	 * @param color
	 * @param dir
	 */
	public TBloquesColores(Color color, byte dir){
		super(0, 0);
		this.color = color;
		this.dir = dir;
	}
	
	/**
	 * Sets the color of this block to the specified color
	 * @param color
	 */
	public void setColor(Color color){
		Color[] find = {this.color};
		Color[] replace = {color};
		for(Sprite sprite: corners){
			sprite.replaceColors(find,replace);
		}
		this.color = color;
	}
	
	
	public void init(Serializer s){
		
		byte dir = direction;
		Color color = menuColor;
		
		menuColor = new Color(s.ints[super.numInts()],s.ints[super.numInts() + 1],s.ints[super.numInts() + 2]);
		direction = (byte)s.ints[super.numInts() + 3];
		this.color = menuColor;
		this.dir = direction;
		super.init(s);
		direction = dir;
		menuColor = color;
	}
	public Serializer serialize(){
		Serializer s = super.serialize();
		s.ints[super.numInts()] = color.getRed();
		s.ints[super.numInts() + 1] = color.getBlue();
		s.ints[super.numInts() + 2] = color.getGreen();
		s.ints[super.numInts() + 3] = dir;
		return s;
	}
	public int numInts(){return super.numInts() + 4;}
	
	public void init(Color color, byte dir){
		super.init();
		if(corners == null){
			corners = new Sprite[4];
		}
		corners[0] = new Sprite("Imagenes/sprites/coloredblock/TLcorner.png");
		corners[1] = new Sprite("Imagenes/sprites/coloredblock/Tedge.png");
		corners[2] = new Sprite("Imagenes/sprites/coloredblock/Ledge.png");
		corners[3] = new Sprite("Imagenes/sprites/coloredblock/center.png");
		if(color == null)return;
		this.color = Color.WHITE;
		this.dir = dir;
		//System.out.println("\t" + this.color + "\t" + color);
		setColor(color);
	}
	
	
	public void init(){
		init(menuColor, direction);
	}
	
	public BufferedImage preview(){
		return getBuffer(TBloquesColores.direction, TBloquesColores.CORNERS);
	}
	
	/**
	 * Changes the type/direction of this block to the next one, called when it is clicked on in the PantallaSpawn
	 */
	public static void cycleDirections(){
		switch(direction){
		//corners
		case DESDE_IZQUIERDA + DESDE_ARRIBA:
			direction = DESDE_ARRIBA;
		break; case DESDE_IZQUIERDA + DESDE_ABAJO:
			direction = DESDE_ABAJO;
		break; case DESDE_DERECHA + DESDE_ARRIBA:
			direction = DESDE_IZQUIERDA;
		break; case DESDE_DERECHA + DESDE_ABAJO:
			direction = DESDE_IZQUIERDA + DESDE_ARRIBA;
		//side
		break; case DESDE_ARRIBA:
			direction = DESDE_ARRIBA + DESDE_DERECHA;
		break; case DESDE_ABAJO:
			direction = DESDE_ABAJO + DESDE_DERECHA;
		break; case DESDE_IZQUIERDA:
			direction = DESDE_NINGUNO;
		break; case DESDE_DERECHA:
			direction = DESDE_IZQUIERDA + DESDE_ABAJO;
		//center
		break;default:
			direction = DESDE_DERECHA;
		break; 
		}
	}
	
	/**
	 * Changes the color of the block to the next one in line, called when c is pressed in NivelEditor
	 */
	public static void cycleColors(){
		int old = colorIndex;
		colorIndex++;
		if(colorIndex == COLORS.length){
			colorIndex = 0;
		}
		menuColor = COLORS[colorIndex];
		Color[] find = {COLORS[old]};
		Color[] replace = {menuColor};
		for(Sprite sprite: CORNERS){
			sprite.replaceColors(find, replace);
		}
		
	}
	
	private BufferedImage getBuffer(byte dir, Sprite[] corners){
		switch(dir){
		//corners
		case DESDE_IZQUIERDA + DESDE_ARRIBA:
			return corners[0].getBuffer();
		case DESDE_IZQUIERDA + DESDE_ABAJO:
			return corners[0].flipY();
		case DESDE_DERECHA + DESDE_ARRIBA:
			return corners[0].flipX();
		case DESDE_DERECHA + DESDE_ABAJO:
			return corners[0].flipXY();
		//side
		case DESDE_ARRIBA:
			return corners[1].getBuffer();
		case DESDE_ABAJO:
			return corners[1].flipY();
		case DESDE_IZQUIERDA:
			return corners[2].getBuffer();
		case DESDE_DERECHA:
			return corners[2].flipX();
		//center
		default:
			return corners[3].getBuffer();
		}
	}
	
	public BufferedImage figureOutDrawImage(){
		return getBuffer(dir,corners);
	}
	
}
