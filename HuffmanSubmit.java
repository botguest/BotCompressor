/*
Author: Jingxing Xu
Email: jxu79@u.rochester.edu
Date: November 20 2022
Student ID: 32101577
Lab: CSC172-14 Mon/Wed 14:00-15:15
Workshop: CSC172-19 Thursday 16:50-18:05
*/

import java.util.*;

public class HuffmanSubmit implements Huffman {

    //Vars for encode.
    public BinaryIn in; //BinaryIn analyzer. HELPER
    public ArrayList<Character> in_list; //All input chars in the file. Repeat included. FINAL
    public HashMap<Character, Integer> char_freq_map; //Output of get_char_freq_map(String file_name). FINAL
    public ArrayList<HuffmanNode> char_freq_list; //Output of get_char_freq_list(HashMap<Character, Integer> char_freq_table). FINAL
    public PriorityQueue<HuffmanNode> heap; //The heap of Huffman nodes! FINAL
    public HuffmanNode root_node; //Output of huff_tree_constructor(PriorityQueue<HuffmanNode> min_heap). FINAL
    public HashMap<Character, String> huff_map; //The better code book for each char. FINAL after huff_assign_code.
    //Vars for encode.

    //Vars for decode.
    public BinaryIn freq_in; //BinaryIn analyzer. HELPER
    public HashMap<Character, Integer> char_freq_map_decode;
    public ArrayList<HuffmanNode> char_freq_list_decode;
    public PriorityQueue<HuffmanNode> heap_decode;
    public HuffmanNode root_node_decode;
    //Vars for decode.

    //HuffmanNode is done.
    public class HuffmanNode { //This is a node in a huffman tree

        public char character;
        public int freq;
        public HuffmanNode left, right;

        //Constructors
        public HuffmanNode(boolean dummy){
            character = '\u0000'; //Unicode for null
            freq = -1;
            if (dummy){
                left = null;
                right = null;
            }
            else {
                left = new HuffmanNode(true);
                right = new HuffmanNode(true);
            }
        }

        public HuffmanNode(char character_input, int freq_input){
            character = character_input;
            freq = freq_input;
            left = new HuffmanNode(true);
            right = new HuffmanNode(true);
        }
        //Constructors
    }
    //HuffmanNode is done.

    //Class constructor.
    public HuffmanSubmit(){

    }
    //Class constructor.

    //The following methods are for encode().

    //The process of getting a heap: get_char_freq_map -- get_char_freq_list -- get_heap

    //This method makes a char_freq_map.
    public HashMap<Character, Integer> get_char_freq_map(String file_name){
        //Playing around with binaryIn.
        in = new BinaryIn(file_name);
        in_list = new ArrayList<>();
        while (!in.isEmpty()){
            in_list.add(in.readChar());
        } //in_list is now filled with chars in the file.

        char_freq_map = new HashMap<>();

        for (int i = 0; i < in_list.size(); i++){
            if (char_freq_map.containsKey(in_list.get(i))){
                char current_char = in_list.get(i);
                Integer val_original = char_freq_map.get(current_char);
                Integer val_new = val_original + 1;
                char_freq_map.replace(current_char, val_new);
                continue;
            }
            char_freq_map.put(in_list.get(i), 1);
        }

        //System.out.println(char_freq_map); //De-comment to debug.
        return char_freq_map;
    }
    //This method makes a char_freq_map.

    //This method makes a char_freq_list of huffman nodes.
    public ArrayList<HuffmanNode> get_char_freq_list(HashMap<Character, Integer> char_freq_table){
        Set<Character> char_set = char_freq_table.keySet();
        ArrayList<Character> char_list = new ArrayList<>(char_set); //Building an arrayList of keys.

        char_freq_list = new ArrayList<>();

        for (int i = 0; i < char_list.size(); i++){
            HuffmanNode current_node = new HuffmanNode(char_list.get(i), char_freq_table.get(char_list.get(i)));
            char_freq_list.add(current_node);
        }

        return char_freq_list;
    }
    //This method makes a char_freq_list of huffman nodes.

