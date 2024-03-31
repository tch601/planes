package BsmchFlights.Models;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "t_flights")
public class Flight {
    public List<Reservation> getPassengers() {
        return new ArrayList<>(passengers);
    }

    @OneToMany(targetEntity = Reservation.class, mappedBy = "flightId")
    private List<Reservation> passengers = new ArrayList<Reservation>();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "takeofftime")
    private Date takeofftime;

    @ManyToOne
    @JoinColumn(name = "takeofflocation")
    private Airport takeoffAirport;

    @Column(name = "arrivaltime")
    private Date arrivalTime;

    @ManyToOne
    @JoinColumn(name = "arrivallocation")
    private Airport arrivalAirport;

    @Column(name = "price")
    private double price;

    @ManyToOne
    @JoinColumn(name = "planeid")
    private Plane plane;

    public Flight() {
    }

    public Flight(int id) {
        this.id = id;
    }

    public Flight(int id, Date takeoff_time, Airport takeoff_airport, Date arrivalTime, Airport arrival_airport, double price, Plane plane) {
        this.id = id;
        this.takeofftime = takeoff_time;
        this.takeoffAirport = takeoff_airport;
        this.arrivalTime = arrivalTime;
        this.arrivalAirport = arrival_airport;
        this.price = price;
        this.plane = plane;
    }

    public Flight(int id, Airport takeoff_airport, Airport arrival_airport) {
        this.id = id;
        this.takeoffAirport = takeoff_airport;
        this.arrivalAirport = arrival_airport;
    }

    public Flight(int id, Airport takeoff_airport, Airport arrival_airport, Date takeofftime, Date arrivalTime) {
        this(id, takeoff_airport, arrival_airport);
        this.takeofftime = takeofftime;
        this.arrivalTime = arrivalTime;
    }

    public int getId() {
        return id;
    }

    public boolean isFlightFull() {
        return Integer.parseInt(this.getPlane().getSeatsquantity()) == this.getPassengers().size();
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getTakeofftime() {
        return takeofftime;
    }

    public void setTakeofftime(Date takeoff_time) {
        this.takeofftime = takeoff_time;
    }

    public Airport getTakeoffAirport() {
        return takeoffAirport;
    }

    public void setTakeoffAirport(Airport takeoff_airport) {
        this.takeoffAirport = takeoff_airport;
    }

    public Date getArrivaltime() {
        return arrivalTime;
    }

    public void setArrivaltime(Date arrival_time) {
        this.arrivalTime = arrival_time;
    }

    public Airport getArrivalAirport() {
        return arrivalAirport;
    }

    public void setArrivalAirport(Airport arrival_airport) {
        this.arrivalAirport = arrival_airport;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Plane getPlane() {
        return plane;
    }

    public void setPlane(Plane planeid) {
        this.plane = planeid;
    }

    @Override
    public boolean equals(Object ob) {
        return (this.id == ((Flight) ob).getId());
    }
}
