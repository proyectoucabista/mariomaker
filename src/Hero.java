



import java.awt.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.util.ArrayList;


public class Hero extends Thing{
	
	
	//FIELDS
	
		private static final AePlayWave JUMP = new AePlayWave("mario_salta.wav");
		private static final String MARIO_SPRITE_PATH = "Imagenes/sprites/mario/";
		
		
		private boolean jumpDown;
		
		private boolean muerte;
		private double deadTime;
		
		/**
		 * draw images
		 */
		public final Sprite[] IMAGE = {
			new Sprite(MARIO_SPRITE_PATH+"stand.gif"),
			new Sprite(MARIO_SPRITE_PATH+"saltar.gif"),
			new Sprite(MARIO_SPRITE_PATH+"caminar.gif"),
			new Sprite(MARIO_SPRITE_PATH+"stand.gif"),
			new Sprite(MARIO_SPRITE_PATH+"cambiar.gif"),
			new Sprite(MARIO_SPRITE_PATH+"muerte.gif"),
			
		};
		
		private final Color[] IMAGE_COLORS = {
			new Color(248,48,80), //light
			new Color(216,0,40),
			new Color(176,0,0),//dark
		};
		
		private boolean movingUp;
		
		private boolean star;
		private double starTime;
		
		private boolean cape;
		private double capeTime;
		
		private boolean invulerable;
		private double invulerableTime;
		
		boolean metal;
		
		private int room = -1;
		private Point2D.Double telePos = new Point2D.Double();
		
		private byte move;
		private double xOffset;
		private double yOffset;
		private boolean jumping,crouched,piped,changeWorlds,infSpeed;
		
		private Point2D.Double deathPos;
		
		private double jumpAdd;
		private double lineToCross;
		
		private static final double
			X_OFFSET = 100,
			Y_OFFSET = JGameMaker.screenHeight - 30,
			MAX_X_VEL = 5,
			X_VEL_PER_TICK = 0.1,
			JUMP_VEL = 3;
		
		public static final int
			WIDTH = 24,
			HEIGHT = 30;
		
		private int spriteColor = 0;
		
		private boolean ganador;
		private static TextButton ammo;
		
		private int numBullets;
		private long shotTime;
		
		private boolean facingRight,movingRight,movingLeft,rightFoot;
		
		private double lastX;
		
	
	//CONSTRUCTOR
		public Hero(){
			super(0,0,WIDTH,HEIGHT);
			deathPos = new Point2D.Double();
		}
		
		public void init(){
			jumpDown = false;
			movingUp = false;
			
			muerte = false;
			deadTime = 0;
			
			star = false;
			starTime = 0;
			
			ganador = false;
			
			cape = false;
			capeTime = 0;
			
			pos = new Point2D.Double();
			vel = new Point2D.Double();
			acc = new Point2D.Double();
			
			invulerable = false;
			invulerableTime = 0;
			xOffset = 0;
			yOffset = Y_OFFSET/8;
			move = 0;
			jumpAdd = 0;
			facingRight = true;
			lastX = 0;
			rightFoot = true;
			
		}
	
	
	//ACTION METHODS
		
		
		public void setSpriteColor(int c){setSpriteColor(c,false);}
		
		/**
		 * 
		 * @param override true if should kill even if invulerable, false if not
		 */
		public void kill(boolean override){
			if(!override){
				if(star || invulerable || piped)return;
			}
			deathPos.setLocation(pos);
			if(deathPos.y < 0){
				deathPos.y = 0;
			}
			
			muerte = true;
			falling = false;
			acc = new Point2D.Double();
			vel = new Point2D.Double();
			deadTime = 25;
                        
                        AePlayWave.fondoMusica.finalizarMusica();// si se muere, quitar musica de fondo
                        
                        AePlayWave.fondoMusica = new AePlayWave("Sonidos/muerte.wav"); 
                        AePlayWave.fondoMusica.start(); // colocar musica de muerte de fondo
		}
		
		/**
		 * determines if in star mode
		 * @return true if in star mode
		 */
		public boolean isInStarMode(){
			return star;
		}
		
		public void kill(){
			kill(false);
		}
		
		public boolean enableGravity(){
			return true;
		}
		
		/**
		 * sets the respawn position
		 */
		public void setDeathPos(){
			setPos(deathPos.x,deathPos.y);
		}
		