    //This method builds a priority queue of Huffman nodes.
    public PriorityQueue<HuffmanNode> get_heap(ArrayList<HuffmanNode> char_freq_list){
        Comparator<HuffmanNode> huffComparator = new Comparator<HuffmanNode>() {
            @Override
            public int compare(HuffmanNode o1, HuffmanNode o2) {
                return Integer.compare(o1.freq, o2.freq);
            }
        };

        heap = new PriorityQueue<>(huffComparator);

        heap.addAll(char_freq_list);

        return heap;
    }
    //This method builds a priority queue of Huffman nodes.

    //This method builds the huffman tree from min_heap. Returns root node de huff_tree.
    public HuffmanNode huff_tree_constructor(PriorityQueue<HuffmanNode> min_heap) {

        if (min_heap.size() <= 3) {
            throw new IllegalArgumentException("The input file needs to contain more kinds of characters!");
        }

        while (min_heap.size() > 1) {

            HuffmanNode first_node = min_heap.poll();
            HuffmanNode second_node = min_heap.poll();

            HuffmanNode first_root = new HuffmanNode('\u0000', first_node.freq + second_node.freq);
            first_root.left = first_node;
            first_root.right = second_node;
            min_heap.add(first_root);
        }

        root_node = min_heap.peek();

        huff_map = new HashMap<>(); //Construct the better code map.

        return min_heap.peek();

        //Repeat this until the heap contains only one node.
    }
    //This method builds the huffman tree from min_heap. Returns root node de huff_tree.

    //This method builds a new and better code book. The book is a Hashmap, huff_map.
    public void huff_assign_code(HuffmanNode huff_current, String code){
        //if it's root node: Add it to Hashmap.
        if (huff_current.left.freq == -1 && huff_current.right.freq == -1) {
            huff_map.put(huff_current.character, code);
            return;
        }

        huff_assign_code(huff_current.left, code + "0");
        huff_assign_code(huff_current.right, code + "1");
    }
    //This method builds a new and better code book. The book is a Hashmap, huff_map.

    //This method creates the encoded file with the codebook. MUST BE CALLED AFTER CALLING EVERYTHING ABOVE!
    public void output_file_generator(String output_file_name){

        BinaryOut output_file_out = new BinaryOut(output_file_name);

        for (Character current_character: in_list){
            if (huff_map.containsKey(current_character)){
                for (int i = 0; i < huff_map.get(current_character).length(); i++){
                    if (huff_map.get(current_character).charAt(i) == '0'){
                        output_file_out.write(false);
                    }
                    if (huff_map.get(current_character).charAt(i) == '1'){
                        output_file_out.write(true);
                    }
                }
            }
        }

        output_file_out.close();

    }
    //This method creates the encoded file with the codebook. MUST BE CALLED AFTER CALLING EVERYTHING ABOVE!

    //This method generates the frequency file. MUST BE CALLED AFTER CALLING get_heap!!!
    public void freq_file_generator(String freq_file_name){

        BinaryOut fre_file_out = new BinaryOut(freq_file_name);
        for (HuffmanNode current_node: char_freq_list){
            fre_file_out.write(Integer.valueOf((int)current_node.character).toString());
            fre_file_out.write(":"); //String
            fre_file_out.write(String.valueOf(current_node.freq)); //String
            fre_file_out.write("\n"); //String
        }

        fre_file_out.close();
    }
    //This method generates the frequency file. MUST BE CALLED AFTER CALLING get_heap!!!

    //The above methods are for encode().

    //Finally, I've reached the encode method!!
	public void encode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here
        HuffmanNode root = this.huff_tree_constructor(get_heap(get_char_freq_list(get_char_freq_map(inputFile))));
        this.huff_assign_code(root, "");

