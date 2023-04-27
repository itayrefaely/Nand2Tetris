import java.io.FileReader;
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

    public Token[] tokenArr;
    private int counter = 0;
    public int arrIndex;


    public JackTokenizer(FileReader inputFile) {
        scanner = new Scanner(inputFile);
        tokenArr = new Token[1];
        arrIndex = -1;
        
        String currToken = "";
        String currTokenWithSpaces = "";

        while (scanner.hasNextLine()) {
            currToken = "";
            currTokenWithSpaces = "";
            String line = scanner.nextLine().trim();

            for(int i = 0; i < line.length(); i++) {
                char currChar = line.charAt(i);
                char nextChar = line.charAt(Math.min(i + 1, line.length() - 1));
                currToken += currChar;
                currTokenWithSpaces += currChar;
                currToken = currToken.trim();

                if(currToken.length() > 0 && !isComment(currToken)) {   // current token is not empty and not a comment
                    if(currToken.charAt(0) == '"') {    // token is a string
                        if(currToken.length() > 1 && currChar == '"') { // token is not empty and starts with quotes
                            
                            Token token = new Token();
                            token.setTokenType(tokenType(currToken));
                            token.setTokenValue(tokenValue(currTokenWithSpaces.trim()).substring(1, currTokenWithSpaces.trim().length() - 1));
                            
                            if(tokenArr.length <= 2 * counter) {
                                growSize(tokenArr);
                            }
                            tokenArr[counter] = token;
                            counter++;

                            // reset strings 
                            currToken = "";
                            currTokenWithSpaces = "";
                        }

                    } else if((isEndofToken(currChar, nextChar))) {

                        Token token = new Token();
                        token.setTokenType(tokenType(currToken));
                        token.setTokenValue(tokenValue(currToken));
                        
                        if(tokenArr.length <= 2 * counter) {
                            tokenArr = growSize(tokenArr);
                        }
                        tokenArr[counter] = token;
                        counter++;

                        // reset tokens 
                        currToken = "";
                        currTokenWithSpaces = "";
                    }
                }
            }
        }
        if(counter < tokenArr.length - 1) {
            for(int i = counter + 1; i < tokenArr.length; i++) {
                tokenArr[i] = null;
            }
        }  
    }

    public boolean hasMoreTokens() {
        if(this.arrIndex < this.tokenArr.length - 1) {
            if(this.tokenArr[arrIndex + 1] != null) {
                return true;
            }
        }
        return false;
    }

    public void advance() {
        this.arrIndex++;
    }

    public void decrement() {
        if(arrIndex > 0) this.arrIndex--;
    }
    
    public String tokenType(String currToken)
    {
        if(currToken.length() == 1 && isSymbol(currToken.charAt(0))){
            return "symbol";
        } 

        else if (Arrays.asList(Keywords).contains(currToken)) {
            return "keyword";
        }

        else if (isIntVal(currToken)) {
            return "integerConstant";
        }

        else if(currToken.charAt(0) == '"'){
            return "stringConstant";
        }

        else if(Character.isLetter(currToken.charAt(0)) || currToken.charAt(0) == '_'){
            return "identifier";
        }
     
        else if(isComment(currToken)){
            return "comment";
        }

        else {
            return "";
        }
    }

    public String tokenValue(String currToken) {
        char c = currToken.charAt(0);
        if(isSymbol(c)){
            switch(c) {
                case '<':
                    return "&lt;";
                case '>':
                    return "&gt;";
                case '"':
                    return "&quot;";
                case '&':
                    return "&amp;";
            }

            return currToken;

        } else {
            return currToken;
        }
    }
    

    public boolean isEndofToken(char currChar, char nextChar) {
        if(currChar == '/') {
            return nextChar == ' ';
        } else {
            return isSymbol(currChar)|| isSymbol(nextChar) || nextChar == ' ';
        }
    }
    
    public boolean isSymbol(char target) {
        for(char c : Symbols) {
            if(c == target) {
                return true;
            }
        }
        return false;
    }

    public boolean isIntVal(String currToken) {
        boolean isInt = true;
        if (currToken.length() > 0) {
            for (char c : currToken.toCharArray()) {
                if (!Character.isDigit(c)) {
                    isInt = false;
                }
            }
        } else {
            isInt = false;
        }
        return isInt;
    }

    public boolean isComment(String currentToken) {
        return currentToken.startsWith("//") || currentToken.startsWith("/*") || currentToken.startsWith("*");
    }

    public Token[] growSize(Token[] arr)   
    {         
        Token temp[];  
        temp = new Token[arr.length * 2];   
        for (int i = 0; i < arr.length; i++) {   
            temp[i] = arr[i];   
        }   
        return temp;
    }   

    /** getters */

    public String getTokenType() {
        return tokenArr[arrIndex].getTokenType();
    }

    public String getTokenValue() {
        return tokenArr[arrIndex].getTokenValue();
    }

}
