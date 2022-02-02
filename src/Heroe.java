



import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.ArrayList;


public class Heroe extends Thing{
	
	
	//FIELDS
	
		private static final AePlayWave SALTAR = new AePlayWave("mario_salta.wav");
		private static final String MARIO_SPRITE_PATH = "Imagenes/sprites/mario/";
		
		
		private boolean saltoAbajo;
		
		private boolean muerte;
		private double tiempoMuerto;
		
		/**
		 * draw images
		 */
		public final Sprite[] IMAGEN = {
			new Sprite(MARIO_SPRITE_PATH+"stand.gif"),
			new Sprite(MARIO_SPRITE_PATH+"saltar.gif"),
			new Sprite(MARIO_SPRITE_PATH+"caminar.gif"),
			new Sprite(MARIO_SPRITE_PATH+"stand.gif"),
			new Sprite(MARIO_SPRITE_PATH+"cambiar.gif"),
			new Sprite(MARIO_SPRITE_PATH+"muerte.gif"),
			
		};
		
		private final Color[] COLOR_IMAGEN = {
			new Color(248,48,80), //light
			new Color(216,0,40),
			new Color(176,0,0),//dark
		};
		
		private boolean movimientoArriba;
		
		private boolean estrella;
		private double tiempoEstrella;
		
		
		
		private boolean invulnerable;
		private double tiempoInvulnerable;
		
		
		private int lobby = -1;
		private Point2D.Double telePos = new Point2D.Double();
		
		private byte move;
		private double xOffset;
		private double yOffset;
		private boolean saltando,agachado,tuberiado,cambiarMundos,infVelocidad;
		
		private Point2D.Double muertePos;
		
		private double addSalto;
		private double lineaMeta;
		
		private static final double
			X_OFFSET = 100,
			Y_OFFSET = JGameMaker.screenHeight - 30,
			MAX_X_VEL = 5,
			X_VEL_POR_T = 0.1,
			SALTO_VEL = 3;
		
		public static final int
			WIDTH = 24,
			HEIGHT = 30;
		
		private int spriteColor = 0;
		
		private boolean ganador;
		
		
		private boolean frenteDerecha,movimientoDerecha,movimientoIzquierda,pieDerecho;
		
		private double ultimaX;
		
	
	//CONSTRUCTOR
		public Heroe(){
			super(0,0,WIDTH,HEIGHT);
			muertePos = new Point2D.Double();
		}
		
		public void init(){
			saltoAbajo = false;
			movimientoArriba = false;
			
			muerte = false;
			tiempoMuerto = 0;
			
			estrella = false;
			tiempoEstrella = 0;
			
			ganador = false;
			
			
			
			pos = new Point2D.Double();
			vel = new Point2D.Double();
			acc = new Point2D.Double();
			
			invulnerable = false;
			tiempoInvulnerable = 0;
			xOffset = 0;
			yOffset = Y_OFFSET/8;
			move = 0;
			addSalto = 0;
			frenteDerecha = true;
			ultimaX = 0;
			pieDerecho = true;
			
		}
	
	
	//ACTION METHODS
		
		
		public void setSpriteColor(int c){setSpriteColor(c,false);}
		
		/**
		 * 
		 * @param override true if should matar even if invulnerable, false if not
		 */
		public void matar(boolean override){
			if(!override){
				if(estrella || invulnerable || tuberiado)return;
			}
			muertePos.setLocation(pos);
			if(muertePos.y < 0){
				muertePos.y = 0;
			}
			
			muerte = true;
			cayendo = false;
			acc = new Point2D.Double();
			vel = new Point2D.Double();
			tiempoMuerto = 25;
                        
                        AePlayWave.fondoMusica.finalizarMusica();// si se muere, quitar musica de fondo
                        
                        AePlayWave.fondoMusica = new AePlayWave("Sonidos/muerte.wav"); 
                        AePlayWave.fondoMusica.start(); // colocar musica de muerte de fondo
		}
		
