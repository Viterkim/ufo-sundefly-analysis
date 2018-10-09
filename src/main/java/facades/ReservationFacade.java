
package facades;

import entity.Passenger;
import entity.Reservation;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;


public class ReservationFacade {
    private EntityManagerFactory emf;
    private EntityManager em;

    public ReservationFacade() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("pu");
            em = emf.createEntityManager();
        }
    }
    
    public void persistReservation(Reservation r){
        em.getTransaction().begin();
        em.persist(r);
        em.getTransaction().commit();
    }
    
    public void persistPassenger(Passenger p){
        em.getTransaction().begin();
        em.persist(p);
        em.getTransaction().commit();
    }
    
    public void mergeReservation(Reservation r){
        em.getTransaction().begin();
        em.merge(r);
        em.getTransaction().commit();
    }
    
    public void mergePassenger(Passenger p){
        em.getTransaction().begin();
        em.merge(p);
        em.getTransaction().commit();
    }
    
}
