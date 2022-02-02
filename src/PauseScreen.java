


import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;


/**
 * Represents the screen that comes up when there is a current juego and is pausado.
 * @author Reed Weichler
 *
 */
public class PauseScreen extends Pantalla {
	
	private static final int TOP = 150,ESPACIADO = 5;
	
	private TextButton continuar,menuPrincipal,guardar,salir;
	
	private TextButton estasSeguro,datosNoGuardados,aceptar,cancelar;
	
	private boolean menuPrincipalSeleccionado,quitSelected,puedeGuardar;
	
	public PauseScreen(boolean puedeGuardar){
		this.puedeGuardar = puedeGuardar;
		menuPrincipalSeleccionado = false;
		
		continuar = new TextButton("Continuar", JGameMaker.FONT_GRANDE);
		menuPrincipal = new TextButton("Menu Principal", JGameMaker.FONT_GRANDE);
		salir = new TextButton("Salir", JGameMaker.FONT_GRANDE);
		estasSeguro = new TextButton("Estas Seguro?", JGameMaker.FONT_GRANDE, TextButton.TITULO,TextButton.TITULO);
		int textheight = continuar.getHeight();
		if(puedeGuardar){
			datosNoGuardados = new TextButton("Los datos no guardados. (se perderan)", JGameMaker.FONT_MEDIO, TextButton.TITULO,TextButton.TITULO);
			guardar = new TextButton("Guardar nivel", JGameMaker.FONT_GRANDE);
		}else{
			datosNoGuardados = new TextButton("TODOS LOS PROGRESOS SE PERDERAN", JGameMaker.FONT_MEDIO, TextButton.TITULO,TextButton.TITULO);
			guardar = new TextButton("REINICIAR NIVEL", JGameMaker.FONT_GRANDE);
		}
		aceptar = new TextButton("Aceptar", JGameMaker.FONT_GRANDE);
		cancelar = new TextButton("Cancelar", JGameMaker.FONT_GRANDE);
		
		continuar.setPos((JGameMaker.screenWidth - continuar.getWidth())/2,		TOP);
		menuPrincipal.setPos((JGameMaker.screenWidth - menuPrincipal.getWidth())/2,	TOP + (ESPACIADO + textheight)*1);
		salir.setPos((JGameMaker.screenWidth - salir.getWidth())/2,TOP + (ESPACIADO + textheight)*5);
		guardar.setPos((JGameMaker.screenWidth - guardar.getWidth())/2,TOP + (ESPACIADO + textheight)*3);
		datosNoGuardados.setPos((JGameMaker.screenWidth - datosNoGuardados.getWidth())/2,	TOP + (ESPACIADO + textheight)*2);

		estasSeguro.setPos((JGameMaker.screenWidth - estasSeguro.getWidth())/2,	TOP + (ESPACIADO + textheight)*1);
		aceptar.setPos(JGameMaker.screenWidth/2 - aceptar.getWidth() - 20,TOP + (ESPACIADO + textheight)*3);
		cancelar.setPos(JGameMaker.screenWidth/2 + 20,TOP + (ESPACIADO + textheight)*3);
	}
	
	public void draw(Graphics g) {
		g.setColor(new Color(0,0,0,150));
		g.fillRect(0, 0, JGameMaker.screenWidth, JGameMaker.screenHeight);
		if(!(menuPrincipalSeleccionado || quitSelected)){
			menuPrincipal.draw(g);
			continuar.draw(g);
			guardar.draw(g);
			salir.draw(g);
		}else{
			estasSeguro.draw(g);
			datosNoGuardados.draw(g);
			aceptar.draw(g);
			cancelar.draw(g);
		}
	}

	@Override
	public void key(KeyEvent e, boolean down) {
		if(e.getKeyCode() == KeyEvent.VK_ESCAPE && down){
			controller.pause(false);
			menuPrincipalSeleccionado = false;
		}

	}

	@Override
	public void mouse(MouseEvent e, boolean down) {
		if(down)return;
		Point mouse = e.getPoint();
		if(menuPrincipalSeleccionado){
			if(aceptar.contains(mouse)){
                                AePlayWave.fondoMusica.finalizarMusica();// quitar musica del juego, si se sale al menu principal
				controller.menuPrincipal();
			}else if(cancelar.contains(mouse)){
				menuPrincipalSeleccionado = false;
			}
		}else if(quitSelected){
			if(aceptar.contains(mouse)){
				System.exit(0);
			}else if(cancelar.contains(mouse)){
				quitSelected = false;
			}
		}else{
			if(continuar.contains(mouse)){
				controller.pause(false);
			}else if(menuPrincipal.contains(mouse)){
				menuPrincipalSeleccionado = true;
			}else if(salir.contains(mouse)){
				quitSelected = true;
			}else if(guardar.contains(mouse)){
				if(puedeGuardar)
					controller.guardarJuego();
				else
					controller.reiniciarUnJugador();
			}
		}
	}

	public void think() {

	}

}
