import java.util.*;
public class Room
{
   public Wall north, east, south, west; 
   public int x, y; 
   public List<Room> adj; 
   public int roomName; 
   public Room parent; 
   public boolean isTraversed = false;
   public boolean isFoundByAI = false;
   public boolean visited = false;
   
   // For A* Search algorithm
   public int f_value;
   public int g_value;
   public int h_value;

   
   public Room(int x, int y)
   {
      this.x = x;
      this.y = y;
      adj = new LinkedList<Room>();
      parent = null;
      roomName = 0;
   }

   public int getRoomName()
   {
      return roomName++;
   }

}