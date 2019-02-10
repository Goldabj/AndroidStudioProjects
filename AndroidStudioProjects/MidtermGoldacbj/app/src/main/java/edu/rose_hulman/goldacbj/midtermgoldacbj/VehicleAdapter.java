package edu.rose_hulman.goldacbj.midtermgoldacbj;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.rose_hulman.goldacbj.midtermgoldacbj.Utils.FontManager;

/**
 * Created by goldacbj on 7/6/2016.
 */
public class VehicleAdapter extends RecyclerView.Adapter<VehicleAdapter.ViewHolder> {
    private Context mContext;
    private RecyclerView mRecycler;
    private MatchingGame gamedata;
    private MainUIController mainController;

    public VehicleAdapter(RecyclerView recycler, Context context, MatchingGame game) {
        mContext = context;
        mRecycler = recycler;
        gamedata = game;
        mainController = (MainUIController) context;
    }

    public void resetGame(MatchingGame newGame) {
        gamedata = newGame;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ArrayList<Word> data = gamedata.getWords();
        Word wordPositoin = data.get(position);
        String icon = wordPositoin.getUnicode();
        holder.iconText.setText(icon);

        String word = wordPositoin.getEnglish();
        holder.wordText.setText(word);
    }

    @Override
    public int getItemCount() {
        return gamedata.getWords().size();
    }

    public void addItem(int position) {
        notifyItemInserted(position);
    }

    public void removeItem(int position) {
        notifyItemRemoved(position);
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView iconText;
        TextView wordText;

        public ViewHolder(View itemView) {
            super(itemView);
            iconText = (TextView) itemView.findViewById(R.id.wordIconTextView);
            wordText = (TextView) itemView.findViewById(R.id.wordTextView);

            // set iconView to hold icon
            Typeface iconFont = FontManager.getTypeface(mContext, FontManager.FONTAWESOME);
            FontManager.markAsIconContainer(iconText, iconFont);

            // Set onclick listenters
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    gamedata.hint();
                    mainController.itemLongClicked(getAdapterPosition());
                    return false;
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean match = gamedata.checkForMatch(getAdapterPosition());
                    mainController.itemClicked(match, getAdapterPosition());
                    if (match) {
                        removeItem(getAdapterPosition());
                    }
                }
            });
        }
    }

    public static interface MainUIController {
        public void itemClicked(boolean matched, int position);
        public void itemLongClicked(int position);
    }
}
