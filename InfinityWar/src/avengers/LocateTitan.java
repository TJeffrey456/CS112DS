package avengers;
/**
 * 
 * Using the Adjacency Matrix of n vertices and starting from Earth (vertex 0), 
 * modify the edge weights using the functionality values of the vertices that each edge 
 * connects, and then determine the minimum cost to reach Titan (vertex n-1) from Earth (vertex 0).
 * 
 * Steps to implement this class main method:
 * 
 * Step 1:
 * LocateTitanInputFile name is passed through the command line as args[0]
 * Read from LocateTitanInputFile with the format:
 *    1. g (int): number of generators (vertices in the graph)
 *    2. g lines, each with 2 values, (int) generator number, (double) funcionality value
 *    3. g lines, each with g (int) edge values, referring to the energy cost to travel from 
 *       one generator to another 
 * Create an adjacency matrix for g generators.
 * 
 * Populate the adjacency matrix with edge values (the energy cost to travel from one 
 * generator to another).
 * 
 * Step 2:
 * Update the adjacency matrix to change EVERY edge weight (energy cost) by DIVIDING it 
 * by the functionality of BOTH vertices (generators) that the edge points to. Then, 
 * typecast this number to an integer (this is done to avoid precision errors). The result 
 * is an adjacency matrix representing the TOTAL COSTS to travel from one generator to another.
 * 
 * Step 3:
 * LocateTitanOutputFile name is passed through the command line as args[1]
 * Use Dijkstra’s Algorithm to find the path of minimum cost between Earth and Titan. 
 * Output this number into your output file!
 * 
 * Note: use the StdIn/StdOut libraries to read/write from/to file.
 * 
 *   To read from a file use StdIn:
 *     StdIn.setFile(inputfilename);
 *     StdIn.readInt();
 *     StdIn.readDouble();
 * 
 *   To write to a file use StdOut (here, minCost represents the minimum cost to 
 *   travel from Earth to Titan):
 *     StdOut.setFile(outputfilename);
 *     StdOut.print(minCost);
 *  
 * Compiling and executing:
 *    1. Make sure you are in the ../InfinityWar directory
 *    2. javac -d bin src/avengers/*.java
 *    3. java -cp bin avengers/LocateTitan locatetitan.in locatetitan.out
 * 
 * @author Yashas Ravi
 * 
 */
import java.util.*;
public class LocateTitan {
	
    public static void main (String [] args) {
    	
        if ( args.length < 2 ) {
            StdOut.println("Execute: java LocateTitan <INput file> <OUTput file>");
            return;
        }
        // WRITE YOUR CODE HERE
        //take the file as String argument and then read it  
        String input = args[0];
        StdIn.setFile(input);

        //for number of wormhole gens
    	//take in the num of vertices(wormhole generators and functionality value) STEP 1
        int numOfVertices = StdIn.readInt();
        //make a HashMap to store the functionality values
        HashMap<Integer,Double> wormholeGen = new HashMap<>();
        for(int i = 0; i<numOfVertices; i++){
            //read in position # and functionality value
            wormholeGen.put(StdIn.readInt(), StdIn.readDouble());
        }

        //to find paths for wormhole gens and establish wormholes
        double[][] wormholeAdjM = new double[numOfVertices][numOfVertices];
        //traverse row-wise
        for(int i=0;i<numOfVertices;i++){
            for(int j=0;j<numOfVertices;j++){
                wormholeAdjM[i][j] = StdIn.readDouble();//read the value
                //factor in functionality STEP 2
                wormholeAdjM[i][j] = (int)((wormholeAdjM[i][j])/(wormholeGen.get(i)*wormholeGen.get(j)));
            }
        }

        //find shortest distance - Dijkstra's STEP 3
        String output = args[1];
        StdOut.setFile(output);

        StdOut.print(dijkstra(numOfVertices, wormholeAdjM));
    }

    //helper method for dijkstra's algo
    //parameter of n - where n is the number of vertices 
    public static int dijkstra(int n, double[][] wAdjM){
        //2 arrs - one for minCost to reach a wormhole(vertex) and if it has been processed
        int[] minCosts = new int[n];
        boolean[] processed = new boolean[n];

        //set source to 0 and the rest to infinity
        minCosts[0]=0;
        for(int i=1;i<n;i++){
            minCosts[i] = Integer.MAX_VALUE;
        }

        int currSource;

        //loop through V-1 times(no need to check for 0-first one)
        for(int i=0;i<n;i++){
            currSource = getMinCost(minCosts, processed);
            processed[currSource] = true; //bc we are now visiting it

            //Relax all adj vertices
            for(int j=0;j<n;j++){
                if(processed[j]==false && minCosts[currSource]!=Integer.MAX_VALUE && wAdjM[currSource][j]!=0 
                && minCosts[currSource]+wAdjM[currSource][j]<minCosts[j]){
                    minCosts[j] = minCosts[currSource] + (int)wAdjM[currSource][j];
                }
            }
        }
        return minCosts[n-1];

    }

    //help method
    //get the min cost Node of the unvisted vertices
    public static int getMinCost(int[] mc, boolean[] p){
        int minCostNode = Integer.MAX_VALUE;
        for(int i=0;i<p.length;i++){
            if(p[i]==false && (minCostNode==Integer.MAX_VALUE || mc[i]<mc[minCostNode])){
                minCostNode = i;
            }
        }
        return minCostNode;
    }

}