		/**
		 * changes the sprite color
		 * @param color color that should be changed to <br/>&nbsp;&nbsp;1: green<br/>&nbsp;&nbsp;2:blue<br/>&nbsp;&nbsp;3:yellow<br/>&nbsp;&nbsp;4:purple<br/>&nbsp;&nbsp;5:gray
		 * @param star whether or not this is just for the starman sequence
		 */
		public void setSpriteColor(int color, boolean star){
			if(!star)
				spriteColor = color;
			Color[] replace = new Color[3];
			switch(color){
				case 0: //red
					for(int i = 0; i < IMAGE.length; i++)
						(IMAGE[i]).renew();
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
			
			for(int i = 0; i < IMAGE.length; i++){
				Sprite limg = IMAGE[i];
				limg.renew();
				limg.replaceColors(IMAGE_COLORS, replace);
			}
		}
		/**
		 * starts the metal cap sequence
		 */
		public void startMetal(){
			metal = true;
		}
		
		/**
		 * starts the starman sequence
		 */
		public void startStar(){
			star = true;
			starTime = 0;
		}
		
		/**
		 * starts temporary invulerability
		 */
		public void startInvulnerable(){
			invulerable = true;
			invulerableTime = 0;
		}
		
		
		
		/**
		 * called when this wants to move through a tuberia
		 * @param lineToCross the y coordinate this must pass before teleporting to its new position
		 * @param room room to move to
		 * @param telePos position to move to
		 */
		public void tuberia(double lineToCross, int room, Point2D.Double telePos){
			if(crouched){
				new AePlayWave("Sonidos/tuberia.wav").start();
				crouched = false;
				piped = true;
				vel.y = -1;
				vel.x = 0;
				this.lineToCross = lineToCross;
				this.room = room;
				this.telePos.setLocation(telePos);
			}
		}
		/**
		 * sets this position to that of the new tuberia it should move to
		 * @return room this should move to
		 */
		public int getRoomAndSetNewPosition(){
			this.setPos(telePos.x,telePos.y);
			vel.y = 1;
			vel.x = 0;
			piped = true;
			lineToCross = (int)(pos.y + height);
			return room;
		}
		/**
		 * determines if the player finished moving through a tuberia and needs to change to the connected tuberia
		 * @return true if finished moving through a tuberia, false if not
		 */
		public boolean piped(){
			boolean p = changeWorlds;
			if(p){
				piped = false;
				setPos(pos.x, (double)lineToCross);
				vel.y = 0;
			}
			changeWorlds = false;
			return p;
		}
		/**
		 * determines if the player is moving through a tuberia
		 * @return true if moving through a tuberia
		 */
		public boolean piping(){
			return piped;
		}
		/**
		 * makes this crouch (for pipes)
		 * @param crouched
		 */
		public void crouch(boolean crouched){
			this.crouched = crouched;
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
			movingRight = rightward;
			movingLeft = !rightward;
			facingRight = rightward;
		}
		/**
		 * makes this stop accelerating horizontally
		 * @param rightward true if the unpressed button is the right key, false if it is the left key
		 */
		public void stop(boolean rightward){
			if(move == -1 && !rightward || move == 1 && rightward)
				move = 0;
			if(rightward)
				movingRight = false;
			else
				movingLeft = false;
		}
		/**
		 * determines if the saltar key is down or not (used when stomping on enemies)
		 * @return true if the saltar button is down, false if not
		 */
		public boolean jumpDown(){
			return jumpDown;
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
				jumpDown = true;
			else
				jumpDown = false;
			if(cape){
				if(pressed){
					movingUp = true;
					acc.y = JGameMaker.GRAVEDAD;
					jumping = true;
				}else{
					movingUp = false;
					jumping = false;
					acc.y = 0;
				}
				return;
			}
			
			if(pressed){
				if(vel.y == 0 ){
					vel.y = JUMP_VEL + JGameMaker.GRAVEDAD;
					jumping = true;
					jumpAdd = JGameMaker.TIEMPO_SALTO_COMPLETO;
					if(sound)
						JUMP.start();
				}
			}else{
				jumpAdd = -JGameMaker.TIEMPO_SALTO_COMPLETO;
			}
		}
		//double jolt = 
		private void jumpAdd(){
			if(vel.y == 0 || muerte){
				jumpAdd = 0;
			}else{
				double add = 0.4*jumpAdd/JGameMaker.TIEMPO_SALTO_COMPLETO*JGameMaker.time();
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
		
		public boolean touching(Thing t){
			if(piped && vel.y < 0 || muerte)
				return false;
			else
				return super.touching(t);
		}
		
		public void onTouch(Thing t){
			if(muerte) return;
			if(star && t instanceof TEnemy ){
				new AePlayWave("Sonidos/patada.wav").start();
				t.kill(new Point2D.Double(vel.x*2, Math.random()*16+3));
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
			
			boolean transparent = invulerable && (invulerableTime < 3000/15 || System.currentTimeMillis() % 60 > 30);
			if(transparent)
				((Graphics2D)g).setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
			int x = JGameMaker.scaleW(JGameMaker.screenWidth/2.0 + xOffset - 4);
			int y = JGameMaker.scaleH(JGameMaker.screenHeight - yOffset);
			
			
			
			g.drawImage(
				figureOutDrawImage(IMAGE),
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
			
			if(Math.abs((int)(vel.y*100)) > 0 && !piped){
				img = imgs[1];
			}else if( !(movingRight || movingLeft) && vel.x == 0 )
				img = imgs[0];
			else{
				if(movingRight && vel.x < 0 || movingLeft && vel.x > 0)
					img = imgs[4];
				else{
					if(rightFoot)
						img = imgs[3];
					else
						img = imgs[2];
				}
			}

			if(star && (starTime < 8000/15 || System.currentTimeMillis() % 60 > 30)){
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
			}else if(metal){
				int	width = img.getBuffer().getWidth(),
					height = img.getBuffer().getHeight();
				Sprite limg = new Sprite(img.getBuffer());
				for(int x = 0; x < width; x++){
					for(int y = 0; y < height; y++){
						Color pixel = limg.getPixel(x,y);
						if(pixel.getAlpha() != 0){
							int gray = (pixel.getRed() + pixel.getBlue() + pixel.getGreen())/3;
							limg.setPixel(x,y,new Color(gray,gray,gray, pixel.getAlpha()));
						}
					}
				}
				img = limg;
			}
			
			if(facingRight){
				return img.flipX();
			}else{
				return img.getBuffer();
			}
		}
		
		public boolean dying(){
			return muerte;
		}
		/**
		 * determines if the player died, and already went to the bottom of the screen after the little saltar upward
		 * @return true if muerte, false if not
		 */
		public boolean isDead(){
			return muerte && yOffset < -300;
		}
		
		public void think(){
			if(muerte && deadTime > 0){
				deadTime -= JGameMaker.time();
				if(deadTime < 0){
					vel.x = 0;
					vel.y = 15;
				}
				return;
			}
			

			if(star){
				starTime += JGameMaker.time();
				if(starTime > 10000/15){ //if you've been in starman mode for more than 10 seconds, stop
					star = false;
					starTime = 0;
					setSpriteColor(spriteColor,true);
				}else{
					setSpriteColor((int)(Math.random()*6),true);
				}
			}
			
			if(invulerable){
				invulerableTime += JGameMaker.time();
				if(invulerableTime > 5000/15){
					invulerable = false;
					invulerableTime = 0;
				}
			}
			
			if(cape){
				capeTime += JGameMaker.time();
				if(capeTime > 10000/15){
					cape = false;
					capeTime = 0;
				}
			}
			
			

			updatePosition();
			xOffset += pos.x - posLast.x;
			if(xOffset > X_OFFSET)
				xOffset = X_OFFSET;
			else if(xOffset < -X_OFFSET)
				xOffset = -X_OFFSET;
				
			yOffset += pos.y - posLast.y;
			if(!(muerte || falling)){
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
			falling = false;
		}
		
		private void updateVelocity(){
			if(piped){
				return;
			}
			if(jumpAdd > 0){
				jumpAdd-= JGameMaker.time();
				jumpAdd();
				acc.y = 0;
			}
			if(jumping){
				jumping = false;
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
					xadd = tempMove*X_VEL_PER_TICK*2.0*JGameMaker.time();
				//moving forward
				}else{
					xadd = tempMove*X_VEL_PER_TICK*JGameMaker.time();
				}
				if(vel.y < 0 && !metal){
					vel.x += xadd/3;
				}else{
					vel.x += xadd;
				}
				if(move == 0 && tempMove*vel.x > 0){
					vel.x = 0;
				}
				if(!infSpeed){
					if(vel.x < -MAX_X_VEL)
						vel.x = -MAX_X_VEL;
					else if(vel.x > MAX_X_VEL)
						vel.x = MAX_X_VEL;
				}
			}
			
			if(vel.x < X_VEL_PER_TICK*JGameMaker.time() && vel.x > -X_VEL_PER_TICK*JGameMaker.time())
				vel.x = 0;
			
			vel.x += acc.x*JGameMaker.time();
			vel.y += acc.y*JGameMaker.time();
			
			//S//ystem.out.println(acc.y);
			if(falling && invulerable){
				falling = false;
			}
			if(pos.y > 0 && !(cape && movingUp) || falling){
				acc.y = -JGameMaker.GRAVEDAD;
				if(cape){
					acc.y /= 4;
				}
			}else if(cape && movingUp && vel.y > 5){
				acc.y = 0;
				vel.y = 5;
			}
			else if(!jumping && !muerte && !(cape && movingUp)){
				vel.y = 0;
				acc.y = 0;
			}
		}
		
		private void updatePosition(){
			updatePosLast();
			
			pos.setLocation(pos.x+vel.x*JGameMaker.time(),pos.y+vel.y*JGameMaker.time());

			if(!muerte && pos.y+JGameMaker.NIVEL_SUELO < 0){
				kill(true);
				//pos.y = pos.y + JGameMaker.NIVEL_SUELO + 1;
				falling = false;
			}else if(pos.y < 0 && !muerte && !piped && (!falling || invulerable)){
				setPos(pos.x,0);
				//yOffset = JGameMaker.NIVEL_SUELO;
			}
			
			if(pos.x > lastX + 20 || pos.x < lastX - 20){
				rightFoot = !rightFoot;
				lastX = pos.x;
			}
			if(piped && pos.y + height< lineToCross && vel.y < 0) changeWorlds = true;
			if(piped && pos.y > lineToCross && vel.y > 0){
				vel.y = 0;
				pos.y = lineToCross;
				piped = false;
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