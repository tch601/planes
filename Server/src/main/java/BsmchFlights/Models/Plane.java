package BsmchFlights.Models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="t_planes")
public class Plane {

    @Id
    @Column(name="id")
    private int id;

    @Column(name="planetype")
    private String planeType;

    @Column(name="seatsquantity")
    private String seatQuantity;

    public Plane() {
    }

    public Plane(int id, String planeType, String seatQuantity) {
        this.id = id;
        this.planeType = planeType;
        this.seatQuantity = seatQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlanetype() {
        return planeType;
    }

    public void setPlanetype(String plane_type) {
        this.planeType = plane_type;
    }

    public String getSeatsquantity() {
        return seatQuantity;
    }

    public void setSeatsquantity(String seats_quantity) {
        this.seatQuantity = seats_quantity;
    }
}
