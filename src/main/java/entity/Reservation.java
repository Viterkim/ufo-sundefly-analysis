/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@Entity
@Table(name = "Reservation")
@XmlRootElement
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="reservationId")
@NamedQueries({
    @NamedQuery(name = "Reservation.findAll", query = "SELECT r FROM Reservation r")})
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "reservationId")
    private Integer reservationId;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "totalPrice")
    private Integer totalPrice;
    @Size(max = 255)
    @Column(name = "reservEmail")
    private String reservEmail;
    @Size(max = 255)
    @Column(name = "reservPhone")
    private String reservPhone;
    @Size(max = 255)
    @Column(name = "reservName")
    private String reservName;
    @JoinColumn(name = "fk_flightInstanceId", referencedColumnName = "flightInstanceId")
    @ManyToOne
    private FlightInstance fkflightInstanceId;
    @OneToMany(mappedBy = "fkreservationId", cascade = CascadeType.ALL)
    private List<Passenger> passengerList;

    public Reservation() {
    }

    public Reservation(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getReservationId() {
        return reservationId;
    }

    public void setReservationId(Integer reservationId) {
        this.reservationId = reservationId;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getReservEmail() {
        return reservEmail;
    }

    public void setReservEmail(String reservEmail) {
        this.reservEmail = reservEmail;
    }

    public String getReservPhone() {
        return reservPhone;
    }

    public void setReservPhone(String reservPhone) {
        this.reservPhone = reservPhone;
    }

    public String getReservName() {
        return reservName;
    }

    public void setReservName(String reservName) {
        this.reservName = reservName;
    }

    public FlightInstance getFkflightInstanceId() {
        return fkflightInstanceId;
    }

    public void setFkflightInstanceId(FlightInstance fkflightInstanceId) {
        this.fkflightInstanceId = fkflightInstanceId;
    }

    @XmlTransient
    public List<Passenger> getPassengerList() {
        return passengerList;
    }

    public void setPassengerList(List<Passenger> passengerList) {
        this.passengerList = passengerList;
    }
    
}
