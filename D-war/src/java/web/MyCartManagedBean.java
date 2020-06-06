/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entity.CartItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import session.ShopCartBeanRemote;

/**
 *
 * @author User
 */
@Named(value = "cartManagedBean")
@SessionScoped
public class MyCartManagedBean implements Serializable {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @EJB
    private ShopCartBeanRemote cart;
    private String userId;
    private String userName;
    private String userEmail;
    private String itemId;
    private String productName;
    private double unitPrice;
    private int quantity;
    private int numberOfItem;
    private double total;
    private ArrayList<CartItem> itemList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public int getNumberOfItem() {
        return numberOfItem;
    }

    public void setNumberOfItem(int numberOfItem) {
        this.numberOfItem = numberOfItem;
    }

    public ArrayList<CartItem> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<CartItem> itemList) {
        this.itemList = itemList;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private double subTotal;

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public MyCartManagedBean() {
        this.cart = null;
        this.itemId = null;
        this.productName = null;
        this.unitPrice = 0.0;
        this.quantity = 0;
        this.total = 0.0;
    }

    public ShopCartBeanRemote getCart() {
        return cart;
    }

    public void setCart(ShopCartBeanRemote cart) {
        this.cart = cart;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String addItemtoCart() {
        // all information seems to be valid
        // try add the employee
        if (quantity == 0) {
            return "failure";
        } else {
            CartItem item = new CartItem(itemId, productName, unitPrice, quantity);
            boolean result = cart.addCartItem(item);
            if (result) {
                return "success";
            } else {
                return "failure";
            }
        }
    }
    

    public String removeItem() {
        if (cart.deleteCartItem(itemId)) {
            return "success";
        } else {
            return "failure";
        }
    }

    public void displayCart() {
        itemList = cart.getCart();
        numberOfItem = itemList.size();
        if (itemList.isEmpty()) {
            System.out.println("The shopping cart is empty!");
            return;
        }
        System.out.println("Your shopping cart has the following items:");
        total = 0.0;
        for (CartItem item : itemList) {
            unitPrice = item.getUnitPrice();
            quantity = item.getQuantity();
            subTotal = (quantity* unitPrice);
            System.out.println("Item: " + item.getProductName()
                    + "\tUnit Price: " + item.getUnitPrice()
                    + "\tQuantity: " + item.getQuantity()
                    + "\tSub-Total: " + subTotal);
            total = total + subTotal;
        }
        System.out.println("---");
        System.out.println("Total price: " + total);
        System.out.println("----End of Shopping Cart---");
    }

    public String displayItem() {
        if (productName == null) {
            return "failure";
        } else {
            return "success";
        }
    }
    
    public String confirm() {
        itemList = cart.getCart();
        if (userName == null || itemList.size() < 1) {
            return "failure";
        } else {
            sendEmailToUser();
            return "success";
        }
    }


    public void sendEmailToUser() {
        itemList = cart.getCart();
        String smtpServer = "smtp.gmail.com";
        String from = "jaredtaback@gmail.com";
        String to = this.userEmail;
        String subject = "Your Swin Shop purchase";
        
        String pass = "N0v0selic";
        String emailUser = from;
        // Generate recovery code for changing password
        total = 0.0;
        String body = "Hi " + userName + " ,\nTHere are your order details: ";
        for (CartItem item : itemList) {
            unitPrice = item.getUnitPrice();
            quantity = item.getQuantity();
            subTotal = quantity * unitPrice;
            body += "\nItem: " + item.getProductName()
                    + "\tUnit Price: " + item.getUnitPrice()
                    + "\tQuantity: " + item.getQuantity()
                    + "\tSub-Total: " + subTotal;
            total = total + subTotal;
        }
        body += "\n---";
        body += "\nTotal price: " + total;
        body += "\nRegards,\nSwin Shop online store\n";
        try {
                Properties props = new Properties();
                props.put("mail.smtp.auth", "true");
                props.put("mail.smtp.starttls.enable", "true");
                props.put("mail.smtp.host", smtpServer);
                props.put("mail.smtp.port", "587");
            // --prepare a password authenticator --
            MyAuthenticator myPA = new MyAuthenticator(emailUser, pass);
            // see MyAuthenticator class
            // get a session
            Session session = Session.getInstance(props);
            // --Create a new message --
            Message msg = new MimeMessage(session);
            // --Set the FROM and TO fields --
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
            // --Set the subject and body text --
            msg.setSubject(subject);
            msg.setText(body);
           
            msg.setSentDate(new Date());
            // --Send the message --
            Transport.send(msg);
            Transport.send(msg, emailUser, pass);
            System.out.println("Message sent OK.");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public class MyAuthenticator extends Authenticator {

        PasswordAuthentication mypa;

        public MyAuthenticator(String username, String password) {
            mypa = new PasswordAuthentication(username, password);
        }

        @Override
        public PasswordAuthentication getPasswordAuthentication() {
            return mypa;
        }
    }
}
