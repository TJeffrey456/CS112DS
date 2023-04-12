package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.CheckedInputStream;

import javax.sound.sampled.AudioFormat.Encoding;

/**
 * This class contains methods which, when used together, perform the
 * entire Huffman Coding encoding and decoding process
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class HuffmanCoding {
    private String fileName;
    private ArrayList<CharFreq> sortedCharFreqList;
    private TreeNode huffmanRoot;
    private String[] encodings;

    /**
     * Constructor used by the driver, sets filename
     * DO NOT EDIT
     * @param f The file we want to encode
     */
    public HuffmanCoding(String f) { 
        fileName = f; 
    }

    /**
     * Reads from filename character by character, and sets sortedCharFreqList
     * to a new ArrayList of CharFreq objects with frequency > 0, sorted by frequency
     */
    public void makeSortedList() {
        StdIn.setFile(fileName);
        //make temp arr
        int[] ASCII = new int[128];
        //need to count number of scanned chars
        double charScanned = 0;//made a double so it can do float division

        //dump until it is empty
        while(StdIn.hasNextChar()){
            ASCII[StdIn.readChar()] +=1;
            //this notation bc it will convert char into ASCII
            charScanned++;
        }

        //instantiate Array List
        sortedCharFreqList = new ArrayList<>();
        //adding to Array List
        for(int i=0;i<ASCII.length;i++){
            //only add if there is more than 0 occurances
            if(ASCII[i]>0){
                sortedCharFreqList.add(new CharFreq((char)i, (ASCII[i]/charScanned)));
                //convert index back into its char
            }
        }

        /*The Huffman Coding algorithm does not work when there is only 1 distinct character. For this specific case, 
        you must add a different character with probOcc 0 to your ArrayList, so you can build a valid tree and encode 
        properly later. For this assignment, simply add the character with ASCII value one more than the distinct character. 
        If you are already at ASCII value 127, wrap around to ASCII 0. DO NOT add more than one of these, and also DO NOT add 
        any characters with frequency 0 in any normal input case. 
        */
        if(sortedCharFreqList.size()==1){
            if(sortedCharFreqList.get(0).getCharacter()==127){
                sortedCharFreqList.add(new CharFreq((char)0, 0));
            } else {
                sortedCharFreqList.add(new CharFreq((char)(sortedCharFreqList.get(0).getCharacter()+1), 0));
            }
        }

        //sort
        Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {
        //STEP 1
        //make two queues for comparison
	    Queue<CharFreq> source = new Queue<>();//from sorted CharFreqList
        Queue<TreeNode> target = new Queue<>();

        //STEP 2 and STEP 3
        //fill source queue with sortedCharFreqList
        for(int i=0;i<sortedCharFreqList.size();i++){
            source.enqueue(sortedCharFreqList.get(i));
        }

        //to start tree and set up huffman algo first iteration of STEP 4
        //compare once
        TreeNode leftNode = new TreeNode(source.dequeue(), null, null);
        //compare twice
        TreeNode rightNode = new TreeNode(source.dequeue(), null, null);
        //left will be less than right because the queue is in ascending order

        //make a new CharFreq w/ combined prob
        CharFreq addedProb = new CharFreq(null, leftNode.getData().getProbOcc() + rightNode.getData().getProbOcc());
        //make a new null node w/ addedProb
        TreeNode combinedNode = new TreeNode(addedProb, leftNode, rightNode);
        target.enqueue(combinedNode);//add it to queue
        
        //rest of interation for Huffman algo STEP 4
        //loop until source is empty or target only has one
        while(!(source.isEmpty()) || target.size()!=1){
            //update left
            leftNode = findLow(source, target);
            //update right
            //in case there was only one in target which just gor removed
            rightNode = findLow(source, target);

            addedProb = new CharFreq(null, leftNode.getData().getProbOcc() + rightNode.getData().getProbOcc());
            //make a new null node w/ addedProb
            combinedNode = new TreeNode(addedProb, leftNode, rightNode);
            target.enqueue(combinedNode);//add it to target queue
        }
        //save complete Huffman algo to huffman root
        huffmanRoot = target.dequeue();
    }

    //helper method for huffman
    private static TreeNode findLow(Queue<CharFreq> s, Queue<TreeNode> t){
        TreeNode tn = new TreeNode();
        if(s.isEmpty()){ //source is empty
            tn = t.dequeue();
        } else if(t.isEmpty()){ //target is empty
            tn = new TreeNode(s.dequeue(), null, null);
        } else if(s.peek().getProbOcc()<=t.peek().getData().getProbOcc()){ //neither is empty
            tn = new TreeNode(s.dequeue(), null, null); //source has lowest prob
        } else { 
            tn = t.dequeue();//target has lowest prob
        }

        return tn;
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {

	    //assign arr to hold ASCII
        encodings = new String[128];

        //go check the sortedCharFreqList
        for(int i=0;i<sortedCharFreqList.size();i++){
            char checkLetter = sortedCharFreqList.get(i).getCharacter();
            reachEnd(huffmanRoot, checkLetter, "");
        }
    }

    //helper method to reach the end
    private void reachEnd(TreeNode tn, char target, String eString){
        if(tn.getData().getCharacter()!=null){//if its a letter
            if(tn.getData().getCharacter()==target){//and its the target
                encodings[target] = eString;
            }//dont need to set to null because all string objects by default are null
        } else {//its not a char
            if(tn.getLeft()!=null){//left is possbile go left
                reachEnd(tn.getLeft(), target, eString+"0");
            } 
            if(tn.getRight()!=null){//right is possible
                reachEnd(tn.getRight(), target, eString+"1");
            }
        }
    } 

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);//read file in
        String encodedMsg = "";
        //while the file has characters I want to encode into my compression into a String
        while(StdIn.hasNextChar()){
            char letterToEncode = StdIn.readChar();
            //linear search my array bc its random and unsorted
            for(int i=0;i<encodings.length;i++){
                if(i==(int)letterToEncode){//take letter and convert to ASCII
                    encodedMsg += encodings[i];
                }
            }
        }
        //writeString into the file
        writeBitString(encodedFile, encodedMsg);
    }
    
    /**
     * Writes a given string of 1's and 0's to the given file byte by byte
     * and NOT as characters of 1 and 0 which take up 8 bits each
     * DO NOT EDIT
     * 
     * @param filename The file to write to (doesn't need to exist yet)
     * @param bitString The string of 1's and 0's to write to the file in bits
     */
    public static void writeBitString(String filename, String bitString) {
        byte[] bytes = new byte[bitString.length() / 8 + 1];
        int bytesIndex = 0, byteIndex = 0, currentByte = 0;

        // Pad the string with initial zeroes and then a one in order to bring
        // its length to a multiple of 8. When reading, the 1 signifies the
        // end of padding.
        int padding = 8 - (bitString.length() % 8);
        String pad = "";
        for (int i = 0; i < padding-1; i++) pad = pad + "0";
        pad = pad + "1";
        bitString = pad + bitString;

        // For every bit, add it to the right spot in the corresponding byte,
        // and store bytes in the array when finished
        for (char c : bitString.toCharArray()) {
            if (c != '1' && c != '0') {
                System.out.println("Invalid characters in bitstring");
                return;
            }

            if (c == '1') currentByte += 1 << (7-byteIndex);
            byteIndex++;
            
            if (byteIndex == 8) {
                bytes[bytesIndex] = (byte) currentByte;
                bytesIndex++;
                currentByte = 0;
                byteIndex = 0;
            }
        }
        
        // Write the array of bytes to the provided file
        try {
            FileOutputStream out = new FileOutputStream(filename);
            out.write(bytes);
            out.close();
        }
        catch(Exception e) {
            System.err.println("Error when writing to file!");
        }
    }

    /**
     * Using a given encoded file name, this method makes use of the readBitString method 
     * to convert the file into a bit string, then decodes the bit string using the 
     * tree, and writes it to a decoded file. 
     * 
     * @param encodedFile The file which has already been encoded by encode()
     * @param decodedFile The name of the new file we want to decode into
     */
    public void decode(String encodedFile, String decodedFile) {
        StdOut.setFile(decodedFile);//set output file
        String encodedMsg = readBitString(encodedFile);
        int encodeLength = 1;//length of my encoding
        ArrayList<String> encodingsList= new ArrayList<>(Arrays.asList(encodings));
        //check character by character to see if it is contained in the encodings array
        while(encodedMsg.length()>0){
            //find the index of it, will be neg if not found
            int index = encodingsList.indexOf(encodedMsg.substring(0,encodeLength));
            if(index>0){//its in the array
                //write it to the file
                StdOut.print((char)index);
                //cut that part out 
                encodedMsg = encodedMsg.substring(encodeLength);
                //then reset length
                encodeLength = 1;
            } else {
                //add one to length
                encodeLength++;
            }
        }
        //ran into issue where further testing leads to all printing done in decoded and not terminal
        //found work around on Piazza note@322
        //set output back to terminal
        //StdOut.open();
    }

    /**
     * Reads a given file byte by byte, and returns a string of 1's and 0's
     * representing the bits in the file
     * DO NOT EDIT
     * 
     * @param filename The encoded file to read from
     * @return String of 1's and 0's representing the bits in the file
     */
    public static String readBitString(String filename) {
        String bitString = "";
        
        try {
            FileInputStream in = new FileInputStream(filename);
            File file = new File(filename);

            byte bytes[] = new byte[(int) file.length()];
            in.read(bytes);
            in.close();
            
            // For each byte read, convert it to a binary string of length 8 and add it
            // to the bit string
            for (byte b : bytes) {
                bitString = bitString + 
                String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
            }

            // Detect the first 1 signifying the end of padding, then remove the first few
            // characters, including the 1
            for (int i = 0; i < 8; i++) {
                if (bitString.charAt(i) == '1') return bitString.substring(i+1);
            }
            
            return bitString.substring(8);
        }
        catch(Exception e) {
            System.out.println("Error while reading file!");
            return "";
        }
    }

    /*
     * Getters used by the driver. 
     * DO NOT EDIT or REMOVE
     */

    public String getFileName() { 
        return fileName; 
    }

    public ArrayList<CharFreq> getSortedCharFreqList() { 
        return sortedCharFreqList; 
    }

    public TreeNode getHuffmanRoot() { 
        return huffmanRoot; 
    }

    public String[] getEncodings() { 
        return encodings; 
    }
}
