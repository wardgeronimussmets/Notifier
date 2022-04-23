package wardgeronimussmets.Notifier;

import java.util.ArrayList;

public class GameDealCategory {

    private ArrayList<GameDeal> gameDeals;
    private String category;

    public GameDealCategory(String category) {
        gameDeals = new ArrayList<>();
        this.category = category;
    }

    public ArrayList<GameDeal> getGameDeals() {
        return gameDeals;
    }

    public String getCategory() {
        return category;
    }

    public void addGameDeal(GameDeal gameDeal){
        gameDeals.add(gameDeal);
    }
}
