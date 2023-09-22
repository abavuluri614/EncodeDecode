package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;

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

	    /* Your code goes here */
        double[] freqArray = new double[128];
        double count = 0;
        sortedCharFreqList = new ArrayList<CharFreq>();
        while(StdIn.hasNextChar())
        {
            freqArray[StdIn.readChar()]++;
            count++;
        }
        for(int i = 0; i < 128; i++)
        {
            if(freqArray[i] != 0)
            {
                int num = i;
                CharFreq c = new CharFreq((char)num, ((double)(freqArray[i])/count));
                sortedCharFreqList.add(c);
            }
        }
        //if only one distinct character
        if(sortedCharFreqList.size() == 1)
        {
            //if it is at index 127, add it to index 0 with freq 0
            if((int)(sortedCharFreqList.get(0).getCharacter()) == 127)
                sortedCharFreqList.add(new CharFreq((char)(0), 0));
            
            //add next character with freq 0
            else
            {
                CharFreq n = sortedCharFreqList.get(0);
                int x = (int)(n.getCharacter());
                CharFreq second = new CharFreq((char)(x+1), 0);
                sortedCharFreqList.add(second);
            }

           
        }
        Collections.sort(sortedCharFreqList);

    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    public void makeTree() {

	    /* Your code goes here */
        Queue<TreeNode> source = new Queue<TreeNode>();
        Queue<TreeNode> target = new Queue<TreeNode>();

        //create source queue
        for(int i = 0; i < sortedCharFreqList.size(); i++)
        {
            //create a new treeNode with data of 1st obj in SCFL
            TreeNode t = new TreeNode();
            t.setData(sortedCharFreqList.get(i));

            //enqueue the node into source 
            source.enqueue(t);
        }

        //get first dequeued node
        //while source is not empty and target has only 1 node
        //i need to find the right conditional to put in here. 
        while(!source.isEmpty() || !(target.size() == 1))
        {
            if(target.size() == 1)
            {
                if((target.peek().getData().getProbOcc() == 1))
                    break;
            }
            //dequeue the 2 least probOcc
            TreeNode first = new TreeNode();
            TreeNode second = new TreeNode();

            //if target is empty
            if(target.isEmpty())
            {
                first = source.dequeue();
            }
            //if source is empty
            else if(source.isEmpty())
            {
                first = target.dequeue();
            }
            //if the peek of source is less than the peek of target
            else if(source.peek().getData().getProbOcc() < target.peek().getData().getProbOcc())
            {
                first = source.dequeue();
            }
            //if peek of target is less than peek of source
            else if(source.peek().getData().getProbOcc() > target.peek().getData().getProbOcc())
            {
                first = target.dequeue();
            }
            
            //get second dequeued node
            //if target is empty
            if(target.isEmpty())
            {
                second = source.dequeue();
            }
            //if source is empty
            else if(source.isEmpty())
            {
                second = target.dequeue();

            }
            //if peek of source is less than peek of target
            else if(source.peek().getData().getProbOcc() < target.peek().getData().getProbOcc())
            {
                second = source.dequeue();
            }
            //if peek of target is less than peek of source
            else if(source.peek().getData().getProbOcc() > target.peek().getData().getProbOcc())
            {
                second = target.dequeue();
            }
            //if they are both equal
            else if(source.peek().getData().getProbOcc() == target.peek().getData().getProbOcc())
            {
                if(first.getData() == null)
                {
                    first = source.dequeue();
                    if(target.isEmpty())
                    {
                        second = source.dequeue();
                    }
                    else if(source.isEmpty())
                    {
                        second = target.dequeue();
                    }
                    else if(source.peek().getData().getProbOcc() < target.peek().getData().getProbOcc())
                    {
                        second = source.dequeue();
                    }
                    else if(source.peek().getData().getProbOcc() > target.peek().getData().getProbOcc())
                    {
                        second = target.dequeue();
                    }
                    else
                    {
                        second = source.dequeue();
                    }
                }
                else if(first.getData() != null)
                {
                    second = source.dequeue();
                }
            }
            
            //create a null node will null and prob of combined dequeued
            CharFreq combo = new CharFreq(null, first.getData().getProbOcc() + second.getData().getProbOcc());
            huffmanRoot = new TreeNode(combo, first, second);

            //enqueue into target
            target.enqueue(huffmanRoot);

        }
    }

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    public void makeEncodings() {

	    /* Your code goes here */
        encodings = new String[128];
        recursion(huffmanRoot, "", encodings);
    }

    private void recursion(TreeNode root, String s, String[] en)
    {

        if(root.getLeft() == null && root.getRight() == null)
        {
            en[(int)(root.getData().getCharacter())] = s;

        }
        else
        {
            recursion(root.getLeft(), s + "0", en);
            recursion(root.getRight(), s + "1", en);
        }

    }

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {

        StdIn.setFile(fileName);

        //get how many characters are in the input file 
        ArrayList<Character> charList = new ArrayList<Character>();
        while(StdIn.hasNextChar())
        {
            charList.add(StdIn.readChar());
        }

        String[] stringArray = new String[charList.size()];

        for(int i = 0; i < charList.size(); i++)
        {
            char c = charList.get(i);
            for(int j = 0; j < encodings.length; j++)
            {
                if((int)c == j)
                {
                    String s = encodings[c];
                    stringArray[i] = s;
                }
            }
        }

        String encoded = new String("");
        for(int i = 0; i < stringArray.length; i++)
        {
            encoded = encoded + stringArray[i];
        }

        writeBitString(encodedFile, encoded);
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
        StdOut.setFile(decodedFile);

	    /* Your code goes here */
        //read the bit string to get the string of 1's and 0's
        String onesAndZeroes = readBitString(encodedFile);

        //create an array of all the individual numbers
        char[] numbers = new char[onesAndZeroes.length()];
        for(int i = 0; i < onesAndZeroes.length(); i++)
        {
            numbers[i] = onesAndZeroes.charAt(i);
        }
        
        TreeNode root = new TreeNode();
        //traverse the tree using the string
        int count = 0;

        while(count < numbers.length)
        {
            root = huffmanRoot;
            String s = "";
            while(root.getLeft() != null && root.getRight() != null)
            {    
                if(numbers[count] == '0')
                {
                    root = root.getLeft();
                    s = s + "0";
                    count++;

                }
                else if(numbers[count] == '1')
                {
                    root = root.getRight();
                    s = s + "1";
                    count++;
                }
            }

            StdOut.print(root.getData().getCharacter());
        }

       
       
       
    }

    /**
     * Reads a given file byt
     * e by byte, and returns a string of 1's and 0's
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
