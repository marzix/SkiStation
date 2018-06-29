/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import View.*;

/**
 * Klasa obsługująca zdarzenia po naciśnięciu klawisza Enter dla danego komponentu
 * @author Marzena
 */
public class KeyboardEventHandler implements KeyListener {
    
    /**
     * obiekt przechowujący główne okno aplikacji w celu umożliwienia korzystania z jego metod
     */
    static MainWindow mainWindow;
    
    /**
     * Domyslny konstruktor, zachowuje obiekt głównego okna aplikacji
     * @param mw - obiekt głównego okna aplikacji
     */
    public KeyboardEventHandler( MainWindow mw ){
        mainWindow = mw;
    }
    
    @Override
    public void keyTyped(KeyEvent e) {
    char c = e.getKeyChar();
  }

  /** 
   * Wywołuje zdarzenia domyślne dla naciśnięcia klawisza Enter gdy dany komponent jest zaznaczony
   * @param e zdarzenie naciśnięcia klawisza
   */
    @Override
  public void keyPressed(KeyEvent e) {
    if( e.getKeyCode() != KeyEvent.VK_ENTER )
        return;
    
    String componentName = e.getComponent().getName();
    
    switch( componentName )
    {
        case "LoginPanel":
            mainWindow.PerformLoginAction();
            break;
        case "RegisterButton":
            mainWindow.register();
            break;
        case "BackButton":
            mainWindow.PreviewView();
            break;
        case "CashierPanel":
            break;
    }    
  }

    @Override
  public void keyReleased(KeyEvent e) {
    char c = e.getKeyChar();
  }
}
