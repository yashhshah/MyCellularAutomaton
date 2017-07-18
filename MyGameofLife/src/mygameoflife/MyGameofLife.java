/*
 * Yash Shah
 * Cellular Automaton Assignment
 * Representing the mating behaviour of Mountain Gorillas
 */

//import packages
package mygameoflife;
import java.io.*;
import java.awt.*; //needed for graphics
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.*; //needed for graphics
import static javax.swing.JFrame.EXIT_ON_CLOSE; //needed for graphics
/**
 *
 * @author Yash Shah
 */
public class MyGameofLife extends JFrame {
    //FIELDS AND GLOBAL VARIABLES
    int checkMales = 0;
    int numGenerations = 100;//the number of years/generations
    int currGeneration = 0;//starting generation
    int width = 800; //width of the window in pixels
    int height = 800;
    int borderWidth = 50;
    int numCellsX = 50; //width of the grid (in cells)
    int numCellsY = 50;
    
    //all arrays including checking surrounding dead cells, gender, age, babyage etc.
    int checkDeadX[] = new int[100];
    int checkDeadY[] = new int[100];
    int checkMaleX[] = new int[100]; 
    int checkMaleY[] = new int[100];
    int checkFemaleX[] = new int[100]; 
    int checkFemaleY[] = new int[100];
    boolean alive[][] = new boolean[numCellsY][numCellsX];
    boolean aliveNext[][] = new boolean[numCellsY][numCellsX];//the next arrays are used to replace current info
    boolean baby[][] = new boolean[numCellsY][numCellsX];
    boolean nextbaby[][] = new boolean[numCellsY][numCellsX];
    int babyAge[][] = new int[numCellsY][numCellsX]; 
    int nextbabyAge[][] = new int[numCellsY][numCellsX];
    int cellWidth = (width-2*borderWidth)/numCellsX; 
    boolean gender[][] = new boolean[numCellsY][numCellsX];//true represents male, false represents female
    boolean gendernext[][] = new boolean[numCellsY][numCellsX];
    boolean canCheck[][]= new boolean[numCellsY][numCellsX];
    int age[][] = new int[numCellsY][numCellsX];
    int agenext[][] = new int[numCellsY][numCellsX];
    boolean father[][] = new boolean[numCellsY][numCellsX]; 
    boolean fathernext[][] = new boolean[numCellsY][numCellsX];
    boolean mother[][] = new boolean[numCellsY][numCellsX];
    boolean rejected[][] = new boolean[numCellsY][numCellsX]; 
    boolean rejectedNext[][] = new boolean[numCellsY][numCellsX];
    
    //create the label/boundry around the cells  
    int labelX = width / 2;
    int labelY = borderWidth;
    
    //creates the random class that will be used to derive gender and rejection of baby
    Random r = new Random();
 
    
    //plants individual herds. I planted alot of them to see how they work. They can change in size
    public void plantFirstGeneration() throws IOException {
        makeEveryoneDead();
        apeHerd(12,15,2,2);
        apeHerd(6,7,2,2);
        apeHerd(9,10,2,2);
        apeHerd(20,20,3,3);
        apeHerd(1,3,3,3);
        apeHerd(5,2,4,4);       
        apeHerd(27,24,3,3);        
        apeHerd(26,3,3,3);
        apeHerd(24,10,8,8);
        apeHerd(25,25,3,3);
        apeHerd(25,15,4,4);
        apeHerd(10,21,3,3);
        apeHerd(17,15,4,4);
        apeHerd(16,5,4,4);
        apeHerd(16,5,4,4);
        apeHerd(5,26,4,4);
        apeHerd(3,15,4,4);
        apeHerd(3,12,4,4);        
        apeHerd(25,28,2,2);
        apeHerd(20,28,2,2);
        apeHerd(17,28,2,2);
        apeHerd(13,27,3,3);
        apeHerd(13,40,3,3);
        apeHerd(21,45,3,3);
        apeHerd(37,38,4,4);
        apeHerd(45,37,5,5);
        apeHerd(37,25,4,4);
        apeHerd(37,16,3,3);
        apeHerd(4,36,5,5);
        apeHerd(40,3,3,3);
        apeHerd(47,16,1,2);
        apeHerd(4,36,5,3); 

    }

    
    
