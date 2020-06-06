/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.Product;
import entity.ProductDTO;
import java.util.ArrayList;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author User
 */
@Stateless
public class ProductFacade implements ProductFacadeRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
      @PersistenceContext(unitName = "D-ejbPU")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return em;
    }

    private void create(Product product) {
       
        em.persist(product);
    }

    private void edit(Product product) {
        em.merge(product);
    }

    private void remove(Product product) {
        em.remove(em.merge(product));
    }

    private Product find(Object id) {
        return em.find(Product.class, id);
    }

    @Override
    public ArrayList<ProductDTO> productList() {
        ArrayList<Product> productList = (ArrayList<Product>) em.createNamedQuery("Product.findAll").getResultList();
        ArrayList<ProductDTO> productListDTO = new ArrayList<>();

        productList.stream().map((product) -> myDAO2DTO(product)).forEachOrdered((productDTO) -> {
            productListDTO.add(productDTO);
        });
        return productListDTO;
    }

    private Product myDTO2DAO(ProductDTO productDTO) {
        Product product = new Product();
        product.setProductid(productDTO.getProductid());
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setImage(productDTO.getImage());
        product.setGender(productDTO.getGender());
        product.setSize(productDTO.getSize());
        
        return product;
    }

    @Override
    public boolean checkProduct(String productid) {
        if (find(productid) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean createRecord(ProductDTO productDTO) {
        if (find(productDTO.getProductid()) != null) {
// user whose userid can be found
            return false;
        }
// user whose userid could not be found
        try {
            Product product = this.myDTO2DAO(productDTO);
            this.create(product); // add to database
            return true;
        } catch (Exception ex) {
            return false; // something is wrong, should not be here though
        }
    }

    private ProductDTO myDAO2DTO(Product product) {
        String id = product.getProductid();
        String name = product.getName();
        String description = product.getDescription();
        Double price = product.getPrice();
        Integer quantity = product.getQuantity();
        String imgfile = product.getImage();
        String gender = product.getGender();
        Integer size = product.getSize();
       
        ProductDTO productDTO = new ProductDTO(id, name, description, price, quantity,
                imgfile, gender, size);
        return productDTO;
    }

    @Override
    public ProductDTO getRecord(String id) {
        Product product = new Product();
        product = find(id);
        if (product != null) {
            // can find a customer with the same custid

            ProductDTO productDTO = myDAO2DTO(product);
            // System.out.println(id + userName + userPass + userEmail + userPhone + userAdd + qn + ans);
            return productDTO;
        } else {
            System.out.println("no product found");
            return null;
        }
    }

    @Override
    public boolean updateRecord(ProductDTO productDTO) {
        boolean result = false;

        Product product = em.find(Product.class, productDTO.getProductid());

        if (product == null) {
            result = false;
        } else {
            try {
                product = this.myDTO2DAO(productDTO);
                em.merge(product);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean deleteRecord(String id) {
        boolean result = false;

        Product product = em.find(Product.class, id);

        if (product == null) {
            // cannot find a customer - cannot delete
            result = false;
        } else {
            // can now remove the customer found
            try {
                em.remove(product);

                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
