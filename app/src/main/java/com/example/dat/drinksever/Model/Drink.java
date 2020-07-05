package com.example.dat.drinksever.Model;

public class Drink {
    private String Id;
    private String Name;
    private String Link;
    private String MenuId;
    private String Price;

    public Drink() {
    }

    public String getId() {
        return Id;
    }

    public void setI(String i) {
        Id = i;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getLink() {
        return Link;
    }

    public void setLink(String link) {
        Link = link;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }
}