    //Sets all cells to dead, meaning they are green in colour(to fit in with the jungle theme)
    public void makeEveryoneDead() {
        for (int i = 0; i < numCellsY; i++) {
            for (int j = 0; j < numCellsX; j++) {
                alive[i][j] = false;                
            }
        }
    }
    
    //this will create the ape herds
    public void apeHerd(int startX, int startY, int numColumns, int numRows) {
        //sets endCol and endRow
        int endCol = Math.min(startX + numColumns, numCellsX);
        int endRow = Math.min(startY + numRows, numCellsY);
        
        //creates the loops for the cells
        for (int i = startY; i < endRow; i++) {
            for (int j = startX; j < endCol; j++) {
                alive[i][j] = true;                
                gender[i][j] = r.nextBoolean();
                baby[i][j]=false; 
                father[i][j]=false;
                age[i][j]=7;                     
            }
        }
    }
    
    //computes the next generation    
    public void computeNextGeneration() { 
        //for loop to go through every cell
        for(int i = 0; i<numCellsY; i++){            
            for(int j = 0; j<numCellsX; j++){
                //initialize variables
                int checkFemales=0; //checks single females
                int checkDead = 0; //checks dead cells around it
                int checkSingleMales = 0;
                int silverBacks =0; //checks the dominant male
                
                //sets variables to check the borders
                int startRow, endRow, startCol, endCol;                  
                //copies arrays since these conditions have to be updated as soon as they happen
                System.arraycopy(fathernext[i], 0, father[i], 0, numCellsX); 
                System.arraycopy(nextbaby[i], 0, baby[i], 0, numCellsX); 
                System.arraycopy(rejectedNext[i], 0, rejected[i], 0, numCellsX); 
                
                //sets different rows and columns depending on which cell we are checking
                if ( i==0 ) {//if cell we are checking the cell in the first row
                    startRow = i;
                    endRow = i+1;
                } else if ( i == numCellsY-1 ) {//if we are checking the cell in the last row
                    startRow = i-1;
                    endRow = i;
                } else {//a regular cell in the middle rows, checks the 3x3 square around it
                    startRow = i-1;
                    endRow = i+1;            
                }
                //checks different columns
                if ( j==0 ) {//if the cell is in the first column
                    startCol = j;
                    endCol = j+1;
                } else if ( j == numCellsX-1 ) {
                    startCol = j-1;
                    endCol = j;
                } else {
                    startCol = j-1;
                    endCol = j+1;            
                }
        
                //if the cell is a male, make sure it is not being counted itself        
                if (alive[i][j]){
                    if(gender[i][j]){
                        checkMales--;                
                    }          
                }
        
                //if the cell is alive and it can be checked; some cells cannot be checked if they change in state      
                if(alive[i][j]&&canCheck[i][j]){  
                    //keeps the same states as the previous generation unless stated otherwise
                    aliveNext[i][j]=true;
                    gendernext[i][j]=gender[i][j];
                    agenext[i][j] = age[i][j];
                   
            
                    //checks the squares around it depending on the limits we set earlier
                    for (int m = startRow; m<=endRow; m++ ) {
                        for (int n = startCol; n<=endCol; n++ ) {
                    
                    //now checks for the alive cells around it and increments variables accordingly
                            if (alive[m][n]){
                                if(gender[m][n]){                            
                                    checkMales++;
                            //checks if the male is not a father and if it is of mating age
                                    if(father[m][n]==false && age[m][n]>=7 && aliveNext[m][n]){
                                        checkMaleX[checkSingleMales]=m; 
                                        checkMaleY[checkSingleMales]=n;
                                        checkSingleMales++;
                                    }
                                    //checks the number of dominant males around them
                                    else if(father[m][n] && aliveNext[m][n]){                            
                                    silverBacks++;                            
                                    }
                        //if the cell is a female and it is not a mother yet
                                }else{
                                    if(baby[m][n]==false &&age[m][n]>=7){
                                    checkFemaleX[checkFemales]=m; 
                                    checkFemaleY[checkFemales]=n;                                  
                                    checkFemales++;
                                    }
                                }
                              
                       
                    
                    //if the cell is dead
                            }else{   
                                nextbaby[m][n]=false; 
                                fathernext[m][n]=false;
                                if(aliveNext[m][n]==false){
                                    checkDeadX[checkDead]=m; 
                                    checkDeadY[checkDead]=n;
                                    checkDead++;
                                }                         
                            }
                        }//closes the nested loop 'n'              
           
                    }//closes the first loop 'm'
            
                    //if the cell is female
                    if(gender[i][j]==false){ 
                        //if the female is ready to mate
                        if(baby[i][j]==false &&age[i][j]>=7&&checkSingleMales>0&&checkDead>0){
                            //declare random variables
                            int a = r.nextInt(checkDead);
                            int b = r.nextInt(checkSingleMales);
                            int c = r.nextInt(100);
                     
                            //sets the selected male to a father
                            fathernext[checkMaleX[b]][checkMaleY[b]]=true;     
                            rejectedNext[checkMaleX[b]][checkMaleY[b]]=false;
                    
                            //plant baby
                            aliveNext[checkDeadX[a]][checkDeadY[a]]=true;
                            gendernext[checkDeadX[a]][checkDeadY[a]]=r.nextBoolean();
                            agenext[checkDeadX[a]][checkDeadY[a]]= 1;
                            canCheck[checkDeadX[a]][checkDeadY[a]]=false;
                            nextbaby[i][j]=true;
                            nextbabyAge[i][j] = agenext[checkDeadX[a]][checkDeadY[a]];
                            
                            //the mother has a 40% chance of rejecting the new born
                            if (c<=40 && gendernext[checkDeadX[a]][checkDeadY[a]]){
                                rejectedNext[checkDeadX[a]][checkDeadY[a]]=true;
                                fathernext[checkDeadX[a]][checkDeadY[a]]=false;                
                            }                
                        }
                
                        //if the female is ready to mate and there was noone to mate with
                        else if(checkDead>0&&baby[i][j]==false&&age[i][j]>=7){
                                aliveNext[i][j]=false;
                                //moves to a random spot
                                int a = r.nextInt(checkDead);
                                aliveNext[checkDeadX[a]][checkDeadY[a]]=true;
                                gendernext[checkDeadX[a]][checkDeadY[a]]=false;
                                agenext[checkDeadX[a]][checkDeadY[a]]= 7;
                                canCheck[checkDeadX[a]][checkDeadY[a]]=false;  
                        }
                
                        //mother attacks female if it gets near the child
                        else if(checkFemales>0 && baby[i][j]==true && babyAge[i][j]<7){
                            aliveNext[checkFemaleX[0]][checkFemaleY[0]]=false;     
                        }
                        //females die with they have nowhere to move
                        else if(checkDead==0 && baby[i][j]==false){
                        aliveNext[i][j]=false;               
                        }      
                    }
            
                    //checks if a dominant male is around another dominant male, which forces them to fight
                    else{
                        if(father[i][j]&&silverBacks-1>0){
                            aliveNext[i][j]=false;
                        }
              
                        //if the baby is a rejected one, it will move away from its herd once it grows up
                        else if(rejected[i][j]&&age[i][j]>=7 && father[i][j]==false&&checkDead>0&&checkFemales==0){
                                aliveNext[i][j]=false;                       
                                int a = r.nextInt(checkDead);
                                aliveNext[checkDeadX[a]][checkDeadY[a]]=true;
                                gendernext[checkDeadX[a]][checkDeadY[a]]=true;
                                agenext[checkDeadX[a]][checkDeadY[a]]= age[i][j];
                                canCheck[checkDeadX[a]][checkDeadY[a]]=false;
                                rejectedNext[checkDeadX[a]][checkDeadY[a]]=true;           
                        }//closes the else if     
                        else if(checkDead==0 && rejected[i][j]){
                            aliveNext[i][j]=false;               
                        }      
                    }//closes the else that checks if the cell is male 
                }//closes the alive if statement       
            }//closes the loop with 'j' (the nested loop)
        }//closes the starting loop with 'i'
        
        //increases the ages of each cell after the checking loop    
        for(int f = 0; f<numCellsY; f++){
            for(int d = 0; d<numCellsX; d++){
                agenext[f][d]+=1;
                if(baby[f][d]){
                    nextbabyAge[f][d]+=1;        
                }
            }//closes the nested for loop
        }//closes the first for loop
    }//closes the method
    
