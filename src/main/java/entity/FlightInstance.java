/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author vikto
 */
@Entity
@Table(name = "FlightInstance")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "FlightInstance.findAll", query = "SELECT f FROM FlightInstance f")})
public class FlightInstance implements Serializable {

    @Size(max = 255)
    @Column(name = "departureDate")
    private String departureDate;
    @Size(max = 255)
    @Column(name = "departureTime")
    private String departureTime;
    @Size(max = 255)
    @Column(name = "arrivalTime")
    private String arrivalTime;
    @Size(max = 255)
    @Column(name = "arrivalDate")
    private String arrivalDate;

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "flightInstanceId")
    private Integer flightInstanceId;
    @Size(max = 255)
    @Column(name = "travelTime")
    private String travelTime;
    @Column(name = "availableSeats")
    private Integer availableSeats;
    @Column(name = "price")
    private Integer price;
    @JoinColumn(name = "fk_startingAirport", referencedColumnName = "IATACode")
    @ManyToOne
    private Airport fkstartingAirport;
    @JoinColumn(name = "fk_destinationAirport", referencedColumnName = "IATACode")
    @ManyToOne
    private Airport fkdestinationAirport;
    @JoinColumn(name = "fk_flightNumber", referencedColumnName = "flightNumber")
    @ManyToOne
    private Flight fkflightNumber;

    public FlightInstance() {
    }

    public FlightInstance(Integer flightInstanceId) {
        this.flightInstanceId = flightInstanceId;
    }

    public Integer getFlightInstanceId() {
        return flightInstanceId;
    }

    public void setFlightInstanceId(Integer flightInstanceId) {
        this.flightInstanceId = flightInstanceId;
    }

    public String getTravelTime() {
        return travelTime;
    }

    public void setTravelTime(String travelTime) {
        this.travelTime = travelTime;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Airport getFkstartingAirport() {
        return fkstartingAirport;
    }

    public void setFkstartingAirport(Airport fkstartingAirport) {
        this.fkstartingAirport = fkstartingAirport;
    }

    public Airport getFkdestinationAirport() {
        return fkdestinationAirport;
    }

    public void setFkdestinationAirport(Airport fkdestinationAirport) {
        this.fkdestinationAirport = fkdestinationAirport;
    }

    public Flight getFkflightNumber() {
        return fkflightNumber;
    }

    public void setFkflightNumber(Flight fkflightNumber) {
        this.fkflightNumber = fkflightNumber;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightInstanceId != null ? flightInstanceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof FlightInstance)) {
            return false;
        }
        FlightInstance other = (FlightInstance) object;
        if ((this.flightInstanceId == null && other.flightInstanceId != null) || (this.flightInstanceId != null && !this.flightInstanceId.equals(other.flightInstanceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.FlightInstance[ flightInstanceId=" + flightInstanceId + " ]";
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }
    
}
