/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entity.ProductDTO;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import session.ProductFacadeRemote;

/**
 *
 * @author User
 */
@Named(value = "myProductManagedBean")
@ConversationScoped
public class MyProductManagedBean implements Serializable {

    @Inject
    private Conversation conversation;
    @EJB
    private ProductFacadeRemote productFacade;

    private String productid;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    private String image;
    private String gender;
    private Integer shoe_size;
    private List<ProductDTO> productList = new ArrayList();
    /**
     * Creates a new instance of MyEmpManagedBean
     */
    public MyProductManagedBean() {
       productid = null;
       name = null;
       description = null;
       price = null;
       quantity = null;
       image = null;
       gender = null;
       shoe_size = null;
       productList = null;
    }

    public String getProductId() {
        return productid;
    }

    public void setProductId(String productid) {
        this.productid = productid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }
    

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getSize() {
        return shoe_size;
    }

    public void setSize(Integer size) {
        this.shoe_size = size;
    }
    
    public List<ProductDTO> getProductList() {
        return productList;
    }

    public void setProductList(List<ProductDTO> productList) {
        this.productList = productList;
    }

    public void startConversation() {
        conversation.begin();
    }

    public void endConversation() {
        conversation.end();
    }
    
    public String checkProduct(){
        if (productFacade.checkProduct(productid)){
            return "true";
        }
        else{
            return "false";
        }
    }

    public String addProduct() {

        // check empId is null
        if (isNull(productid)) {
            return "debug";
        }

        // all information seems to be valid
        // try add the employee
        ProductDTO productDTO = new ProductDTO(productid, name, description,
                price, quantity, image, gender, shoe_size);
        boolean result = productFacade.createRecord(productDTO);
        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }

    public String changeProduct() {
        // check empId is null
        if (isNull(productid)) {
            return "debug";
        }

        ProductDTO productDTO = new ProductDTO(productid, name, description,
                price, quantity, image, gender, shoe_size);
        boolean result = productFacade.updateRecord(productDTO);

        // note the endConversation of the conversation
        endConversation();

        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }

   
    public String deleteProduct() {
        // check empId is null
        if (isNull(productid)) {
            return "debug";
        }

        boolean result = productFacade.deleteRecord(productid);

        if (result) {
            return "success";
        } else {
            return "failure";
        }

    }

    public String displayProduct() {
        // check empId is null
        if (isNull(productid) || conversation == null) {
            return "debug";
        }

        return setProductDetails();
    }

    private boolean isNull(String s) {
        return (s == null);
    }

    private String setProductDetails() {

        if (isNull(productid) || conversation == null) {
            return "debug";
        }

        ProductDTO p = productFacade.getRecord(productid);

        if (p == null) {
            // no such employee
            return "failure";
        } else {
            // found - set details for display
            this.productid = p.getProductid();
            this.name = p.getName();
            this.description = p.getDescription();
            this.price = p.getPrice();
            this.quantity = p.getQuantity();
            this.image = p.getImage();
            this.gender = p.getGender();
            this.shoe_size = p.getSize();
           
            return "success";
        }
    }

    private boolean validAddProductInfo() {
        return (productid != null && name != null && description != null
                && price != null
                && quantity != null && image !=null && gender !=null 
                && shoe_size != null);
    }
    
}
