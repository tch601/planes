import BsmchFlights.Models.Airport;
import BsmchFlights.Models.ConnectionFlight;
import BsmchFlights.Services.AirportsService;
import BsmchFlights.Services.ConnectionsService;
import BsmchFlights.Services.FlightsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class ConnectionsServiceTest {

  @TestConfiguration
  static class FlightsServiceTestingContextConfiguration {

    @Bean
    public ConnectionsService connectionsService() {
      return new ConnectionsService();
    }
  }

  @Autowired @SpyBean private ConnectionsService connectionsService;

  @MockBean private AirportsService airportsService;
  @MockBean private FlightsService flightsService;

  private Airport TLV;
  private Airport AVL;
  private Airport DUB;
  private Airport LHR;

  @Before
  public void setUp() {
    this.TLV = new Airport("TLV");
    this.AVL = new Airport("AVL");
    this.DUB = new Airport("DUB");
    this.LHR = new Airport("LHR");

    Mockito.when(this.airportsService.getByIata("DUB")).thenReturn(this.DUB);
    Mockito.when(this.airportsService.getByIata("AVL")).thenReturn(this.AVL);
    Mockito.when(this.airportsService.getByIata("TLV")).thenReturn(this.TLV);
    Mockito.when(this.airportsService.getByIata("LHR")).thenReturn(this.LHR);
  }

  @Test
  public void isDistanceValid_ConnectionBetweenAirports() {
    Airport rightAirport = new Airport();
    rightAirport.setLat(0);
    rightAirport.setLon(0);

    Airport leftAirport = new Airport();
    leftAirport.setLat(1);
    leftAirport.setLon(1);

    Airport connectionAirport = new Airport();
    connectionAirport.setLat(0.5);
    connectionAirport.setLon(0.5);

    final boolean ACTUAL =
        this.connectionsService.isDistanceValid(rightAirport, leftAirport, connectionAirport);

    assertTrue(ACTUAL);
  }

  @Test
  public void isDistanceValid_InsideDistanceRadius() {
    Airport rightAirport = new Airport();
    rightAirport.setLat(0);
    rightAirport.setLon(0);

    Airport leftAirport = new Airport();
    leftAirport.setLat(-2);
    leftAirport.setLon(-2);

    Airport connectionAirport = new Airport();
    connectionAirport.setLat(-3);
    connectionAirport.setLon(-3);

    final boolean ACTUAL =
        this.connectionsService.isDistanceValid(rightAirport, leftAirport, connectionAirport);

    assertTrue(ACTUAL);
  }

  @Test
  public void isDistanceValid_OutsideDistanceRadius() {
    Airport rightAirport = new Airport();
    rightAirport.setLat(0);
    rightAirport.setLon(0);

    Airport leftAirport = new Airport();
    leftAirport.setLat(-1);
    leftAirport.setLon(-1);

    Airport connectionAirport = new Airport();
    connectionAirport.setLat(-2);
    connectionAirport.setLon(-2);

    final boolean ACTUAL =
        this.connectionsService.isDistanceValid(rightAirport, leftAirport, connectionAirport);

    assertFalse(ACTUAL);
  }

  @Test
  public void getFlights_noFlightsToDestination() throws ParseException {
    Date on = new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01");

    final List<ConnectionFlight> ACTUAL =
        this.connectionsService.getFlights("DUB", "A", "A", on, 0);

    assertTrue(ACTUAL.isEmpty());
  }

  @Test
  public void getFlights_noFlightsFromOrigin() throws ParseException {
    Date on = new SimpleDateFormat("yyyy-MM-dd").parse("2019-01-01");

    final List<ConnectionFlight> ACTUAL =
            this.connectionsService.getFlights("A", "AVL", "AVL", on, 1);

    assertTrue(ACTUAL.isEmpty());
  }
}
