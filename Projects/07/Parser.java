import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class Parser {

    public static final int C_ARITHMETIC = 1;
    public static final int C_PUSH = 2;
    public static final int C_POP = 3;
    public BufferedReader reader;
    public String command;
    public String arg1;
    public int arg2;

    // Constructor for the Parser Class
     Parser(String fileName) throws IOException 
	{
		reader = new BufferedReader(new FileReader(fileName));
        command = null; 
	}

    // checks if file has more lines
    public boolean hasMoreLines() {
		if(command != null) return true;
		return false;
	}
    // Reads the next command from the input and makes it the current  command
    public  boolean advance() throws IOException
	{
		command = read();
		if(hasMoreLines()){
            command = command.trim();
		    command = removeComments(command);
		    return true;
		}
		reader.close();
		return false;
	}

    // Returns the command type of the current command
    public int commandType(){
        if(command.equalsIgnoreCase("add") || command.equalsIgnoreCase("sub") || command.equalsIgnoreCase("neg") || command.equalsIgnoreCase("eq") || command.equalsIgnoreCase("gt") || command.equalsIgnoreCase("lt") || command.equalsIgnoreCase("and") || command.equalsIgnoreCase("or") || command.equalsIgnoreCase("not")){
            return C_ARITHMETIC;
        }
        else if(command.contains("push")){
            return C_PUSH;
        }
        else if(command.contains("pop")){
            return C_POP;
        } else return 0;
    }

    // Returns the first argument of the current command
    public String arg1(){
        if(command.contains("push") || command.contains("pop")){
            String[] current = command.split("\\s");
            return current[1];
        } else return command;
    }

    //  // Returns the second argument of the current command
    public int arg2(){
        if(command == null || command.isEmpty()) return -1;
    
        StringBuilder sb = new StringBuilder();
        boolean found = false;
        for(char c : command.toCharArray()){
            if(Character.isDigit(c)){
                sb.append(c);
                found = true;
            } else if(found){
                // If we already found a digit before and this char is not a digit, stop looping
                break;                
            }
        }
        
        String arg2 = sb.toString();
        return Integer.parseInt(arg2);
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
        
        public String removeComments(String command) {
            String newCommand =  command.replaceAll( "//.*|(\"(?:\\\\[^\"]|\\\\\"|.)*?\")|(?s)/\\*.*?\\*/|(?m)^[ \t]*\r?\n|null|\t", "" );
            newCommand = newCommand.replaceAll("(?m)^[ \t]*\r?\n", "");
            return newCommand;
        }

}