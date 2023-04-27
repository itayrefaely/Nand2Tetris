import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class VMWriter {

    private FileWriter fileWriter;

    public VMWriter(File outputFile) {
        try {
            fileWriter = new FileWriter(outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a push command
    public void writePush(String segment, int index) {
        if (segment.equals("field")) {
            segment = "this";
        }
        if (segment.equals("var")) {
            segment = "local";
        }
        try {
            fileWriter.write("push " + segment + " " + index + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a pop command
    public void writePop(String segment, int index) {
        if (segment.equals("field")) {
            segment = "this";
        }
        if (segment.equals("var")) {
            segment = "local";
        }
        try {
            fileWriter.write("pop " + segment + " " + index + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes an arithmetic command
    public void writeArithmetic(String command) {
        try {
            fileWriter.write(command + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a label command
    public void writeLabel(String label) {
        try {
            fileWriter.write("label " + label + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a goto command
    public void writeGoto(String label) {
        try {
            fileWriter.write("goto " + label + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes an if-goto command
    public void writeIf(String label) {
        try {
            fileWriter.write("if-goto " + label + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a call command
    public void writeCall(String name, int numArgs) {
        try {
            fileWriter.write("call " + name + " " + numArgs + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a function command
    public void writeFunction(String name, int locals) {
        try {
            fileWriter.write("function " + name + " " + locals + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // writes a return command
    public void writeReturn() {
        try {
            fileWriter.write("return\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // closes the output file
    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}