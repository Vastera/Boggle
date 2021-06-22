/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public class BoggleSolver {
    // dictionary stored by String type
    private final TriesSET26 dic;
    private ArrayList<Bag<Integer>> adj;
    private int cols;
    private char[] dice;
    private TriesSET26 validWords;
    private boolean[] marked;

    // Initializes the data structure using the given array of strings as the directory.
    //(You can assume each word in the directory contains only the uppercase letters A through Z.)
    public BoggleSolver(String[] dictionary) {
        if (dictionary == null)
            throw new IllegalArgumentException("input dictionary is null~");
        // use a tries set to store all the words in the dictionary.
        dic = new TriesSET26();
        for (int i = 0; i < dictionary.length; i++)
            dic.add(dictionary[i]);
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        // adj store all adjacency of all dice
        if (board == null)
            throw new IllegalArgumentException("input board is null~");
        cols = board.cols();
        dice = new char[board.cols() * board.rows()];
        for (int i = 0; i < dice.length; i++)
            dice[i] = board.getLetter(i / cols, i % cols);

        adj = new ArrayList<Bag<Integer>>();
        Bag<Integer> tempBag;
        for (int i = 0; i < dice.length; i++) {
            tempBag = new Bag<Integer>();
            for (int m = -1; m <= 1; m++)
                for (int n = -1; n <= 1; n++)
                    if ((m != 0 || n != 0) && isValidDie(i / cols + m, i % cols + n))
                        tempBag.add(i + m * cols + n); // (i/cols-m)*cols+i%cols-n=i-m*cols-n
            adj.add(tempBag);
        }
        char letter;
        StringBuilder curPat;
        validWords = new TriesSET26();
        for (int i = 0; i < dice.length; i++) {
            curPat = new StringBuilder();
            marked = new boolean[dice.length];
            letter = dice[i];
            if (letter == 'Q') {
                curPat.append("QU");
            }
            else {
                curPat.append(letter);
            }
            marked[i] = true;
            findAllPathes(dic, curPat, i);
        }
        return validWords;
    }

    private void findAllPathes(TriesSET26 branch, StringBuilder curPat, int curP) {
        // check if there is a occurrence of current pattern
        int curLen = curPat.length();
        String pattern = curPat.toString();
        TriesSET26 newBranch = new TriesSET26();
        for (String word : branch.keysWithPrefix(pattern))
            newBranch.add(word);
        if (newBranch.size() == 0) // if there is no branch for current prefix
            return;
        if (curLen >= 3 && newBranch.contains(pattern) && !validWords.contains(pattern))
            validWords.add(pattern);

        // next points
        Bag<Integer> curAdj = adj.get(curP);
        for (int i : curAdj) {
            if (!marked[i]) {
                char letter = dice[i];
                if (letter == 'Q') {
                    curPat.append("QU");
                }
                else {
                    curPat.append(letter);
                }
                marked[i] = true;
                findAllPathes(newBranch, curPat, i);
                if (letter == 'Q')
                    curPat.delete(curLen, curLen + 2);
                else
                    curPat.deleteCharAt(curLen);
                marked[i] = false;
            }
        }
    }

    private boolean isValidDie(int i, int j) {
        return i >= 0 && i < dice.length / cols && j >= 0 && j < cols;
    }

    // Returns the score of the give word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        int[] scoreTable = new int[dice.length + 1];
        for (int i = 0; i < 3; i++)
            scoreTable[i] = 0;
        scoreTable[3] = 1;
        scoreTable[4] = 1;
        for (int i = 5; i < 7; i++)
            scoreTable[i] = i - 3;
        scoreTable[7] = 5;
        for (int i = 8; i < scoreTable.length; i++)
            scoreTable[i] = 11;
        return scoreTable[word.length()];
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
