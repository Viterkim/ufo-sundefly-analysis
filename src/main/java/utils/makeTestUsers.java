package utils;

import entity.Role;
import entity.User;
import facades.UserFacade;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class makeTestUsers {

  //Only for initial testing REMOVE BEFORE PRODUCTION
  //Run this file to setup the users required to use the initial version of the seed
  public static void main(String[] args) {
    EntityManager em = Persistence.createEntityManagerFactory("pu").createEntityManager();
    try {
      System.out.println("Creating TEST Users");
      if (em.find(User.class, "user") == null) {
        em.getTransaction().begin();
        Role adminRole = new Role("Admin");
        User admin = new User("admin", "password123");
        admin.addRole(adminRole);
        em.persist(adminRole);
        em.persist(admin);
        em.getTransaction().commit();
        System.out.println("Created TEST Users");
      }
    } catch (Exception ex) {
      Logger.getLogger(UserFacade.class.getName()).log(Level.SEVERE, null, ex);
      em.getTransaction().rollback();
    } finally {
      em.close();
    }
  }
}