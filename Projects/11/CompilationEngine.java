import java.io.File;

public class CompilationEngine {
    private JackTokenizer jt;
    private SymbolTable st;
    private VMWriter vmW;
    private String className = "";
    private String subRoutineName = "";

    private int labelIndex;

    public CompilationEngine(File inFile, File outFile) {
        jt = new JackTokenizer(inFile);
        st = new SymbolTable();
        vmW = new VMWriter(outFile);
        labelIndex = 0;
    }

    // compiles a complete class
    public void compileClass() {
        jt.advance();
        jt.advance();

        className = jt.identifier();
        jt.advance();
        compileClassVarDec();
        compileSubRoutine();

        vmW.close();

    }

    // compiles a static declaration or a field declaration
    public void compileClassVarDec() {
        jt.advance();
        while (jt.keyword().equals("static") || jt.keyword().equals("field")) {
            // field or static
            String kind;
            String type;
            if (jt.keyword().equals("static")) {
                kind = "static";
            }
            else {
                kind = "field";
            }

            jt.advance();
            if (jt.tokenType().equals("identifier")) {
                type = jt.identifier();
            }
            else {
                type = jt.keyword();
            }

            jt.advance();
            st.define(jt.identifier(), type, kind);
            jt.advance();
            // if there are multiple in 1 line - e.g. field int x, y
            while (jt.symbol() == ',') {
                jt.advance();
                st.define(jt.identifier(), type, kind);
                jt.advance();
            }
            // semicolon
            jt.advance();
        }

        // if reach a subroutine, go back in the arraylist 
        if (jt.keyword().equals("function") || jt.keyword().equals("method") || jt.keyword().equals("constructor")) {
            jt.decrement();
            return;
        }


    }

    // compiles a complete method, function, or a constructor
    public void compileSubRoutine() {
        jt.advance();
        // base case for recursion
        if (jt.symbol() == '}' && jt.tokenType().equals("symbol")) {
            return;
        }
        String keyword = "";
        if (jt.keyword().equals("function") || jt.keyword().equals("method") || jt.keyword().equals("constructor")) {
            keyword = jt.keyword();
            // new subroutine - reset symbol table
            st.resetSubRoutine();
            if (jt.keyword().equals("method")) {
                st.define("this", className, "argument");

            }
            jt.advance();
        }

        if (jt.tokenType().equals("keyword") && jt.keyword().equals("void")) {
            
            jt.advance();
        } else if (jt.tokenType().equals("keyword") && (jt.keyword().equals("int") || jt.keyword().equals("boolean") || jt.keyword().equals("char"))) {
         
            jt.advance();
        }
        // identifier
        else {
            jt.advance();
        }
        // name of the subroutine
        if (jt.tokenType().equals("identifier")) {
            subRoutineName = jt.identifier();
            jt.advance();
        }

        // get parameters, or lack there of
        if (jt.symbol() == '(') {


            compileParameterList();


        }
        jt.advance();
        // start subroutine body
        if (jt.symbol() == '{') {

            jt.advance();
        }
        // get all var declarations in the subroutine
        while (jt.keyword().equals("var") && (jt.tokenType().equals("keyword"))) {
            jt.decrement();
            compileVarDec();
        }
        String functionName = "";
        if (className.length() != 0 && subRoutineName.length() != 0) {
            functionName += className + "." + subRoutineName;
        }
        vmW.writeFunction(functionName, st.varCount("var"));
        // need to push the first argument (this) and pop it into the pointer 0
        if (keyword.equals("method")) {
            vmW.writePush("argument", 0);
            vmW.writePop("pointer", 0);

        } else if (keyword.equals("constructor")) {
            vmW.writePush("constant", st.varCount("field"));
            vmW.writeCall("Memory.alloc", 1);
            vmW.writePop("pointer", 0);
        }

        compileStatements();

        // recursive call
        compileSubRoutine();

    }


    // compiles a (possibly empty) parameter list including the "()"
    public void compileParameterList() {
        jt.advance();
        String type = "";
        String name = "";
        boolean hasParam = false;
        while (!(jt.tokenType().equals("symbol") && jt.symbol() == ')')) {
            if (jt.tokenType().equals("keyword")) {
                hasParam = true;
                type = jt.keyword();
            } else if (jt.tokenType().equals("identifier")) {
                type = jt.identifier();
            }
            jt.advance();

            if (jt.tokenType().equals("identifier")) {
                name = jt.identifier();
            }
            jt.advance();

            if (jt.tokenType().equals("symbol") && jt.symbol() == ',') {
                st.define(name, type, "argument");
                jt.advance();
            }
        }
        // if has parameters add these to symbol table 
        if (hasParam) {
            st.define(name, type, "argument");
        }

    }

