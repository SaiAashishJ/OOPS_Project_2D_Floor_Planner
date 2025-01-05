import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

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
        frame.getContentPane().setBackground(new Color(0xec80fe)); // 0xec80fe
        // frame.setBackground(Color.black);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setLayout(null);

        frame.setVisible(true);

        frameHeight = frame.getHeight();
        frameWidth = frame.getWidth();

        Border border = BorderFactory.createLineBorder(new Color(0x431e7e), 3); // 0x431e7e

        // Maximum frame = ~1280x775

        controlPanel = new JPanel();
        canvasPanel = new JPanel();

        canvasPanel.setBounds(((frameWidth - 30) / 4) + 20, 10, (((frameWidth - 30) * 3) / 4), (frameHeight - 50));
        // canvasPanel.setBounds(333, 10, 935, 725);
        canvasPanel.setBackground(new Color(0xc199fd)); // 0xc199fd
        canvasPanel.setBorder(border);
        canvasPanel.addMouseListener(this);
        canvasPanel.setLayout(null);

        controlPanel.setBounds(10, 10, ((frameWidth - 30) / 4), (frameHeight - 50));
        // controlPanel.setBounds(10, 10, 313, 725);
        controlPanel.setBackground(new Color(0xaa4df1)); // 0x8f39ec // 0xaa4df1
        controlPanel.setBorder(border);

        floor = new JLayeredPane();
        floor.setBorder(border);
        floor.setBounds(0, 0, (((frameWidth - 30) * 3) / 4), (frameHeight - 50));
        floor.setLayout(null);

        canvasPanel.add(floor);

        NewPlan = new JButton("New Plan");
        NewPlan.setFocusable(false);
        NewPlan.setFont(new Font("Serif", Font.PLAIN, 19));
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

        newRoom = new JButton("New Room                     +");
        newRoom.setFont(new Font("Serif", Font.PLAIN, 20));
        newRoom.setFocusable(false);
        newRoom.setBackground(new Color(0x007BFF));
        newRoom.setForeground(new Color(0,0,0));
        newRoom.setBounds(30, 230, 250, 50);

        newRoom.repaint();
        newRoom.revalidate();
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
                newRoom.revalidate();
                newRoom.repaint();
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
            // ArrayList<SerializablePanel> serializablePanels = new ArrayList<>();
            // for (JPanel panel : rooms) {
            // SerializablePanel sp = new SerializablePanel(
            // panel.getSize(),
            // panel.getLocation(),
            // panel.getBackground()
            // );
            // serializablePanels.add(sp);
            // }

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

                JMenu Furniture = new JMenu("Furniture");
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

                JMenuItem North = new JMenuItem("Top");
                Window.add(North);
                JMenuItem South = new JMenuItem("Bottom");
                Window.add(South);
                JMenuItem West = new JMenuItem("Left");
                Window.add(West);
                JMenuItem East = new JMenuItem("Right");
                Window.add(East);

                JMenuItem Bed = new JMenuItem("Bed");
                Furniture.add(Bed);
                JMenuItem Chair = new JMenuItem("Chair");
                Furniture.add(Chair);
                JMenuItem Table = new JMenuItem("Table");
                Furniture.add(Table);
                JMenuItem Sofa = new JMenuItem("Sofa");
                Furniture.add(Sofa);
                JMenuItem Diningset = new JMenuItem("Dining Set");
                Furniture.add(Diningset);

                JMenu Fixture = new JMenu("Fixtures");
                JMenuItem Stove = new JMenuItem("Stove");
                Fixture.add(Stove);
                JMenuItem Kitsink = new JMenuItem("Kitchen sink");
                Fixture.add(Kitsink);
                JMenuItem Shower = new JMenuItem("Shower");
                Fixture.add(Shower);
                JMenuItem Wash = new JMenuItem("Washbasin");
                Fixture.add(Wash);
                JMenuItem Commode = new JMenuItem("Commode");
                Fixture.add(Commode);

                popupmenu.add(Delete);
                popupmenu.add(Door);
                popupmenu.add(Window);

                if (rightPanel.getBackground().equals(new Color(0x0050FF)) || rightPanel.getBackground().equals(new Color(0xFF1515))) {
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

                // Doors
                Top.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        JPanel door = new JPanel();
                        door.setBounds(0, 0, 30, 4);
                        door.setBackground(color);

                        door.addMouseListener(new MouseListener() {
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

                        door.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JPanel) e.getSource()).getX();
                                int thisY = ((JPanel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;

                                int newX = thisX + xMoved;

                                Point new01 = new Point(newX, thisY);
                                Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(), new01.y + ((JPanel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
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

                        door.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JPanel) e.getSource()).getX();
                                int thisY = ((JPanel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;

                                int newX = thisX + xMoved;

                                Point new01 = new Point(newX, thisY);
                                Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(), new01.y + ((JPanel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
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

                        door.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JPanel) e.getSource()).getX();
                                int thisY = ((JPanel) e.getSource()).getY();

                                int yMoved = e.getY() - initialClick.y;

                                int newY = thisY + yMoved;

                                Point new01 = new Point(thisX, newY);
                                Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(), new01.y + ((JPanel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
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

                        door.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JPanel) e.getSource()).getX();
                                int thisY = ((JPanel) e.getSource()).getY();

                                int yMoved = e.getY() - initialClick.y;

                                int newY = thisY + yMoved;

                                Point new01 = new Point(thisX, newY);
                                Point new10 = new Point(new01.x + ((JPanel) e.getSource()).getWidth(), new01.y + ((JPanel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
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

                // Windows
                North.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {
                       
                        ImageIcon win = new ImageIcon("HoriWindow.png");
                        JLabel window = new JLabel();
                        window.setBounds(0, 0, 30, 2);
                        window.setIcon(win);

                        window.addMouseListener(new MouseListener() {
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

                        window.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;

                                int newX = thisX + xMoved;

                                Point new01 = new Point(newX, thisY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, thisY);
                                }
                            }
                        });
                        rightPanel.setLayout(null);
                        rightPanel.add(window);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                South.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        JLabel window = new JLabel();
                        ImageIcon win = new ImageIcon("HoriWindow.png");
                        window.setBounds(0, length - 2, 30, 2);
                        window.setIcon(win);

                        window.addMouseListener(new MouseListener() {
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

                        window.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;

                                int newX = thisX + xMoved;

                                Point new01 = new Point(newX, thisY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, thisY);
                                }
                            }
                        });
                        rightPanel.setLayout(null);
                        rightPanel.add(window);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                West.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        JLabel window = new JLabel();
                        ImageIcon win = new ImageIcon("VertiWindow.png");
                        window.setBounds(0, 0, 4, 30);
                        window.setIcon(win);

                        window.addMouseListener(new MouseListener() {
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

                        window.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int yMoved = e.getY() - initialClick.y;

                                int newY = thisY + yMoved;

                                Point new01 = new Point(thisX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(thisX, newY);
                                }
                            }
                        });
                        rightPanel.setLayout(null);
                        rightPanel.add(window);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                East.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        JLabel window = new JLabel();
                        ImageIcon win = new ImageIcon("VertiWindow.png");
                        window.setBounds(width - 2, 0, 2, 30);
                        window.setIcon(win);

                        window.addMouseListener(new MouseListener() {
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

                        window.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                               
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int yMoved = e.getY() - initialClick.y;

                                int newY = thisY + yMoved;

                                Point new01 = new Point(thisX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(thisX, newY);
                                }
                            }
                        });
                        rightPanel.setLayout(null);
                        rightPanel.add(window);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                // Furniture
                Bed.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon bed = new ImageIcon("Bed.png");
                        JLabel bedlabel = new JLabel();
                        bedlabel.setBounds(0, 0, 38, 60);
                        bedlabel.setIcon(bed);

                        bedlabel.addMouseListener(new MouseListener() {
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

                        bedlabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);

                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });
                        rightPanel.setLayout(null);
                        rightPanel.add(bedlabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Sofa.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon sofa = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel sofalabel = new JLabel();
                        sofalabel.setBounds(0, 0, 30, 30);
                        sofalabel.setIcon(sofa);

                        sofalabel.addMouseListener(new MouseListener() {
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
                       
                        sofalabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(sofalabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Diningset.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon Dining = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel dininglabel = new JLabel();
                        dininglabel.setBounds(0, 0, 30, 30);
                        dininglabel.setIcon(Dining);

                        dininglabel.addMouseListener(new MouseListener() {
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
                       
                        dininglabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(dininglabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Kitsink.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon KitSink = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel kitlabel = new JLabel();
                        kitlabel.setBounds(0, 0, 30, 30);
                        kitlabel.setIcon(KitSink);

                        kitlabel.addMouseListener(new MouseListener() {
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
                       
                        kitlabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(kitlabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Chair.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon chair = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel chairlabel = new JLabel();
                        chairlabel.setBounds(0, 0, 30, 30);
                        chairlabel.setIcon(chair);

                        chairlabel.addMouseListener(new MouseListener() {
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
                       
                        chairlabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(chairlabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Stove.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon stove = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel stovelabel = new JLabel();
                        stovelabel.setBounds(0, 0, 30, 30);
                        stovelabel.setIcon(stove);

                        stovelabel.addMouseListener(new MouseListener() {
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

                        stovelabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(stovelabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Commode.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon commode = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel commodelabel = new JLabel();
                        commodelabel.setBounds(0, 0, 30, 30);
                        commodelabel.setIcon(commode);

                        commodelabel.addMouseListener(new MouseListener() {
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

                        commodelabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(commodelabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Wash.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon wash = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel washlabel = new JLabel();
                        washlabel.setBounds(0, 0, 30, 30);
                        washlabel.setIcon(wash);

                        washlabel.addMouseListener(new MouseListener() {
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

                        washlabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(washlabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Shower.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon shower = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel showerlabel = new JLabel();
                        showerlabel.setBounds(0, 0, 30, 30);
                        showerlabel.setIcon(shower);

                        showerlabel.addMouseListener(new MouseListener() {
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

                        showerlabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(showerlabel);
                        rightPanel.revalidate();
                        rightPanel.repaint();
                    }
                });

                Table.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent f) {

                        ImageIcon table = new ImageIcon("/Users/akarshjain/Desktop/Files/Personal/MJ Music.png");

                        JLabel tablelabel = new JLabel();
                        tablelabel.setBounds(0, 0, 30, 30);
                        tablelabel.setIcon(table);

                        tablelabel.addMouseListener(new MouseListener() {
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

                        tablelabel.addMouseMotionListener(new MouseAdapter() {
                            @Override
                            public void mouseDragged(MouseEvent e) {
                                int thisX = ((JLabel) e.getSource()).getX();
                                int thisY = ((JLabel) e.getSource()).getY();

                                int xMoved = e.getX() - initialClick.x;
                                int yMoved = e.getY() - initialClick.y;

                                int newX = thisX + xMoved;
                                int newY = thisY + yMoved;

                                Point new01 = new Point(newX, newY);
                                Point new10 = new Point(new01.x + ((JLabel) e.getSource()).getWidth(), new01.y + ((JLabel) e.getSource()).getHeight());

                                if (new01.x < 0 || new01.y < 0 || new10.x > rightPanel.getWidth() || rightPanel.getHeight() < new10.y);
                                else {
                                    ((JLabel) e.getSource()).setLocation(newX, newY);
                                }
                            }
                        });

                        rightPanel.setLayout(null);
                        rightPanel.add(tablelabel);
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
       
        xCoord = 0;
        yCoord = 0;
        rooms.add(new JPanel());
        rooms.get(roomNum).setBounds(xCoord, yCoord, b, l);

        while (true)
        {
            if (!OverlapCheck(rooms.get(roomNum)))
            {    
                if (xCoord + b > floor.getWidth()) {
                    yCoord = yCoord + 100;
                    xCoord = 0;
                }
                rooms.get(roomNum).setBounds(xCoord, yCoord, b, l);
                xCoord = xCoord + rounder;
            }
            else {
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
                }
                else {
                    newX += rounder - newX % (rounder / 2);
                }

                if (newY % (rounder / 2) <= rounder / 2) {
                    newY -= newY % rounder;
                }
                else {
                    newY += rounder - newY % (rounder / 2);
                }

                ((JPanel) e.getSource()).setLocation(newX, newY);
            }
        });

        yCoordMax = Math.max(yCoordMax, l);

        switch (roomType) {
            case "Bedroom":
                rooms.get(roomNum).setBackground(new Color(0x4fce5d)); // Green
                // rooms.get(roomNum).setBorder(BorderFactory.createLineBorder(new Color(0x30A130), 2));
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

                    if (fir01.x >= sec10.x || sec01.x >= fir10.x || fir01.y >= sec10.y || sec01.y >= fir10.y);
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
