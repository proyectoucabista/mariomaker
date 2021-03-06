import java.awt.image.BufferedImage;

/**
 * removes objects from the juego
 * @author Reed Weichler
 *
 */
public class TRemover extends TTool {
	private Sprite preview = new Sprite("Imagenes/sprites/tools/borrador.png");
	public TRemover(){
		super();
	}
	public void enContacto(Thing t){
		t.matar();
		matar();
	}
	public BufferedImage preview(){
		return preview.getBuffer();
	}
}