    // compiles a var declaration
    public void compileVarDec() {
        jt.advance();
        String type = "";
        String name = "";
        if (jt.keyword().equals("var") && (jt.tokenType().equals("keyword"))) {
            jt.advance();
        }
        
        if (jt.tokenType().equals("identifier")) {
            type = jt.identifier();
            jt.advance();
        }
        
        else if (jt.tokenType().equals("keyword")) {
            type = jt.keyword();
            jt.advance();
        }
        
        if (jt.tokenType().equals("identifier")) {
            name = jt.identifier();
            jt.advance();

        }
        st.define(name, type, "var");

        // if there are mutliple variables in 1 line
        while ((jt.tokenType().equals("symbol")) && (jt.symbol() == ',')) {
            jt.advance();
            name = jt.identifier();
            st.define(name, type, "var");

            jt.advance();
        }

        // end of var line
        if ((jt.tokenType().equals("symbol")) && (jt.symbol() == ';')) {
            jt.advance();
        }

    }

    // compiles a sequence of statements
    public void compileStatements() {
        if (jt.symbol() == '}' && (jt.tokenType().equals("symbol"))) {
            return;

        } else if (jt.keyword().equals("do") && (jt.tokenType().equals("keyword"))) {
            compileDo();

        } else if (jt.keyword().equals("let") && (jt.tokenType().equals("keyword"))) {
            compileLet();

        } else if (jt.keyword().equals("if") && (jt.tokenType().equals("keyword"))) {
            compileIf();

        } else if (jt.keyword().equals("while") && (jt.tokenType().equals("keyword"))) {
            compileWhile();

        } else if (jt.keyword().equals("return") && (jt.tokenType().equals("keyword"))) {
            compileReturn();
        }

        jt.advance();
        compileStatements();
    }

    private void compileCall() {
        jt.advance();
        String first = jt.identifier();  // first part
        int nArguments = 0;
        jt.advance();
        // if .
        if ((jt.tokenType().equals("symbol")) && (jt.symbol() == '.')) {
            String objectName = first;
            jt.advance();
            jt.advance();
            first = jt.identifier();
            String type = st.typeOf(objectName);
            if (type.equals("")) {
                first = objectName + "." + first;
            } else {
                nArguments = 1;
                vmW.writePush(st.kindOf(objectName), st.indexOf(objectName));
                first = st.typeOf(objectName) + "." + first;
            }

            // parameters in the parentheses
            nArguments += compileExpressionList();
            jt.advance();
            vmW.writeCall(first, nArguments);
        }
        
        // if ( 
        else if ((jt.tokenType().equals("symbol")) && (jt.symbol() == '(')) {
            vmW.writePush("pointer", 0);
            nArguments = compileExpressionList() + 1;

            // )
            jt.advance();
            vmW.writeCall(className + "." + first, nArguments);
        }
    }

    // compiles a let statement
    public void compileLet() {
        jt.advance();
        String varName = jt.identifier();
        jt.advance();
        boolean isArray = false;
        if ((jt.tokenType().equals("symbol")) && (jt.symbol() == '[')) {
            isArray = true;
            vmW.writePush(st.kindOf(varName), st.indexOf(varName));
            compileExpression();
            jt.advance();
            vmW.writeArithmetic("add");
            jt.advance();
        }

        compileExpression();
        // semi colon
        jt.advance();
        if (isArray) {
            vmW.writePop("temp", 0);
            vmW.writePop("pointer", 1);
            // put the value into that
            vmW.writePush("temp", 0);
            vmW.writePop("that", 0);
        } else {
            // pop directly
            vmW.writePop(st.kindOf(varName), st.indexOf(varName));
        }
    }

     // compiles an if statement, possibly with a trailing else clause
     public void compileIf() {
        String labelElse = "LABEL_" + labelIndex++;
        String labelEnd = "LABEL_" + labelIndex++;
        jt.advance();
        // expression within if () condition
        compileExpression();
        jt.advance();
        // if not condition got to label else
        vmW.writeArithmetic("not");
        vmW.writeIf(labelElse);
        jt.advance();
        // compile statements within if clause { }
        compileStatements();
        // after statement finishes, go to the end label
        vmW.writeGoto(labelEnd);
        vmW.writeLabel(labelElse);
        jt.advance();
        // if there is an else clause of the if statement
        if (jt.tokenType().equals("keyword") && jt.keyword().equals("else")) {
            jt.advance();
            jt.advance();
            // compile statements within else clause
            compileStatements();
        } else {
            // keep placeholder correct
            jt.decrement();
        }
        vmW.writeLabel(labelEnd);
    }

    // compiles a while statement
    public void compileWhile() {
        String secondLabel = "LABEL_" + labelIndex++;
        String firstLabel = "LABEL_" + labelIndex++;
        vmW.writeLabel(firstLabel);
        // while
        jt.advance();
        // (
        // compile inside of ()
        compileExpression();
        // )
        jt.advance();
        vmW.writeArithmetic("not");
        vmW.writeIf(secondLabel);
        jt.advance();
        // {
        // inside of while statement
        compileStatements();
        // }
        vmW.writeGoto(firstLabel);
        vmW.writeLabel(secondLabel);
    }

    // compiles a do statement
    public void compileDo() {
        // function call
        compileCall();
        // semi colon
        jt.advance();
        vmW.writePop("temp", 0);
    }