        this.freq_file_generator(freqFile);
        this.output_file_generator(outputFile);
   }

   //The following methods are for decode().

   public HashMap<Character, Integer> freq_file_to_char_freq_list(String freqFile){
        freq_in = new BinaryIn(freqFile);
       char_freq_map_decode = new HashMap<>();

       boolean is_char = true;
       char latest_char = '\u0000';
       StringBuilder current_char_builder = new StringBuilder();
       StringBuilder current_freq_builder = new StringBuilder();
       current_char_builder.setLength(0);
       current_freq_builder.setLength(0);

       while (!freq_in.isEmpty()){

           char current_char = freq_in.readChar();

           if (current_char == ':'){
               is_char = false;
               char char_result = (char) Integer.parseInt(current_char_builder.toString());
               latest_char = char_result;
               char_freq_map_decode.put(char_result, null);
               current_char_builder.setLength(0);
           }

           else if (current_char == '\n'){
               is_char = true;
               int freq_result = Integer.parseInt(current_freq_builder.toString());
               char_freq_map_decode.put(latest_char, freq_result);
               current_freq_builder.setLength(0);
           }

           else if (is_char){
               current_char_builder.append(current_char);
           }

           else {
               current_freq_builder.append(current_char);
           }
       }

       return char_freq_map_decode;
   }

    public ArrayList<HuffmanNode> get_char_freq_list_decode(HashMap<Character, Integer> char_freq_table){
        Set<Character> char_set = char_freq_table.keySet();
        ArrayList<Character> char_list = new ArrayList<>(char_set); //Building an arrayList of keys.

        char_freq_list_decode = new ArrayList<>();

        for (int i = 0; i < char_list.size(); i++){
            HuffmanNode current_node = new HuffmanNode(char_list.get(i), char_freq_table.get(char_list.get(i)));
            char_freq_list_decode.add(current_node);
        }

        return char_freq_list_decode;
    }

    public PriorityQueue<HuffmanNode> get_heap_decode(ArrayList<HuffmanNode> char_freq_list){
        Comparator<HuffmanNode> huffComparator = new Comparator<HuffmanNode>() {
            @Override
            public int compare(HuffmanNode o1, HuffmanNode o2) {
                return Integer.compare(o1.freq, o2.freq);
            }
        };

        heap_decode = new PriorityQueue<>(huffComparator);

        heap_decode.addAll(char_freq_list);

        return heap_decode;
    }

    public HuffmanNode huff_tree_constructor_decode(PriorityQueue<HuffmanNode> min_heap) {

        if (min_heap.size() <= 3) {
            throw new IllegalArgumentException("The input file needs to contain more kinds of characters!");
        }

        while (min_heap.size() > 1) {

            HuffmanNode first_node = min_heap.poll();
            HuffmanNode second_node = min_heap.poll();

            HuffmanNode first_root = new HuffmanNode('\u0000', first_node.freq + second_node.freq);
            first_root.left = first_node;
            first_root.right = second_node;
            min_heap.add(first_root);
        }

        root_node_decode = min_heap.peek();

        return min_heap.peek();

        //Repeat this until the heap contains only one node.
    }

    public void original_file_generator(String input_file_name, String output_file_name){
        StringBuilder original_file_builder = new StringBuilder();
        original_file_builder.setLength(0);
        HuffmanNode current_node = root_node_decode;

        BinaryIn input = new BinaryIn(input_file_name);
        BinaryOut output = new BinaryOut(output_file_name);

        while (!input.isEmpty()) {
            boolean current_bool = input.readBoolean();

            if (current_bool == false){
                current_node = current_node.left;
                if (current_node.left.freq == -1 && current_node.right.freq == -1){
                    original_file_builder.append(current_node.character);
                    current_node = root_node_decode;
                }
            }
            else if (current_bool == true){
                current_node = current_node.right;
                if (current_node.left.freq == -1 && current_node.right.freq == -1){
                    original_file_builder.append(current_node.character);
                    current_node = root_node_decode;
                }
            }
        }

        output.write(original_file_builder.toString());
        output.close();
    }

    //The above methods are for decode().

   public void decode(String inputFile, String outputFile, String freqFile){
		// TODO: Your code here
       HuffmanNode decode_root = this.huff_tree_constructor_decode(get_heap_decode(get_char_freq_list_decode(freq_file_to_char_freq_list(freqFile))));
       this.original_file_generator(inputFile, outputFile);
   }

   //Helper functions.
    //Codes from https://java2blog.com/binary-tree-level-order-traversal-java/
    public void print_huff_tree(HuffmanNode huff_current){
        Queue<HuffmanNode> queue = new LinkedList<>();
        queue.add(huff_current);
        while(!queue.isEmpty())
        {
            HuffmanNode tempNode = queue.poll();
            System.out.print(tempNode.character + "-" + tempNode.freq + "\n");
            if(tempNode.left.freq!=-1) {
                queue.add(tempNode.left);
            }
            if(tempNode.right.freq!=-1) {
                queue.add(tempNode.right);
            }
        }

    }
    //Helper functions.


   public static void main(String[] args) {

       //Standard Test
       /*
        HuffmanSubmit test = new HuffmanSubmit();
        test.encode("alice30.txt","alice30_compressed.jxc","alice30_freq.txt");

       System.out.println("\n");

       HuffmanSubmit test_decode = new HuffmanSubmit();
       test_decode.decode("alice30_compressed.jxc", "alice30_recovered.txt", "alice30_freq.txt");
        */
       //Standard Test

        //Standard Test (Instructor's Version)

       Huffman  huffman = new HuffmanSubmit();
       huffman.encode("tp1.txt", "tp1.enc", "freq1.txt");
       huffman.decode("tp1.enc", "tp1new.jpg", "freq1.txt");

       // After decoding, both ur.jpg and ur_dec.jpg should be the same.
       // On linux and mac, you can use `diff' command to check if they are the same.
       //Standard Test (Instructor's Version)

       //UNIT TEST OF ALL METHODS INVOLVED IN ENCODE. DO NOT DECOMMENT THIS UNLESS THERE'S A BUG FOUND IN THE STANDARD TEST.
        /*
        //Testing get_char_freq_map. Method has built-in debugger.
        HashMap<Character, Integer> test_map = test.get_char_freq_map("test.txt");
       //Testing get_char_freq_map. Method has built-in debugger.

       //Testing get_char_freq_list.
        ArrayList<HuffmanNode> test_list = test.get_char_freq_list(test_map);
        System.out.println("\n");
        for (int i = 0; i < test_list.size(); i++){
            System.out.print(test_list.get(i).character + "-" + test_list.get(i).freq + ", ");
        }
       //Testing get_char_freq_list.

       //Testing get_heap.
        System.out.println("\n");
        PriorityQueue<HuffmanNode> test_heap = test.get_heap(test_list);
        //Decomment the following codes to debug. The debugger here is destructive.
        while(test_heap.peek() != null) {
            HuffmanNode current_node = test_heap.poll();
            System.out.print(current_node.character + "-" + current_node.freq + ", ");
        }
        //Decomment the above codes to debug. The debugger here is destructive.
       //Testing get_heap.

       //Testing huff_tree_constructor.
       HuffmanNode huff_root = test.huff_tree_constructor(test_heap);
       test.print_huff_tree(huff_root);
       //Testing huff_tree_constructor.

       //Testing freq_file_generator.
       test.freq_file_generator("test_freq.txt");
       //Testing freq_file_generator.

       //Testing huff_assign_code.
       test.huff_assign_code(huff_root, "");
       System.out.println(test.huff_map);
       //Testing huff_assign_code.

       //Testing output_file_generator.
       test.output_file_generator("test_output.txt");

       BinaryIn test_in = new BinaryIn("test_output.txt");
       ArrayList<Integer> test_in_list = new ArrayList<>();
       while (!test_in.isEmpty()){
           test_in_list.add(test_in.readInt(1));
       }
       System.out.println("\n");
       System.out.println(test_in_list);
       //Testing output_file_generator.
       */
       //UNIT TEST OF ALL METHODS INVOLVED IN ENCODE. DO NOT DECOMMENT THIS UNLESS THERE'S A BUG FOUND IN THE STANDARD TEST.

       //Live Testing. DO NOT DECOMMENT THESE.
       /*
       BinaryOut test_out = new BinaryOut("out_test.txt");
       test_out.write(false);
       test_out.write(true);
       test_out.write(false);
       test_out.write(true);
       test_out.write(true);
       test_out.write(false);
       test_out.write(false);
       test_out.write(false);
       test_out.flush();

       BinaryIn test_in = new BinaryIn("out_test.txt");
       System.out.println("\n");

       while(!test_in.isEmpty()) {
           System.out.print(test_in.readBoolean());
       }
        */

       /*Testing string property
       String test_string = "Testing";
       test_string = test_string + "Addition";
       System.out.println(test_string);
        */

       /*Checking the content of compressed file.
       BinaryIn test_in = new BinaryIn("test_compressed.jxc");
       ArrayList<Integer> test_in_list = new ArrayList<>();
       while (!test_in.isEmpty()){
           test_in_list.add(test_in.readInt(1));
       }
       System.out.println("\n");
       System.out.println(test_in_list);
       */
       //Live Testing. DO NOT DECOMMENT THESE.
   }
}
