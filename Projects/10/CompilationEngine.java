import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class CompilationEngine {
   
    FileWriter outputFile;
    JackTokenizer jackTokenizer;
    private boolean bFirstRoutine;

    public CompilationEngine(FileReader inputFile, FileWriter outputFile) {
       this.outputFile = outputFile;
       jackTokenizer = new JackTokenizer(inputFile);
       while(jackTokenizer.hasMoreTokens()){
        jackTokenizer.advance();
       }
       jackTokenizer.arrIndex = -1;
       bFirstRoutine = true;
    }

    public void compileClass() throws IOException {
        jackTokenizer.advance();
        outputFile.write("<class>\n");
        outputFile.write("<keyword> class </keyword>\n");
        jackTokenizer.advance();
        outputFile.write("<identifier> " + jackTokenizer.tokenArr[jackTokenizer.arrIndex].getTokenValue() + " </identifier>\n");
        jackTokenizer.advance();
        outputFile.write("<symbol> { </symbol>\n");
        compileClassVarDec();
        compileSubRoutine();
        outputFile.write("<symbol> } </symbol>\n");
        outputFile.write("</class>\n");
        outputFile.close();
    }

    public void compileClassVarDec() throws IOException {
        jackTokenizer.advance();

        while(jackTokenizer.getTokenType().equals("keyword") && 
             (jackTokenizer.getTokenValue().equals("static") || jackTokenizer.getTokenValue().equals("field"))) {
                
                outputFile.write("<classVarDec>\n");
                // field or static
                outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
                jackTokenizer.advance();
                // if for example, field Square square (Square)
                if (jackTokenizer.getTokenType().equals("identifier")) {
                    outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
                }
                // if for example, field int square (int)
                else {
                    outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");

                }
                jackTokenizer.advance();
                // third word of the classvardec - e.g. square in the above - field int square
                outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
                jackTokenizer.advance();
                // if there are multiple in 1 line - e.g. field int x, y
                if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ',') {
                    outputFile.write("<symbol> , </symbol>\n");
                    jackTokenizer.advance();
                    outputFile.write(("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n"));
                    jackTokenizer.advance();
                }
                // semicolon
                outputFile.write("<symbol> ; </symbol>\n");
                jackTokenizer.advance();
                outputFile.write("</classVarDec>\n");
        }

        if (jackTokenizer.getTokenType().equals("keyword") && 
            (jackTokenizer.getTokenValue().equals("function") || jackTokenizer.getTokenValue().equals("method") || jackTokenizer.getTokenValue().equals("constructor"))) {
            jackTokenizer.decrement();
            return;
        }
    }

    public void compileSubRoutine() throws IOException {
        boolean hasSubRoutines = false;

        jackTokenizer.advance();
        
        // once reach the end, return  - no more subroutines - base case for the recursive call
        if (jackTokenizer.tokenArr[jackTokenizer.arrIndex] == null || jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '}') {
            return;
        }
        // subroutinedec tag
        if ((bFirstRoutine) && jackTokenizer.getTokenType().equals("keyword") && 
            (jackTokenizer.getTokenValue().equals("function") || jackTokenizer.getTokenValue().equals("method") || jackTokenizer.getTokenValue().equals("constructor"))) {
            outputFile.write("<subroutineDec>\n");
            hasSubRoutines = true;
        }
        // function ,e
        if (jackTokenizer.getTokenType().equals("keyword") && 
            (jackTokenizer.getTokenValue().equals("function") || jackTokenizer.getTokenValue().equals("method") || jackTokenizer.getTokenValue().equals("constructor"))) {
            outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
            hasSubRoutines = true;
            jackTokenizer.advance();
        }
        // if there is an identifier in the subroutine statement position 2 e.g. function Square getX()
        if (jackTokenizer.getTokenType().equals("identifier")) {
            outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
            jackTokenizer.advance();
        }
        // if keyword instead for subroutine statement position 2 e.g. function int getX()
        else if (jackTokenizer.getTokenType().equals("keyword")) {
            outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
            jackTokenizer.advance();
        }
        // name of the subroutine
        if (jackTokenizer.getTokenType().equals("identifier")) {
            outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
            jackTokenizer.advance();
        }
        // get parameters, or lack there of
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '(') {
            outputFile.write("<symbol> ( </symbol>\n");
            outputFile.write("<parameterList>\n");

            compileParameterList();
            outputFile.write("</parameterList>\n");
            outputFile.write("<symbol> ) </symbol>\n");
        }

        jackTokenizer.advance();
        // start subroutine body
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '{') {
            outputFile.write("<subroutineBody>\n");
            outputFile.write("<symbol> { </symbol>\n");
            jackTokenizer.advance();
        }
        // get all var declarations in the subroutine
        while (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("var")) {
            outputFile.write("<varDec>\n ");
            jackTokenizer.decrement();
            compileVarDec();
            outputFile.write(" </varDec>\n");
        }

        outputFile.write("<statements>\n");
        compileStatements();
        outputFile.write("</statements>\n");
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");

        if (hasSubRoutines) {
            outputFile.write("</subroutineBody>\n");
            outputFile.write("</subroutineDec>\n");
            bFirstRoutine = true;
        }

        // recursive call
        compileSubRoutine();
    }

    public void compileParameterList() throws IOException {
        jackTokenizer.advance();
        // until reach the end - )
        while (!(jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ')')) {
            if (jackTokenizer.getTokenType().equals("identifier")) {
                outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
                jackTokenizer.advance();
            } else if (jackTokenizer.getTokenType().equals("keyword")) {
                outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
                jackTokenizer.advance();
            }
            // commas separate the list, if there are multiple
            else if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ',') {
                outputFile.write("<symbol> , </symbol>\n");
                jackTokenizer.advance();

            }
        }
    }
              
    public void compileVarDec() throws IOException{
        jackTokenizer.advance();

        if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("var")) {
            outputFile.write("<keyword> var </keyword>\n");
            jackTokenizer.advance();
        }
        // type of var, if identifier, e.g. Square or Array
        if (jackTokenizer.getTokenType().equals("identifier")) {
            outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
            jackTokenizer.advance();
        }
        // type of var, if keyword, e.g. int or boolean
        else if (jackTokenizer.getTokenType().equals("keyword")) {
            outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
            jackTokenizer.advance();
        }
        // name of var
        if (jackTokenizer.getTokenType().equals("identifier")) {
            outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
            jackTokenizer.advance();
        }
        // if there are mutliple in 1 line
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ',') {
            outputFile.write("<symbol> , </symbol>\n");
            jackTokenizer.advance();
            outputFile.write(("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n"));
            jackTokenizer.advance();
        }
        // end of var line
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ';') {
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            jackTokenizer.advance();
        }
    }

    public void compileStatements() throws IOException{
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '}') {
            return;

        } else if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("do")) {
            outputFile.write("<doStatement>\n ");
            compileDo();
            outputFile.write((" </doStatement>\n"));

        } else if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("let")) {
            outputFile.write("<letStatement>\n ");
            compileLet();
            outputFile.write((" </letStatement>\n"));

        } else if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("if")) {
            outputFile.write("<ifStatement>\n ");
            compileIf();
            outputFile.write((" </ifStatement>\n"));

        } else if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("while")) {
            outputFile.write("<whileStatement>\n ");
            compileWhile();
            outputFile.write((" </whileStatement>\n"));
            
        } else if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("return")) {
            outputFile.write("<returnStatement>\n ");
            compileReturn();
            outputFile.write((" </returnStatement>\n"));
        }
        jackTokenizer.advance();
        compileStatements();
    }
    
      // compiles a let statement
      public void compileLet() throws IOException {
        outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
        jackTokenizer.advance();
        outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
        jackTokenizer.advance();

        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '[') {
            // there is an expression -- because we have x[5] for example
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            compileExpression();
            jackTokenizer.advance();
            if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ']') {
                outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            }
            // only advance if there is an expression
            jackTokenizer.advance();

        }

        // = sign
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");

        compileExpression();
        // semi colon
        outputFile.write("<symbol> ; </symbol>\n");
        jackTokenizer.advance();
    }

    public void compileIf() throws IOException {
        outputFile.write("<keyword> if </keyword>\n");
        jackTokenizer.advance();
        outputFile.write("<symbol> ( </symbol>\n");
        // expression within if () condition
        compileExpression();
        outputFile.write("<symbol> ) </symbol>\n");
        jackTokenizer.advance();
        outputFile.write("<symbol> { </symbol>\n");
        jackTokenizer.advance();
        outputFile.write("<statements>\n");
        // compile statements within if clause { }
        compileStatements();
        outputFile.write("</statements>\n");
        outputFile.write("<symbol> } </symbol>\n");
        jackTokenizer.advance();
        // if there is an else clause of the if statement
        if (jackTokenizer.getTokenType().equals("keyword") && jackTokenizer.getTokenValue().equals("else")) {
            outputFile.write("<keyword> else </keyword>\n");
            jackTokenizer.advance();
            outputFile.write("<symbol> { </symbol>\n");
            jackTokenizer.advance();
            outputFile.write("<statements>\n");
            // compile statements within else clause
            compileStatements();
            outputFile.write("</statements>\n");
            outputFile.write("<symbol> } </symbol>\n");
        } else {
            // keep placeholder correct
            jackTokenizer.decrement();
        }
    }

    public void compileWhile() throws IOException {
        // while
        outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
        jackTokenizer.advance();
        // (
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
        // compile inside of () - expression
        compileExpression();
        // )
        jackTokenizer.advance();
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
        jackTokenizer.advance();
        // {
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
        // inside of while statement
        outputFile.write("<statements>\n");
        compileStatements();
        outputFile.write("</statements>\n");
        // }
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
    }

     // compiles a do statement
     public void compileDo() throws IOException {
        if (jackTokenizer.getTokenValue().equals("do")) {
            outputFile.write("<keyword> do </keyword>\n");
        }
        // function call
        compileCall();
        // semi colon
        jackTokenizer.advance();
        outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
    }

    // compiles a return statement
    public void compileReturn() throws IOException {
        outputFile.write("<keyword> return </keyword>\n");
        jackTokenizer.advance();
        if (!(jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ';')) {
            jackTokenizer.decrement();
            compileExpression();
        }
        //if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ';') {
            outputFile.write("<symbol> ; </symbol>\n");
        //}
    }

    private void compileCall() throws IOException {
        jackTokenizer.advance();
        // first part
        outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
        jackTokenizer.advance();
        // if . - then is something like Screen.erase()
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '.') {
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            jackTokenizer.advance();
            outputFile.write("<identifier> " + jackTokenizer.getTokenValue() + " </identifier>\n");
            jackTokenizer.advance();
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            // parameters in the parentheses
            outputFile.write("<expressionList>\n");
            compileExpressionList();
            outputFile.write("</expressionList>\n");
            jackTokenizer.advance();
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");


        }
        // if ( then is something like erase()
        else if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '(') {
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            outputFile.write("<expressionList>\n");
            compileExpressionList();
            outputFile.write("</expressionList>\n");
            // parentheses )
            jackTokenizer.advance();
            outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
        }
    }

     // compiles an expression
    public void compileExpression() throws IOException {
        outputFile.write("<expression>\n");
        compileTerm();
        while (true) {
            jackTokenizer.advance();
            if (jackTokenizer.getTokenType().equals("symbol") && 
                (jackTokenizer.getTokenValue().charAt(0) == '+' || jackTokenizer.getTokenValue().charAt(0) == '-' ||
                 jackTokenizer.getTokenValue().charAt(0) == '*' || jackTokenizer.getTokenValue().charAt(0) == '/' ||
                 jackTokenizer.getTokenValue().charAt(0) == '&' || jackTokenizer.getTokenValue().charAt(0) == '|' ||
                 jackTokenizer.getTokenValue().charAt(0) == '<' || jackTokenizer.getTokenValue().charAt(0) == '>' ||
                 jackTokenizer.getTokenValue().charAt(0) == '=')) {

                outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
                compileTerm();
            } else {
                jackTokenizer.decrement();
                break;
            }
        }
        outputFile.write("</expression>\n");
    }

    // compiles a term - if current token is an identifier, must distinguish between variable, array entry, and subroutine call
    // single look ahead token which may be "{" "(" or "." to distinguish between the three possibilities
    public void compileTerm() throws IOException {
        outputFile.write("<term>\n");
        jackTokenizer.advance();
        if (jackTokenizer.getTokenType().equals("identifier")) {
            String prevIdentifier = jackTokenizer.getTokenValue();
            jackTokenizer.advance();
            // for [] terms
            if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '[') {
                outputFile.write("<identifier> " + prevIdentifier + " </identifier>\n");
                outputFile.write("<symbol> [ </symbol>\n");
                compileExpression();
                jackTokenizer.advance();
                outputFile.write("<symbol> ] </symbol>\n");
            }
            // for ( or . - subroutine calls
            else if (jackTokenizer.getTokenType().equals("symbol") && 
                    (jackTokenizer.getTokenValue().charAt(0) == '(') || jackTokenizer.getTokenValue().charAt(0) == '.') {
                jackTokenizer.decrement();
                jackTokenizer.decrement();
                compileCall();

            } else {
                outputFile.write("<identifier> " + prevIdentifier + " </identifier>\n");
                jackTokenizer.decrement();
            }
        } else {
            // integer
            if (jackTokenizer.getTokenType().equals("integerConstant")) {
                outputFile.write("<integerConstant> " + jackTokenizer.getTokenValue() + " </integerConstant>\n");

            }
            // strings
            else if (jackTokenizer.getTokenType().equals("stringConstant")) {
                outputFile.write("<stringConstant> " + jackTokenizer.getTokenValue() + " </stringConstant>\n");
            }
            // this true null or false
            else if (jackTokenizer.getTokenType().equals("keyword") && 
                    (jackTokenizer.getTokenValue().equals("this") || jackTokenizer.getTokenValue().equals("null") ||
                     jackTokenizer.getTokenValue().equals("false") || jackTokenizer.getTokenValue().equals("true"))) {
                outputFile.write("<keyword> " + jackTokenizer.getTokenValue() + " </keyword>\n");
            }
            // parenthetical separation
            else if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == '(') {
                outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
                compileExpression();
                jackTokenizer.advance();
                outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
            }
            // unary operators
            else if (jackTokenizer.getTokenType().equals("symbol") &&
                    (jackTokenizer.getTokenValue().charAt(0) == '-' || jackTokenizer.getTokenValue().charAt(0) == '~')) {
                outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
                // recursive call
                compileTerm();
            }
        }
        outputFile.write("</term>\n");
    }

      // compiles (possibly empty) comma separated list of expressions
      public void compileExpressionList() throws IOException {
        jackTokenizer.advance();
        // end of list
        if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ')') {
            jackTokenizer.decrement();
        } else {
            jackTokenizer.decrement();
            compileExpression();
        }

        while (true) {
            jackTokenizer.advance();
            if (jackTokenizer.getTokenType().equals("symbol") && jackTokenizer.getTokenValue().charAt(0) == ','){
                try {
                    outputFile.write("<symbol> " + jackTokenizer.getTokenValue() + " </symbol>\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                compileExpression();
            } else {
                jackTokenizer.decrement();
                break;
            }
        }
    }

}
