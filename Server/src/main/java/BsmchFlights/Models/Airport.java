package BsmchFlights.Models;

import javax.persistence.*;

@Entity
@Table(name="t_airports")
public class Airport {

    @Id
    @Column(name="iata")
    private String iata;

    @Column(name="lat")
    private double lat;

    @Column(name="lon")
    private double lon;

    @Column(name="name")
    private String name;

    @ManyToOne
    @JoinColumn(name="city_id")
    private City city;

    public Airport() {
    }

    public Airport(String iata) {
        this.iata = iata;
    }

    public Airport(String iata, String name) {
        this.iata = iata;
        this.name = name;
    }

    public Airport(String iata, double lat, double lon, String name, City city) {
        this.iata = iata;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.city = city;
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    @Override
    public boolean equals(Object ob) {
        return this.iata.equals(((Airport)ob).getIata());
    }
}
