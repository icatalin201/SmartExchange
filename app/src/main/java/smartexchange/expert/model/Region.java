package smartexchange.expert.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Region implements Parcelable {

    private String name;
    private Exchange exchange;
    private int flag;
    private float value;
    private String symbol;
    private List<Banknote> banknoteList;

    public Region() { }

    public Region(String name, Exchange exchange, int flag, float value, String symbol) {
        this.name = name;
        this.exchange = exchange;
        this.flag = flag;
        this.value = value;
        this.symbol = symbol;
    }

    private Region(Parcel in) {
        name = in.readString();
        exchange = in.readParcelable(Exchange.class.getClassLoader());
        flag = in.readInt();
        value = in.readFloat();
        symbol = in.readString();
    }

    public static final Creator<Region> CREATOR = new Creator<Region>() {
        @Override
        public Region createFromParcel(Parcel in) {
            return new Region(in);
        }

        @Override
        public Region[] newArray(int size) {
            return new Region[size];
        }
    };

    public List<Banknote> getBanknoteList() {
        return banknoteList;
    }

    public void setBanknoteList(List<Banknote> banknoteList) {
        this.banknoteList = banknoteList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Exchange getExchange() {
        return exchange;
    }

    public void setExchange(Exchange exchange) {
        this.exchange = exchange;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeParcelable(exchange, flags);
        dest.writeInt(flag);
        dest.writeFloat(value);
        dest.writeString(symbol);
    }
}
