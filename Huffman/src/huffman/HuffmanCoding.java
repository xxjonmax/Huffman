package huffman;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import javax.xml.transform.Source;

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
        sortedCharFreqList = new ArrayList();
        StdIn.setFile(fileName);
        //initialize array
        int[] charOccs = new int[128];
        int charcount=0;
        while (StdIn.hasNextChar()){
            char index = StdIn.readChar();
            ++charOccs[index];
            ++charcount;
        }
        for (char i=(char)0;i<charOccs.length;i++){
            if (charOccs[i]!=0){
                CharFreq charfreq = new CharFreq(i,(double)charOccs[i]/charcount);
                sortedCharFreqList.add(charfreq);
            }
        }
        if (sortedCharFreqList.size()==1){
            CharFreq fix = new CharFreq((char)((sortedCharFreqList.get(0).getCharacter()+1)%128), 0.0);
            sortedCharFreqList.add(fix);
        }
        Collections.sort(sortedCharFreqList);
    }

    /**
     * Uses sortedCharFreqList to build a huffman coding tree, and stores its root
     * in huffmanRoot
     */
    /* public void makeTree() {
        // make tree of Tree nodes that contain charfreqs as data
        Queue<TreeNode> Source = new Queue();
        Queue<TreeNode> Target = new Queue();
        for (int i=0;i<sortedCharFreqList.size();i++){
            TreeNode node = new TreeNode(sortedCharFreqList.get(i),null,null);
            Source.enqueue(node);
        }
        if (!Source.isEmpty()){
            TreeNode[] nodes = new TreeNode[2];
            while (!Source.isEmpty()){
                for (int i=0;i<2;i++){
                    if (!Target.isEmpty() && Source.peek().getData().compareTo(Target.peek().getData())>0){
                        nodes[i] = Target.dequeue(); 
                    } else{
                        nodes[i] = Source.dequeue();
                        if (Source.isEmpty()){
                            if (!Target.isEmpty()){
                                nodes[1] = Target.dequeue();
                            }
                            break;
                        }
                    }
                    System.out.println(nodes[i].getData().getCharacter());
                }
                double newFreq = nodes[0].getData().getProbOcc()+nodes[1].getData().getProbOcc();
                CharFreq newData = new CharFreq(null,newFreq);
                TreeNode newNode = new TreeNode(newData, nodes[0],nodes[1]);
                Target.enqueue(newNode);
            } while (Target.size()>1){
                nodes[0] = Target.dequeue();
                nodes[1] = Target.dequeue();
                double newFreq = nodes[0].getData().getProbOcc()+nodes[1].getData().getProbOcc();
                CharFreq newData = new CharFreq(null,newFreq);
                TreeNode newNode = new TreeNode(newData, nodes[0],nodes[1]);
                Target.enqueue(newNode);
            }
            huffmanRoot = Target.dequeue();
        }else{
            huffmanRoot = null;
        }
    } */
    public static TreeNode dequeue_smallest(Queue<TreeNode> q1, Queue<TreeNode> q2){
        TreeNode one = null;
        TreeNode two = null;
        if(!q1.isEmpty()){
            one = q1.peek();
        }
        if(!q2.isEmpty()){
            two = q2.peek();
        }
        TreeNode result;
        if (one != null && two != null){
            if (one.getData().getProbOcc()<=two.getData().getProbOcc())
                result = q1.dequeue();
            else
                result = q2.dequeue();
        }
        else if (one != null)
            result = q1.dequeue();
        else if (two != null)
            result = q2.dequeue();
        else
            result = null;
        return result;
    }

    public static TreeNode combine_tree_nodes(TreeNode left, TreeNode right){
        double combined_freq = left.getData().getProbOcc() + right.getData().getProbOcc();
        CharFreq combined_CharFreq_node = new CharFreq(null, combined_freq);
        TreeNode combined_node = new TreeNode(combined_CharFreq_node, left, right);
        return combined_node;
    }

    public void makeTree(){
        //make source queue and target queue
        Queue<TreeNode> src= new Queue<>(), dest = new Queue<TreeNode>();
        //makes a new treenode for every charfreq
        for(CharFreq CharFreq: sortedCharFreqList)
            src.enqueue(new TreeNode(CharFreq, null, null));

        TreeNode left, right;
        while (!src.isEmpty()){
            left = dequeue_smallest(src, dest);
            right = dequeue_smallest(src, dest);
            if (left != null && right != null){
                TreeNode combined_node = combine_tree_nodes(left, right);
                dest.enqueue(combined_node);
            }
            else if (left != null)
                dest.enqueue(left);
            else
                dest.enqueue(right);
        }
        while (dest.size() > 1){
            left = dest.dequeue();
            right = dest.dequeue();
            TreeNode combined_node = combine_tree_nodes(left, right);
            dest.enqueue(combined_node);
        }
        huffmanRoot = dest.dequeue();
    }

    /* public void makeTree(){
        //1
        Queue<TreeNode> source = new Queue<>();
        Queue<TreeNode> target = new Queue<>();
        TreeNode[] trashNodes = new TreeNode[2];
        //2&3
        for (int i=0;i<sortedCharFreqList.size();i++){
            TreeNode node = new TreeNode(sortedCharFreqList.get(i),null,null);
            source.enqueue(node);
        }
        //4
        //do first
        for(int i=0;i<2&&!source.isEmpty();i++){
            Boolean isSource = source.peek().getData().compareTo(target.peek().getData())<=0;
            if (target.isEmpty()){
                isSource = true;
            }
            if (isSource){
                trashNodes[i] = source.dequeue(); 
            } else{
                trashNodes[i] = target.dequeue();
                }
        }
        double newFreq = trashNodes[0].getData().getProbOcc()+trashNodes[1].getData().getProbOcc();
        CharFreq newData = new CharFreq(null,newFreq);
        TreeNode newNode = new TreeNode(newData, trashNodes[0],trashNodes[1]);
        target.enqueue(newNode);
        //do as a loop
        while (!source.isEmpty()&&target.size()!=1){
            for(int i=0;i<2&&!source.isEmpty();i++){
                Boolean isSource = source.peek().getData().compareTo(target.peek().getData())<=0;
                if (target.isEmpty()){
                    isSource = true;
                }
                if (isSource){
                    trashNodes[i] = source.dequeue(); 
                } else{
                    trashNodes[i] = target.dequeue();
                    }
            }
            newFreq = trashNodes[0].getData().getProbOcc()+trashNodes[1].getData().getProbOcc();
            newData = new CharFreq(null,newFreq);
            newNode = new TreeNode(newData, trashNodes[0],trashNodes[1]);
            target.enqueue(newNode);
        }
    } */

    /**
     * Uses huffmanRoot to create a string array of size 128, where each
     * index in the array contains that ASCII character's bitstring encoding. Characters not
     * present in the huffman coding tree should have their spots in the array left null.
     * Set encodings to this array.
     */
    /* private String mapBits(Character targChar){
        String solution = "";
        TreeNode target = 
        TreeNode cursor = huffmanRoot;
        while (target!=cursor){
           while (cursor.getData()!=null){
               cursor = cursor.getRight();
           }
           if (cursor!=target){}
        }
        return null
    } */
    private void preOrder(TreeNode node, String[] codes, ArrayList<String> bits){
        if(node.getData().getCharacter()!=null){
            codes[node.getData().getCharacter()]=String.join("", bits);
            bits.remove(bits.size()-1);
            return;
        }
        if (node.getLeft()!=null){
            bits.add("0");
        }
        preOrder(node.getLeft(), codes, bits);
        if (node.getRight()!=null){
            bits.add("1");
        }
        preOrder(node.getRight(), codes, bits);
        if (!bits.isEmpty()){
            bits.remove(bits.size()-1);
        }
    }
    
    public void makeEncodings() {
        String[] codes = new String[128];
        ArrayList<String> bits = new ArrayList<>();
        preOrder(huffmanRoot, codes, bits);
        encodings = codes;
    }
    /*
    TreeNode left = cursor.getLeft();
            TreeNode right = cursor.getRight();
            if(left.getData().getCharacter()!=null){
                char targ = left.getData().getCharacter();
                if (bits.isEmpty()){
                    codes[targ]="0";
                    percentDone+=left.getData().getProbOcc();
                } else{
                    String assignement = "";
                    while (!bits.isEmpty()){
                        assignement=assignement+bits.dequeue();
                    }
                    bits.enqueue(assignement);
                    codes[targ]=assignement;
                    percentDone+=left.getData().getProbOcc();
                }
            }
            if (right.getData().getCharacter()!=null){
                char targ = right.getData().getCharacter();
                if (bits.isEmpty()){
                    codes[targ]="0";
                    percentDone+=right.getData().getProbOcc();
                } else{
                    String assignement = "";
                    while (!bits.isEmpty()){
                        assignement=assignement+bits.dequeue();
                    }
                    bits.enqueue(assignement);
                    codes[targ]=assignement;
                    percentDone+=right.getData().getProbOcc();
                }
            }
            */

    /**
     * Using encodings and filename, this method makes use of the writeBitString method
     * to write the final encoding of 1's and 0's to the encoded file.
     * 
     * @param encodedFile The file name into which the text file is to be encoded
     */
    public void encode(String encodedFile) {
        StdIn.setFile(fileName);

	/* Your code goes here */
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