		/**
		 * determines if in estrella mode
		 * @return true if in estrella mode
		 */
		public boolean vModoEstrella(){
			return estrella;
		}
		
		public void matar(){
			matar(false);
		}
		
		public boolean activarGravedad(){
			return true;
		}
		
		/**
		 * sets the respawn position
		 */
		public void setMuertePos(){
			setPos(muertePos.x,muertePos.y);
		}
		
		/**
		 * changes the sprite color
		 * @param color color that should be changed to <br/>&nbsp;&nbsp;1: green<br/>&nbsp;&nbsp;2:blue<br/>&nbsp;&nbsp;3:yellow<br/>&nbsp;&nbsp;4:purple<br/>&nbsp;&nbsp;5:gray
		 * @param estrella whether or not this is just for the starman sequence
		 */
		public void setSpriteColor(int color, boolean estrella){
			if(!estrella)
				spriteColor = color;
			Color[] replace = new Color[3];
			switch(color){
				case 0: //red
					for(int i = 0; i < IMAGEN.length; i++)
						(IMAGEN[i]).renew();
					return; //the image is already red so why go through all sorts of bullshit?
				//break;
				
				case 1: //green
					replace[0] = new Color(41,255,41);
					replace[1] = new Color(0,216,0);
					replace[2] = new Color(0,176,0);
				break;
				
				case 2: //blue
					replace[0] = new Color(41,171,255);
					replace[1] = new Color(0,140,216);
					replace[2] = new Color(0,100,176);
				break;
				
				case 3: //yellow
					replace[0] = new Color(252,255,41);
					replace[1] = new Color(213,216,0);
					replace[2] = new Color(174,176,0);
				break;
				
				case 4: //purple
					replace[0] = new Color(255,41,252);
					replace[1] = new Color(216,0,213);
					replace[2] = new Color(176,0,174);
				break;
				
				default: //gray
					replace[0] = new Color(250,250,250);
					replace[1] = new Color(170,170,170);
					replace[2] = new Color(150,150,150);
				break;
			}
			
			for(int i = 0; i < IMAGEN.length; i++){
				Sprite limg = IMAGEN[i];
				limg.renew();
				limg.replaceColors(COLOR_IMAGEN, replace);
			}
		}
		
		
		/**
		 * starts the starman sequence
		 */
		public void startStar(){
			estrella = true;
			tiempoEstrella = 0;
		}
		
		/**
		 * starts temporary invulerability
		 */
		public void startInvulnerable(){
			invulnerable = true;
			tiempoInvulnerable = 0;
		}
		
		
		