    // compiles a return statement
    public void compileReturn() {
        jt.advance();
        if (!((jt.tokenType().equals("symbol") && jt.symbol() == ';'))) {
            jt.decrement();
            compileExpression();
        } else if (jt.tokenType().equals("symbol") && jt.symbol() == ';') {
            vmW.writePush("constant", 0);
        }
        vmW.writeReturn();
    }

    // compiles an expression
    public void compileExpression() {
        compileTerm();
        while (true) {
            jt.advance();
            if (jt.tokenType().equals("symbol") && jt.isOperation()) {
                // < > & = have different xml code
                if (jt.symbol() == '<') {
                    compileTerm();
                    vmW.writeArithmetic("lt");

                } else if (jt.symbol() == '>') {
                    compileTerm();
                    vmW.writeArithmetic("gt");

                } else if (jt.symbol() == '&') {
                    compileTerm();
                    vmW.writeArithmetic("and");

                } else if (jt.symbol() == '+') {
                    compileTerm();
                    vmW.writeArithmetic("add");

                } else if (jt.symbol() == '-') {
                    compileTerm();
                    vmW.writeArithmetic("sub");

                } else if (jt.symbol() == '*') {
                    compileTerm();
                    vmW.writeCall("Math.multiply", 2);

                } else if (jt.symbol() == '/') {
                    compileTerm();
                    vmW.writeCall("Math.divide", 2);

                } else if (jt.symbol() == '=') {
                    compileTerm();
                    vmW.writeArithmetic("eq");

                } else if (jt.symbol() == '|') {
                    compileTerm();
                    vmW.writeArithmetic("or");
                }

            } else {
                jt.decrement();
                break;
            }
        }
    }

    // compiles (possibly empty) comma separated list of expressions
    public int compileExpressionList() {
        int nArguments = 0;
        jt.advance();
        // end of list
        if (jt.symbol() == ')' && jt.tokenType().equals("symbol")) {
            jt.decrement();
        } else {
            nArguments = 1;
            jt.decrement();
            compileExpression();
        }
        while (true) {
            jt.advance();
            if (jt.tokenType().equals("symbol") && jt.symbol() == ',') {
                compileExpression();
                nArguments++;
            } else {
                jt.decrement();
                break;
            }
        }
        return nArguments;
    }

    // compiles a term - if current token is an identifier, must distinguish between variable, array entry, and subroutine call
    public void compileTerm() {
        jt.advance();
        if (jt.tokenType().equals("identifier")) {
            String previdentifier = jt.identifier();
            jt.advance();
            // for [] terms
            if (jt.tokenType().equals("symbol") && jt.symbol() == '[') {
                // push the array start
                vmW.writePush(st.kindOf(previdentifier), st.indexOf(previdentifier));
                compileExpression();
                jt.advance();
                // add array number to array start, pop into pointer for that, and push into that
                vmW.writeArithmetic("add");
                vmW.writePop("pointer", 1);
                vmW.writePush("that", 0);
            }
            // for ( or . - subroutine calls
            else if (jt.tokenType().equals("symbol") && (jt.symbol() == '(' || jt.symbol() == '.')) {
                jt.decrement();
                jt.decrement();
                compileCall();

            } else {
                jt.decrement();
                vmW.writePush(st.kindOf(previdentifier), st.indexOf(previdentifier));
            }

        } else {
            // integer
            if (jt.tokenType().equals("integerConstant")) {
                vmW.writePush("constant", jt.intVal());

            }
            // strings
            else if (jt.tokenType().equals("stringConstant")) {
                String token = jt.stringVal();
                vmW.writePush("constant", token.length());
                vmW.writeCall("String.new", 1);
                for (int i = 0; i < token.length(); i++) {
                    vmW.writePush("constant", (int) token.charAt(i));
                    vmW.writeCall("String.appendChar", 2);
                }
            }
            // this - push this pointer
            else if (jt.tokenType().equals("keyword") && jt.keyword().equals("this")) {
                vmW.writePush("pointer", 0);
            }
            // false and null - 0
            else if (jt.tokenType().equals("keyword") && (jt.keyword().equals("null") || jt.keyword().equals("false"))) {
                vmW.writePush("constant", 0);

            }
            // true - not 0
            else if (jt.tokenType().equals("keyword") && jt.keyword().equals("true")) {
                vmW.writePush("constant", 0);
                vmW.writeArithmetic("not");
            }

            // parenthetical separation
            else if (jt.tokenType().equals("symbol") && jt.symbol() == '(') {
                compileExpression();
                jt.advance();
            }
            // unary operators
            else if (jt.tokenType().equals("symbol") && (jt.symbol() == '-' || jt.symbol() == '~')) {
                char symbol = jt.symbol();
                // recursive call
                compileTerm();
                if (symbol == '-') {
                    vmW.writeArithmetic("neg");
                } else if (symbol == '~') {
                    vmW.writeArithmetic("not");
                }
            }
        }
    }
}