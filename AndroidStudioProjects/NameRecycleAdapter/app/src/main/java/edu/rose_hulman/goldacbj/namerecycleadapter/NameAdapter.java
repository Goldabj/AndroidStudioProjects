package edu.rose_hulman.goldacbj.namerecycleadapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by goldacbj on 6/17/2016.
 */
public class NameAdapter extends RecyclerView.Adapter<NameAdapter.ViewHolder> {
    private Context mContext;
    final ArrayList<String> mNames = new ArrayList<String>();
    private Random mRandom = new Random();
    private RecyclerView mRecyclerView;

    public NameAdapter(Context context, RecyclerView recyclerView) {
        mContext = context;
        for(int i = 0; i< 5; i++) {
            mNames.add(getRandomName());
        }
        mRecyclerView = recyclerView;
    }

    private String getRandomName() {
        String[] names = new String[]{
                "Hannah", "Emily", "Sarah", "Madison", "Brianna",
                "kaylee", "Kaitlyn", "Hailey", "Alexis", "Elizabeth",
                "Micheal", "Jacob", "Matthew", "Nicholas", "Christopher",
                "Joseph", "Joshua", "Zachary", "Andrew", "William"
        };
        return names[mRandom.nextInt(names.length)];
    }



    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.name_view, null, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = mNames.get(position);
        holder.mNameTextView.setText(name);
        holder.mPositoinTextView.setText(String.format("I am #%d", (position+1)));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return mNames.size();
    }

    public void addName() {
        mNames.add(0, getRandomName());
        notifyItemInserted(0);
        mRecyclerView.getLayoutManager().scrollToPosition(0);
    }

    public void removeName(int postion) {
        mNames.remove(postion);
        notifyItemRemoved(postion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView mNameTextView;
        private TextView mPositoinTextView;

        public ViewHolder(View itemView) {
            super(itemView);

            mNameTextView = (TextView) itemView.findViewById(R.id.name_view);
            mPositoinTextView = (TextView) itemView.findViewById(R.id.position_view);

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int pos = getAdapterPosition();
                    removeName(pos);
                    return true;
                }
            });

        }

    }
}
