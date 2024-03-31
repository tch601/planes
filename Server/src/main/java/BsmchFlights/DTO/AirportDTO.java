package BsmchFlights.DTO;

import BsmchFlights.Models.Airport;

import java.util.Objects;

public class AirportDTO {

    private String iata;
    private String name;

    public AirportDTO(String iata, String name) {
        this.iata = iata;
        this.name = name;
    }

    public AirportDTO(Airport airport) {
        this.iata = airport.getIata();
        this.name = airport.getName();
    }

    public String getIata() {
        return iata;
    }

    public void setIata(String iata) {
        this.iata = iata;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AirportDTO that = (AirportDTO) o;
        return Objects.equals(iata, that.iata) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(iata, name);
    }
}
