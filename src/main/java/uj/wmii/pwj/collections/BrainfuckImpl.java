package uj.wmii.pwj.collections;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;


public class BrainfuckImpl implements Brainfuck {
    private final String program;
    private final PrintStream out;
    private final InputStream in;
    private final byte[] dataStack;
    private final Map<Integer, Integer> bracketMap = new HashMap<>();

    public BrainfuckImpl(String program, PrintStream out, InputStream in, int stackSize) {
        if (program == null || out == null || in == null || stackSize < 1) {
            throw new IllegalArgumentException("Arguments cannot be null and stackSize must be at least 1");
        }

        this.program = program;
        this.out = out;
        this.in = in;
        this.dataStack = new byte[stackSize];
    }

    @Override
    public void execute() {
        buildBracketMap();
        int instructionIndex = 0;
        int dataStackIndex = 0;
        while (instructionIndex < program.length()) {
            char i = program.charAt(instructionIndex);
            switch (i) {
                case '>':
                    dataStackIndex++;
                    if (dataStackIndex >= dataStack.length) {
                        throw new IndexOutOfBoundsException("Data stack overflow");
                    }
                    break;
                case '<':
                    dataStackIndex--;
                    if (dataStackIndex < 0) {
                        throw new IndexOutOfBoundsException("Data stack underflow");
                    }
                    break;
                case '+':
                    dataStack[dataStackIndex]++;
                    break;
                case '-':
                    dataStack[dataStackIndex]--;
                    break;
                case '.':
                    out.print((char) dataStack[dataStackIndex]);
                    break;
                case ',':
                    try {
                        dataStack[dataStackIndex] = (byte) in.read();
                    } catch (Exception e) {
                        System.out.println("Error reading input: " + e.getMessage());
                    }
                    break;
                case '[':
                    if (dataStack[dataStackIndex] == 0) {
                        instructionIndex = bracketMap.get(instructionIndex);
                    }
                    break;
                case ']':
                    if (dataStack[dataStackIndex] != 0) {
                        instructionIndex = bracketMap.get(instructionIndex);
                    }
                    break;
            }
            instructionIndex++;
        }
    }

    public void buildBracketMap() {
        Stack<Integer> openedBrackets = new Stack<>();

        for (int i = 0; i < program.length(); i++) {
            char c = program.charAt(i);
            if (c == '[') {
                openedBrackets.push(i);
            } else if (c == ']') {
                if (openedBrackets.isEmpty()) {
                    throw new IllegalArgumentException("Unmatched closing bracket at position " + i);
                }
                int openingIndex = openedBrackets.pop();
                bracketMap.put(openingIndex, i);
                bracketMap.put(i, openingIndex);
            }
        }

        if (!openedBrackets.isEmpty()) {
            throw new IllegalArgumentException("Unmatched opening bracket at position " + openedBrackets.pop());
        }
    }
}