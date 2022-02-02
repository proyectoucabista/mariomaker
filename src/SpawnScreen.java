



import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;



/**
 * The most important feature of the Level Editor. This holds all of the spawnable Things in the juego and has the ability to add them to the juego.
 * @author Reed Weichler
 *
 */
public class SpawnScreen extends Pantalla {
	
	private boolean visible;
	private int cosaElegida;
	private Heroe heroe;
	private int cosaFlotante;
	private boolean nivelPartida, congelarPartida;
	
	private TextButton cambiarNivel, congelarTiempo;
	
	private static final Color  YELLOW = new Color(255,255,0,120),GREEN = new Color(0,255,0,120),RED = new Color(255,0,0,120);
	
	private Thing[] things = {
		new TGoomba(0, 0),
		new TKoopa(0,0),
		new TPirana(),
		new TTuberia(0,0),
		new TBloque(0,0,TBloque.LADRILLO_MARRON,null),
		new TBloque(TBloque.SUELO),
		new TBloque(TBloque.PILAR),
		new TBloque(TBloque.BLOQUE_PREGUNTA_DESACTIVADO),
		null,
		null,
		new TBloque(TBloque.HONGO_IZQUIERDO),
		new TBloque(TBloque.HONGO_MEDIO),
		new TBloque(TBloque.HONGO_DERECHO),
		new TBGBloque(TBloque.HONGO_ARRIBA),
		new TBGBloque(TBloque.HONGO_ABAJO),
                new TStar(),
		new TSpawn(),
		new TMeta(),
		new THorizontalBound(),
		new TVerticalBound(),
		new TLinker(),
		new TRemover(),
		new AgujeroTierra(),
	};
	
	public SpawnScreen(Heroe heroe){
		visible = false;
		nivelPartida = false;
		cosaElegida = 0;
		cosaFlotante = -1;
		cambiarNivel = new TextButton("CAMBIAR FONDO", JGameMaker.FONT_MEDIO, 160, 160 + 48*(things.length - 1 + 10)/10);
		congelarTiempo = new TextButton("CONGELAR TIEMPO", JGameMaker.FONT_MEDIO, 160, 160 + 48*(things.length - 1 + 10)/10 + 20 + cambiarNivel.getHeight());
		this.heroe = heroe;
	}

