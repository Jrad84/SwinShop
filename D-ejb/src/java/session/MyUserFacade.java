/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package session;

import entity.MyUser;
import entity.MyUserDTO;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author User
 */
@Stateless
public class MyUserFacade implements MyUserFacadeRemote {

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    
    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    @PersistenceContext(unitName = "D-ejbPU")
    private EntityManager em;

     protected EntityManager getEntityManager() {
        return em;
    }

    private void create(MyUser user) {
        em.persist(user);
    }

    private void edit(MyUser user) {
        em.merge(user);
    }

    private void remove(MyUser user) {
        em.remove(em.merge(user));
    }

    private MyUser find(Object id) {
        return em.find(MyUser.class, id);
    }

    private MyUser myDTO2DAO(MyUserDTO userDTO) {
        MyUser user = new MyUser();
        user.setUserid(userDTO.getUserid());
        user.setName(userDTO.getName());
        user.setPhone(userDTO.getPhone());
        user.setAddress(userDTO.getAddress());
        user.setEmail(userDTO.getEmail());
        user.setUsername(userDTO.getUsername());
        user.setPassword(userDTO.getPassword());
        user.setAppgroup(userDTO.getAppGroup());
        return user;
    }

    @Override
    public boolean checkUser(String userid) {
        if (find(userid) != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean createRecord(MyUserDTO userDTO) {
        if (find(userDTO.getUserid()) != null) {
            // user whose userid can be found
            return false;
        }
        // user whose userid could not be found
        try {
            MyUser user = this.myDTO2DAO(userDTO);
            this.create(user); // add to database
            return true;
        } catch (Exception ex) {
            return false; // something is wrong, should not be here though
        }
    }

    private MyUserDTO myDAO2DTO(MyUser user) {
        String id = user.getUserid();
        String name = user.getName();
        String phone = user.getPhone();
        String address = user.getAddress();
        String email = user.getEmail();
        String username = user.getUsername();
        String password = user.getPassword();
        String appgroup = user.getAppgroup();

        MyUserDTO userDTO = new MyUserDTO(id, name, phone,  address,email, username,
                password, appgroup);
        return userDTO;
    }
     
      
    @Override
    public MyUserDTO getRecord(String userID) {
        MyUser user = new MyUser();
        user = find(userID);
        if (user != null) {
            // can find a customer with the same custid

            MyUserDTO userDTO = myDAO2DTO(user);
            // System.out.println(id + userName + userPass + userEmail + userPhone + userAdd + qn + ans);
            return userDTO;
        } else {
            System.out.println("no user found");
            return null;
        }
    }

    @Override
    public boolean updateRecord(MyUserDTO userDTO) {
        boolean result = false;

        MyUser user = em.find(MyUser.class, userDTO.getUserid());

        if (user == null) {
            result = false;
        } else {
            try {
//                user = this.myDTO2DAO(userDTO);
                em.merge(user);
                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    @Override
    public boolean updatePassword(String id, String password) {
        // find the employee
        MyUser user = find(id);

        // check again - just to play it safe
        if (user == null) {
            return false;
        }

        // only need to update the password field
        user.setPassword(password);
        return true;
    }

    @Override
    public boolean deleteRecord(String userID) {
        boolean result = false;

        MyUser user = em.find(MyUser.class, userID);

        if (user == null) {
            // cannot find a customer - cannot delete
            result = false;
        } else {
            // can now remove the customer found
            try {
                em.remove(user);

                result = true;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
}
