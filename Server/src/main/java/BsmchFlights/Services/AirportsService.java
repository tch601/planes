package BsmchFlights.Services;

import BsmchFlights.Utils.QueryCondition;
import BsmchFlights.DTO.AirportDTO;
import BsmchFlights.Models.Airport;
import BsmchFlights.Repositories.AirportsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AirportsService {
    private final Logger logger = LoggerFactory.getLogger(AirportsService.class);
    @Autowired
    private AirportsRepository airportsRepository;

    public List<Airport> getAll() {
        logger.info("getting all flights");
        return this.airportsRepository.findAll();
    }

    public List<AirportDTO> getAirportsReduced() {
        logger.info("getting IATA's and names for airports");
        List<AirportDTO> airportDTOS = new ArrayList<>();

            this.getAll().forEach(airport -> {
                airportDTOS.add(new AirportDTO(airport));
            });

        return airportDTOS;
    }

    public Airport getByIata(String iata) {
        logger.info("getting airport with IATA " + iata);
        return this.airportsRepository.findByIata(iata);
    }

    public List<Airport> getAirportsByQuery(List<QueryCondition> queryConditions)  {
        logger.info("getting airports with query conditions");
        List<Airport> airports = new ArrayList<>(this.getAll());

        airports.removeIf(airport -> !isAirportInBounds(airport, queryConditions.get(0).getParameters()));

        return airports;
    }
    public boolean isAirportInBounds(Airport airport, String[] parameters) {
        logger.info("checking bounds for airport with IATA " + airport.getIata());
        return !(airport.getLat() < Double.parseDouble(parameters[0]) || airport.getLat() > Double.parseDouble(parameters[2]) ||
                 airport.getLon() < Double.parseDouble(parameters[1]) || airport.getLon() > Double.parseDouble(parameters[3]));
    }
}