		/**
		 * called when this wants to move through a tuberia
		 * @param lineaMeta the y coordinate this must pass before teleporting to its new position
		 * @param lobby lobby to move to
		 * @param telePos position to move to
		 */
		public void tuberia(double lineaMeta, int lobby, Point2D.Double telePos){
			if(agachado){
				new AePlayWave("Sonidos/tuberia.wav").start();
				agachado = false;
				tuberiado = true;
				vel.y = -1;
				vel.x = 0;
				this.lineaMeta = lineaMeta;
				this.lobby = lobby;
				this.telePos.setLocation(telePos);
			}
		}
		/**
		 * sets this position to that of the new tuberia it should move to
		 * @return lobby this should move to
		 */
		public int getRoomAndSetNewPosition(){
			this.setPos(telePos.x,telePos.y);
			vel.y = 1;
			vel.x = 0;
			tuberiado = true;
			lineaMeta = (int)(pos.y + height);
			return lobby;
		}
		/**
		 * determines if the player finished moving through a tuberia and needs to change to the connected tuberia
		 * @return true if finished moving through a tuberia, false if not
		 */
		public boolean tuberiado(){
			boolean p = cambiarMundos;
			if(p){
				tuberiado = false;
				setPos(pos.x, (double)lineaMeta);
				vel.y = 0;
			}
			cambiarMundos = false;
			return p;
		}
		/**
		 * determines if the player is moving through a tuberia
		 * @return true if moving through a tuberia
		 */
		public boolean tuberiando(){
			return tuberiado;
		}
		/**
		 * makes this crouch (for pipes)
		 * @param agachado
		 */
		public void crouch(boolean agachado){
			this.agachado = agachado;
		}
		/**
		 * makes this accelerate horizontally
		 * @param rightward true if should move right, false if should move left
		 */
		public void move(boolean rightward){
			if(rightward){
				move = 1;
			}else{
				move = -1;
			}
			movimientoDerecha = rightward;
			movimientoIzquierda = !rightward;
			frenteDerecha = rightward;
		}
		/**
		 * makes this stop accelerating horizontally
		 * @param rightward true if the unpressed button is the right key, false if it is the left key
		 */
		public void stop(boolean rightward){
			if(move == -1 && !rightward || move == 1 && rightward)
				move = 0;
			if(rightward)
				movimientoDerecha = false;
			else
				movimientoIzquierda = false;
		}
		/**
		 * determines if the saltar key is down or not (used when stomping on enemies)
		 * @return true if the saltar button is down, false if not
		 */
		public boolean saltoAbajo(){
			return saltoAbajo;
		}

		/**
		 * makes this saltar, makes the sound play by default
		 * @param pressed true if saltar button is down
		 */
		public void saltar(boolean pressed){
			saltar(pressed,true);
		}
		/**
		 * makes this saltar
		 * @param pressed true if saltar button is down
		 * @param sound true if should play sound
		 */
		public void saltar(boolean pressed, boolean sound){
			if(muerte) return;
			if(pressed)
				saltoAbajo = true;
			else
				saltoAbajo = false;
			
			
			if(pressed){
				if(vel.y == 0 ){
					vel.y = SALTO_VEL + JGameMaker.GRAVEDAD;
					saltando = true;
					addSalto = JGameMaker.TIEMPO_SALTO_COMPLETO;
					if(sound)
						SALTAR.start();
				}
			}else{
				addSalto = -JGameMaker.TIEMPO_SALTO_COMPLETO;
			}
		}
		//double jolt = 
		private void addSalto(){
			if(vel.y == 0 || muerte){
				addSalto = 0;
			}else{
				double add = 0.4*addSalto/JGameMaker.TIEMPO_SALTO_COMPLETO*JGameMaker.time();
				vel.y += add;
			}
		}
		
		public void setPos(double x, double y){
			if(x != pos.x)
				xOffset -= pos.x -  x;
			if(y != pos.y)
				yOffset -= pos.y -  y;
			pos.setLocation(x,y);
		}
		
		public void setPos(Point2D.Double pos){
			setPos(pos.x,pos.y);
		}
		
		public boolean tocando(Thing t){
			if(tuberiado && vel.y < 0 || muerte)
				return false;
			else
				return super.tocando(t);
		}
		
		public void enContacto(Thing t){
			if(muerte) return;
			if(estrella && t instanceof TEnemy ){
				new AePlayWave("Sonidos/patada.wav").start();
				t.matar(new Point2D.Double(vel.x*2, Math.random()*16+3));
			}else if(t instanceof TGoal){
				ganador = true;
			}
		}
		/**
		 * determines if this hit a TGoal
		 * @return true if this hit the goal
		 */
		public boolean ganador(){
			boolean temp = ganador;
			ganador = false;
			return temp;
		}
		
