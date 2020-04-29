package my.code;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * aidl 的实体类，只需要保证包名、类名一致就可以，不需要放在同一个目录下
 */
public final class Book implements Parcelable {
    private String mName;
    private int mNumber;

    public Book(String mName, int mNumber) {
        this.mName = mName;
        this.mNumber = mNumber;
    }

    protected Book(Parcel in) {
        this.mName = in.readString();
        this.mNumber = in.readInt();
    }

    public String getName() {
        return mName;
    }

    public int getNumber() {
        return mNumber;
    }

    public static final Creator<Book> CREATOR = new Creator<Book>() {
        @Override
        public Book createFromParcel(Parcel in) {
            return new Book(in);
        }

        @Override
        public Book[] newArray(int size) {
            return new Book[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
        dest.writeInt(mNumber);
    }
}
