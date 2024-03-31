package BsmchFlights.Controllers;

import BsmchFlights.Services.AirportsService;
import BsmchFlights.Utils.QueryCondition;
import BsmchFlights.DTO.AirportDTO;
import BsmchFlights.Models.Airport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/airports")
public class AirportsController {
    @Autowired
    private AirportsService airportsService;
   
    @CrossOrigin
    @GetMapping("")
    public List<Airport> getAll() {
        return this.airportsService.getAll();
    }

    @CrossOrigin
    @GetMapping("/reduced")
    public List<AirportDTO> getAirportsReduced() {
        return this.airportsService.getAirportsReduced();
    }

    @CrossOrigin
    @GetMapping("/{iata}")
    public Airport getByIata(@PathVariable String iata) {
        return this.airportsService.getByIata(iata);
    }

    @CrossOrigin
    @PostMapping("/query")
    public List<Airport> getAirportsByQuery(@RequestBody List<QueryCondition> queryConditions) throws Exception {
        return this.airportsService.getAirportsByQuery(queryConditions);
    }
}
