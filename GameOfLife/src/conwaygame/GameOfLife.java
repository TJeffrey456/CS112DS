package conwaygame;
import java.io.File;
import java.util.ArrayList;
/**
 * Conway's Game of Life Class holds various methods that will
 * progress the state of the game's board through it's many iterations/generations.
 *
 * Rules 
 * Alive cells with 0-1 neighbors die of loneliness.
 * Alive cells with >=4 neighbors die of overpopulation.
 * Alive cells with 2-3 neighbors survive.
 * Dead cells with exactly 3 neighbors become alive by reproduction.

 * @author Seth Kelley 
 * @author Maxwell Goldberg
 */
public class GameOfLife {

    // Instance variables
    private static final boolean ALIVE = true;
    private static final boolean  DEAD = false;

    private boolean[][] grid;    // The board has the current generation of cells
    private int totalAliveCells; // Total number of alive cells in the grid (board)

    /**
    * Default Constructor which creates a small 5x5 grid with five alive cells.
    * This variation does not exceed bounds and dies off after four iterations.
    */
    public GameOfLife() {
        grid = new boolean[5][5];
        totalAliveCells = 5;
        grid[1][1] = ALIVE;
        grid[1][3] = ALIVE;
        grid[2][2] = ALIVE;
        grid[3][2] = ALIVE;
        grid[3][3] = ALIVE;
    }

    /**
    * Constructor used that will take in values to create a grid with a given number
    * of alive cells
    * @param file is the input file with the initial game pattern formatted as follows:
    * An integer representing the number of grid rows, say r
    * An integer representing the number of grid columns, say c
    * Number of r lines, each containing c true or false values (true denotes an ALIVE cell)
    */
    public GameOfLife (String file) {
        //need to intialize before adding to it
        totalAliveCells=0;
        //reading from a file (has 2 numbers for number of row and columns) and then lines of true and false
        StdIn.setFile(file);
        //need dimensions of 2D array to fill it
        int rows = StdIn.readInt();
        int columns = StdIn.readInt();
        //make 2D array
        grid = new boolean[rows][columns];

        //fill the array 
        //keep going until nothing is left{
        for (int i = 0; i<grid.length; i++){
            for (int j = 0; j<grid[i].length; j++){
                //check if its possibly
                if(StdIn.hasNextLine()){
                    grid[i][j] = StdIn.readBoolean();
                    if (grid[i][j]){
                        totalAliveCells++;
                    }
                }
            }
        }
    }

    /**
     * Returns grid
     * @return boolean[][] for current grid
     */
    public boolean[][] getGrid () {
        return grid;
    }
    
    /**
     * Returns totalAliveCells
     * @return int for total number of alive cells in grid
     */
    public int getTotalAliveCells () {
        return totalAliveCells;
    }

    /**
     * Returns the status of the cell at (row,col): ALIVE or DEAD
     * @param row row position of the cell
     * @param col column position of the cell
     * @return true or false value "ALIVE" or "DEAD" (state of the cell)
     */
    public boolean getCellState (int row, int col) {

        // grid is 2D array of boolean so return grid 
        return grid[row][col]; // update this line, provided so that code compiles
    }

    /**
     * Returns true if there are any alive cells in the grid
     * @return true if there is at least one cell alive, otherwise returns false
     */
    public boolean isAlive () {
        return getTotalAliveCells()>0; 
    }

    /**
     * Determines the number of alive cells around a given cell.
     * Each cell has 8 neighbor cells which are the cells that are 
     * horizontally, vertically, or diagonally adjacent.
     * 
     * @param col column position of the cell
     * @param row row position of the cell
     * @return neighboringCells, the number of alive cells (at most 8).
     */
    public int numOfAliveNeighbors (int row, int col) {

        // Need to implement to factor wrapping
        //go around the 3x3 surrounding it
        int aliveNeighbors = 0;
        int r = row - 1;//for row count

        //check 3x3
        for(int i = 0; i<3; i++){
        
            int c = col - 1;// for column count
            for(int j = 0; j<3; j++){
                //wrapping
                r = rowWrapper(r);
                c = colWrapper(c);
                //testing
                if (getCellState(r, c) && !(r==row && c==col)){
                    aliveNeighbors++;
                }
                c++;
            }
            r++;
        }
        return aliveNeighbors; // update this line, provided so that code compiles
    }

