package wardgeronimussmets.Notifier;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Comparator;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.MyViewHolder> {
    private ArrayList<GameDealCategory> gameCategoryList;
    private Context context;

    public MyRecyclerAdapter(ArrayList<GameDeal> gamesList,Context context){
        this.context = context;
        gameCategoryList = new ArrayList<>();
        String category = "";
        int index = -1;
        for(GameDeal gameDeal: orderGamesByCategory(gamesList)){
            if(!category.equals(gameDeal.getCategory())){
                //new category
                category = gameDeal.getCategory();
                gameCategoryList.add(new GameDealCategory(category));
                index ++;
            }
            gameCategoryList.get(index).addGameDeal(gameDeal);
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView category;
        private LinearLayout linearLayout;
        private ViewGroup parent;

        public MyViewHolder(final View view,ViewGroup parent){
            super(view);
            this.parent = parent;
            category = view.findViewById(R.id.category_tv);
            linearLayout = view.findViewById(R.id.lineairLayout);

        }
        public ViewGroup getParent(){
            return parent;
        }
    }


    @NonNull
    @Override
    public MyRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_category,parent, false);

        return new MyViewHolder(itemView,parent);
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecyclerAdapter.MyViewHolder holder, int position) {
        holder.category.setText(gameCategoryList.get(position).getCategory());
        ViewGroup parent = holder.getParent();
        for(GameDeal deal: gameCategoryList.get(position).getGameDeals()){
            View gameView = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_deal,parent,false);
            TextView textView = gameView.findViewById(R.id.game_deal_body);
            textView.setText(deal.getBody());
            Button button = gameView.findViewById(R.id.game_deal_remove);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("Markel","We would like to remove " + deal.getBody());
                }
            });
            holder.linearLayout.addView(gameView);
        }

    }

    @Override
    public int getItemCount() {
        return gameCategoryList.size();
    }

    private ArrayList<GameDeal> orderGamesByCategory(ArrayList<GameDeal> deals){
        deals.sort(new Comparator<GameDeal>() {
            @Override
            public int compare(GameDeal gameDeal, GameDeal t1) { //negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
                return mapCategoryToInt(gameDeal.getCategory()) - mapCategoryToInt(t1.getCategory());
            }
        });
        return deals;
    }

    private int mapCategoryToInt(String category){
        if(category.equals("Steam")) return 0;
        else if(category.equals("Epic Games")) return 1;
        else return 2;
    }
}
