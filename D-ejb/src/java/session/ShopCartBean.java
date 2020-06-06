/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.CartItem;
import entity.Product;
import java.util.ArrayList;
import javax.ejb.Remove;
import javax.ejb.Stateful;

/**
 *
 * @author User
 */
@Stateful
public class ShopCartBean implements ShopCartBeanRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
      private ArrayList<CartItem> cart;
      private Product product;
      private int stockQuantity;

    public ShopCartBean() {
        cart = new ArrayList<>();
    }

    @Override
    public boolean add(CartItem cartItem) {
        boolean result = false;
        if (!"".equals(cartItem.getItemId())) {
            try {
                result = cart.add(cartItem);
                // update stock levels
                stockQuantity = product.getQuantity();
                product.setQuantity(stockQuantity - cartItem.getQuantity());
            } catch (Exception ex) {
            }

        }

        return result;
    }

    @Override
    public ArrayList<CartItem> getCart() {
        return cart;
    }

    @Remove
    public void remove() {
        cart = null;
    }

    @Override
    public boolean addCartItem(CartItem cartItem) {
        boolean result = false;
        // check if cart is empty
        if (cart.isEmpty()) {
            // add item
            result = add(cartItem);
             // update stock levels
                stockQuantity = product.getQuantity();
                product.setQuantity(stockQuantity - cartItem.getQuantity());
        }
        try {
            // look for matching id
            for (CartItem item : cart) {
                // check id not null

                // if match found, add quantities
                if (item.getItemId().equals(cartItem.getItemId())) {
                    item.setQuantity(item.getQuantity() + cartItem.getQuantity());
                     // update stock levels
                stockQuantity = product.getQuantity();
                product.setQuantity(stockQuantity - cartItem.getQuantity());
                    result = true;
                } else {
                    // if no match, add item
                    result = add(cartItem);
                }

            }
        } catch (Exception ex) {
            System.err.println(ex);
        }
        return result;
    }

    @Override
    public boolean deleteCartItem(String itemId) {
        boolean deleted = false;
        // if cart isn't empty 
        if (!cart.isEmpty()) {
            // look for a match
            for (CartItem item : cart) {
                // if match found, delete item
                if (item.getItemId().equals(itemId)) {
                    cart.remove(item);
                     // update stock levels
                    int stockQuantity = product.getQuantity();
                    product.setQuantity(stockQuantity + item.getQuantity());
                    deleted = true;
                }
            }
        }
        return deleted;
    }

    @Override
    public boolean updateCartItem(CartItem cartItem) {
        boolean updated = false;
        // check cart isnt empty
        if (!cart.isEmpty()) {
            // look for matching item
            for (CartItem item : cart) {
                // if match exists
                if (item.getItemId().equals(cartItem.getItemId())) {
                    // perform update
                    item = cartItem;
                    cart.add(item);
                    updated = true;
                }
            }
        }
        return updated;
    }
}
