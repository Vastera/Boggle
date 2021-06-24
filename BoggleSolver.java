/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.HashMap;

public class BoggleSolver {
    // dictionary stored by String type
    private final TriesSET26 dic;
    private ArrayList<SET<Integer>> adj;
    private int cols;
    private char[] dice;
    private int maxWordLen;
    private TriesSET26 validWords;
    private boolean[] marked;
    private HashMap<Character, Integer> unmarked;

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
        // the possible max length of word
        maxWordLen = dice.length;
        for (int i = 0; i < dice.length; i++) {
            dice[i] = board.getLetter(i / cols, i % cols);
            if (dice[i] == 'Q')
                maxWordLen++;
        }


        //calculate the adjacency for each die
        adj = new ArrayList<SET<Integer>>();
        SET<Integer> tempBag;
        for (int i = 0; i < dice.length; i++) {
            tempBag = new SET<Integer>();
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
            unmarked = new HashMap<Character, Integer>();
            letter = dice[i];
            if (letter == 'Q') {
                curPat.append("QU");
            }
            else {
                curPat.append(letter);
            }
            marked[i] = true;
            for (int j = 0; j < dice.length; j++) {
                if (unmarked.containsKey(dice[j]))
                    unmarked.put(dice[j], unmarked.get(dice[j]) + 1);
                else
                    unmarked.put(dice[j], 1);
            }

            findAllPathes(dic, curPat, i);
        }
        return validWords;
    }

    private void findAllPathes(TriesSET26 branch, StringBuilder curPat, int curP) {
        // check if there is a occurrence of current pattern
        if (unmarked.size() <= 1) {
            findOnePath(branch, curPat, dice[curP]);
            return;
        }
        // remove the current dice from the HashMap of unmarked
        if (unmarked.get(dice[curP]) > 1)
            unmarked.put(dice[curP], unmarked.get(dice[curP]) - 1);
        else
            unmarked.remove(dice[curP]);

        int curLen = curPat.length();
        String pattern = curPat.toString();
        TriesSET26 newBranch = new TriesSET26();
        if (dice[curP] == 'Q') {
            for (String word : branch.keysWithPrefix(pattern, Math.max(0, curLen - 2))) {
                if (word.length() == curLen) {
                    if (curLen >= 3 && !validWords.contains(pattern))
                        validWords.add(pattern);
                }
                else if (word.length() <= maxWordLen) {
                    newBranch.add(word, curLen);
                }
            }
        }
        else {
            for (String word : branch.keysWithPrefix(pattern, Math.max(0, curLen - 1))) {
                if (word.length() == curLen) {
                    if (curLen >= 3 && !validWords.contains(pattern))
                        validWords.add(pattern);
                }
                else if (word.length() <= maxWordLen) {
                    newBranch.add(word, curLen);
                }
            }
        }
        if (newBranch.size() == 0) // if there is no branch for current prefix
            return;

        // next points
        SET<Integer> curAdj = adj.get(curP);
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
                if (unmarked.containsKey(dice[i]))
                    unmarked.put(dice[i], unmarked.get(dice[i]) + 1);
                else
                    unmarked.put(dice[i], 1);
            }
        }
    }

    private void findOnePath(TriesSET26 branch, StringBuilder curPat, char remainChar) {
        unmarked.put(remainChar, unmarked.get(remainChar) - 1);
        if (unmarked.get(remainChar) == 0)
            return;

        int curLen = curPat.length();
        String pattern = curPat.toString();
        TriesSET26 newBranch = new TriesSET26();
        if (remainChar == 'Q') {
            for (String word : branch.keysWithPrefix(pattern, Math.max(0, curLen - 2))) {
                if (word.length() == curLen) {
                    if (curLen >= 3 && !validWords.contains(pattern))
                        validWords.add(pattern);
                }
                else if (word.length() <= maxWordLen) {
                    newBranch.add(word, curLen);
                }
            }
        }
        else {
            for (String word : branch.keysWithPrefix(pattern, Math.max(0, curLen - 1))) {
                if (word.length() == curLen) {
                    if (curLen >= 3 && !validWords.contains(pattern))
                        validWords.add(pattern);
                }
                else if (word.length() <= maxWordLen) {
                    newBranch.add(word, curLen);
                }
            }
        }
        if (newBranch.size() == 0) // if there is no branch for current prefix
            return;
        if (remainChar == 'Q') {
            curPat.append("QU");
        }
        else {
            curPat.append(remainChar);
        }
        findOnePath(newBranch, curPat, remainChar);
    }

    private boolean isValidDie(int i, int j) {
        return i >= 0 && i < dice.length / cols && j >= 0 && j < cols;
    }

    // Returns the score of the give word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        if (!dic.contains(word))
            return 0;
        int ret = 0;
        int len = word.length();
        switch (len) {
            case (1):
            case (2):
                ret = 0;
                break;
            case (3):
            case (4):
                ret = 1;
                break;
            case (5):
                ret = 2;
                break;
            case (6):
                ret = 3;
                break;
            case (7):
                ret = 5;
                break;
            default:
                ret = 11;
        }
        return ret;
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
