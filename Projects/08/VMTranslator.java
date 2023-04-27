import java.io.File;
import java.util.ArrayList;
import java.io.FileNotFoundException;

public class VMTranslator{
    
    public static void main(String[] args) throws Exception {
        
        if(args.length > 0) {

            ArrayList<File> files = new ArrayList<File>();

            File input = new File(args[0]);
            getFiles(input, files);

            if(!files.isEmpty()) {
                String outputName = input.getName();
                if(outputName.indexOf('.') > 0) {
                    outputName = outputName.substring(0, outputName.indexOf('.'));
                } else if(outputName.indexOf('/') > 0) {
                    outputName = outputName.substring(0, outputName.indexOf('/'));
                }

                File output;
                if(input.isFile()) {
                    output = new File(input.getParent(), outputName + ".asm");
                } else {
                    output = new File(input, outputName + ".asm");
                }

                CodeWriter cw = new CodeWriter(output);

                cw.init();

                for(File f : files) {
                    String name = f.getName();
                    name = name.substring(0, name.indexOf('.'));
                    cw.setFileName(name);

                    Parser parser = new Parser(f);



                    while(parser.advance()) {
                        /* 
                        if(parser.commandType() == 0) {
                            System.out.println(f + " contains an invalid instruction.");
                            return;
                        }
                        */
                        if(parser.commandType() == Parser.C_ARITHMETIC) {
                            cw.writeArithmetic(parser.arg1());
                        }
                        else if(parser.commandType() == Parser.C_PUSH || parser.commandType() == Parser.C_POP) {
                            cw.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
                        }
                        else if(parser.commandType() == Parser.C_LABEL){
                            cw.writeLabel(parser.arg1());
                        }
                        else if(parser.commandType() == Parser.C_GOTO){
                            cw.writeGoto(parser.arg1());
                        }
                        else if(parser.commandType() == Parser.C_IF){
                            cw.writeIf(parser.arg1());
                        }
                        else if(parser.commandType() == Parser.C_FUNCTION){
                            cw.writeFunction(parser.arg1, parser.arg2);
                        }
                        else if(parser.commandType() == Parser.C_CALL){
                            cw.writeCall(parser.arg1(), parser.arg2());
                        }
                        else if(parser.commandType() == Parser.C_RETURN){
                            cw.writeReturn();
                        } else break;
                    }
                }
            cw.close();
            }
        }
    }

    private static void getFiles(File input, ArrayList<File> files) throws FileNotFoundException {
        if(input.isFile()) {
            // check for .vm extension before adding to list of files
            String filename = input.getName();
            int extension = filename.indexOf('.');
            if(extension > 0) {
                String fileExtension = filename.substring(extension + 1);
                if(fileExtension.equalsIgnoreCase("vm")) {
                    files.add(input);
                }
            }
        } else if(input.isDirectory()) {
            File[] innerFiles = input.listFiles();
            for(File f : innerFiles) {
                getFiles(f, files);
            }
        } else {
            throw new FileNotFoundException("Could not find file or directory.");
        }
    }
}



        
        
        
	

        
