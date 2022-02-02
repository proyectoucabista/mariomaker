



import javax.swing.*;
import java.awt.*;
/**
 * Stores juego constants and initializes the juego
 * @author Reed Weichler
 *
 */
public class JGameMaker extends JPanel{
	
	
	public static JPanel panel;
	
	private static final long serialVersionUID = 8930735783398997076L;
	private static long ultimoTiempo;
	private static long tiempoPasado;
	private static final long FRECUENCIA_ACTUALIZACION = 15000000;
	public static final int NIVEL_SUELO = 50,TIEMPO_SALTO_COMPLETO = 20;

	public static final Font	FONT_ENORME = new Font("Arial Black", Font.PLAIN, 80),
								FONT_GRANDE = new Font("Arial Black", Font.PLAIN, 40),
								FONT_MEDIO = new Font("Tahoma", Font.PLAIN, 20);
	public static int screenWidth = 800,screenHeight = 600;
	
	public static final double GRAVEDAD = 0.701;
	
	private ScreenManager manager;
	
	/**
	 * returns the amount of time that has passed since the last screen refresh
	 * @return the amount of time that has passed since the last screen refresh
	 */
	public static double time(){
		double d = 1.0*tiempoPasado/FRECUENCIA_ACTUALIZACION;
		//if we lagged too much then pretend no time has passed
		if(d > 10){
			return 0;
		}
		return d;
	}
	
	public static void actualizarTiempo(){
		tiempoPasado = System.nanoTime() - ultimoTiempo;
		ultimoTiempo = System.nanoTime();
	}
	
	/**
	 * used for in-juego objects to round for drawing to the screen (x coordinate)
	 * @param x
	 * @return
	 */
	public static int scaleW(double x){
		return (int)(x + 0.5);
	}
	/**
	 * used for in-juego objects to round for drawing to the screen (y coordinate)
	 * @param y
	 * @return
	 */
	public static int scaleH(double y){
		return (int)(y + 0.5);
	}
	
	public void paintComponent(Graphics g){
		if(!manager.didThink()) return;
		super.paintComponent(g);
		manager.draw(g);
		g.setColor(Color.YELLOW);
		g.setFont(FONT_GRANDE);
		/*if(tiempoPasado == 0) return;
		System.out.println(tiempoPasado);
		int x = (int)(1000000000/tiempoPasado);
		g.drawString("FPS: " + x, JGameMaker.W - 200, JGameMaker.H - 50);*/
	}
	
	/**
	 * makes the juego go full screen
	 * @param w JFrame to be made full screen
	 */
	public void colocarPantallaCompleta(JFrame w){
		w.setUndecorated(true);
		Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
		screenWidth = screensize.width;
		screenHeight = screensize.height;
	}
	
	/**
	 * creates the entire juego
	 */
	public JGameMaker(){
		//(new AePlayWaveLoop("Sonidos/eyeoftiger/eyeoftigere.wav")).start();
		setBackground(Color.BLACK);
		manager = new ScreenManager(new FileOpener(this));
		this.setFocusable(true);
		this.addMouseMotionListener(manager);
		this.addMouseListener(manager);
		this.addKeyListener(manager);
		JFrame w = new JFrame("JGameMaker");
		colocarPantallaCompleta(w);
		w.setBounds(0, 0, screenWidth, screenHeight);
		w.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		w.getContentPane().add(this);
		w.setVisible(true);
		w.setResizable(false);
		
		ultimoTiempo = System.nanoTime() - 10000000;
		
		//Serializer s = new Serializer();
		//s.ints.add(10);
		//System.out.println(s);
		
		//FileOpener opener = new FileOpener(w);
		//opener.saveFile("heh");
		while(true){
			actualizarTiempo();
			//try {
			manager.think();
			//} catch (Exception e) {
				//System.out.println("There's been an exception while trying to call think(): " + e.getMessage());e.printStackTrace();
			//}
		
			if(tiempoPasado/FRECUENCIA_ACTUALIZACION > 1){
				//do not sleep as we are late rendering
			}else{
				try { 
					Thread.sleep(5);
				} catch (Exception e) {
					
				}
			}
			repaint();
			
		}
		
	}
	
	public static void main(String[] args){
		panel = new JGameMaker();
    }
    
}