	//CONSTANT METHODS
		public void draw(Graphics g, ImageObserver o){
			
			boolean transparent = invulnerable && (tiempoInvulnerable < 3000/15 || System.currentTimeMillis() % 60 > 30);
			if(transparent)
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			int x = JGameMaker.scaleW(JGameMaker.screenWidth/2.0 + xOffset - 4);
			int y = JGameMaker.scaleH(JGameMaker.screenHeight - yOffset);
			
			
			
			g.drawImage(
				figureOutDrawImage(IMAGEN),
				x,
				//(int)(Global.H-H-pos.y-Global.NIVEL_SUELO),
				y,
				JGameMaker.scaleW(WIDTH + 8),
				JGameMaker.scaleH(HEIGHT + 2),
				o
			);
			if(transparent)
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			
			//g.setColor(Color.WHITE);
			//g.drawRect((int)(JGameMaker.W/2 - X_OFFSET), (int)(JGameMaker.H - Y_OFFSET), (int)X_OFFSET*2 + width, (int)(Y_OFFSET*2) - JGameMaker.H);
		}
		
		
		private BufferedImage figureOutDrawImage(Sprite[] imgs){
			
			Sprite img;
			if(muerte){
				return imgs[5].getBuffer();
			}
			
			if(Math.abs((int)(vel.y*100)) > 0 && !tuberiado){
				img = imgs[1];
			}else if( !(movimientoDerecha || movimientoIzquierda) && vel.x == 0 )
				img = imgs[0];
			else{
				if(movimientoDerecha && vel.x < 0 || movimientoIzquierda && vel.x > 0)
					img = imgs[4];
				else{
					if(pieDerecho)
						img = imgs[3];
					else
						img = imgs[2];
				}
			}

			if(estrella && (tiempoEstrella < 8000/15 || System.currentTimeMillis() % 60 > 30)){
				int	width = img.getBuffer().getWidth(),
					height = img.getBuffer().getHeight();
				Sprite limg = new Sprite(img.getBuffer());
				for(int x = 0; x < width; x++){
					for(int y = 0; y < height; y++){
						Color pixel = limg.getPixel(x,y);
						if(pixel.getAlpha() != 0){
							limg.setPixel(x,y,new Color(255 - pixel.getRed(), 255 - pixel.getGreen(), 255 - pixel.getBlue(), pixel.getAlpha()));
						}
					}
				}
				img = limg;
			}
			
			if(frenteDerecha){
				return img.flipX();
			}else{
				return img.getBuffer();
			}
		}
		
		public boolean muriendo(){
			return muerte;
		}
		/**
		 * determines if the player died, and already went to the bottom of the screen after the little saltar upward
		 * @return true if muerte, false if not
		 */
		public boolean vMuerte(){
			return muerte && yOffset < -300;
		}
		
		public void think(){
			if(muerte && tiempoMuerto > 0){
				tiempoMuerto -= JGameMaker.time();
				if(tiempoMuerto < 0){
					vel.x = 0;
					vel.y = 15;
				}
				return;
			}
			

			if(estrella){
				tiempoEstrella += JGameMaker.time();
				if(tiempoEstrella > 10000/15){ //if you've been in starman mode for more than 10 seconds, stop
					estrella = false;
					tiempoEstrella = 0;
					setSpriteColor(spriteColor,true);
				}else{
					setSpriteColor((int)(Math.random()*6),true);
				}
			}
			
			if(invulnerable){
				tiempoInvulnerable += JGameMaker.time();
				if(tiempoInvulnerable > 5000/15){
					invulnerable = false;
					tiempoInvulnerable = 0;
				}
			}
			
			
			
			

			actualizarPosicion();
			xOffset += pos.x - posLast.x;
			if(xOffset > X_OFFSET)
				xOffset = X_OFFSET;
			else if(xOffset < -X_OFFSET)
				xOffset = -X_OFFSET;
				
			yOffset += pos.y - posLast.y;
			if(!(muerte || cayendo)){
				if(yOffset > Y_OFFSET)
					yOffset = Y_OFFSET;
				else if(yOffset < Y_OFFSET/8)
					yOffset = Y_OFFSET/8;
			}
			updateVelocity();
			//double v = vel.y;
			//if(v > 0 && vel.y < 0){
			//	System.out.println("position: " + pos.y);
			//}
			cayendo = false;
		}
		
