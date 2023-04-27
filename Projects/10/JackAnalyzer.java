import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JackAnalyzer {

    public static void main(String[] args) throws IOException {
        
        String path = args[0];
        File file = new File(path);

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            assert files != null;
            for (File f : files) {
                if (f.getName().endsWith(".jack")) {
                    String fileName = path + "/" + f.getName().substring(0, f.getName().length() - 5);
                    FileReader fileReader = new FileReader(f);
                    FileWriter output = new FileWriter(fileName + ".xml");
                    CompilationEngine compilationEngine = new CompilationEngine(fileReader, output);
                    compilationEngine.compileClass();
                    output.close();
                }
            }
        } else {
            FileReader fileReader = new FileReader(file + ".jack");
            FileWriter output = new FileWriter(file + ".xml");
            CompilationEngine compilationEngine = new CompilationEngine(fileReader, output);
            compilationEngine.compileClass();
            output.close();
        }
    }
}