package avengers;

/**
 * Given a starting event and an Adjacency Matrix representing a graph of all possible 
 * events once Thanos arrives on Titan, determine the total possible number of timelines 
 * that could occur AND the number of timelines with a total Expected Utility (EU) at 
 * least the threshold value.
 * 
 * 
 * Steps to implement this class main method:
 * 
 * Step 1:
 * UseTimeStoneInputFile name is passed through the command line as args[0]
 * Read from UseTimeStoneInputFile with the format:
 *    1. t (int): expected utility (EU) threshold
 *    2. v (int): number of events (vertices in the graph)
 *    3. v lines, each with 2 values: (int) event number and (int) EU value
 *    4. v lines, each with v (int) edges: 1 means there is a direct edge between two vertices, 0 no edge
 * 
 * Note 1: the last v lines of the UseTimeStoneInputFile is an ajacency matrix for a directed
 * graph. 
 * The rows represent the "from" vertex and the columns represent the "to" vertex.
 * 
 * The matrix below has only two edges: (1) from vertex 1 to vertex 3 and, (2) from vertex 2 to vertex 0
 * 0 0 0 0
 * 0 0 0 1
 * 1 0 0 0
 * 0 0 0 0
 * 
 * Step 2:
 * UseTimeStoneOutputFile name is passed through the command line as args[1]
 * Assume the starting event is vertex 0 (zero)
 * Compute all the possible timelines, output this number to the output file.
 * Compute all the posssible timelines with Expected Utility higher than the EU threshold,
 * output this number to the output file.
 * 
 * Note 2: output these number the in above order, one per line.
 * 
 * Note 3: use the StdIn/StdOut libraries to read/write from/to file.
 * 
 *   To read from a file use StdIn:
 *     StdIn.setFile(inputfilename);
 *     StdIn.readInt();
 *     StdIn.readDouble();
 * 
 *   To write to a file use StdOut:
 *     StdOut.setFile(outputfilename);
 *     //Call StdOut.print() for total number of timelines
 *     //Call StdOut.print() for number of timelines with EU >= threshold EU 
 * 
 * Compiling and executing:
 *    1. Make sure you are in the ../InfinityWar directory
 *    2. javac -d bin src/avengers/*.java
 *    3. java -cp bin avengers/UseTimeStone usetimestone.in usetimestone.out
 * 
 * @author Yashas Ravi
 * 
 */

import java.util.*;

public class UseTimeStone {

    public static void main (String [] args) {
    	
        if ( args.length < 2 ) {
            StdOut.println("Execute: java UseTimeStone <INput file> <OUTput file>");
            return;
        }

    	// WRITE YOUR CODE HERE
        //setfile
        String input = args[0];
        StdIn.setFile(input);
        
        //read in values
        int threshold = StdIn.readInt();
        int numOfEvents = StdIn.readInt();

        //make structures to hold more read in values
        int[] EU = new int[numOfEvents];
        int[][] dAdjM = new int[numOfEvents][numOfEvents];

        for(int i=0;i<numOfEvents;i++){
            EU[StdIn.readInt()] = StdIn.readInt();
        }
        for(int i=0;i<dAdjM.length;i++){
            for(int j=0;j<dAdjM[i].length;j++){
                dAdjM[i][j] = StdIn.readInt();
            }
        }

        int[] maxCosts = new int[numOfEvents];

        //set source to 0 and the rest to infinity
        maxCosts[0]=EU[0];
        for(int i=1;i<numOfEvents;i++){
            maxCosts[i] = Integer.MIN_VALUE;
        }

        ArrayList<Integer> timelines = new ArrayList<>();
        timelines.add(0,EU[0]);//add Eu[0] because 0 itself needs to be counted as a timeline
        dfs(maxCosts,dAdjM,EU,timelines,0);
        
        //count the number of winnable futures
        int winnable = 0;
        for(Integer a : timelines){
            if(a>=threshold)winnable++;
        }
        //set outfile
        String output = args[1];
        StdOut.setFile(output);
        //print out
        StdOut.print(timelines.size()+ "\n" + winnable);

    }

    public static void dfs(int[] maxCosts, int[][] dAdjM, int[] EU, ArrayList<Integer> timeslines, int vertex){
        for(int j=0;j<dAdjM[vertex].length;j++){
            if(maxCosts[vertex]!=Integer.MIN_VALUE && dAdjM[vertex][j]!=0){
                maxCosts[j] = maxCosts[vertex] + EU[j];
                timeslines.add(maxCosts[j]);
                dfs(maxCosts, dAdjM, EU, timeslines,j);
            }
        }
        
    }
}
