package service;

import java.util.ArrayList;
import product.product;

public class services {

    private ArrayList<product> products = new ArrayList<>();

    // CREATE
    public void addProduct(String name, double price, int quantity) {
        if (quantity <= 0) {
            
            
            return;
        }

        products.add(new product(name, price, quantity));
    }

    // READ
    public ArrayList<product> getProducts() {
        return products;
    }

    // UPDATE
    public void updateProduct(String name, int quantity) {
        for (product product : products) {
            if (product.getName().equals(name)) {
                product.setQuantity(quantity);
                return;
            }
        }
    }

    // DELETE
    public void deleteProduct(String name) {
    for (int i = 0; i < products.size(); i++) {
        if (products.get(i).getName().equals(name)) {
            products.remove(i);
            return;
        }
    }
}

    // TOTAL
    public double getTotal() {
        double total = 0;

        for (product product : products) {
            total += product.getSubtotal();
        }

        return total;
    }

    // CLEAR
    public void clearProducts() {
        products.clear();
    }
}