	public void draw(Graphics g) {
		if(visible){
			((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			for(int i = 0; i < things.length; i++){
				if(things[i] == null)continue;
				BufferedImage img = things[i].preview();
				int x = 160+(i%10)*48;
				int y = 160+(i/10)*48;
				if(i == cosaFlotante)
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
				int width = 32, height = 32*img.getHeight()/img.getWidth();
				if(i == cosaElegida){
					x -= width/2;
					y -= height/2;
					width *= 2;
					height *= 2;
				}
				
				g.drawImage(img,x,y,width,height,null);
				if(i == cosaFlotante)
					((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			}
		}else if(cosaElegida != -1){
		
			Thing t = things[cosaElegida];
			//makes the graphics object draw opaquely
			((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.25f));
			t.draw((Graphics2D)g,null,heroe);
		}
		//set it back to normal
		((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		if(visible){
			cambiarNivel.draw(g);
			congelarTiempo.draw(g);
		}
	}
	/**
	 * returns true if this can be seen on-screen
	 * @return
	 */
	public boolean isVisible(){
		return visible;
	}

	public void key(KeyEvent e, boolean down) {
		int code = e.getKeyCode();
		switch(code){
			//make menu visible on these buttons
			case KeyEvent.VK_Q:
			case KeyEvent.VK_SHIFT:
			case KeyEvent.VK_TAB:
			case KeyEvent.VK_ALT:
			case KeyEvent.VK_CONTROL:
				visible = down;
			break;
		}
		if(down){
			switch(code){
				//convenience shortcut buttons
				case KeyEvent.VK_BACK_SPACE:
				case KeyEvent.VK_R:
					cosaElegida =25; //remover index
				break;
				case KeyEvent.VK_G:
					cosaElegida = 0; //goomba index
				break;
				case KeyEvent.VK_K:
					cosaElegida = 1; //koopa index
				break;
				case KeyEvent.VK_P:
					cosaElegida = 3; //tuberia index
				break;
				case KeyEvent.VK_B:
					
					if(cosaElegida < 4 || cosaElegida > 6)
						cosaElegida = 4; //q block index
					else
						cosaElegida++;
				break;
				case KeyEvent.VK_C:
					if(cosaElegida == 26){
						TBloquesColores.cycleColors();
						things[cosaElegida].init();
					}else{
						cosaElegida = 26;
					}
				break;
			}
		}
	}
	/**
	 * returns true if this should toggle whether or not all the Things in the juego should stop moving
	 * @return
	 */
	public boolean shouldToggleFreeze(){
		boolean temp = congelarPartida;
		congelarPartida = false;
		return temp;
	}
	
	/**
	 * returns true if this should change the landscape
	 * @return
	 */
	public boolean shouldToggleLevel(){
		boolean temp = nivelPartida;
		nivelPartida = false;
		return temp;
	}
	
	
	/**
	 * returns the color t should be highlighted (used for tools)
	 * @param t the thing that is checked against
	 * @return a color if <b>t</b> should be highlighted, null if it shouldn't
	 */
	public Color highlightColor(Thing t){
		Thing chosen = things[cosaElegida];
		if(!(chosen.tocando(t) && t.tocando(chosen))){
			return null;
		}
		if(chosen instanceof TRemover){
			return RED;
		}else if(chosen instanceof TLinker){
			TLinker linker = (TLinker)chosen;
			if(t.canLink(linker.getLink())){
				return YELLOW;
			}
		}else if(chosen instanceof TPirana){
			if(t instanceof TTuberia && ((TTuberia)t).getPirana() == null){
				return YELLOW;
			}
		}else if(chosen instanceof TItem && t instanceof TBloque && ((TBloque)t).canAcceptItem()){
			return GREEN;
		}
		return null;
	}
	
	/**
	 * gets the Thing that should be spawned and returns it. The returned Thing is removed from the SpawnScreen and new instancia of it is created to take its place.
	 * @return the Thing to be spawned
	 */
	public Thing getSpawn(){
		if(visible)return null;
		Thing temp = things[cosaElegida];
		if(temp instanceof TLinker && ((TLinker)temp).getLink() == null){
			//do not make a new one.
		//	((TLinker)temp).makeInWorld();
		}else{
			try {
				things[cosaElegida] = temp.getClass().newInstance();
			} catch (Exception e) {
				things[cosaElegida] = null;
				e.printStackTrace();
			}
		}	
		things[cosaElegida].init(temp.serialize());
		return temp;
	}
	/**
	 * returns the Thing that is selected in the SpawnScreen
	 * @return the Thing that is selected in the SpawnScreen
	 */
	public Thing pasarSpawn(){
		return things[cosaElegida];
	}

	public void mouse(MouseEvent e, boolean down) {
		int x = e.getX(), y = e.getY();
		if(down && visible){
			int temp = getIndex(x, y);
			if(temp == cosaElegida){
				if(things[cosaElegida] instanceof TBloquesColores){
					TBloquesColores.cycleDirections();
					things[cosaElegida].init();
				}
			}else if(temp != -1){
				cosaElegida = temp;
			}
			if(cambiarNivel.contains(x,y)){
				nivelPartida = true;
			}else if(congelarTiempo.contains(x,y)){
				congelarPartida =  true;
			}
		}
	}

	public void think() {
		Thing t = things[cosaElegida];
		t.setSpawnPos(ScreenManager.mouse.x +heroe.pos.x - (JGameMaker.screenWidth/2.0 + heroe.xOffset()),JGameMaker.screenHeight + heroe.pos.y + 32 - (ScreenManager.mouse.y + heroe.yOffset()));
		t.vel.x = 0;
		t.vel.y = 0;
		t.acc.x = 0;
		t.acc.y = 0;
		t.think();
		if(!visible)return;
		cosaFlotante = getIndex(ScreenManager.mouse.x, ScreenManager.mouse.y);
	}
	private int getIndex(int x, int y){
		for(int i = 0; i < things.length; i++){
			if(things[i] == null)continue;
			int tx = 160+(i%10)*48;
			int ty = 160+(i/10)*48;
			Rectangle mensaje1 = new Rectangle(tx,ty,32,32);
			if(mensaje1.contains(x,y)){
				return i;
			}
		}
		return -1;
	}

}
