package BsmchFlights.Controllers;

import BsmchFlights.Models.ConnectionFlight;
import BsmchFlights.Models.Flight;
import BsmchFlights.Services.ConnectionsService;
import BsmchFlights.Services.FlightsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/flights")
public class FlightsController {
    @Autowired
    private FlightsService flightsService;
    @Autowired
    private ConnectionsService connectionsService;

    @CrossOrigin
    @GetMapping("")
    public List<Flight> getAll() {
        return this.flightsService.getAll();
    }

    @CrossOrigin
    @GetMapping("/allfrom/{iata}/on/{on}")
    public List<Flight> getFromByName(@PathVariable String iata, @PathVariable String on) throws ParseException {
        Date onDate = new SimpleDateFormat("yyyy-MM-dd").parse(on);

        return this.flightsService.getFiveFirstFlightsFromIata(iata, onDate);
    }

    @CrossOrigin
    @GetMapping("/allto/{name}")
    public List<Flight> getToByName(@PathVariable String name) {
        return this.flightsService.getFlightsToCityName(name);
    }

    @CrossOrigin
    @GetMapping("/from/{from}/to/{to}")
    public List<ConnectionFlight> getFromTo(@PathVariable String from,
                                            @PathVariable String to) {
        return this.flightsService.formatFlights(this.flightsService.getFlightsFromTo(from, to));
    }

    @CrossOrigin
    @GetMapping("/from/{from}/to/{to}/on/{on}")
    public List<ConnectionFlight> getFromToOn(@PathVariable String from,
                                              @PathVariable String to,
                                              @PathVariable String on) throws ParseException {
        Date arriveDate = new SimpleDateFormat("yyyy-MM-dd").parse(on);
        return this.flightsService.formatFlights(this.flightsService.getFlightsFromToOn(from, to, arriveDate));
    }

    @CrossOrigin
    @GetMapping("/from/{from}/to/{to}/on/{on}/stops/{stops}")
    public List<ConnectionFlight> getFlights(@PathVariable String from,
                                             @PathVariable String to,
                                             @PathVariable String on,
                                             @PathVariable int stops) throws ParseException {
        Date arriveDate = new SimpleDateFormat("yyyy-MM-dd").parse(on);

        return this.connectionsService.getFlights(from, to, to, arriveDate, stops);
    }
    @CrossOrigin
    @PatchMapping("/flight-id/{id}/price/{price}")
    public void changePrice(@PathVariable int id,
                                             @PathVariable int price) throws ParseException {

        this.flightsService.changeFlightsPrice(id, price);
    }
}
