


import java.awt.*;
import java.awt.geom.*;

/**
 * Helper class used to draw Strings to the screen.
 * @author Reed Weichler
 *
 */
public class TextoBoton {
	private String str;
	private Font font;
	private Rectangle limites;
	private Color highlight, color;
	
	/**
	 * default colors
	 */
	public static final Color	TITULO = Color.RED,TEXT = Color.WHITE,HIGHLIGHT = new Color(180,180,180);
	
	/**
	 * Creates a new TextoBoton with the specified attributes
	 * @param str text to be displayed
	 * @param font font of text
	 * @param x the very left part of the text
	 * @param y the top part of the text
	 * @param color default color
	 * @param highlight color when the mouse hovers over
	 */
	public TextoBoton(String str, Font font, int x, int y, Color color, Color highlight){
		this.str = str;
		this.color = color;
		this.highlight = highlight;
		this.font = font;
		Rectangle limitesCadena = limitesCadena(str,font);
		limites = new Rectangle(x,y,limitesCadena.width, limitesCadena.height);
	}
	public TextoBoton(String str, Font font, int x, int y){
		this(str,font);
		setPos(x,y);
	}
	
	public TextoBoton(String str, Font font, int x, int y, Color color){
		this(str,font,x,y,color,color);
	}

	public TextoBoton(String str, Font font, Color color, Color highlight){
		this(str,font,0,0,color,highlight);
	}
	public TextoBoton(String str, Font font, Color color){
		this(str,font,color,color);
	}
	public TextoBoton(String str, Font font){
		this(str,font,TEXT,HIGHLIGHT);
	}
	
	/**
	 * determines if p is within the limites of this
	 * @return true if p is within the limites of this, false if not
	 */
	public boolean contains(Point p){
		return limites.contains(p);
	}
	/**
	 * determines if a point at coordinates (x,y) is within the limites of this
	 * @return true if (x,y) is within the limites of this, false if not
	 */
	public boolean contains(int x, int y){
		return limites.contains(x,y);
	}
	public int getWidth(){
		return limites.width;
	}
	public int getHeight(){
		return limites.height;
	}
	public void setPos(Point p){
		limites.setLocation(p);
	}
	public void setPos(int x, int y){
		limites.setLocation(x,y);
	}
	
	public void setText(String str){
		this.str = str;
		Rectangle limitesCadena = limitesCadena(str,font);
		limites = new Rectangle(limites.x,limites.y,limitesCadena.width, limitesCadena.height);
		
	}
	
	//codigo complicado
	private Rectangle limitesCadena(String str, Font font){
		Graphics g = (new Sprite(1,1)).getBuffer().getGraphics();
		Rectangle2D limitesCadena = g.getFontMetrics(font).getStringBounds(str,g);
		return new Rectangle(0,0,(int)(limitesCadena.getWidth() + 0.5),(int)((limitesCadena.getHeight() + 0.5)/2));
	}
	
	/**
	 * draws this to g 
	 * @param mouse coordinates of the mouse
	 */
	public void draw(Graphics g, Point mouse){
		g.setFont(font);
		if(contains(mouse)){
			g.setColor(highlight);
		}else{
			g.setColor(color);
		}
		g.drawString(str, limites.x, limites.y + limites.height);
	}
	/**
	 * draws this to g, mouse coordinates are determined from ScreenPanel.mouse
	 */
	public void draw(Graphics g){
		draw(g,ScreenManager.mouse);
	}
	
}