		private void updateVelocity(){
			if(tuberiado){
				return;
			}
			if(addSalto > 0){
				addSalto-= JGameMaker.time();
				addSalto();
				acc.y = 0;
			}
			if(saltando){
				saltando = false;
			}else if(!muerte){
				int tempMove = move;
				if(move == 0){
					if(vel.x > 0)
						tempMove = -1;
					else
						tempMove = 1;
				}
				double xadd;
				//stopping
				if(vel.x > 0 && move <= 0 || vel.x < 0 && move >= 0){
					xadd = tempMove*X_VEL_POR_T*2.0*JGameMaker.time();
				//moving forward
				}else{
					xadd = tempMove*X_VEL_POR_T*JGameMaker.time();
				}
				if(vel.y < 0){
					vel.x += xadd/3;
				}else{
					vel.x += xadd;
				}
				if(move == 0 && tempMove*vel.x > 0){
					vel.x = 0;
				}
				if(!infVelocidad){
					if(vel.x < -MAX_X_VEL)
						vel.x = -MAX_X_VEL;
					else if(vel.x > MAX_X_VEL)
						vel.x = MAX_X_VEL;
				}
			}
			
			if(vel.x < X_VEL_POR_T*JGameMaker.time() && vel.x > -X_VEL_POR_T*JGameMaker.time())
				vel.x = 0;
			
			vel.x += acc.x*JGameMaker.time();
			vel.y += acc.y*JGameMaker.time();
			
			//S//ystem.out.println(acc.y);
			if(cayendo && invulnerable){
				cayendo = false;
			}
			if(pos.y > 0 && !(movimientoArriba) || cayendo){
				acc.y = -JGameMaker.GRAVEDAD;
				
			}else if(movimientoArriba && vel.y > 5){
				acc.y = 0;
				vel.y = 5;
			}
			else if(!saltando && !muerte && !(movimientoArriba)){
				vel.y = 0;
				acc.y = 0;
			}
		}
		
		private void actualizarPosicion(){
			updatePosLast();
			
			pos.setLocation(pos.x+vel.x*JGameMaker.time(),pos.y+vel.y*JGameMaker.time());

			if(!muerte && pos.y+JGameMaker.NIVEL_SUELO < 0){
				matar(true);
				//pos.y = pos.y + JGameMaker.NIVEL_SUELO + 1;
				cayendo = false;
			}else if(pos.y < 0 && !muerte && !tuberiado && (!cayendo || invulnerable)){
				setPos(pos.x,0);
				//yOffset = JGameMaker.NIVEL_SUELO;
			}
			
			if(pos.x > ultimaX + 20 || pos.x < ultimaX - 20){
				pieDerecho = !pieDerecho;
				ultimaX = pos.x;
			}
			if(tuberiado && pos.y + height< lineaMeta && vel.y < 0) cambiarMundos = true;
			if(tuberiado && pos.y > lineaMeta && vel.y > 0){
				vel.y = 0;
				pos.y = lineaMeta;
				tuberiado = false;
			}
			
		}
	
	//RETRIEVAL METHODS
		
		/**
		 * gets the x-offset (how many pixels the player is from the center of the screen)
		 */
		public double xOffset(){
			return xOffset;
		}
		/**
		 * gets the y-offset (how many pixels the player is from the center of the screen)
		 */
		public double yOffset(){
			return yOffset;
		}
		/**
		 * gets the x coordinate where the player is supposed to be drawn to the screen
		 */
		public double viewX(){
			return pos.x-xOffset+JGameMaker.screenWidth/2.0;
		}
		
		/**
		 * gets the y coordinate of where the player is supposed to be drawn to the screen
		 */
		public double viewY(){
			return JGameMaker.screenHeight - yOffset + pos.y + height;
		}
		
		public void bumpX(){
			vel.x = 0;
		}
	
}