public class Token{
    
    private String tokenType;
    private String tokenValue;

    public Token(){
        tokenType = null;
        tokenValue = null;
    }

    public void setTokenValue(String value){
        this.tokenValue = value; 
    }

    public void setTokenType(String type){
        this.tokenType = type;
    }

    public String getTokenValue(){
        return this.tokenValue;
    }

    public String getTokenType(){
        return this.tokenType;
    }
    
}