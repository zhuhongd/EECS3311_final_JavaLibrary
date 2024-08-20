package model.assets;

public class PhysicalItem extends Item {

    private int copiesAvailable;

    private boolean isLost;
    private String location;

    public PhysicalItem(String title, String author) {
        super(title, author);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public boolean isLost() {
        return isLost;
    }

    public void setLost(boolean lost) {
        isLost = lost;
    }
}
