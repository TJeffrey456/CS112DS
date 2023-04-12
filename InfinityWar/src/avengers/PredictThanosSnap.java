package avengers;

/**
 * Given an adjacency matrix, use a random() function to remove half of the nodes. 
 * Then, write into the output file a boolean (true or false) indicating if 
 * the graph is still connected.
 * 
 * Steps to implement this class main method:
 * 
 * Step 1:
 * PredictThanosSnapInputFile name is passed through the command line as args[0]
 * Read from PredictThanosSnapInputFile with the format:
 *    1. seed (long): a seed for the random number generator
 *    2. p (int): number of people (vertices in the graph)
 *    2. p lines, each with p edges: 1 means there is a direct edge between two vertices, 0 no edge
 * 
 * Note: the last p lines of the PredictThanosSnapInputFile is an ajacency matrix for
 * an undirected graph. 
 * 
 * The matrix below has two edges 0-1, 0-2 (each edge appear twice in the matrix, 0-1, 1-0, 0-2, 2-0).
 * 
 * 0 1 1 0
 * 1 0 0 0
 * 1 0 0 0
 * 0 0 0 0
 * 
 * Step 2:
 * Delete random vertices from the graph. You can use the following pseudocode.
 * StdRandom.setSeed(seed);
 * for (all vertices, go from vertex 0 to the final vertex) { 
 *     if (StdRandom.uniform() <= 0.5) { 
 *          delete vertex;
 *     }
 * }
 * Answer the following question: is the graph (after deleting random vertices) connected?
 * Output true (connected graph), false (unconnected graph) to the output file.
 * 
 * Note 1: a connected graph is a graph where there is a path between EVERY vertex on the graph.
 * 
 * Note 2: use the StdIn/StdOut libraries to read/write from/to file.
 * 
 *   To read from a file use StdIn:
 *     StdIn.setFile(inputfilename);
 *     StdIn.readInt();
 *     StdIn.readDouble();
 * 
 *   To write to a file use StdOut (here, isConnected is true if the graph is connected,
 *   false otherwise):
 *     StdOut.setFile(outputfilename);
 *     StdOut.print(isConnected);
 * 
 * @author Yashas Ravi
 * Compiling and executing:
 *    1. Make sure you are in the ../InfinityWar directory
 *    2. javac -d bin src/avengers/*.java
 *    3. java -cp bin avengers/PredictThanosSnap predictthanossnap.in predictthanossnap.out
*/

public class PredictThanosSnap {
	 
    public static void main (String[] args) {
 
        if ( args.length < 2 ) {
            StdOut.println("Execute: java PredictThanosSnap <INput file> <OUTput file>");
            return;
        }
        
    	// WRITE YOUR CODE HERE
        //setfile
        String input = args[0];
        StdIn.setFile(input);

        //take seed in as LONG
        StdRandom.setSeed(StdIn.readLong());

        //num of vertices
        int numOfVertices = StdIn.readInt();
        //list of vertices
        boolean[] deadV = new boolean[numOfVertices];
        //set up the adj matrix
        int[][] adjM = new int[numOfVertices][numOfVertices];
        for(int i=0;i<adjM.length;i++){
            for(int j=0;j<adjM[i].length;j++){
                adjM[i][j] = StdIn.readInt();
            }
        }

        //go through all vertices and 50/50 delete
        for(int k=0;k<deadV.length;k++){
            
            if(StdRandom.uniform()<=0.50){
                StdOut.print(k);
                deadV[k] = true;
                deleteVertex(k, adjM);
            }
        }

        
        //output
        String output = args[1];
        StdOut.setFile(output);
        StdOut.print(checkConnected(adjM, deadV));
    }

    public static void deleteVertex(int vertex, int[][] m){
        //delete all data in row
        for(int i=0;i<m.length;i++){
            m[vertex][i] = 0;
        }
        //delete all data in column
        for(int j=0;j<m[vertex].length;j++){
            m[j][vertex] = 0;
        }
    }

    //check if eveything is still connected
    //do a BFS and my process list will tell me if its all connected
    public static boolean checkConnected(int[][] m, boolean[] d){
        //check if its alone
        for(int i=0;i<d.length;i++){
            if(!d[i]){
                boolean hasAdf = false;
                for(int j=0;j<d.length;j++){
                    if(m[i][j]==1){
                        hasAdf = true;
                    }
                }
                if(!hasAdf){
                    return false;
                }
            }
            
        }


        return true;
    }
    
}
