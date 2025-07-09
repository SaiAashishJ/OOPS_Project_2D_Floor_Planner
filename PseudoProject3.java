import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;

public class PseudoProject3 implements MouseListener {

    public JFrame frame;
    public JPanel canvasPanel;
    public JPanel controlPanel;
    public ArrayList<JPanel> rooms = new ArrayList<JPanel>();
    public JButton newRoom;
    public JButton newDoor;
    public JButton newWindow;
    public JButton newFixture;
    public JButton newFurniture;
    public JButton NewPlan;
    public JButton Open;
    public JButton Save;
    public JLayeredPane floor;
    int roomNum = 0;
    int frameWidth;
    int frameHeight;
    int xCoord = 0;
    int yCoord = 0;
    int yCoordMax = 0;
    JPanel selectedRoom;
    Point initialClick;
    Point PrevCoord;
    int rounder = 10;

    public PseudoProject3() {

        frame = new JFrame("2D Floor Planner");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(null);

        frame.setVisible(true);

        frameHeight = frame.getHeight();
        frameWidth = frame.getWidth();

        Border border = BorderFactory.createLineBorder(Color.black, 3);

        // Maximum frame = ~1280x775

        controlPanel = new JPanel();
        canvasPanel = new JPanel();

        // Canvas Panel

        canvasPanel.setBounds(((frameWidth - 30) / 4) + 20, 10, (((frameWidth - 30) * 3) / 4), (frameHeight - 50));
        canvasPanel.setBackground(Color.lightGray);
        canvasPanel.setBorder(border);
        canvasPanel.addMouseListener(this);
        canvasPanel.setLayout(null);

        // Control Panel

        controlPanel.setBounds(10, 10, ((frameWidth - 30) / 4), (frameHeight - 50));
        controlPanel.setBackground(new Color(0xffdc44));
        controlPanel.setBorder(border);

        floor = new JLayeredPane();
        floor.setBorder(border);
        floor.setBounds(0, 0, (((frameWidth - 30) * 3) / 4), (frameHeight - 50));
        floor.setLayout(null);

        canvasPanel.add(floor);

        NewPlan = new JButton("New Plan");
        NewPlan.setFocusable(false);
        NewPlan.setFont(new Font("Serif", Font.PLAIN, 18));
        NewPlan.setBounds(80, 20, 150, 50);
        NewPlan.addActionListener(e -> NewPlanFile());

        Open = new JButton("Open");
        Open.setFocusable(false);
        Open.setFont(new Font("Serif", Font.PLAIN, 20));
        Open.setBounds(80, 90, 150, 50);
        Open.addActionListener(e -> OpenFile());

        Save = new JButton("Save");
        Save.setFocusable(false);
        Save.setFont(new Font("Serif", Font.PLAIN, 20));
        Save.setBounds(80, 160, 150, 50);
        Save.addActionListener(e -> SaveFile());

        controlPanel.add(NewPlan);
        controlPanel.add(Open);
        controlPanel.add(Save);

        newRoom = new JButton("New Room +");
        newRoom.setFont(new Font("Serif", Font.PLAIN, 20));
        newRoom.setFocusable(false);
        newRoom.setBounds(30, 230, 250, 50);
        controlPanel.add(newRoom);

        newRoom.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == newRoom) {

                    JDialog dialog = new JDialog(frame, "Room Type");

                    JTextField text1 = new JTextField("100");
                    JTextField text2 = new JTextField("100");

                    String roomName[] = { "Bedroom", "Bathroom", "Kitchen", "Dining Room", "Drawing Room" };

                    JComboBox roomType = new JComboBox(roomName);

                    roomType.setBounds(50, 25, 200, 35);

                    dialog.add(roomType);
                    text1.setBounds(110, 80, 100, 30);
                    text2.setBounds(110, 120, 100, 30);

                    JLabel l1 = new JLabel("Length");
                    JLabel l2 = new JLabel("Width");

                    l1.setBounds(50, 80, 100, 30);
                    l2.setBounds(50, 120, 100, 30);

                    JButton submit = new JButton("Submit");
                    submit.setBounds(120, 180, 80, 40);

                    submit.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            int room_Length = Integer.parseInt(text1.getText());
                            int room_Width = Integer.parseInt(text2.getText());
                            Addroom(room_Length, room_Width, roomType.getSelectedItem().toString());
                            dialog.dispose();
                        }
                    });

                    dialog.add(text1);
                    dialog.add(text2);
                    dialog.add(l1);
                    dialog.add(l2);
                    dialog.add(submit);

                    dialog.setSize(300, 300);
                    dialog.setLocation(200, 200);
                    dialog.setLayout(null);
                    dialog.setVisible(true);
                }
            }
        });

        controlPanel.setLayout(null);

        frame.add(controlPanel);
        frame.add(canvasPanel);

        frame.revalidate();
        frame.repaint();

        frame.setLayout(null);

        frame.setVisible(true);
    }

    public void OpenFile() {

        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            roomNum = 0;
            File file = fileChooser.getSelectedFile();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                ArrayList<JPanel> serializablePanels = (ArrayList<JPanel>) ois.readObject();
                rooms.clear();
                floor.removeAll();

                for (JPanel sp : serializablePanels) {
                    floor.add(sp);
                    rooms.add(sp);
                    roomNum++;
                }

                for (int i = 0; i < roomNum; i++) {

                    rooms.get(i).addMouseMotionListener(new MouseAdapter() {
                        @Override
                        public void mouseDragged(MouseEvent e) {
                            int thisX = ((JPanel) e.getSource()).getX();
                            int thisY = ((JPanel) e.getSource()).getY();

                            int xMoved = e.getX() - initialClick.x;
                            int yMoved = e.getY() - initialClick.y;

                            int newX = thisX + xMoved;
                            int newY = thisY + yMoved;

                            if (newX % (rounder / 2) <= rounder / 2) {
                                newX -= newX % rounder;
                            } else {
                                newX += rounder - newX % (rounder / 2);
                            }
                            if (newY % (rounder / 2) <= rounder / 2) {
                                newY -= newY % rounder;
                            } else {
                                newY += rounder - newY % (rounder / 2);
                            }

                            ((JPanel) e.getSource()).setLocation(newX, newY);
                        }
                    });

                    Component components[] = rooms.get(i).getComponents();
                    for (int q = 0; q < components.length; q++) {
                        final int j = i;
                        components[q].addMouseListener(new MouseListener() {
                            public void mousePressed(MouseEvent e) {
                                initialClick = e.getPoint();
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
                        components[q].addMouseMotionListener(new MouseAdapter() {
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

                                if (new01.x < 0 || new01.y < 0 || new10.x > rooms.get(j).getWidth()
                                        || rooms.get(j).getHeight() < new10.y)
                                    ;

                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                    }
                    rooms.get(i).addMouseListener(this);
                    rooms.get(i).setBorder(BorderFactory.createLineBorder(Color.black, 2));
                }

                floor.revalidate();
                floor.repaint();
                JOptionPane.showMessageDialog(frame, "Panels loaded successfully!");
            } catch (IOException | ClassNotFoundException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error loading panels!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void SaveFile() {

        JFileChooser fileChooser = new JFileChooser();
        int option = fileChooser.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(rooms);
                JOptionPane.showMessageDialog(frame, "Panels saved successfully!");
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Error saving panels!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void NewPlanFile() {

        int confirm = JOptionPane.showConfirmDialog(frame, "Do you want to save the current content?", "New File",
                JOptionPane.YES_NO_CANCEL_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SaveFile();
        }
        if (confirm == JOptionPane.YES_OPTION || confirm == JOptionPane.NO_OPTION) {
            roomNum = 0;
            rooms.clear();
            floor.removeAll();

            floor.revalidate();
            floor.repaint();

            xCoord = 0;
            yCoord = 0;
            yCoordMax = 0;
        }
    }

    public static void main(String[] args) {
        PseudoProject3 p1 = new PseudoProject3();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        if (e.getSource() == canvasPanel) {
        for (int i = 0; i < roomNum; i++) {
        rooms.get(i).setBorder(BorderFactory.createLineBorder(Color.black, 2));
        }
        return;
        }

        JPanel temppanel = (JPanel) e.getSource();

        for (int i = 0; i < roomNum; i++) {
        rooms.get(i).setBorder(BorderFactory.createLineBorder(Color.black, 2));
        }

        temppanel.setBorder(BorderFactory.createLineBorder(new Color(0xADD8E6), 2));
    }

    @Override
    public void mousePressed(MouseEvent e) {

        PrevCoord = ((JPanel) e.getSource()).getLocation();
        int width = ((JPanel) e.getSource()).getWidth();
        int length = ((JPanel) e.getSource()).getHeight();
        Color color = ((JPanel) e.getSource()).getBackground();
        initialClick = e.getPoint();

        if (SwingUtilities.isRightMouseButton(e)) {
            if (rooms.contains(e.getSource())) {
                JPanel rightPanel = (JPanel) e.getSource();
                final JPopupMenu popupmenu = new JPopupMenu("Edit");
                JMenuItem Delete = new JMenuItem("Delete");
                JMenu Furniture = new JMenu("Furnitur");
                JMenu Door = new JMenu("Door");
                JMenu Window = new JMenu("Window");

                JMenuItem Top = new JMenuItem("Top");
                Door.add(Top);
                JMenuItem Bottom = new JMenuItem("Bottom");
                Door.add(Bottom);
                JMenuItem Left = new JMenuItem("Left");
                Door.add(Left);
                JMenuItem Right = new JMenuItem("Right");
                Door.add(Right);
                
                String[] Beds = {"Bed1.png","Bed2.png","Bed3.png","Bed4.png"};
                IconMenuItem Bed = new IconMenuItem(rightPanel,Beds,"Bed");
                Furniture.add(Bed);
                String[] Chairs = {"Chair1.png","Chair2.png","Chair3.png","Chair4.png"};
                IconMenuItem Chair = new IconMenuItem(rightPanel,Chairs,"Chair");
                Furniture.add(Chair);
                String[] Tables = {"Table1.png","Table2.png","Table3.png","Table4.png"};
                IconMenuItem Table = new IconMenuItem(rightPanel,Tables,"Table");
                Furniture.add(Table);
                String[] Sofas = {"Sofa1.png","Sofa2.png","Sofa3.png","Sofa4.png"};
                IconMenuItem Sofa = new IconMenuItem(rightPanel,Sofas,"Sofa");
                Furniture.add(Sofa);
                String[] Diningsets = {"Diningset1.png","Diningset2.png","Diningset3.png","Diningset4.png"};
                IconMenuItem Diningset = new IconMenuItem(rightPanel,Diningsets,"Diningset");
                Furniture.add(Diningset);

                JMenu Fixture = new JMenu("Fixtures");
                String[] Stoves = {"Stove1.png","Stove2.png","Stove3.png","Stove4.png"};
                IconMenuItem Stove = new IconMenuItem(rightPanel,Stoves,"Stove");
                Fixture.add(Stove);
                String[] Kitsinks = {"Kitsink1.png","Kitsink2.png","Kitsink3.png","Kitsink4.png"};
                IconMenuItem Kitsink = new IconMenuItem(rightPanel,Kitsinks,"Kitsink");
                Fixture.add(Kitsink);
                String[] Showers = {"Shower1.png","Shower2.png","Shower3.png","Shower4.png"};
                IconMenuItem Shower = new IconMenuItem(rightPanel,Showers,"Shower");
                Fixture.add(Shower);
                String[] Washbasins = {"Washbasin1.png","Washbasin2.png","Washbasin3.png","Washbasin4.png"};
                IconMenuItem Washbasin = new IconMenuItem(rightPanel,Washbasins,"Wash");
                Fixture.add(Washbasin);
                String[] Commodes = {"Commode1.png","Commode2.png","Commode3.png","Commode4.png"};
                IconMenuItem Commode = new IconMenuItem(rightPanel,Commodes,"Commode");
                Fixture.add(Commode);

                popupmenu.add(Delete);
                popupmenu.add(Door);

                if (rightPanel.getBackground().equals(new Color(0x0050FF))|| rightPanel.getBackground().equals(new Color(0xFF1515))) {
                    popupmenu.add(Fixture);
                } 
                else {
                    popupmenu.add(Furniture);
                }
                popupmenu.show((JPanel) e.getSource(), e.getX(), e.getY());

                Delete.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        rooms.remove(rightPanel);
                        roomNum--;
                        floor.remove(rightPanel);
                        floor.revalidate();
                        floor.repaint();
            }
        });

        Top.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent f) {

                JPanel door = new JPanel();
                door.setBounds(0, 0, 30, 4);
                door.setBackground(color);

                door.addMouseListener(new MouseListener() {
                    public void mousePressed(MouseEvent e) {
                        initialClick = e.getPoint();
                    }
                    @Override public void mouseClicked(MouseEvent e) {}@Override public void mouseReleased(MouseEvent e) {}@Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
                });

                door.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {

                        int thisX = ((JPanel) e.getSource()).getX();
                        int thisY = ((JPanel) e.getSource()).getY();

                        int xMoved = e.getX() - initialClick.x;

                        int newX = thisX + xMoved;

                        Point new01 = new Point(newX, thisY);
                        Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(),
                                new01.y + ((JPanel) e.getSource()).getHeight());

                        if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth()
                                || rightPanel.getHeight() < new10.y)
                            ;
                        else {
                            ((JPanel) e.getSource()).setLocation(newX, thisY);
                        }
                    }
                });
                rightPanel.setLayout(null);
                rightPanel.add(door);
                rightPanel.revalidate();
                rightPanel.repaint();
            }
        });

        Bottom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent f) {

                JPanel door = new JPanel();
                door.setBounds(0, length - 4, 30, 4);
                door.setBackground(color);

                door.addMouseListener(new MouseListener() {
                    public void mousePressed(MouseEvent e) {
                        initialClick = e.getPoint();
                    }

                    @Override public void mouseClicked(MouseEvent e) {}@Override public void mouseReleased(MouseEvent e) {}@Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
                });

                door.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {

                        int thisX = ((JPanel) e.getSource()).getX();
                        int thisY = ((JPanel) e.getSource()).getY();

                        int xMoved = e.getX() - initialClick.x;

                        int newX = thisX + xMoved;

                        Point new01 = new Point(newX, thisY);
                        Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(),
                                new01.y + ((JPanel) e.getSource()).getHeight());

                        if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth()
                                || rightPanel.getHeight() < new10.y)
                            ;
                        else {
                            ((JPanel) e.getSource()).setLocation(newX, thisY);
                        }
                    }
                });
                rightPanel.setLayout(null);
                rightPanel.add(door);
                rightPanel.revalidate();
                rightPanel.repaint();
            }
        });

        Left.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent f) {

                JPanel door = new JPanel();
                door.setBounds(0, 0, 4, 30);
                door.setBackground(color);

                door.addMouseListener(new MouseListener() {
                    public void mousePressed(MouseEvent e) {
                        initialClick = e.getPoint();
                    }

                    @Override public void mouseClicked(MouseEvent e) {}@Override public void mouseReleased(MouseEvent e) {}@Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
                });

                door.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {

                        int thisX = ((JPanel) e.getSource()).getX();
                        int thisY = ((JPanel) e.getSource()).getY();

                        int yMoved = e.getY() - initialClick.y;

                        int newY = thisY + yMoved;

                        Point new01 = new Point(thisX, newY);
                        Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(),
                                new01.y + ((JPanel) e.getSource()).getHeight());

                        if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth()
                                || rightPanel.getHeight() < new10.y)
                            ;
                        else {
                            ((JPanel) e.getSource()).setLocation(thisX, newY);
                        }
                    }
                });
                rightPanel.setLayout(null);
                rightPanel.add(door);
                rightPanel.revalidate();
                rightPanel.repaint();
            }
        });

        Right.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent f) {

                JPanel door = new JPanel();
                door.setBounds(width - 4, 0, 4, 30);
                door.setBackground(color);

                door.addMouseListener(new MouseListener() {
                    public void mousePressed(MouseEvent e) {
                        initialClick = e.getPoint();
                    }

                    @Override public void mouseClicked(MouseEvent e) {}@Override public void mouseReleased(MouseEvent e) {}@Override public void mouseEntered(MouseEvent e) {} @Override public void mouseExited(MouseEvent e) {}
                });

                door.addMouseMotionListener(new MouseAdapter() {
                    @Override
                    public void mouseDragged(MouseEvent e) {

                        int thisX = ((JPanel) e.getSource()).getX();
                        int thisY = ((JPanel) e.getSource()).getY();

                        int yMoved = e.getY() - initialClick.y;

                        int newY = thisY + yMoved;

                        Point new01 = new Point(thisX, newY);
                        Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(),
                                new01.y + ((JPanel) e.getSource()).getHeight());

                        if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth()
                                || rightPanel.getHeight() < new10.y)
                            ;
                        else {
                            ((JPanel) e.getSource()).setLocation(thisX, newY);
                        }
                    }
                });
                rightPanel.setLayout(null);
                rightPanel.add(door);
                rightPanel.revalidate();
                rightPanel.repaint();
            }
        });


    }
}
}

    @Override
    public void mouseReleased(MouseEvent e) {

        if (!OverlapCheck((JPanel) e.getSource())) {
            JOptionPane.showMessageDialog(frame, "Room can't be placed", "Overlapping", JOptionPane.ERROR_MESSAGE);
            ((JPanel) e.getSource()).setLocation(PrevCoord);
        }

        if (!BoundaryCheck((JPanel) e.getSource())) {
            JOptionPane.showMessageDialog(frame, "Room can't be placed", "Room Out Of Bound",
                    JOptionPane.ERROR_MESSAGE);
            ((JPanel) e.getSource()).setLocation(PrevCoord);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    public void Addroom(int l, int b, String roomType) {
        // if (xCoord + b > floor.getWidth()) {
        // yCoord = yCoordMax;
        // yCoordMax = yCoordMax + l;
        // xCoord = 0;
        // }
        xCoord = 0;
        yCoord = 0;
        rooms.add(new JPanel());
        rooms.get(roomNum).setBounds(xCoord, yCoord, b, l);

        while (true) {
            if (!OverlapCheck(rooms.get(roomNum))) {
                if (xCoord + b > floor.getWidth()) {
                    yCoord = yCoord + 100;
                    xCoord = 0;
                }
                rooms.get(roomNum).setBounds(xCoord, yCoord, b, l);
                xCoord = xCoord + 10;
            } else {
                break;
            }
        }
        rooms.get(roomNum).addMouseListener(this);
        rooms.get(roomNum).setVisible(true);

        rooms.get(roomNum).addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                int thisX = ((JPanel) e.getSource()).getX();
                int thisY = ((JPanel) e.getSource()).getY();

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                int newX = thisX + xMoved;
                int newY = thisY + yMoved;

                if (newX % (rounder / 2) <= rounder / 2) {
                    newX -= newX % rounder;
                } else {
                    newX += rounder - newX % (rounder / 2);
                }

                if (newY % (rounder / 2) <= rounder / 2) {
                    newY -= newY % rounder;
                } else {
                    newY += rounder - newY % (rounder / 2);
                }

                ((JPanel) e.getSource()).setLocation(newX, newY);
            }
        });

        yCoordMax = Math.max(yCoordMax, l);
        // yCoordMax++;

        switch (roomType) {
            case "Bedroom":
                rooms.get(roomNum).setBackground(new Color(0x99DD5C)); // Green
                break;
            case "Bathroom":
                rooms.get(roomNum).setBackground(new Color(0x0050FF)); // Blue
                break;
            case "Kitchen":
                rooms.get(roomNum).setBackground(new Color(0xFF1515)); // Red
                break;
            case "Dining Room":
                rooms.get(roomNum).setBackground(new Color(0xFF7700)); // Orange
                break;
            case "Drawing Room":
                rooms.get(roomNum).setBackground(Color.YELLOW); // Yellow
                break;
        }

        rooms.get(roomNum).setBorder(BorderFactory.createLineBorder(Color.black, 2));
        floor.add(rooms.get(roomNum), Integer.valueOf(2));
        roomNum++;

        System.out.println(roomType + " - " + l + " " + b);

        floor.revalidate();
        floor.repaint();
    }

    public boolean OverlapCheck(JPanel p) {
        if (!p.equals(canvasPanel)) {
            Point fir01 = p.getLocation();
            Point fir10 = new Point(fir01.x + p.getWidth(), fir01.y + p.getHeight());

            for (int i = 0; i < roomNum; i++) {
                if (rooms.get(i) == p)
                    continue;
                else {
                    Point sec01 = rooms.get(i).getLocation();
                    Point sec10 = new Point(sec01.x + rooms.get(i).getWidth(), sec01.y + rooms.get(i).getHeight());

                    if (fir01.x >= sec10.x || sec01.x >= fir10.x || fir01.y >= sec10.y || sec01.y >= fir10.y)
                        ;
                    else
                        return false;
                }
            }
        }
        return true;
    }

    public boolean BoundaryCheck(JPanel p) {

        if (!p.equals(canvasPanel)) {
            Point new01 = p.getLocation();
            Point new10 = new Point(new01.x + p.getWidth(), new01.y + p.getHeight());

            if (new01.x < 0 || new01.y < 0 || new10.x > floor.getWidth() || floor.getHeight() < new10.y)
                return false;
        }
        return true;
    }
}