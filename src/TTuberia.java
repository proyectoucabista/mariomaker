



import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.*;
import java.util.ArrayList;

import javax.swing.ImageIcon;

/**
 * A long green thing with a hole in the top. The hole can have a TPirana and also can be used as a teleport for the player.
 * @author rweichler
 *
 */
public class TTuberia extends TGridded{
	
	public static final String PIPE_PATH = "Imagenes/sprites/tuberia/";
	
	public static final int SQUARE_WIDTH = 32;
	
	/**
	 * the lobby this tuberia is in
	 */
	public int lobby;
	private int teleRoom;
	private Point2D.Double telePos;
	
	
	private Sprite[] PIPE = {
			new Sprite(PIPE_PATH+"top_left.gif"),
			new Sprite(PIPE_PATH+"top_right.gif"),
			new Sprite(PIPE_PATH+"mid_left.gif"),
			new Sprite(PIPE_PATH+"mid_right.gif"),
	};
	
	private TPirana pirana;
	
	public TTuberia(){
		this(0,0);
	}
	
	public TTuberia(double x, double y){
		super(x,0,SQUARE_WIDTH*2,(int)y);
		lobby = 0;
		teleRoom = -1;
		telePos = null;
		pirana = null;
		if(height<SQUARE_WIDTH)
			height = SQUARE_WIDTH;
	}
	
	

	public void init(Serializer s){
		teleRoom = s.ints[super.numInts()];
		if(teleRoom != -1){
			telePos = new Point2D.Double(s.doubles[super.numDoubles()], s.doubles[super.numDoubles() + 1]);
		}
		super.init(s);
	}
	public Serializer serialize(){
		Serializer s = super.serialize();
		s.ints[super.numInts()] = teleRoom;
		if(telePos != null){
			s.doubles[super.numDoubles()] = telePos.x;
			s.doubles[super.numDoubles()+1] = telePos.y;
		}
		return s;
	}
	public int numDoubles(){return super.numDoubles() + 2;}
	public int numInts(){return super.numInts() + 1;}
	
	
	public void setPos(double x, double y){
		super.setPos(x,0);
		this.pos.x = getGridCoord(x)*32;
		this.height = (int)getGridCoord(y)*32;
		//if(height < SQUARE_WIDTH*2){
		//	height = SQUARE_WIDTH*2;
		//}
	}
	
	public TPirana getPirana(){
		return pirana;
	}
	
	public void addPirana(TPirana pirana){
		this.pirana = pirana;
		pirana.setPos(pos.x + (width - pirana.width)/2, height - pirana.height);
		pirana.enContacto(this);
		pirana.startBite(height);
	}
	
	public byte getDirection(TGridded othergrid){
		Rectangle other = othergrid.representation();
		if(other == null)return DESDE_NINGUNO;
		Point gridPos = getGridPos();
		int x = gridPos.x;
		int y = gridPos.y + height/32 - 1;
		//horizontally next to eachother
		Point	up1 = new Point(x, y + 1),
				up2 = new Point(x + 1, y + 1),
				left = new Point(x - 1, y),
				right = new Point(x + 2, y);
		if(other.contains(up1) || other.contains(up2))
			return DESDE_ARRIBA;
		if(other.contains(left))
			return DESDE_IZQUIERDA;
		if(other.contains(right))
			return DESDE_DERECHA;
		return DESDE_NINGUNO;
	}
	
	
	public boolean tocando(Thing t){
		if(t == pirana)return false;
		return supertouching(t);
	}
	
	public Rectangle representation(){
		return new Rectangle(getGridPos().x, 0, width/32, height/32);
	}
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		if(inPlayerView(heroe)){
			if(pirana != null){
				pirana.drawTuberia(g,o,heroe);
			}

			int[] c = getDrawCoords(heroe);

			int bodyHeight = JGameMaker.screenHeight - (c[1] + c[2]/2);
			g.drawImage(PIPE[0].getBuffer(), c[0], c[1], c[2]/2, c[2]/2, null);
			g.drawImage(PIPE[1].getBuffer(), c[0] + c[2]/2, c[1], c[2]/2, c[2]/2, null);
			g.drawImage(PIPE[2].getBuffer(), c[0], c[1] + c[2]/2, c[2]/2, bodyHeight, null);
			g.drawImage(PIPE[3].getBuffer(), c[0] + c[2]/2, c[1] + c[2]/2, c[2]/2, bodyHeight, null);
		}
	}
	
	public BufferedImage preview(){
		return PIPE[0].getBuffer();
	}
	
	public void enContacto(Thing t){
		super.enContacto(t);
		if(t instanceof Heroe){
			if(pirana != null){
				pirana.warnHero();
			}
			if(lobby != -1 && telePos != null && !((Heroe)t).tuberiando()){
				if(/*fromWhere(t) == facing() &&*/ t.pos.x - 4 > pos.x && t.pos.x + t.width + 4 < pos.x + width){
					Heroe h = (Heroe)t;
					h.tuberia(pos.y + height, teleRoom, telePos);
				}
			}
		}
	}
	/**
	 * 
	 * @return true if this has the capability to teleport the player
	 */
	public boolean isTeleporter(){
		return telePos != null;
	}
	
	public void matar(){
		if(pirana != null){
			pirana.matar();
		}
		super.matar();
	}
	
	public void think(){
		super.think();
		if(pirana != null && pirana.asesinado()){
			pirana = null;
		}
	}
	
	public boolean canLink(Thing t){
		if(t == this)return false;
		if(telePos != null)return false;
		if(t == null)return true;
		if(t instanceof TTuberia){
			if(!((TTuberia)t).isTeleporter()){
				return true;
			}
		}
		return false;
	}
	
	public void setSpawnPos(double x, double y){
		setPos(x - 16, y + 32);
	}
	
	public void link(Thing t){
		TTuberia tuberia = (TTuberia)t;
		teleRoom = tuberia.lobby;
		telePos = new Point2D.Double(tuberia.pos.x + (tuberia.width - Heroe.WIDTH)/2, tuberia.pos.y + tuberia.height - Heroe.HEIGHT);
	}
	
	
}