    /**
     * Creates a new grid with the next generation of the current grid using 
     * the rules for Conway's Game of Life.
     * 
     * @return boolean[][] of new grid (this is a new 2D array)
     */
    public boolean[][] computeNewGrid () {

        boolean[][] newGrid = new boolean[grid.length][grid[0].length];

        //check the current grid
        for (int i = 0; i<grid.length;i++){
            for (int j = 0; j<grid[i].length; j++){
                //Rule 1 Alive cells with no neighbors or one neighbor die of loneliness
                if(getCellState(i,j) && numOfAliveNeighbors(i, j)<2){
                    newGrid[i][j] = DEAD;
                    
                //Rule 2 Dead cells with exactly three neighbors become alive by reproduction.
                } else if (!getCellState(i,j) && numOfAliveNeighbors(i, j) == 3){
                    newGrid[i][j] = ALIVE;

                //Rule 4 Alive cells with four or more neighbors die of overpopulation.
                } else if (getCellState(i,j) && numOfAliveNeighbors(i, j) >= 4){
                    newGrid[i][j] = DEAD;

                //Rule 3 Alive cells with two or three neighbors survive.
                } else if (getCellState(i,j) && numOfAliveNeighbors(i, j) >=2){
                    newGrid[i][j] = ALIVE;
                }
            }
        }
        
        return newGrid;//return new 2d boolean based off current board
    }

    /**
     * Updates the current grid (the grid instance variable) with the grid denoting
     * the next generation of cells computed by computeNewGrid().
     * 
     * Updates totalAliveCells instance variable
     */
    public void nextGeneration () {
        //updating current grid
        grid = computeNewGrid();

        //start @ 0 to recount
        totalAliveCells = 0;
        //traverse board
        for (int i = 0; i<grid.length; i++){
            for(int j = 0; j<grid[0].length; j++){
                if(getCellState(i, j)){
                    totalAliveCells++;
                } 
            }
        }
        
    }

    /**
     * Updates the current grid with the grid computed after multiple (n) generations. 
     * @param n number of iterations that the grid will go through to compute a new grid
     */
    public void nextGeneration (int n) {

        for(int i = 0; i<n; i++){
            nextGeneration();
        }
    }

    /**
     * Determines the number of separate cell communities in the grid
     * @return the number of communities in the grid, communities can be formed from edges
     */
    public int numOfCommunities() {
        WeightedQuickUnionUF ufo = new WeightedQuickUnionUF(grid.length, grid[0].length);

        //first I need to make communities 
        //a cell need to be alive to be part of a community
        for (int row = 0; row<grid.length;row++){
            for(int col = 0; col<grid[row].length; col++){
                int r = row - 1;//for row count
                //check 3x3
                for(int i = 0; i<3; i++){
                    int c = col - 1;// for column count
                    for(int j = 0; j<3; j++){
                        //wrapping
                        r = rowWrapper(r);
                        c = colWrapper(c);

                        //testing to see if current cell is alive, adjacent cell is alive, and it's not counting itself
                        if ((getCellState(r, c) && getCellState(row, col)) && !(r==row && c==col)){
                            ufo.union(r, c, row, col);
                        }
                        c++;
                    }
                    r++;
                }
            }
        }

        //find roots
        //if roots are the same they are part of the same union
        //go through board and add a memeber of a community into an ArrayList if the roots is not already there

        //make arrlist object
        ArrayList<Integer> rootList = new ArrayList<Integer>();

        for(int i=0; i<grid.length;i++){
            for(int j=0;j<grid[i].length;j++){
                //check if the root does not exists in rootList and is alive add it
                if(!(rootList.contains(ufo.find(i, j))) && getCellState(i, j)) {
                    rootList.add(ufo.find(i, j));
                }
            }
        }

        return rootList.size(); // update this line, provided so that code compiles
    }


    //helper methods
    //method to wrap vertically
    private int rowWrapper(int row){
        //row wrapping
        int r = row; 
        if(row<0){
            r = grid.length-1;
        } else if (row>grid.length-1){
            r = 0;
        }
        return r;
    }
    //method to wrap horizontally
    private int colWrapper(int col){
        //col wrapping
        int c = col;
        if(col<0){
            c = grid[0].length-1;
        } else if (col>grid[0].length-1){
            c = 0;
        }
        return c;
    }
        
    
}
