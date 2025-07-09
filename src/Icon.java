import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class Icon extends JLabel{
    Point initialClick;
    public int rotateIndex = 0;
    public int x = 0;
    public int y = 0;
    public ImageIcon ico;
    public String[] dir;
    Icon(String[] s,JPanel pan){
        this.dir = s;
        ico = new ImageIcon(dir[0]);
        this.setIc(0,pan);
        this.addMouseListener(new MouseListener() {
                            public void mousePressed(MouseEvent e) {
                                initialClick = e.getPoint();
                                if (SwingUtilities.isRightMouseButton(e)) {
                                    final JPopupMenu popupmenu = new JPopupMenu("Edit");
                                    JMenuItem Delete = new JMenuItem("Delete");
                                    JMenuItem Rotate90 = new JMenuItem("Rotate");
                                    popupmenu.add(Delete);
                                    popupmenu.add(Rotate90);
                                    popupmenu.show((JLabel)e.getSource(), e.getX(), e.getY());
                                    Delete.addActionListener(new ActionListener() {
                                        public void actionPerformed(ActionEvent e) {
                                            remIc(pan);
                                            pan.revalidate();
                                            pan.repaint();
                                        }
                                    });
                                    Rotate90.addActionListener(new ActionListener() {
                                            public void actionPerformed(ActionEvent e) {
                                                remIc(pan);
                                                setIc(1,pan);

                                            }
                                        
                                    });
                                }
                                
                            }

                            @Override
                            public void mouseClicked(MouseEvent e) {
                            }

                            @Override
                            public void mouseReleased(MouseEvent e) {
                            }

                            @Override
                            public void mouseEntered(MouseEvent e) {
                            }

                            @Override
                            public void mouseExited(MouseEvent e) {
                            }
                        });

                        this.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(),
                                        new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > pan.getWidth()
                                        || pan.getHeight() < new10.y)
                                    ;

                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                                x = getX();
                                y = getY();
                            }
                        });

    }
    void setIc(int rotating,JPanel pan){
        rotateIndex += rotating;
        ico = new ImageIcon(dir[rotateIndex%4]);
        this.setIcon(ico);
        this.setBounds(x, y, ico.getIconWidth(), ico.getIconHeight());
        pan.add(this);
        pan.revalidate();
        pan.repaint();
    }
    void remIc(JPanel pan){
        pan.remove(this);
    }
    

}
