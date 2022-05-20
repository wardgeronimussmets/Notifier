import java.util.HashMap;

public class GameDeal {
    private String category;
    private String body;
    private String link;
    public GameDeal(HashMap<String,Object> game){
        category = game.get("Category").toString();
        body = game.get("Body").toString();
        link = game.get("Link").toString();
    }

    public String getCategory() {
        return category;
    }

    public String getBody() {
        return body;
    }

    public String getLink() {
        return link;
    }
}
