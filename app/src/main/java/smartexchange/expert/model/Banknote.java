package smartexchange.expert.model;

public class Banknote {

    private String name;
    private String year;
    private int image;

    public Banknote() { }

    public Banknote(String name, int image) {
        this.name = name;
        this.image = image;
    }

    public Banknote(String name, String year, int image) {
        this.name = name;
        this.year = year;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
