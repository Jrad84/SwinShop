/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import entity.MyUserDTO;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import session.MyUserFacadeRemote;

/**
 *
 * @author User
 */
@Named(value = "userManagedBean")
@RequestScoped
public class MyUserManagedBean {

    @EJB
    private MyUserFacadeRemote userFacade;
    @Inject
    private Conversation conversation;

    private String newPassword;
    private String userid;
    private String name;
    private String phone;
    private String address;
    private String email;
    private String username;
    private String password;
    private String appGroup;
    private String confirmPassword;
    private String pin; 
    private String recoveryPin; 

    public MyUserManagedBean() {
        this.userid = null;
        this.name = null;
        this.phone = null;
        this.address = null;
        this.email = null;
        this.username = null;
        this.password = null;
        this.appGroup = null;
        this.confirmPassword = null;
        this.pin = null;
        this.recoveryPin = null;
        this.newPassword = null;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @PostConstruct
    public void init() {
        userid = FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getRecoveryPin() {
        return recoveryPin;
    }

    public void setRecoveryPin(String recoveryPin) {
        this.recoveryPin = recoveryPin;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    /**
     * Creates a new instance of UserManagedBean
     * @return 
     */
    public String getUserId(){
        return userid;
    }

    public void setUserId(String userid) {
        this.userid = userid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAppGroup() {
        return appGroup;
    }

    public void setAppGroup(String appGroup) {
        this.appGroup = appGroup;
    }


    public String addUser() {

        // check empId is null
        if (isNull(userid)) {
            return "debug";
        }

        // all information seems to be valid
        // try add the employee
        appGroup = "ED-APP-USERS";
        MyUserDTO userDTO = new MyUserDTO(userid, name, phone,
                address, email, username, password, appGroup);
        boolean result = userFacade.createRecord(userDTO);
        
        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }
    
    public String addAdmin() {

        // check empId is null
        if (isNull(userid)) {
            return "debug";
        }

        // all information seems to be valid
        // try add the employee
        appGroup = "ED-APP-ADMIN";
        MyUserDTO userDTO = new MyUserDTO(userid, name, phone,
                address, email,username, password, appGroup);
        boolean result = userFacade.createRecord(userDTO);
        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }

    public String setDetailsForChange() {
        // check empId is null
        if (isNull(userid) || conversation == null) {
            return "debug";
        }

        if (!userFacade.checkUser(userid)) {
            return "failure";
        }

        // get employee details
        return setDetails();
    }

    public String editDetails() {
        // check empId is null
        if (isNull(userid)) {
            return "debug";
        }

        MyUserDTO userDTO = new MyUserDTO(userid, name, phone,
                address, email, username, password, appGroup);
        boolean result = userFacade.updateRecord(userDTO);

        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }

    public void validateNewPassword(FacesContext context,
            UIComponent componentToValidate, Object value)
            throws ValidatorException {

        // get new password
        String oldPwd = (String) value;

        // get old password
        UIInput newPwdComponent = (UIInput) componentToValidate.getAttributes().get("newpwd");
        String newPwd = (String) newPwdComponent.getSubmittedValue();

        if (oldPwd.equals(newPwd)) {
            FacesMessage message = new FacesMessage(
                    "Old Password and New Password are the same! They must be different.");
            throw new ValidatorException(message);
        }

    }

    public void validatePasswordPair(FacesContext context,
            UIComponent componentToValidate,
            Object pwdValue) throws ValidatorException {

        // get password
        String pwd = (String) pwdValue;

        // get confirm password
        UIInput cnfPwdComponent = (UIInput) componentToValidate.getAttributes().get("cnfpwd");
        String cnfPwd = (String) cnfPwdComponent.getSubmittedValue();

        System.out.println("password : " + pwd);
        System.out.println("confirm password : " + cnfPwd);

        if (!pwd.equals(cnfPwd)) {
            FacesMessage message = new FacesMessage(
                    "Password and Confirm Password are not the same! They must be the same.");
            throw new ValidatorException(message);
        }
    }

    public void validateNewPasswordPair(FacesContext context,
            UIComponent componentToValidate,
            Object newValue) throws ValidatorException {

        // get new password
        String newPwd = (String) newValue;

        // get confirm password
        UIInput cnfPwdComponent = (UIInput) componentToValidate.getAttributes().get("cnfpwd");
        String cnfPwd = (String) cnfPwdComponent.getSubmittedValue();

        System.out.println("new password : " + newPwd);
        System.out.println("confirm password : " + cnfPwd);

        if (!newPwd.equals(cnfPwd)) {
            FacesMessage message = new FacesMessage(
                    "New Password and Confirm New Password are not the same! They must be the same.");
            throw new ValidatorException(message);
        }
    }

    public String editPassword() {
        // check empId is null
        if (isNull(userid)) {
            return "debug";
        }

        // newPassword and confirmPassword are the same - checked by the validator during input to JSF form
        boolean result = userFacade.updatePassword(userid, newPassword);

        System.out.println("result = " + result);

        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }

    public String deleteUser() {
        // check empId is null
        if (isNull(userid)) {
            return "debug";
        }
        boolean result = userFacade.deleteRecord(userid);
        if (result) {
            return "success";
        } else {
            return "failure";
        }
    }

    public String displayDetails() {
        // check empId is null
        if (isNull(userid)) {
            return "debug";
        }

        return setDetails();
    }

    private boolean isNull(String s) {
        return (s == null);
    }

    private String setDetails() {

        if (isNull(userid)) {
            return "debug";
        }

        MyUserDTO user = userFacade.getRecord(userid);

        if (user == null) {
            // no such employee
            return "failure";
        } else {
            // found - set details for display
            this.userid = user.getUserid();
            this.name = user.getName();
            this.phone = user.getPhone();
            this.address = user.getAddress();
            this.email = user.getEmail();
            this.username = user.getUsername();
            this.password = user.getPassword();
            this.appGroup = user.getAppGroup();
            return "success";
        }
    }

    public void retrieveDetail(String userID) {

        MyUserDTO user = userFacade.getRecord(userID);

        this.userid = user.getUserid();
        this.name = user.getName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.appGroup = user.getAppGroup();

    }

  public String checkUserEmail() { 
      // check if id is null 
      if (isNull(userid)) { 
          return "debug"; 
      } 
      MyUserDTO usrDTO = userFacade.getRecord(userid);
      this.name = usrDTO.getName(); 
      // do emails match?
      if (email.equals(usrDTO.getEmail())) { 
          sendEmail(); 
          return "success"; 
      } else { 
          return "failure"; 
      } 
    } 
    
    public void sendEmail(){
       
      String to = email;
      String from = "jaredtaback@gmail.com";
      final String username = from; //
      final String password = "N0v0selic"; //

     String smtpServer = "smtp.gmail.com";
     
      //generate a 4 digit integer
      int randomCode = (int)(Math.random()*9000)+1000;
      recoveryPin = String.valueOf(randomCode);
      
      this.setRecoveryPin(recoveryPin);

      Properties props = new Properties();
      props.put("mail.smtp.auth", "true");
      props.put("mail.smtp.starttls.enable", "true");
      props.put("mail.smtp.host", smtpServer);
      props.put("mail.smtp.port", "587");

      // Get the Session object.
      Session session = Session.getInstance(props,
         new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
               return new PasswordAuthentication(username, password);
	   }
         });

      try {
	   // Create a default MimeMessage object.
	   Message message = new MimeMessage(session);
	
	   // Set From: header field of the header.
	   message.setFrom(new InternetAddress(from));
	
	   // Set To: header field of the header.
	   message.setRecipients(Message.RecipientType.TO,
               InternetAddress.parse(to));
	
	   // Set Subject: header field
	   message.setSubject("Password reset");
	
	   // Now set the actual message
	   message.setText("Hello, your password reset code is \n" + recoveryPin
                   + "\n" );
		
	   // Send message
	   Transport.send(message);
	   System.out.println("Sent message successfully....");
      } catch (MessagingException e) {
         throw new RuntimeException(e);
      }
    }
    
       public String checkRecoveryPin() 
    { 
        if (pin.equals(recoveryPin)) 
        { 
            return "success"; 
        } 
        else {
            return "failure"; 
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

    