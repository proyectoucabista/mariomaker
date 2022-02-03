
import java.awt.Rectangle;

/**
 * Represents a TBlock that can be walked through
 * @author Reed Weichler
 *
 */
public class TBGBloque extends TBlock {
	public TBGBloque(){
		super();
	}
	public TBGBloque(byte img) {
		super(img);
	}
	public TBGBloque(double x, double y, byte img, TItem item, boolean movesWhenHit){
		super(x,y,img,item,movesWhenHit);
	}
	public TBGBloque(double x, double y, byte img){
		super(x,y,img);
	}
	
	public TBGBloque(double x, double y, byte img, TItem item){
		super(x,y,img,item);
	}
	public void enContacto(Thing t){
		return;
	}
}