    //Overwrites the current generation's 2-D array with the values from the next generation's 2-D array
    public void plantNextGeneration() {
        for(int i = 0; i<numCellsY; i++){
            System.arraycopy(aliveNext[i], 0, alive[i], 0, numCellsX);               
            System.arraycopy(gendernext[i], 0, gender[i], 0, numCellsX);   
            System.arraycopy(agenext[i], 0, age[i], 0, numCellsX);  
            System.arraycopy(nextbabyAge[i], 0, babyAge[i], 0, numCellsX);    
        }
    }

    
    //Makes the pause between generations
    public static void sleep(int duration) {
        try {
            Thread.sleep(duration);
        } 
        catch (Exception e) {}
    }

    
    //Displays the statistics at the top of the screen
    void drawLabel(Graphics g, int state) {
        //creates borders around the cells
        g.setColor(Color.black);
        g.fillRect(0, 0, width, borderWidth);       
        g.fillRect(0,0,borderWidth,800);
        g.fillRect(800-borderWidth,0,800,800);
        g.fillRect(0,800-borderWidth,800,800);
        g.setColor(Color.yellow);
        //displays the year on the top of the screen
        g.drawString("Year: " + state, labelX, labelY);
    }
    
    //creates a buffered image to smoothen up creation of the cells
    public Image createImage(){
        //buffered images creation
        BufferedImage bufferedImage = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB); 
        Graphics2D g = (Graphics2D) bufferedImage.getGraphics(); 
        //creates variables for rows and columns
        int x, y, i, j;
         y = borderWidth;
         
