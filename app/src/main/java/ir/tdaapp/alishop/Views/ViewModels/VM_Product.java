package ir.tdaapp.alishop.Views.ViewModels;

public class VM_Product {
    private int Id;
    private String ProductName;
    private String Image;
    private int Place;
    private int Total;

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public int getPlace() {
        return Place;
    }

    public void setPlace(int place) {
        Place = place;
    }

    public int getTotal() {
        return Total;
    }

    public void setTotal(int total) {
        Total = total;
    }
}
