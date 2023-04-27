import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileNotFoundException;


public class CodeWriter{

    private PrintWriter writer;
    private static int count = 0; 
    private String filename;
 
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

    // Constructor for CodeWriter class
    // opens an output file and gets ready to write into it
    public CodeWriter(String output) throws FileNotFoundException{
		try {
            filename = output.substring(output.lastIndexOf('/') + 1, output.lastIndexOf('.'));
            File out = new File(output);
			writer = new PrintWriter(out);
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
                    writeLine("@" + filename + "." + idx + "\n" + "D=M\n" + PUSH);
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
                    writeLine("@" + filename + "." + idx + "\n" + "D=A\n" + POP);
                }

                if(segment.equalsIgnoreCase("temp")) {
                    writeLine(  "@R5\n" + "D=A\n" + "@" + idx + "\n" + "D=D+A\n" + POP);   
                }
            }
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

    
    private String nextCount() {
        count++;
        return Integer.toString(count);
    }

    public void close(){
       writer.close();
    }
    
}