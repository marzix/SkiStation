/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controller;

import View.Help;
import java.awt.Label;
import java.io.File;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JWindow;

/**
 *
 * @author Rafał
 */
public class HelpWindow {
    
    public static String HELP_INDEX = "/help/";
    
    public HelpWindow(String fileName)
    {
    Help frame = new Help();
    
    URL url = this.getClass().getResource(HELP_INDEX + fileName + ".html");
    
    frame.SetContent(url);
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    frame.getContentPane().add(new Label());

    frame.pack();

    frame.setVisible(true);
    }
}
