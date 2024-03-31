package BsmchFlights.Models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private int reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User userId;

    @ManyToOne
    @JsonIgnoreProperties("passengers")
    @JoinColumn(name = "flight_id")
    private Flight flightId;

    @Column(name = "passport_id")
    private int passportId;
    @Column(name = "insertion_date")
    private Date insertionDate;
    @Column(name = "order_confirmation")
    private String orderConfirmation;

    @Column(name = "order_payment")
    private int payment;

    @Column(name = "ticket_code")
    private String ticketCode;

    @Column(name = "passenger_name")
    private String passengerName;


    @Column(name = "passenger_sex")
    private String passengerSex;

    @Column(name = "passenger_birth_date")
    private Date passengerBirthDate;

    @Column(name = "is_seat_taken")
    private boolean isSeatTaken;

    public Reservation() {
    }

    public Reservation(int reservationId,
                       User userId,
                       Flight flightId,
                       String orderConfirmation,
                       int payment,
                       String ticketCode,
                       String passengerName,
                       String passengerSex,
                       Date passengerBirthDate,
                       boolean isSeatTaken,
                       Date insertionDate) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.flightId = flightId;
        this.orderConfirmation = orderConfirmation;
        this.insertionDate = insertionDate;
        this.payment = payment;
        this.ticketCode = ticketCode;
        this.passengerName = passengerName;
        this.passengerBirthDate = passengerBirthDate;
        this.passengerSex = passengerSex;
        this.isSeatTaken = isSeatTaken;
    }

    public int getReservationId() {
        return reservationId;
    }

    public void setReservationId(int reservationId) {
        this.reservationId = reservationId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

    public Date getInsertionDate() {
        return insertionDate;
    }

    public void setInsertionDate(Date insertionDate) {
        this.insertionDate = insertionDate;
    }

    public boolean isSeatTaken() {
        return isSeatTaken;
    }

    public void setSeatTaken(boolean seatTaken) {
        isSeatTaken = seatTaken;
    }

    public Flight getFlightId() {
        return flightId;
    }

    public void setFlightId(Flight flightId) {
        this.flightId = flightId;
    }

    public int getPassportId() {
        return passportId;
    }

    public void setPassportId(int passportId) {
        this.passportId = passportId;
    }

    public String getOrderConfirmation() {
        return orderConfirmation;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public int getPayment() {
        return payment;
    }

    public void setOrderConfirmation(String orderConfirmation) {
        this.orderConfirmation = orderConfirmation;
    }

    public String getTicketCode() {
        return ticketCode;
    }

    public void setTicketCode(String ticketCode) {
        this.ticketCode = ticketCode;
    }

    public String getPassengerName() {
        return passengerName;
    }

    public void setPassengerName(String passengerName) {
        this.passengerName = passengerName;
    }

    public String getPassengerSex() {
        return passengerSex;
    }

    public void setPassengerSex(String passengerSex) {
        this.passengerSex = passengerSex;
    }

    public Date getPassengerBirthDate() {
        return passengerBirthDate;
    }

    public void setPassengerBirthDate(Date passengerBirthDate) {
        this.passengerBirthDate = passengerBirthDate;
    }

    public boolean getIsSeatTaken() {
        return isSeatTaken;
    }

    public void setIsSetSeatTaken(boolean isSeatTaken) {
        isSeatTaken = isSeatTaken;
    }
}
