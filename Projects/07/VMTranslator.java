public class VMTranslator{
    
    public static void main(String[] args) throws Exception {

        String name = args[0].substring(0, args[0].indexOf('.'));	//copies name of existing file without the file type

		String outFileName = name+".asm";  //out file name

        Parser parser = new Parser(args[0]); // creates Parser

        CodeWriter cw = new CodeWriter(outFileName); // creates CodeWriter

        while(parser.advance()) {
            if(parser.commandType() == Parser.C_ARITHMETIC) {
                cw.writeArithmetic(parser.arg1());
            } else if(parser.commandType() == Parser.C_PUSH || parser.commandType() == Parser.C_POP) {
                cw.writePushPop(parser.commandType(), parser.arg1(), parser.arg2());
            }
        }

    cw.close();

    }
}



        
        
        
	

        
