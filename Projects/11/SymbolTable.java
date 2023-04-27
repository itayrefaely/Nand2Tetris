import java.util.HashMap;

public class SymbolTable {

    // static and field variables
    private HashMap<String, Symbol> classTable;
    // var and argument
    private HashMap<String, Symbol> subRoutineTable;
    // keeps track of indices for each symbol kind, static field arg and var
    private HashMap<String, Integer> indices;

    // creates new empty symbol table
    public SymbolTable() {
        classTable = new HashMap<>();
        subRoutineTable = new HashMap<>();
        indices = new HashMap<>();
        indices.put("var", 0);
        indices.put("argument", 0);
        indices.put("static", 0);
        indices.put("field", 0);
    }

    // resets the subroutine table
    public void resetSubRoutine() {
        subRoutineTable.clear();
        indices.put("var", 0);
        indices.put("argument", 0);
    }

    // defines a new variable with an index of last index + 1
    public void define(String name, String type, String kind) {
        int index = varCount(kind);
        Symbol newSymbol = new Symbol(type, kind, index);
        indices.put(kind, index + 1);

        if(kind.equals("static") || kind.equals("field")) {
            classTable.put(name, newSymbol);
            
        } 
        else if(kind.equals("argument") || kind.equals("var")) {
            subRoutineTable.put(name, newSymbol);
        }
    }

    // returns the number of variables of the given kind already defined in the table
    public int varCount(String kind) {
        return indices.get(kind);
    }

    // return the kind of the named identifier, if not found returns NONE
    public String kindOf(String name) {
        if(classTable.containsKey(name)) {
            return classTable.get(name).getKind();
        }
        else if(subRoutineTable.containsKey(name)) {
            return subRoutineTable.get(name).getKind();
        }
        else return "NONE";
    }

    // returns the type of the named variable
    public String typeOf(String name) {
        if (subRoutineTable.containsKey(name)) {
            return subRoutineTable.get(name).getType();

        }
        else if (classTable.containsKey(name)) {
            return classTable.get(name).getType();
        } 
        else {
            return "";
        }
    }

    // returns the index of the named variable
    public int indexOf(String name) {
        if(classTable.containsKey(name)) {
            return classTable.get(name).getIndex();
        }
        else if(subRoutineTable.containsKey(name)) {
            return subRoutineTable.get(name).getIndex();
        }
        else return -1;
    }
}