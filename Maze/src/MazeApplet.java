import java.applet.Applet;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class MazeApplet extends Applet implements KeyListener
{

    private Room[][] rooms;
    private ArrayList<Wall> walls;
    private Random rand;
    private int height;
    private int width;
    private int num;
    private JoinRoom ds;

    private int x_cord; 
    private int y_cord;
    private int roomSize;
    private int randomWall;
    private int x_nav;
    private int y_nav;
    
    private static int INFINITY = Integer.MAX_VALUE;
    
    public void init()
    {
    	JTextField xField = new JTextField(5);
    	JTextField yField = new JTextField(5);
    	
    	JPanel myPanel = new JPanel();
        myPanel.add(new JLabel("Vertical:"));
        myPanel.add(xField);
        myPanel.add(Box.createHorizontalStrut(15)); // a spacer
        myPanel.add(new JLabel("Horizontal:"));
        myPanel.add(yField);

        int result = JOptionPane.showConfirmDialog(null, myPanel, "Maze Size", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) 
        {
           int height = Integer.parseInt(xField.getText());
           int width = Integer.parseInt(yField.getText());
           
           this.height = height;
           this.width = width;
           x_nav = 0;
           y_nav = 0;
           roomSize = 20;
           rooms = new Room[height][width];
           walls = new ArrayList<Wall>((height - 1) * (width - 1));
           generateRandomMaze();
           addKeyListener(this);
           setFocusable(true);
           requestFocusInWindow();
        }
    }
    
    public void keyTyped(KeyEvent e) {}
    
    public void keyReleased(KeyEvent e) {}
    
    public void keyPressed(KeyEvent e)
    {
    	if (e.getKeyCode() == KeyEvent.VK_UP)
    	{
    		if (y_nav > 0)
    		{
    			if (rooms[y_nav][x_nav].north.isGone)
    			{
    				
    				if (rooms[y_nav-1][x_nav].isTraversed == false)
    				{
    					rooms[y_nav-1][x_nav].isTraversed = true;
    				}
    				else
    				{
    					rooms[y_nav][x_nav].isTraversed = false;
    				}
    				y_nav--;
    				
    			}
    		}
    	}
    	if (e.getKeyCode() == KeyEvent.VK_DOWN)
    	{
    		if (y_nav < height - 1)
    		{
    			if (rooms[y_nav+1][x_nav].north.isGone)
    			{
    				if (rooms[y_nav+1][x_nav].isTraversed == false)
    				{
    					rooms[y_nav+1][x_nav].isTraversed = true;
    				}
    				else
    				{
    					rooms[y_nav][x_nav].isTraversed = false;
    				}
    				y_nav++;
    			}
    		}
    	}
    	if (e.getKeyCode() == KeyEvent.VK_LEFT)
    	{
    		if (x_nav > 0)
    		{
    			if (rooms[y_nav][x_nav].west.isGone)
    			{
    				if (rooms[y_nav][x_nav-1].isTraversed == false)
    				{
    					rooms[y_nav][x_nav-1].isTraversed = true;
    				}
    				else
    				{
    					rooms[y_nav][x_nav].isTraversed = false;
    				}
    				x_nav--;
    			}
    		}
    	}
    	if (e.getKeyCode() == KeyEvent.VK_RIGHT)
    	{
    		if (x_nav < width - 1)
    		{
    			if (rooms[y_nav][x_nav+1].west.isGone)
    			{
    				if (rooms[y_nav][x_nav+1].isTraversed == false)
    				{
    					rooms[y_nav][x_nav+1].isTraversed = true;
    				}
    				else
    				{
    					rooms[y_nav][x_nav].isTraversed = false;
    				}
    				x_nav++;
    			}
    		}
    	}
    	repaint();
    }
    
    private void generateRandomMaze() 
    {
        generateInitialRooms();
        ds = new JoinRoom(width * height);
        rand = new Random(); 
        num = width * height;

        while (num > 1) 
        {
            randomWall = rand.nextInt(walls.size());
            Wall temp = walls.get(randomWall);
        	int roomA = temp.currentRoom.x + temp.currentRoom.y * width;
            int roomB = temp.nextRoom.x + temp.nextRoom.y * width;

            if (ds.find(roomA) != ds.find(roomB)) 
            {
                walls.remove(randomWall);
                ds.unionRooms(ds.find(roomA), ds.find(roomB));
                temp.isGone = true;
                temp.currentRoom.adj.add(temp.nextRoom);
                temp.nextRoom.adj.add(temp.currentRoom);
                num--;
            }
        }
    }

    private int roomNumber = 0;
    private void generateInitialRooms()
    {
        for (int i = 0; i < height; i++) 
        {
            for (int j = 0; j < width; j++) 
            {
                rooms[i][j] = new Room(j, i);
                if (i == 0) 
                {
                    rooms[i][j].north = new Wall(rooms[i][j]);
                } 
                else 
                {
                    rooms[i][j].north = new Wall(rooms[i - 1][j], rooms[i][j]);
                    walls.add(rooms[i][j].north);
                }
                if (i == height - 1) 
                {
                    rooms[i][j].south = new Wall(rooms[i][j]);
                }
                if (j == 0) 
                {
                    rooms[i][j].west = new Wall(rooms[i][j]);
                } else 
                {
                    rooms[i][j].west = new Wall(rooms[i][j - 1], rooms[i][j]);
                    walls.add(rooms[i][j].west);
                }
                if (j == width - 1) 
                {
                    rooms[i][j].east = new Wall(rooms[i][j]);
                }
                rooms[i][j].roomName = roomNumber++;
            }
        }
        rooms[0][0].west.isGone = true;
        rooms[0][0].roomName = 0;
        rooms[0][0].isTraversed = true;
        rooms[height - 1][width - 1].south.isGone = true;
        rooms[height - 1][width - 1].roomName = (height * width);
    }
	
    private void AStarSolve()
    {
    	ArrayList<Room>	openList = new ArrayList<Room>();
    	ArrayList<Room>	closedList = new ArrayList<Room>();
    	openList.add(rooms[0][0]);
    	rooms[0][0].f_value = 0;
    	
    	while (openList.size() > 0)
    	{
    		int minIndex = 0;
    		int smallestF = openList.get(0).f_value;
    		for (int i = 1; i < openList.size(); i++)
    		{
    			if (openList.get(i).f_value < smallestF)
    			{
    				minIndex = i;
    				smallestF = openList.get(i).f_value;
    			}
    		}
    		Room current = openList.get(minIndex);
    		int x_value = current.x;
    		int y_value = current.y;
    		Room t = rooms[y_value][x_value];
    		ArrayList<Room> neighbors = getNeighbors(x_value, y_value);
    		openList.remove(minIndex);
    		
    		for (int j = 0; j < neighbors.size(); j++)
    		{
    			Room temp = neighbors.get(j);
    			if (temp.x == width - 1 && temp.y == height - 1)
    				return;
    			temp.g_value = t.g_value + 1;
    			temp.h_value = (height - 1 - temp.x + width - 1 - temp.y) * 10;
    			temp.f_value = temp.g_value + temp.h_value;
    			
    			Room temp2 = rooms[temp.y][temp.x];
    			int counter = 0;
    			if (contains(openList, temp2) != null)
    			{
    				if (contains(openList, temp2).f_value < temp.f_value)
    					counter++;
    			}
    			
    			if (contains(closedList, temp2) != null)
    			{
    				if (contains(closedList, temp2).f_value < temp.f_value)
    					counter++;
    			}
    			
    			if (counter == 0)
    				openList.add(temp);		
    		}
    		closedList.add(t);    		
    	}
    	
    	trace(rooms[height-1][width-1]);
    }
    
    private void trace(Room r)
    {
    	int x = r.x;
    	int y = r.y;
    	if (x == 0 && y == 0)
    		rooms[0][0].isFoundByAI = true;
    	else
    	{
    		rooms[y][x].isFoundByAI = true;
    		trace(r.parent);
    	}
    }
    
    public Room contains(ArrayList<Room> list, Room r)
    {
    	for (Room a : list)
    	{
    		if (a.x == r.x && a.y == r.y)
    			return a;
    	}
    	return null;
    }
    
    public ArrayList<Room> getNeighbors(int x, int y)
    {
    	ArrayList<Room> list = new ArrayList<Room>();
    	
    	if (y > 0)
    	{
    		if (rooms[y][x].north.isGone)
    		{
    			list.add(rooms[y-1][x]);
    			rooms[y-1][x].parent = rooms[y][x];
    		}
    	}
    	
    	if (y < height - 1)
    	{
    		if (rooms[y+1][x].north.isGone)
    		{
    			list.add(rooms[y+1][x]);
    			rooms[y+1][x].parent = rooms[y][x];
    		}
    	}
    	
    	if (x > 0)
    	{
    		if (rooms[y][x].west.isGone)
    		{
    			list.add(rooms[y][x-1]);
    			rooms[y][x-1].parent = rooms[y][x];
    		}
    	}
    	
    	if (x < width - 1)
    	{
    		if (rooms[y][x+1].west.isGone)
    		{
    			list.add(rooms[y][x+1]);
    			rooms[y][x+1].parent = rooms[y][x];
    		}
    	}
    	return list;
    }
    
	 public void paint(Graphics g) 
	 {
		Graphics2D g2 = (Graphics2D) g;
        x_cord = 0;
        y_cord = 0;

        int x = x_cord;
        int y = y_cord;
        
        //AStarSolve(); not complete yet
        g2.setStroke(new BasicStroke(2));
        
        for (int i = 0; i <= height - 1; i++) 
        {
            for (int j = 0; j <= width - 1; j++) 
            {
            	
            	if (rooms[i][j].isFoundByAI)
            	{
            		g2.setColor(Color.BLUE);
            		g2.fillRect(j*roomSize, i*roomSize, roomSize, roomSize);
            	}
            	
            	if (rooms[i][j].isTraversed)
                {
                	g2.setColor(Color.RED);
                    g2.fillRect(j*roomSize, i*roomSize, roomSize, roomSize);
                }
            	
                g2.setColor(Color.black);
                
                if (!(rooms[i][j].north.isGone)) 
                {
                    g2.drawLine(x, y, x + roomSize, y);
                }
                if (rooms[i][j].west.isGone == false) 
                {
                    g2.drawLine(x, y, x, y + roomSize);
                }
                if ((i == height - 1) && rooms[i][j].south.isGone == false) 
                {
                    g2.drawLine(x, y + roomSize, x + roomSize,
                            y + roomSize);
                }
                if ((j == width - 1) && rooms[i][j].east.isGone == false) 
                {
                    g2.drawLine(x + roomSize, y, x + roomSize,
                            y + roomSize);
                }
                x += roomSize;
            }
            x = x_cord;
            y += roomSize;
        }
        
        if (rooms[height-1][width-1].isTraversed)
        	JOptionPane.showMessageDialog(null, "You solved the puzzle!");
	 }
}