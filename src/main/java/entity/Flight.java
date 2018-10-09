/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.util.List;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author William Pfaffe
 */
@Entity
@Table(name = "Flight")
@XmlRootElement
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="flightNumber")
@NamedQueries({
    @NamedQuery(name = "Flight.findAll", query = "SELECT f FROM Flight f")})
public class Flight implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "flightNumber")
    private Integer flightNumber;
    @Column(name = "seats")
    private Integer seats;
    @OneToMany(mappedBy = "fkflightNumber")
    private List<FlightInstance> flightInstanceList;
    @JoinColumn(name = "fk_AirlineId", referencedColumnName = "id")
    @ManyToOne
    private Airline fkAirlineId;
    @JoinColumn(name = "fk_currentAirport", referencedColumnName = "IATACode")
    @ManyToOne
    private Airport fkcurrentAirport;

    public Flight() {
    }

    public Flight(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(Integer flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Integer getSeats() {
        return seats;
    }

    public void setSeats(Integer seats) {
        this.seats = seats;
    }

    @XmlTransient
    public List<FlightInstance> getFlightInstanceList() {
        return flightInstanceList;
    }

    public void setFlightInstanceList(List<FlightInstance> flightInstanceList) {
        this.flightInstanceList = flightInstanceList;
    }

    public Airline getFkAirlineId() {
        return fkAirlineId;
    }

    public void setFkAirlineId(Airline fkAirlineId) {
        this.fkAirlineId = fkAirlineId;
    }

    public Airport getFkcurrentAirport() {
        return fkcurrentAirport;
    }

    public void setFkcurrentAirport(Airport fkcurrentAirport) {
        this.fkcurrentAirport = fkcurrentAirport;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (flightNumber != null ? flightNumber.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Flight)) {
            return false;
        }
        Flight other = (Flight) object;
        if ((this.flightNumber == null && other.flightNumber != null) || (this.flightNumber != null && !this.flightNumber.equals(other.flightNumber))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Flight[ flightNumber=" + flightNumber + " ]";
    }
    
}
