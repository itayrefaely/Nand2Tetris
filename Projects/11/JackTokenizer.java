import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;


public class JackTokenizer {
    private Scanner scanner;
    final public String[] Keywords = {"class","constructor","method","function","int",
    "boolean","char","void","var","static","field","let","do","if","else",
    "while","return","true","false","null","this"};

    final public char[] Symbols = {
        '{' , '}' , '(' , ')' , '[' , ']' , '.' , ',' , ';' , '+' , '-' , '*' ,
        '/' , '&' , '|' , '<' , '>' , '=' , '~'};

    final public char[] Operations = {'"', '+', '-', '*', '/', '&', '|', '<', '>', '='};

    private ArrayList<String> tokens;
    private String currTokenType;
    private String currKeyword;
    private char currSymbol;
    private String currIdentifier;
    private String currStringVal;
    private int currIntVal;
    
    private int pointer;
    private boolean isFirst;

    // opens input file/stream tokenizes it
    public JackTokenizer(File file) {
        try {
            scanner = new Scanner(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // keep all input in 1 long string - jackcode, to parse part by part in the advance method
        String jackcode = "";
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            while (line.equals("") || isComment(line)) {
                if (isComment(line)) {
                    line = removeComments(line);
                }
                if (line.trim().equals("")) {
                    if (scanner.hasNextLine()) {
                        line = scanner.nextLine();
                    } else {
                        break;
                    }
                }
            }
            jackcode += line.trim();
        }
        // add tokens from the jackcode string into an arraylist of the tokens
        tokens = new ArrayList<String>();
        while (jackcode.length() > 0) {
            while (jackcode.charAt(0) == ' ') {
                jackcode = jackcode.substring(1);
            }
            // keyword
            if (Arrays.asList(Keywords).contains(jackcode)) {
                String keyword = jackcode;
                tokens.add(keyword);
                jackcode = jackcode.substring(keyword.length());
            }
            // symbol
            if (isSymbol(jackcode.charAt(0))) {
                char symbol = jackcode.charAt(0);
                tokens.add(Character.toString(symbol));
                jackcode = jackcode.substring(1);
            }
            // integer constant
            else if (Character.isDigit(jackcode.charAt(0))) {
                String value = "" + jackcode.charAt(0);
                jackcode = jackcode.substring(1);
                while (Character.isDigit(jackcode.charAt(0))) {
                    value += jackcode.charAt(0);
                    jackcode = jackcode.substring(1);

                }
                tokens.add(value);

            }
            // string constant
            else if (jackcode.charAt(0) == '\'') {
                jackcode = jackcode.substring(1);
                String stringConstant = "\"";
                while ((jackcode.charAt(0) != '\"')) {
                    stringConstant += jackcode.charAt(0);
                    jackcode = jackcode.substring(1);

                }
                stringConstant = stringConstant + '\'';
                tokens.add(stringConstant);
                jackcode = jackcode.substring(1);
            }
            // identifier
            else if (Character.isLetter(jackcode.charAt(0)) || jackcode.charAt(0) == '_') {
                String identifier = "" + jackcode.charAt(0);
                jackcode = jackcode.substring(1);
                while (Character.isLetter(jackcode.charAt(0)) || jackcode.charAt(0) == '_') {
                    identifier += jackcode.charAt(0);
                    jackcode = jackcode.substring(1);
                }

                tokens.add(identifier);
            }
            // start with pointer at position 0
            isFirst = true;
            pointer = 0;
        }
    }

    // checks if there are more tokens to parse
    public boolean hasMoreTokens() {
        if (pointer < tokens.size() - 1) {
            return true;
        }
        return false;

    }

    // gets next token from input and makes it current token, only called if hasMoreTokens() is true, initially no current token
    public void advance() {
        if (hasMoreTokens()) {
            if (!isFirst) {
                pointer++;
            }
            // if at position 0 of tokens, we do not want to increment yet
            else if (isFirst) {
                isFirst = false;
            }
            String currToken = tokens.get(pointer);
            // assign token type and corresponding value to current token

            if (Arrays.asList(Keywords).contains(currToken)) {
                currTokenType = "keyword";
                currKeyword = currToken;

            } else if (currToken.length() == 1 && isSymbol(currToken.charAt(0))) {
                currSymbol = currToken.charAt(0);
                currTokenType = "symbol";

            } else if (Character.isDigit(currToken.charAt(0))) {
                currIntVal = Integer.parseInt(currToken);
                currTokenType = "integerConstant";
                
            } else if (currToken.substring(0, 1).equals("\"")) {
                currTokenType = "stringConstant";
                currStringVal = currToken.substring(1, currToken.length() - 1);

            } else if ((Character.isLetter(currToken.charAt(0))) || (currToken.charAt(0) == '_')) {
                currTokenType = "identifier";
                currIdentifier = currToken;
            }
        } else {
            return;
        }
    }

    // go to previous token
    public void decrement() {
        if (pointer > 0) {
            pointer--;
        }
    }

    // tcheck if line is a comment
    private boolean isComment(String line) {
        if (line.contains("//") || line.contains("/*") || line.trim().startsWith("*")) {
            return true;
        }
        return false;
    }

    // removes comments from a line
    private String removeComments(String line) {
        String noComments = line;
        if (isComment(line)) {
            int offSet;
            if (line.trim().startsWith("*")) {
                offSet = line.indexOf("*");
            } else if (line.contains("/*")) {
                offSet = line.indexOf("/*");
            } else {
                offSet = line.indexOf("//");
            }
            noComments = line.substring(0, offSet).trim();
        }
        return noComments;
    }

    // returns the type of the current token - keyword, symbol, identifier, integerConstant, or stringConstant
    public String tokenType() {
        return currTokenType;
    }

    /** gettes */
    
    public String keyword() {
        return currKeyword;
    }

    public char symbol() {
        return currSymbol;
    }

    public String identifier() {
        return currIdentifier;
    }

    public int intVal() {
        return currIntVal;
    }

    public String stringVal() {
        return currStringVal;
    }

    // checks if the given char is a symbol
    public boolean isSymbol(char target) {
        for(char c : Symbols) {
            if(c == target) {
                return true;
            }
        }
        return false;
    }

    // checks if a symbol is an operation, i.e., =, +, -, &, |, etc.
    public boolean isOperation() {
        for (char c : Operations) {
            if (currSymbol == c) {
                return true;
            }
        }
        return false;
    }

}