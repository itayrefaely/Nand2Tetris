public class Symbol {

    private String type;
    private String kind;
    private int index;

    // constructs a new symbol with the given type, kind and index
    public Symbol(String type, String kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    //* getters */
    
    public String getType() {
        return type;
    }

    public String getKind() {
        return kind;
    }

    public int getIndex() {
        return index;
    }


}