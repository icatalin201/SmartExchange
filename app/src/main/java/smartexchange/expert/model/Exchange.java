package smartexchange.expert.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Exchange implements Parcelable {

    private int id;
    private String name;
    private float value;
    private int multiplier;
    private String calculatorFavorite;
    private String convertorFavorite;

    public Exchange() { }

    public Exchange(int id, String name, float value, int multiplier,
                    String calculatorFavorite, String convertorFavorite) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.multiplier = multiplier;
        this.calculatorFavorite = calculatorFavorite;
        this.convertorFavorite = convertorFavorite;
    }

    private Exchange(Parcel in) {
        id = in.readInt();
        name = in.readString();
        value = in.readFloat();
        multiplier = in.readInt();
        calculatorFavorite = in.readString();
        convertorFavorite = in.readString();
    }

    public static final Creator<Exchange> CREATOR = new Creator<Exchange>() {
        @Override
        public Exchange createFromParcel(Parcel in) {
            return new Exchange(in);
        }

        @Override
        public Exchange[] newArray(int size) {
            return new Exchange[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeFloat(value);
        dest.writeInt(multiplier);
        dest.writeString(calculatorFavorite);
        dest.writeString(convertorFavorite);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(int multiplier) {
        this.multiplier = multiplier;
    }

    public String getCalculatorFavorite() {
        return calculatorFavorite;
    }

    public void setCalculatorFavorite(String calculatorFavorite) {
        this.calculatorFavorite = calculatorFavorite;
    }

    public String getConvertorFavorite() {
        return convertorFavorite;
    }

    public void setConvertorFavorite(String convertorFavorite) {
        this.convertorFavorite = convertorFavorite;
    }
}
