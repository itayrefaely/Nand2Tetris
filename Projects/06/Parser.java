import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.DataInputStream;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParsePosition;


public class Parser {
	public  String strFile;  //for reading line by line from file
	public  String strFileArr[];  //string array
	public static String currComm;// actualy line in string array
	//public int lineCount; //line counter
	public BufferedReader reader;
	public static instructionType comType;  //A,C,L Command
	public static int symbValue  = 16; //symbol value
	
	
	//c-tor opens the input file and gets ready to parse it 
	Parser(String fileName) throws IOException 
	{
		reader = new BufferedReader(new FileReader(fileName)); 
	}
         
		
		   /*copies string to string array
		   strFileArr = strFile.split("\n");
		   //trim
		   for(int i=0; i < strFileArr.length; i++){
			   strFileArr[i] =  strFileArr[i].trim();
       }
	   */
		   
	
	
	

	//has more command
	public boolean hasMoreLines() {
		if(strFile != null) return true;
		return false;
		
	}
	
	//reads next command
	public  boolean advance() throws IOException
	{
		strFile = read();
		if(hasMoreLines()){
		strFile = removeComments(strFile);
		return true;
		}
		reader.close();
		return false;
	}
	
	//Returns type of current command
	public instructionType instructionType()
	{
		if(strFile.contains("("))
		{
			return comType = instructionType.L_COMMAND;
		}
		else if(strFile.contains("@"))
		{
			return comType = instructionType.A_COMMAND;
		}
		return instructionType.C_COMMAND;
	}
	
	//symbol
	public String symbol()
	{
		String retLable = "";
		if(strFile.contains("@"))
		{
			retLable = strFile.substring(strFile.lastIndexOf("@") + 1);
			//retLable = retLable.replaceAll("@", "");
		}
		else 
			if(strFile.contains("("))
			{
				retLable = strFile.substring(strFile.lastIndexOf("(") + 1, strFile.length() - 1);
				//retLable = retLable.replaceAll("\\((.*?)\\)", "$1");
			}
		return retLable;
	}
	
	//dest
	public String dest() {
		if(strFile.contains("="))
		{
		String retDest = strFile.replaceAll("\\s", "");
		int endIndex = retDest.lastIndexOf("=");
		retDest =  retDest.substring(0,endIndex);
		return retDest;
		}
		return null;
	}
	//comp
	public String comp() {
		String retComp = strFile.replaceAll("\\s", ""); 
		if(strFile.contains("="))
		{
		int endIndex = retComp.lastIndexOf("=");

		retComp =  retComp.substring(endIndex+1,retComp.length());
		}
		else if(strFile.contains(";"))
		{
			//retComp =  retComp.substring(0,1) ;
			int endIndex = retComp.lastIndexOf(";");
			retComp =  retComp.substring(0,endIndex);
		}
		return retComp;
		
	}
	
	//jump
	public String jump() {
		if(strFile.contains(";"))
		{
			String retJump = strFile.replaceAll("\\s", ""); 
			int endIndex = retJump.lastIndexOf(";");
			return retJump.substring(endIndex+1,retJump.length());
		}
		return null;
		
	}
	
	
	/*
	 * help methods
	 */

	public String read(){
		try {
			return reader.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	//removes comments from source file
	public String removeComments(String file) {
		String tmpFile =  file.replaceAll( "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/|(?m)^[ \t]*\r?\n|null|\t", "" );
		tmpFile = tmpFile.replaceAll("(?m)^[ \t]*\r?\n", "");
        return tmpFile;
	}
	
	//command type, enums
	public enum instructionType
	{
		A_COMMAND,L_COMMAND,C_COMMAND
	}
	
    // Help methods for HackAssembler class

	//dec to bin converter
	public static String decimalToBinary(int value) {
	String binVal = Integer.toBinaryString(value);
		return binVal;
		
	}
	//check's if number
	public boolean isNum(String num)
	{
		NumberFormat formatter = NumberFormat.getInstance();
		  ParsePosition pos = new ParsePosition(0);
		  formatter.parse(num, pos);
		  return  num.length() == pos.getIndex();
		
	}
	
	//adds zeroes
	public String addZero(String num)
	{
		StringBuilder sb = new StringBuilder();

		for (int toPrepend=16-num.length(); toPrepend>0; toPrepend--) {
		    sb.append('0');
		}

		sb.append(num);
		String result = sb.toString();
		return result;
	}
}



   
    


