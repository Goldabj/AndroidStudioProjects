package edu.rosehulman.movieqoutes;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Matt Boutell on 12/15/2015.
 */
public class MovieQuoteAdapter extends RecyclerView.Adapter<MovieQuoteAdapter.ViewHolder> {

    private List<MovieQuote> mMovieQuotes;
    private Callback mCallback;

    private DatabaseReference mDatabase;


    public MovieQuoteAdapter(Callback callback) {
        mCallback = callback;
        mMovieQuotes = new ArrayList<>();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("quotes");
        mDatabase.addChildEventListener(new MovieQoutListener());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_quote_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final MovieQuote movieQuote = mMovieQuotes.get(position);
        holder.mQuoteTextView.setText(movieQuote.getQuote());
        holder.mMovieTextView.setText(movieQuote.getMovie());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCallback.onEdit(movieQuote);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                remove(mMovieQuotes.get(position));
                return true;
            }
        });
    }

    public void remove(MovieQuote movieQuote) {
        //TODO: Remove the next line(s) and use Firebase instead
        mDatabase.child(movieQuote.getKey()).removeValue();
    }


    @Override
    public int getItemCount() {
        return mMovieQuotes.size();
    }

    public void add(MovieQuote movieQuote) {
        //TODO: Remove the next line(s) and use Firebase instead
        mDatabase.push().setValue(movieQuote);
    }

    public void update(MovieQuote movieQuote, String newQuote, String newMovie) {
        //TODO: Remove the next line(s) and use Firebase instead
        movieQuote.setQuote(newQuote);
        movieQuote.setMovie(newMovie);
        mDatabase.child(movieQuote.getKey()).setValue(movieQuote);
    }

    public interface Callback {
        public void onEdit(MovieQuote movieQuote);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mQuoteTextView;
        private TextView mMovieTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            mQuoteTextView = (TextView) itemView.findViewById(R.id.quote_text);
            mMovieTextView = (TextView) itemView.findViewById(R.id.movie_text);
        }
    }

    public class MovieQoutListener implements  ChildEventListener {

        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            MovieQuote qoute = dataSnapshot.getValue(MovieQuote.class);
            qoute.setKey(dataSnapshot.getKey());
            mMovieQuotes.add(qoute);
            notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String key = dataSnapshot.getKey();
            int pos = 0;
            for (int i = 0; i < mMovieQuotes.size(); i++) {
                if( key.equals(mMovieQuotes.get(i).getKey())) {
                    pos = i;
                    break;
                }
            }
            MovieQuote qoute = dataSnapshot.getValue(MovieQuote.class);
            mMovieQuotes.get(pos).setMovie(qoute.getMovie());
            mMovieQuotes.get(pos).setQuote(qoute.getQuote());
            notifyDataSetChanged();
        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String key = dataSnapshot.getKey();
            int pos = 0;
            for (int i = 0; i < mMovieQuotes.size(); i++) {
                if( key.equals(mMovieQuotes.get(i).getKey())) {
                    pos = i;
                    break;
                }
            }
            mMovieQuotes.remove(pos);
            notifyDataSetChanged();
        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e("MQ", databaseError.getMessage());
        }
    }


}
