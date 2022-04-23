package wardgeronimussmets.Notifier;

public class GameDeal {
    private String Category;
    private String Body;
    private String Link;

    private GameDeal(){}

    public GameDeal(String category, String body, String link) {
        this.Category = category;
        this.Body = body;
        this.Link = link;
    }

    public String getCategory() {
        return Category;
    }

    public String getBody() {
        return Body;
    }

    public String getLink() {
        return Link;
    }

    public void setCategory(String category) {
        this.Category = category;
    }

    public void setBody(String body) {
        this.Body = body;
    }

    public void setLink(String link) {
        this.Link = link;
    }
}
