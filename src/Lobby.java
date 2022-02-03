import java.util.Vector;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.image.ImageObserver;

/**
 * This represents a lobby that contains all of the Things that are in the juego. This handles all interactions between each Thing and also the Heroe, if there is one.
 * @author Reed Weichler
 *
 */
public class Lobby {
	
	private Vector<Thing> things;
	private Fondo fondo;
	
	private int index;
	private Class removerSpawn;
	
	private int numRemover;
	
	/**
	 * creates a new Lobby
	 * @param underground true if the theme is underground, false if not
	 * @param index the index in the array of the parent GameScreen
	 */
	public Lobby(boolean underground, int index){
		this.index = index;
		fondo = new Fondo(underground);
		things = new Vector<Thing>();
		numRemover = 0;
		removerSpawn = null;
	}
	/**
	 * draws this to the screen. by default it does not handle any spawnscreen
	 * @param g
	 * @param o
	 * @param heroe
	 */
	public void draw(Graphics g, ImageObserver o, Heroe heroe){
		draw(g,o,heroe,null);
	}

	/**
	 * draws this to the screen, and handles the PantallaSpawn if not null
	 * @param g
	 * @param o
	 * @param heroe
	 * @param spawn
	 */
	public void draw(Graphics g, ImageObserver o, Heroe heroe, PantallaSpawn spawn){
		boolean shouldDrawHero = true;
		if(heroe == null){
			heroe = new Heroe();
			shouldDrawHero = false;
		}
		fondo.draw(g, o, heroe);
		if(heroe.tuberiando() && shouldDrawHero){
			heroe.draw(g, o);
		}
		boolean alreadyHighlighted = spawn == null;
		for(int i = 0; i < things.size(); i++){
			Thing t = things.get(i);
			//if(t == null)continue;
			if(!(spawn == null && t instanceof TTool))
				t.draw(g, o, heroe);
			if(!alreadyHighlighted){
				Color color = spawn.highlightColor(t);
				if(color != null){
					g.setColor(color);
					int[] c = t.getDrawCoords(heroe);
					g.fillRect(c[0],c[1],c[2],c[3]);
					alreadyHighlighted = true;
				}
			}
		}
		if(!heroe.tuberiando() && shouldDrawHero){
			heroe.draw(g, o);
		}
	}
	/**
	 * converts this to something that can be written to a file
	 * @return the Serializable  object that will be converted into a file
	 */
	public Vector<Serializer> serialize(){
		Vector<Serializer> serializers = new Vector<Serializer>();
		for(Thing t: things){
			serializers.add(t.serialize());
		}
		return serializers;
	}
	/**
	 * called every frame
	 * @param heroe
	 * @param modoEditar true if in NivelEditor
	 */
	public void think(Heroe heroe, boolean modoEditar){
		think(heroe,modoEditar,false);
	}
	/**
	 * called every frame
	 * @param heroe
	 * @param modoEditar true if in NivelEditor
	 * @param debeCongelar if true, then none of the Things that are contained within this will think
	 */
	public void think(Heroe heroe, boolean modoEditar,boolean debeCongelar){
		if(heroe != null)
			heroe.think();
		//ArrayList<Thing> removeQueue = new ArrayList<Thing>();
		for( int i = 0; i < things.size(); i++ ){
			Thing t = things.get(i);
			if(t == null) continue;
			if(t.asesinado()){
				//removeQueue.add(t);
				remove(t);
				i--;
				continue;
			}
			
			if( heroe != null && !heroe.muriendo() && !(debeCongelar && t instanceof TEnemigo) && t.tocando(heroe) && heroe.tocando(t) ){
				t.enContacto(heroe);
				heroe.enContacto(t);
			}
			/*if((client != null || server != null) && otherHero.tocando(t)){
				otherHero.enContacto(t);
				t.enContacto(otherHero);
			}*/
			for( int j = i + 1; j < things.size(); j++ ){
				Thing t2 = things.get(j);
				if(t2 == null || t == t2 || t.isStatic() && t2.isStatic())continue;
				if( t2.tocando(t) && t.tocando(t2)){
					t.enContacto(t2);
					t2.enContacto(t);
					
				}
			}
			if(!debeCongelar && !t.isStatic() && (modoEditar || !(t instanceof TEnemigo) || t.inPlayerView(heroe)))
				t.think();
			Thing add = t.getSpawn();
			if(add != null)
				add(add);

		}
		if(heroe != null){
			Thing add = heroe.getSpawn();
			if(add != null)
				add(add);
		}
		/*if(removeQueue.size() > 0){
			for(Thing t: removeQueue){
				if(things.contains(t)){
					things.remove(t);
					if(undo.contains(t)){
						undo.remove(t);
					}
				}
			}
		}*/
		//things.remove(t);
		//if(undo.contains(t)) undo.remove(t);
		//i--;
	}
	
