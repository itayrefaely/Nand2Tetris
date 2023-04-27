import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;




public class HackAssembler {
	public static  int counter=0;
	public static int symbolAddress = 16;
	public static String compT,destT,jumpT; // temp's
	public static void main(String[] args) throws IOException {
	
	
		
		String name = args[0].substring(0, args[0].indexOf('.'));	//copies name of existing file without the file type
		
		String outFileName = name+".hack";  //out file name
		
		SymbolTable st = new SymbolTable(); //init's symbol table
		
		Code ct = new Code();  //init's code tables
		
		Parser newParser = new Parser(args[0]);  //new parser object
	
		File out = new File(outFileName);  //output, .hack file
		
		Writer fw = null;
		try {
			fw = new FileWriter(out.getAbsoluteFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
		BufferedWriter bw = new BufferedWriter(fw); // Ready to write on file
	
		
		
		//first pass
		while(newParser.advance()) {  
		if(newParser.instructionType()== Parser.instructionType.L_COMMAND) { 
			
			st.addEntry(newParser.symbol(),Integer.toString(counter)) ; //adds new symbol to symbol table
		}
		else counter++; //next line
		
		//newParser.advance();  // next command
		
		}
		//newParser.lineCount =-1;   // resets counter for starts from first line
		
		   newParser.reader = new BufferedReader(new FileReader(args[0])); 
		   newParser.strFile = null;
	   
		
		//second pass
		while(newParser.advance())
		{
			if(newParser.instructionType()== Parser.instructionType.A_COMMAND) //@xxx
			{
				if(newParser.instructionType()== Parser.instructionType.A_COMMAND)
				{
				String tmp = newParser.symbol(); //returns xxx
					if(newParser.isNum(tmp))  //checks if xxx is number
					{
						int xxx = Integer.parseInt(tmp);
						tmp = Parser.decimalToBinary(xxx);	// return binary value of xxx
						tmp = newParser.addZero(tmp);
						try {
							bw.write(tmp + '\n');//write to hack
						} catch (IOException e) {
							e.printStackTrace();
						}
						
					}
					else  //if not number
					{
						if(!st.containKey(tmp))  // not exists in Symbol Table
						{
							st.addEntry(tmp,Integer.toString(symbolAddress));  //Adds to Symbol Table
							symbolAddress++;
						}
						 if(st.containKey(tmp)) // already exists in Symbol Table
							{
							String tmp2 = st.getValue(tmp);
							int xxx = Integer.parseInt(tmp2);
							tmp2 = Parser.decimalToBinary(xxx);
							tmp2 = newParser.addZero(tmp2);
							try {
								bw.write(tmp2+'\n');  //write to hack
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}//if command type C_COMMAND 
			if(newParser.instructionType()== Parser.instructionType.C_COMMAND)
			{
				if(newParser.strFile.contains("="))//dest=comp
				{
					destT = ct.getDest(newParser.dest());
					compT = ct.getComp(newParser.comp());
					jumpT = ct.getJump("NULL");  //no need jump
					try {
						bw.write("111" + compT + destT + "000" +'\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
				else if(newParser.strFile.contains(";")) //jump
				{
					destT = ct.getDest("NULL"); // no need dest
					compT = ct.getComp(newParser.comp());
					jumpT = ct.getJump(newParser.jump());
					
					try {
						bw.write("111" + compT + "000" + jumpT +'\n');
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}//if command type C_COMMAND 
			//newParser.advance();		
		}//end while
		
try {
	bw.close();
} catch (IOException e) {
	e.printStackTrace();
}
}//main

}//end class