        //creates a green cells on the whole screen before things are printed on top of it
        //this is because I had an issue of a black background being printed on growing cells
        for (i = 0; i < numCellsY; i++) {
            //sets the column to the borderwidth
            x = borderWidth;    
            for (j = 0; j < numCellsX; j++) {
                g.setColor(Color.green);
                g.fillRect(x, y, cellWidth, cellWidth);    
                //goes to next column
                x+=cellWidth;
            }
            //goes to next row
            y+=cellWidth;
        }
        //does the same thing as above essentially again
        y = borderWidth;
        for (i = 0; i < numCellsY; i++) {
            x = borderWidth;     
            //does the same thing, starts with the first column
            //loop to cycle through columns
            for (j = 0; j < numCellsX; j++) {
                //sets all the canchecks to true. This is the only 
                //value setting in the paint function
                canCheck[i][j]=true;               
                if(alive[i][j]){
                    
                    //if the cell is male
                    if(gender[i][j]){
                        if(father[i][j])
                            g.setColor(Color.GRAY);
                        else
                            g.setColor(Color.BLUE);
                    }
                    
                    //checks if the cell is female
                    else{
                        if(baby[i][j])
                            g.setColor(Color.MAGENTA);
                        else                    
                            g.setColor(Color.PINK);
                    }             
                }
                
                //prints the cell green if the cell is dead
                else{                    
                g.setColor(Color.green);                
                }   
               
                //if the cell is 7 years old(old enough to mate) it covers the whole cell
                if(age[i][j]>=7){      
                    g.fillRect(x,y,cellWidth,cellWidth);
                }
            
                //if the cell is still a growing child, it does not cover the whole cell
                else{
                    g.fillRect(x+(7-age[i][j]),y+(7-age[i][j]),cellWidth+(-14+(age[i][j])*2),cellWidth+(-14+(age[i][j])*2));
                }
            
                //draws the borders
                g.setColor(Color.black);
                g.drawRect(x, y, cellWidth, cellWidth);
                //moves on to next column
                x+=cellWidth;   
            }
            
        y+=cellWidth;
            
        }
        
        //returns the buffered image
        drawLabel(g, currGeneration);
        return bufferedImage;
    }//closes buffered method  
    
    //Draws the current generation of living cells on the screen-using buffered images
    public void paint( Graphics g ) {
        Image img = createImage(); 
        g.drawImage(img, 0, 0, this);        
    }


    //sets up JFrame
    public void initialWindow(){
   
        setTitle("Gorilla Simulator"); 
        setSize(height, width);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setBackground(Color.GREEN);
        setVisible(true); //calls paint() for the first time    
    }
    
    //Main algorithm
    public static void main(String args[]) throws IOException {

        MyGameofLife current = new MyGameofLife();
        //plants the first generation
        current.plantFirstGeneration();
        current.initialWindow();    
        current.sleep(100); 
         
        //Sets the initial generation of living cells, either by reading from a file or creating them algorithmically       
        for (int i = 1; i <= current.numGenerations; i++) { 
            //computes and displays next generation
            current.computeNextGeneration(); 
            current.plantNextGeneration();            
            current.sleep(100);             
            current.repaint();
            current.currGeneration++;    
        }
    }     
} //end of class