package sample.animation;

public class Population {
    private String year;
    private String quantity;

    public Population(String year, String quantity){
        this.year = year;
        this.quantity = quantity;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
