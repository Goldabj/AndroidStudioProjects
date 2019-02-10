package edu.rose_hulman.goldacbj.linearlightsout;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LinearGame implements Parcelable{
	protected int[] lights;
	protected int numberOfX = 0;
	protected int numberOfO = 0;
	protected int numberOfButtons = 0;

	public LinearGame() {this(7);}

	/**
	 * 
	 * Constructs a panel of light buttons and a panel of buttons that start a
	 * new game and another that exits.
	 *
	 * @param numberOfButtons
	 *            -- number of light buttons
	 */
	public LinearGame(int numberOfButtons) {
		this.numberOfButtons = numberOfButtons;
		lights = new int[numberOfButtons];

		// set the buttons to either X or O
		for (int i = 0; i < this.numberOfButtons; i++) {
			if (Math.random() <= 0.5) {
				lights[i] = 1;
				this.numberOfX++;
			} else {
				lights[i] = 0;
				this.numberOfO++;
			}
		}
	}


    protected LinearGame(Parcel in) {
        lights = in.createIntArray();
        numberOfX = in.readInt();
        numberOfO = in.readInt();
        numberOfButtons = in.readInt();
    }

    public static final Creator<LinearGame> CREATOR = new Creator<LinearGame>() {
        @Override
        public LinearGame createFromParcel(Parcel in) {
            return new LinearGame(in);
        }

        @Override
        public LinearGame[] newArray(int size) {
            return new LinearGame[size];
        }
    };


    /**
		 * performs the correct action for light clicked at position index
		 *
		 */
		public boolean lightClickedAt(int index) {
			changeLight(index);
			if (index > 0) {
				changeLight(index - 1);
			}
			if (index < numberOfButtons - 1) {
				changeLight(index + 1);
			}
			return isGameOver();
		}


	/**
	 * 
	 * changes the label of the button pressed, and changes the label of the
	 * surrounding buttons
	 *
	 * @param i
	 * 		-- the index of the button
	 */
	public void changeLight(int i) {
		if (lights[i] == 1) {
			lights[i] = 0;
			this.numberOfX--;
			this.numberOfO++;
		} else {
			lights[i] = 1;
			this.numberOfO--;
			this.numberOfX++;
		}
	}

	/**
	 * 
	 * compares the number of Os and Xs, if either match the number of buttons
	 * then it returns true. else it returns false
	 *
	 * @return
	 */
	protected boolean isGameOver() {
		if (this.numberOfO == this.numberOfButtons
				|| this.numberOfX == this.numberOfButtons) {
			return true;
		}
		return false;
	}

	/*
	 * returns the text of the light at the current postitoin
	 *
	 * @param i
	 * 		-- the number button
	 */
	public int getLightValue(int i) {
		if(i > numberOfButtons) {
			return 0;
		}
		return lights[i];
	}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(lights);
        dest.writeInt(numberOfButtons);
        dest.writeInt(numberOfO);
        dest.writeInt(numberOfX);
    }
}
