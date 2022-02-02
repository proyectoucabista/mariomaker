
import java.awt.Rectangle;

/**
 * Representa un TBloque que se puede recorrer
 * @author Reed Weichler
 *
 */
public class TBGBloque extends TBloque {
	public TBGBloque(){
		super();
	}
	public TBGBloque(byte img) {
		super(img);
	}
	public TBGBloque(double x, double y, byte img, TItem item, boolean seMueveCuandoGolpea){
		super(x,y,img,item,seMueveCuandoGolpea);
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
