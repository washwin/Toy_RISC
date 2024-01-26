import java.lang.Math;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter; 
import java.io.IOException;

public class main
{
    public static void main (String[] args)
    {
        int W = 3;
        double p=0.5;
        Border border = new Border(W);
        Sensors sensors = new Sensors();
        Clock clock = new Clock();
        Infiltrator infiltrator = new Infiltrator();
        int x0, y0, x, y, time;
        
        time = clock.tick();
        boolean caught;
        // for (int l=0;l<10;l++)           //sample grid
        // {
            //     for (int j=0;j<W;j++)
            //     {
                //         for (int i=0;i<10;i++)
                //         {
                    //             System.out.print(border.grid[i][j]);
                    //         }
                    //         System.out.println();
                    //     }
                    //     sensors.doDutyCycling(p, W);
                    //     System.out.println();
                    // }
                    
                    // for(int i=0;i<10;i++)           //sample moves
                    // {
                        //     System.out.print(infiltrator.x + ",");
                        //     System.out.println(infiltrator.y);
                        //     infiltrator.makeMove(W);
                        // }
                        
        try {
        FileWriter file = new FileWriter("data.txt");
            file.write("W,p,caught\n");
        for (W=1;W<6;W++){
        
        for(p=0.01; p<1 ; p = p + (double)0.01 ){

        infiltrator.x = (int)(Math.random() * 1000) + 0;        //assigning random starting to the infiltrator
        infiltrator.y = 0;
        clock.time = 0;
        // System.out.println("p vlaue"+p);
        while(true)                     //simulation loop
        {   
            x0 = infiltrator.x;             //initial location of infiltrator
            y0 = infiltrator.y;
            infiltrator.makeMove(W);
            x = infiltrator.x;              //new location of infiltrator
            y = infiltrator.y;
            sensors.doDutyCycling(p,W);
            time = clock.tick();
            if(x0==x && y0==y)              //infiltrator has not moved
            {
                continue;
            }
            if(border.grid[x0][y0] == 1 || border.grid[x][y] == 1)  
            {
                // System.out.println("Infiltrator is caught");
                // System.out.println("Time elapsed = " + t + "s");
                caught = true;
                file.write(W + "," + p + "," + caught + "\n");
                break;
            }
            if(infiltrator.y == W)                  //infiltrator reached dc
            {
                // System.out.println("Infiltrator has succeeded");
                // System.out.println("Time elapsed = " + t + "s");
                caught = false;
                file.write(W + "," + p + "," + caught + "\n");
                break;
            }
        }
        }
        }
        file.close();
        }catch (IOException e) 
        {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}


class Border
{
    static int[][] grid = new int[1000][10];            //creating a grid with length 1000
    static int width;
    public Border(int w)                    //constructor to initialize grid
    {
        width = w;
        for(int i=0; i<1000; i++)
        {
            for(int j=0; j<w; j++)
            {
                grid[i][j] = 0;         //initializing all sensors to 0, meaning off
            }
        }
    }
}

class Sensors
{
    public void doDutyCycling(double p, int w)
    {
        Border border = new Border(w);
        for(int i=0; i<1000; i++)
        {
            for(int j=0; j<border.width; j++)
            {
                if(Math.random() < p)          // probability of getting heads is p
                {
                    border.grid[i][j] = 1;            // if heads then sensor is on
                }
                else
                {
                    border.grid[i][j] = 0;             // else sensor is off
                }   
            }
        }
    }
}

class Infiltrator
{
    static int x, y;                   //coordinates of infiltrator
    public ArrayList<Integer> moveGen(int w)          //function to generate possible moves
    {
        ArrayList<Integer> moves = new ArrayList<Integer>();
        moves.clear();
        if( x == 0 && y == 0)           //top left corner
        {
            // moves[] = {2,3,6,5};
            moves.add(2);
            moves.add(3);
            moves.add(6);
            moves.add(5);
        }
        else if( (x != 0) && (y==0) && (x!=1000))     //top row somewhere in the middle    
        {
            // moves = [1,2,3,4,5,6];
            moves.add(1);
            moves.add(2);
            moves.add(3);
            moves.add(4);
            moves.add(5);
            moves.add(6);
        }
        else if( (x==0) && (y!=0) && (y!=w))          //somewhere middle in the first column
        {
            // moves = [2,3,5,6,8,9];
            moves.add(2);
            moves.add(3);
            moves.add(5);
            moves.add(6);
            moves.add(8);
            moves.add(9);
        }
        else if( (x==1000) && (y==0))       //top right corner
        {
            // moves = [1,2,4,5];
            moves.add(1);
            moves.add(2);
            moves.add(4);
            moves.add(5);
        } 
        else if( (x==1000) && (y!=0) && (y!=w))       //somewhere middle in the last column
        {
            // moves = [1,2,4,5,7,8];
            moves.add(1);
            moves.add(2);
            moves.add(4);
            moves.add(5);
            moves.add(7);
            moves.add(8);
        
        }
        else if(y!=w)                       //all options!
        {
            // moves = [1,2,3,4,5,6,7,8,9];
            moves.add(1);
            moves.add(2);
            moves.add(3);
            moves.add(4);
            moves.add(5);
            moves.add(6);
            moves.add(7);
            moves.add(8);
            moves.add(9);
        }      
        return moves; 
    }
    public void makeMove(int w)
    {
        ArrayList<Integer> moves = moveGen(w);
        int prob = (int)(Math.random() * moves.size());         //choosing a random move
        // System.out.println(moves.size());
        int move = moves.get(prob);                             //the chosen move
        // System.out.println(move);
        switch(move)                                  //change coordinates as per the move
        {
            case 1: 
            x = x - 1;
            y = y + 1;
            break;
            case 2: 
            x = x ;
            y = y + 1;
            break;
            case 3: 
            x = x + 1;
            y = y + 1;
            break;
            case 4: 
            x = x - 1;
            y = y ;
            break;
            case 5: 
            x = x ;
            y = y ;
            break;
            case 6: 
            x = x + 1;
            y = y ;
            break;
            case 7: 
            x = x - 1;
            y = y - 1;
            break;
            case 8: 
            x = x ;
            y = y - 1;
            break;
            case 9: 
            x = x + 1;
            y = y - 1;
            break;
        }
    }
}

class Clock             //to simulate time in our world
{
    int time = 0;
    public int tick()
    {
        time = time + 10;
        return time;
    }
}