	/**
	 * sets up the spawn for the player
	 * @param heroe
	 * @return
	 */
	public boolean setSpawn(Heroe heroe){
		for(Thing t: things){
			if(t instanceof TSpawn){
				heroe.setPos(t.pos.x,t.pos.y);
				return true;
			}
		}
		return false;
	}
	/**
	 * adds a thing to this
	 * @param add
	 */
	public void add(Thing add){
		add(add,true);
	}
	/**
	 * adds a thing to this
	 * @param add Thing to be added
	 * @param shouldInit if false, then add.init() is not called
	 */
	public void add(Thing add, boolean shouldInit){
		if(shouldInit)
			add.init();
		if(fondo.isUnderground()){
			add.makeSpriteUnderground();
		}
		if(add instanceof TGridded){
			TGridded addgrid = (TGridded)add;
			for(Thing t: things){
				if(!(t instanceof TGridded)) continue;
				TGridded tgrid = (TGridded)t;
				addgrid.addAdyacente(tgrid);
				tgrid.addAdyacente((TGridded)add);
			}
		}
		if(add instanceof TItem){
			TItem item = (TItem)add;
			for(Thing t: things){
				if(t instanceof TBloque && ((TBloque)t).canAcceptItem() && t.tocando(add)){
					((TBloque)t).addItem(item);
					return;
				}
			}
			item.onBlockExit();
		}
		if(add instanceof TTuberia){
			((TTuberia)add).lobby = index;
		}
		if(add instanceof TSpawn || add instanceof TMeta){
			removerSpawnOtrosLobbys(add.getClass());
		}
		if(add instanceof AgujeroTierra){
			things.add(0,add);
		}else{
			things.add(add);
		}
	}
	/**
	 * makes every other Lobby contained in the parent GameScreen remove its spawn if it contains any
	 * @param spawn
	 */
	public void removerSpawnOtrosLobbys(Class spawn){
		removerSpawn = spawn;
		removerSpawns(spawn);
	}
	/**
	 * determines if the spawn should be removed
	 * @return the class type of the spawn if it has one, null if not
	 */
	public Class debeRemoverSpawnOtrosLobbys(){
		Class temp = removerSpawn;
		removerSpawn = null;
		return temp;
	}
	/**
	 * removes all spawns / goals, specified by the class
	 * @param spawn can be either a TMeta or a TSpawn, this instancia to be removed
	 */
	public void removerSpawns(Class spawn){
		for(Thing t: things){
			if(t.getClass().equals(spawn)){
				remove(t);
				break;
			}
		}
	}
	/**
	 * removes <b>remove</b> from this
	 * @param remove Thing to be removed
	 */
	public void remove(Thing remove){
		if(remove instanceof TGridded){
			TGridded removegrid = (TGridded)remove;
			for(Thing t: things){
				if(!(t instanceof TGridded)) continue;
				TGridded tgrid = (TGridded)t;
				tgrid.removerAdyacente(removegrid);
			}
		}
		things.remove(remove);
	}
	
}
