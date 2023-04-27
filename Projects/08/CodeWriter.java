import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;


public class CodeWriter{

    private PrintWriter writer;
    private static int count = 0; 
    private String currentFileName;
    private String funcName;
 
    /*
     * assembly value of the arithmetic-logical command
     */

    private static final String ADD =
    "@SP\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "A=A-1\n" +
    "M=D+M";

  private static final String SUB =
    "@SP\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "A=A-1\n" +
    "M=M-D";

  private static final String NEG =
    "@SP\n" +
    "A=M-1\n" +
    "M=-M";

  private static final String AND =
    "@SP\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "A=A-1\n" +
    "M=D&M";

  private static final String OR =
    "@SP\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "A=A-1\n" +
    "M=D|M";

  private static final String NOT =
    "@SP\n" +
    "A=M-1\n" +
    "M=!M";

  // push D -> *SP++
  private static final String PUSH =
    "@SP\n" +
    "A=M\n" +
    "M=D\n" +
    "@SP\n" +
    "M=M+1";

  // pop *SP-- -> *D
  private static final String POP =
    "@R13\n" +
    "M=D\n" +
    "@SP\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "@R13\n" +
    "A=M\n" +
    "M=D";

    private static final String RETURN =
    // *(LCL - 5) -> R13
    "@LCL\n" +
    "D=M\n" +
    "@5\n" +
    "A=D-A\n" +
    "D=M\n" +
    "@R13\n" +
    "M=D\n" +
    // *(SP - 1) -> *ARG
    "@SP\n" +
    "A=M-1\n" +
    "D=M\n" +
    "@ARG\n" +
    "A=M\n" +
    "M=D \n" +
    // ARG + 1 -> SP
    "D=A+1\n" +
    "@SP\n" +
    "M=D\n" +
    // *(LCL - 1) -> THAT; LCL--
    "@LCL\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "@THAT\n" +
    "M=D\n" +
    // *(LCL - 1) -> THIS; LCL--
    "@LCL\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "@THIS\n" +
    "M=D\n" +
    // *(LCL - 1) -> ARG; LCL--
    "@LCL\n" +
    "AM=M-1\n" +
    "D=M\n" +
    "@ARG\n" +
    "M=D\n" +
    // *(LCL - 1) -> LCL
    "@LCL\n" +
    "A=M-1\n" +
    "D=M\n" +
    "@LCL\n" +
    "M=D\n" +
    // R13 -> A
    "@R13\n" +
    "A=M\n" +
    "0;JMP";

    // Constructor for CodeWriter class
    // opens an output file and gets ready to write into it
    public CodeWriter(File output) throws IOException{
        currentFileName = null;
		try {
			writer = new PrintWriter(output);
		} catch(FileNotFoundException fnf) {
            throw new FileNotFoundException("File not found: " + fnf.getMessage());
        }
    }
    
