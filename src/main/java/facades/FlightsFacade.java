package facades;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import entity.*;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.ws.rs.NotFoundException;

public class FlightsFacade {

    private EntityManagerFactory emf;
    private EntityManager em;

    public FlightsFacade() {
        if (emf == null) {
            emf = Persistence.createEntityManagerFactory("pu");
            em = emf.createEntityManager();
        }
    }

    //	2017-05-01 Date format
    private Airport getAirportFromCity(String airportCity) {
        Query q = em.createQuery("SELECT ai FROM Airport ai WHERE ai.city = :airportCity");
        q.setParameter("airportCity", airportCity);
        Airport ai = (Airport) q.getSingleResult();
        if (ai == null) {
            throw new NotFoundException("Dit numsehul");
        }
        return ai;
    }
    
    public FlightInstance getFlightInstanceFromId(int id){
        Query q = em.createQuery("SELECT fi FROM FlightInstance fi WHERE fi.flightInstanceId = :id");
        q.setParameter("id", id);
        FlightInstance fi = (FlightInstance) q.getSingleResult();
        return fi;
    }

    public void mergeFlightInstance(FlightInstance fi){
        em.getTransaction().begin();
        em.merge(fi);
        em.getTransaction().commit();
    }

    public void addFlightInstance(FlightInstance fi){
        em.getTransaction().begin();
        em.persist(fi);
        em.getTransaction().commit();
    }

    public List<FlightInstance> getFlightsFromDateTickets(String from, String date, int tickets) {
        //SELECT FLIGHT INSTANCE FROM DATE AND FROM
        Query q2 = em.createQuery("SELECT fi FROM FlightInstance fi WHERE fi.departureDate = :date AND fi.availableSeats >= :tickets AND fi.fkstartingAirport.iATACode = :from");
        q2.setParameter("date", date);
        q2.setParameter("tickets", tickets);
        q2.setParameter("from", from);

        return (List<FlightInstance>) q2.getResultList();

    }
    
     public List<FlightInstance> getFlightsFromToDateTickets(String from, String to, String date, int tickets) {
        //SELECT FLIGHT INSTANCE FROM DATE AND FROM
        Query q2 = em.createQuery("SELECT fi FROM FlightInstance fi WHERE fi.departureDate = :date AND fi.availableSeats >= :tickets AND fi.fkstartingAirport.iATACode = :from AND fi.fkdestinationAirport.iATACode = :to");
        q2.setParameter("date", date);
        q2.setParameter("tickets", tickets);
        q2.setParameter("from", from);
        q2.setParameter("to", to);

        return (List<FlightInstance>) q2.getResultList();

    }

    public List<FlightInstance> getEverything() {
        Query q = em.createQuery("SELECT fi FROM FlightInstance fi");
        List<FlightInstance> fi = q.getResultList();
        return fi;
    }
    
    public List<Flight> getAllFlights() {
        Query q = em.createQuery("SELECT f FROM Flight f where f.flightNumber >= 0");
        List<Flight> f = q.getResultList();
        System.out.println("[FlightsFacade.getAllFlights]: " + f.size());
        return f;
    }
    
    public List<Flight> getFlightsFromAirportName(String airportName){
        Query q = em.createQuery("SELECT fl FROM Flight fl WHERE fl.fkAirlineId.name = :airportName");
        q.setParameter("airportName", airportName);
        List<Flight> fi = q.getResultList();
        return fi;
    }

    public Flight getFlightFromID(int id){
        Query q = em.createQuery("SELECT fl FROM Flight fl WHERE fl.flightNumber = :id");
        q.setParameter("id", id);
        return (Flight) q.getSingleResult();
    }

    public List<FlightInstance> getEverything(int pageCount, int pageNumber) {
        Query q = em.createQuery("SELECT fi FROM FlightInstance fi");
        q.setMaxResults(pageCount);
        q.setFirstResult(pageCount * (pageNumber-1));
        System.out.println("...This is pageCount from getting all flights: " + pageCount);
        System.out.println("...This is pageNumber from getting all flights: " + pageNumber);
        List<FlightInstance> fi = q.getResultList();
        return fi;
    }
    
    public List<Reservation> getReservationsFromFlightInstanceID(int id){
        Query q = em.createQuery("SELECT re FROM Reservation re where re.fkflightInstanceId.flightInstanceId = :id");
        q.setParameter("id", id);
        return q.getResultList();
    }
    
    public List<Airport> getAirports(int limit) {
        Query q = em.createQuery("SELECT a FROM Airport a ORDER BY a.iATACode").setMaxResults(limit);
        List<Airport> a = q.getResultList().subList(0, limit);
        return a;
    }
    
    public List<Airport> getAirports(String search, int limit) {
        Query q = em.createQuery("SELECT a FROM Airport a WHERE a.name LIKE :search OR a.iATACode LIKE :search OR a.country LIKE :search ORDER BY a.iATACode").setMaxResults(limit);
        q.setParameter("search", "%" + search + "%");
        List<Airport> a = q.getResultList();
        return a;
    }
    
    public Airport getAirport(String IATA) {
        Query q = em.createQuery("SELECT a FROM Airport a WHERE a.iATACode = :IATA");
        q.setParameter("IATA", IATA);
        return (Airport) q.getSingleResult();
    }
    
    public boolean removeFlightInstance(int id){
        em.getTransaction().begin();
        FlightInstance fi = this.getFlightInstanceFromId(id);
        List<Reservation> re = this.getReservationFromFlightInstanceID(id);
        
        for(Reservation r : re){
            em.remove(r);
        }
        
        em.remove(fi);
        em.getTransaction().commit();
        try{
            this.getFlightInstanceFromId(id);
        }catch(Exception e){
            return true;
        }
        return false;
    }
    
    public List<Reservation> getReservationFromFlightInstanceID(int id){
        Query q = em.createQuery("SELECT re FROM Reservation re WHERE re.fkflightInstanceId.flightInstanceId = :id");
        q.setParameter("id", id);
        return (List) q.getResultList();
    }
    
}

// Passenger -> Reservations -> FlightInstance