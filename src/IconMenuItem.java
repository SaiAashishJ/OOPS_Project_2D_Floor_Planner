
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class IconMenuItem extends JMenuItem{
    IconMenuItem(JPanel pan,String[] s,String text){
        this.setText(text);
        
        this.addActionListener(new ActionListener() 
        {
                    public void actionPerformed(ActionEvent f) {

                        Icon ic = new Icon(s,pan);
                        pan.setLayout(null);
                    }
    });

    }
}
