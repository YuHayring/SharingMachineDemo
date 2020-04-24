package cn.hayring.sharingmachine.csjson;

public class Token extends CSJson {
    public Token() {
        super(TOKEN);
    }

    private String id;

    private String token;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Token(String id, String token) {
        super(TOKEN);
        this.id = id;
        this.token = token;
    }
}
