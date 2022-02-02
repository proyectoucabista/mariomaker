import java.awt.Color;

/**
 * Represents a bound that can't be passed BY ANYTHING!!!
 * @author Reed Weichler
 *
 */
public abstract class TUnido extends TVisibleTool {

	private static final Color COLOR = new Color(255,140,0,180);
	public TUnido(){
		this(COLOR);
	}
	public TUnido(Color color){
		super(32,32,color);
	}
}
