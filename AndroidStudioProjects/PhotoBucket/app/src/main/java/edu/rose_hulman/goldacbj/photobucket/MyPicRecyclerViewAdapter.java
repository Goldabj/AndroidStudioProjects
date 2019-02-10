package edu.rose_hulman.goldacbj.photobucket;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Pics} and makes a call to the
 * specified {@link PicFragment.OnListFragmentInteractionListener}.
 */
public class MyPicRecyclerViewAdapter extends RecyclerView.Adapter<MyPicRecyclerViewAdapter.ViewHolder> implements Parcelable {

    private List<Pics> mValues;
    private List<Pics> allValues;
    private List<Pics> mCurrentValues;
    private PicFragment.OnListFragmentInteractionListener mListener;
    private String mCurrentUserID;
    private boolean bAllValues;



    public MyPicRecyclerViewAdapter(PicFragment.OnListFragmentInteractionListener listener, String currrentID) {
        mListener = listener;
        mCurrentUserID = currrentID;
        mValues = new ArrayList<>();
        allValues = new ArrayList<>();
        mCurrentValues = mValues;
        bAllValues = false;
    }

    protected MyPicRecyclerViewAdapter(Parcel in) {
        mValues = in.createTypedArrayList(Pics.CREATOR);
        allValues = in.createTypedArrayList(Pics.CREATOR);
        mCurrentValues = mValues;
    }

    public void addNewPhoto(Pics newPicture) {
        if(mCurrentUserID == null) {
            return;
        }
        allValues.add(newPicture);
        if(newPicture.getUid().equals(mCurrentUserID)) {
            mValues.add(newPicture);
        }
        //setShowAll(bAllValues);
        notifyDataSetChanged();
    }

    public void removePhoto(Pics item) {
        if(mCurrentUserID == null) {
            return;
        }
        for(Pics pic: mValues) {
            if(item.key.equals(pic.key)) {
                mValues.remove(pic);
                break;
            }
        }
        for(Pics pic : allValues) {
            if(item.key.equals(pic.key)) {
                allValues.remove(pic);
                break;
            }
        }
        //setShowAll(bAllValues);
        notifyDataSetChanged();
    }

    public void editPic(Pics newPic) {
        if(mCurrentUserID == null) {
            return;
        }
        for(int i = 0; i < mValues.size(); i++) {
            if(mValues.get(i).key.equals(newPic.key)) {
                mValues.get(i).caption = newPic.caption;
                mValues.get(i).url = newPic.url;
                break;
            }
        }
        for(int i = 0; i < allValues.size(); i++) {
            if(allValues.get(i).key.equals(newPic.key)) {
                allValues.get(i).caption = newPic.caption;
                allValues.get(i).url = newPic.url;
                break;
            }
        }
        //setShowAll(bAllValues);
        notifyDataSetChanged();
    }

    public void setShowAll(boolean showAll) {
        if(showAll) {
            bAllValues = true;
            mCurrentValues = allValues;
        }else {
            bAllValues = false;
            mCurrentValues = mValues;
        }
        notifyDataSetChanged();
    }

    public void setUser(String newUserID) {
        if(mCurrentUserID != null && mCurrentUserID.equals(newUserID)) {
            return;
        }
        mCurrentUserID = newUserID;
        mValues = new ArrayList<>();
        mCurrentValues = mValues;
        for(Pics pic : allValues) {
            if(pic.getUid().equals(mCurrentUserID)) {
                mValues.add(pic);
            }
        }
        notifyDataSetChanged();
    }


    public static final Creator<MyPicRecyclerViewAdapter> CREATOR = new Creator<MyPicRecyclerViewAdapter>() {
        @Override
        public MyPicRecyclerViewAdapter createFromParcel(Parcel in) {
            return new MyPicRecyclerViewAdapter(in);
        }

        @Override
        public MyPicRecyclerViewAdapter[] newArray(int size) {
            return new MyPicRecyclerViewAdapter[size];
        }
    };

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_pic_row_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Pics mItem = mCurrentValues.get(position);

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onLongFragmentClick(mItem);
                return true;
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onListFragmentInteraction(mItem);
            }
        });

        holder.mCaptionText.setText(mItem.getCaption());
        holder.mURLText.setText(mItem.getUrl());
    }

    @Override
    public int getItemCount() {
        return mCurrentValues.size();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(mValues);
        parcel.writeTypedList(allValues);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mCaptionText;
        public final TextView mURLText;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mCaptionText = (TextView) view.findViewById(R.id.caption_text);
            mURLText = (TextView) view.findViewById(R.id.url_text);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mCaptionText.getText() + "'";
        }
    }
}