    /*
     * Writes to the output file the assembly code that implements 
     * the given arithmetic-logical command
     */
    public void writeArithmetic(String command) throws IOException{

        if(command.equalsIgnoreCase("add")){
            writeLine(ADD);
        }

        if(command.equalsIgnoreCase("sub")){
            writeLine(SUB);
        }

        if(command.equalsIgnoreCase("neg")){
            writeLine(NEG);
        }

        if(command.equalsIgnoreCase("and")){
            writeLine(AND);
        }

        if(command.equalsIgnoreCase("or")){
            writeLine(OR);
        }

        if(command.equalsIgnoreCase("not")){
            writeLine(NOT);
        }

        if(command.equalsIgnoreCase("eq")){
            EQ();
        }

        if(command.equalsIgnoreCase("gt")){
            GT();
        }

        if(command.equalsIgnoreCase("lt")){
            LT();;
        }
    }

    
    /*
     * Writes to the output file the assembly code that implements 
     * the given Push or Pop command
     */
    public void writePushPop(int comType, String segment, int idx) throws Exception 
    {
        if(comType == Parser.C_PUSH){

                if(segment.equalsIgnoreCase("local")){
                    writeLine("@LCL\n" + "D=M\n" + "@" + idx + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
                }

                if(segment.equalsIgnoreCase("argument")){
                    writeLine("@ARG\n" + "D=M\n" + "@" + idx + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
                }

                if(segment.equalsIgnoreCase("this")) {
                    writeLine("@THIS\n" + "D=M\n" + "@" + idx + "\n" + "@" + idx + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
                }

                if(segment.equalsIgnoreCase("that")) {
                    writeLine("@THAT\n" + "D=M\n" + "@" + idx + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
                }

                if(segment.equalsIgnoreCase("pointer")) {
                    if (idx == 0){
                        writeLine("@THIS\n" + "D=M\n" + PUSH);
                    }
                    else{
                        writeLine("@THAT\n" + "D=M\n" + PUSH); 
                    }
                }

                if(segment.equalsIgnoreCase("constant")) {
                    writeLine("@" + idx + "\n" + "D=A\n" + PUSH);
                }

                if(segment.equalsIgnoreCase("static")) {
                    writeLine("@" + currentFileName + "." + idx + "\n" + "D=M\n" + PUSH);
                }

                if(segment.equalsIgnoreCase("temp")) {
                    writeLine("@R5\n" + "D=A\n" + "@" + idx + "\n" + "A=D+A\n" + "D=M\n" + PUSH);
                }
                

        } else if(comType == Parser.C_POP){

                if(segment.equalsIgnoreCase("local")) {
                    writeLine("@LCL\n" + "D=M\n" + "@" + idx + "\n" +  "D=D+A\n" + POP);
                }

                if(segment.equalsIgnoreCase("argument")) {
                    writeLine("@ARG\n" + "D=M\n" + "@" + idx + "\n" + "D=D+A\n" + POP);
                }

                if(segment.equalsIgnoreCase("this")) {
                    writeLine("@THIS\n" + "D=M\n" + "@" + idx + "\n" + "D=D+A\n" + POP);  
                }

                if(segment.equalsIgnoreCase("that")) {
                    writeLine( "@THAT\n" + "D=M\n" + "@" + idx + "\n" + "D=D+A\n" + POP);
                }

                if(segment.equalsIgnoreCase("pointer")) {
                    if (idx == 0){
                    writeLine( "@THIS\n" + "D=A\n" +  POP);
                    } else {
                    writeLine("@THAT\n" + "D=A\n" + POP);
                    }
                }

                if(segment.equalsIgnoreCase("static")) {
                    writeLine("@" + currentFileName + "." + idx + "\n" + "D=A\n" + POP);
                }

                if(segment.equalsIgnoreCase("temp")) {
                    writeLine(  "@R5\n" + "D=A\n" + "@" + idx + "\n" + "D=D+A\n" + POP);   
                }
            }
        }

        public void writeLabel(String arg1) throws IOException{
            writeLine("(" + funcName + "$" + arg1 + ")");
        }

        public void writeGoto(String arg1) throws IOException{
            writeLine( "@" + funcName + "$" + arg1);
            writeLine("0;JMP");
        }

        public void writeIf(String arg1) throws IOException{
            writeLine("@SP");
            writeLine("AM=M-1");
            writeLine("D=M");
            writeLine("@" + funcName + "$" + arg1);
            writeLine("D;JNE");
        }

        public void writeFunction(String arg1, int arg2) throws IOException{
            funcName = arg1;
            writeLine("(" + arg1 + ")");
            writeLine("@SP");
            writeLine("A=M");
            
            for (int i = 0; i < arg2; i += 1) {
                writeLine("M=0");
                writeLine("A=A+1");
            }

            writeLine("D=A");
            writeLine("@SP");
            writeLine("M=D");
        }

        public void writeCall(String arg1, int arg2) throws IOException{
            String c = nextCount();
            writeLine(
                    "@SP\n" +
                    "D=M\n" +
                    "@R13\n" +
                    "M=D\n" +
                    // @RET -> *SP
                    "@RET." + c + "\n" +
                    "D=A\n" +
                    "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    // SP++
                    "@SP\n" +
                    "M=M+1\n" +
                    // LCL -> *SP
                    "@LCL\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    // SP++
                    "@SP\n" +
                    "M=M+1\n" +
                    // ARG -> *SP
                    "@ARG\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    // SP++
                    "@SP\n" +
                    "M=M+1\n" +
                    // THIS -> *SP
                    "@THIS\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    // SP++
                    "@SP\n" +
                    "M=M+1\n" +
                    // THAT -> *SP
                    "@THAT\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "A=M\n" +
                    "M=D\n" +
                    // SP++
                    "@SP\n" +
                    "M=M+1\n" +
                    // R13 - n -> ARG
                    "@R13\n" +
                    "D=M\n" +
                    "@" + arg2 + "\n" +
                    "D=D-A\n" +
                    "@ARG\n" +
                    "M=D\n" +
                    // SP -> LCL
                    "@SP\n" +
                    "D=M\n" +
                    "@LCL\n" +
                    "M=D\n" +
                    "@" + arg1 + "\n" +
                    "0;JMP\n" +
                    "(RET." + c + ")");
        }

        public void writeReturn() throws IOException{
            writeLine(RETURN);
        }
    
    // help method that writes to a file
    private void writeLine(String line) throws IOException {
        writer.println(line);
    }

    /* 
    * help method that writes to the file the assembly code
    * that implements the EQ command
    */ 
    private void EQ() throws IOException {
        String n = nextCount();
        writeLine(
          "@SP\n" +
          "AM=M-1\n" +
          "D=M\n" +
          "A=A-1\n" +
          "D=M-D\n" +
          "@EQ.true." + n + "\n" +
          "D;JEQ\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=0\n" +
          "@EQ.after." + n + "\n" +
          "0;JMP\n" +
          "(EQ.true." + n + ")\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=-1\n" +
          "(EQ.after." + n + ")");
      }

    /* 
    * help method that writes to the file the assembly code
    * that implements the GT command
    */ 
      private void GT() throws IOException {
        String n = nextCount();
        writeLine(
          "@SP\n" +
          "AM=M-1\n" +
          "D=M\n" +
          "A=A-1\n" +
          "D=M-D\n" +
          "@GT.true." + n + "\n" +
          "\nD;JGT\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=0\n" +
          "@GT.after." + n + "\n" +
          "0;JMP\n" +
          "(GT.true." + n + ")\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=-1\n" +
          "(GT.after." + n + ")");
      }

    /* 
    * help method that writes to the file the assembly code
    * that implements the EQ command
    */ 
      private void LT() throws IOException {
        String n = nextCount();
        writeLine(
          "@SP\n" +
          "AM=M-1\n" +
          "D=M\n" +
          "A=A-1\n" +
          "D=M-D\n" +
          "@LT.true." + n + "\n" +
          "D;JLT\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=0\n" +
          "@LT.after." + n + "\n" +
          "0;JMP\n" +
          "(LT.true." + n + ")\n" +
          "@SP\n" +
          "A=M-1\n" +
          "M=-1\n" +
          "(LT.after." + n + ")");
      }

      public void init() throws IOException{
        writeLine("@256");
        writeLine("D=A");
        writeLine("@SP");
        writeLine("M=D");
        writeLine("// call Sys.init 0");          
        writeCall("Sys.init", 0);
        writeLine("0;JMP");
      }

    private String nextCount() {
        count++;
        return Integer.toString(count);
    }

    public void setFileName(String currentFileName) {
        this.currentFileName = currentFileName;
    }

    public void close(){
       writer.close();
    }
    
}