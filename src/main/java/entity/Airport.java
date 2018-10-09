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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author William Pfaffe
 */
@Entity
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="IATACode")
@Table(name = "Airport")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Airport.findAll", query = "SELECT a FROM Airport a")})
public class Airport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "IATACode")
    private String iATACode;
    @Size(max = 255)
    @Column(name = "timeZone")
    private String timeZone;
    @Size(max = 255)
    @Column(name = "name")
    private String name;
    @Size(max = 255)
    @Column(name = "country")
    private String country;
    @Size(max = 255)
    @Column(name = "city")
    private String city;
    @OneToMany(mappedBy = "fkstartingAirport")
    private List<FlightInstance> flightInstanceList;
    @OneToMany(mappedBy = "fkdestinationAirport")
    private List<FlightInstance> flightInstanceList1;
    @OneToMany(mappedBy = "fkcurrentAirport")
    private List<Flight> flightList;

    public Airport() {
    }

    public Airport(String iATACode) {
        this.iATACode = iATACode;
    }

    public String getIATACode() {
        return iATACode;
    }

    public void setIATACode(String iATACode) {
        this.iATACode = iATACode;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlTransient
    public List<FlightInstance> getFlightInstanceList() {
        return flightInstanceList;
    }

    public void setFlightInstanceList(List<FlightInstance> flightInstanceList) {
        this.flightInstanceList = flightInstanceList;
    }

    @XmlTransient
    public List<FlightInstance> getFlightInstanceList1() {
        return flightInstanceList1;
    }

    public void setFlightInstanceList1(List<FlightInstance> flightInstanceList1) {
        this.flightInstanceList1 = flightInstanceList1;
    }

    @XmlTransient
    public List<Flight> getFlightList() {
        return flightList;
    }

    public void setFlightList(List<Flight> flightList) {
        this.flightList = flightList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (iATACode != null ? iATACode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Airport)) {
            return false;
        }
        Airport other = (Airport) object;
        if ((this.iATACode == null && other.iATACode != null) || (this.iATACode != null && !this.iATACode.equals(other.iATACode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Airport[ iATACode=" + iATACode + " ]";
    }
    
}
