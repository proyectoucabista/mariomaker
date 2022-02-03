import java.awt.Color;

/**
 * Represents a bound that can't be passed BY ANYTHING!!!
 * @author Reed Weichler
 *
 */
public abstract class TLimite extends TVisibleTool {

	private static final Color COLOR = new Color(255,140,0,180);
	public TLimite(){
		this(COLOR);
	}
	public TLimite(Color color){
		super(32,32,color);
	}
}
