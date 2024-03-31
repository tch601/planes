import BsmchFlights.DTO.AirportDTO;
import BsmchFlights.Models.Airport;
import BsmchFlights.Repositories.AirportsRepository;
import BsmchFlights.Services.AirportsService;
import BsmchFlights.Utils.QueryCondition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class AirportsServiceTest {

    @TestConfiguration
    protected static class AirportsServiceTestingContextConfiguration {

        @Bean
        public AirportsService airportsService() {
            return new AirportsService();
        }
    }

    @Autowired
    private AirportsService airportsService;

    @MockBean
    private AirportsRepository airportsRepository;

    @MockBean
    private QueryCondition queryCondition;

    private List<QueryCondition> bounds;
    private ArrayList<Airport> airports;

    @Before
    public void setUp() {
        String[] boundsArray = new String[4];
        boundsArray[0] = "-1";
        boundsArray[1] = "-1";
        boundsArray[2] = "1";
        boundsArray[3] = "1";

        Mockito.when(this.queryCondition.getParameters()).thenReturn(boundsArray);

        this.bounds = new ArrayList<>();
        this.bounds.add(this.queryCondition);

        this.airports = new ArrayList<>();
        Mockito.when(this.airportsService.getAll()).thenReturn(this.airports);
    }

    @Test
    public void isAirportInBounds_inBounds() {
        Airport airport = new Airport();
        airport.setLat(0);
        airport.setLon(0);

        final boolean ACTUAL =
                this.airportsService.isAirportInBounds(airport, this.bounds.get(0).getParameters());

        assertTrue(ACTUAL);
    }

    @Test
    public void isAirportInBounds_notInBounds() {
        Airport airport = new Airport();
        airport.setLat(50);
        airport.setLon(50);

        final boolean ACTUAL =
                this.airportsService.isAirportInBounds(airport, this.bounds.get(0).getParameters());

        assertFalse(ACTUAL);
    }

    @Test
    public void isAirportInBounds_latInBoundsLonNot() {
        Airport airport = new Airport();
        airport.setLat(0);
        airport.setLon(50);

        final boolean ACTUAL =
                this.airportsService.isAirportInBounds(airport, this.bounds.get(0).getParameters());

        assertFalse(ACTUAL);
    }

    @Test
    public void isAirportInBounds_latNotInBoundsLonIs() {
        Airport airport = new Airport();
        airport.setLat(50);
        airport.setLon(0);

        final boolean ACTUAL =
                this.airportsService.isAirportInBounds(airport, this.bounds.get(0).getParameters());

        assertFalse(ACTUAL);
    }

    @Test
    public void isAirportInBounds_inBoundsOnEdge() {
        Airport airport = new Airport();
        airport.setLat(1);
        airport.setLon(1);

        final boolean ACTUAL =
                this.airportsService.isAirportInBounds(airport, this.bounds.get(0).getParameters());

        assertTrue(ACTUAL);
    }

    @Test
    public void getAirportsByQuery_allAirportsPassQuery() {
        Airport airportOne = new Airport();
        airportOne.setIata("one");
        airportOne.setLat(-1);
        airportOne.setLon(1);

        Airport airportTwo = new Airport();
        airportTwo.setIata("two");
        airportTwo.setLat(1);
        airportTwo.setLon(-1);

        this.airports.add(airportOne);
        this.airports.add(airportTwo);

        final List<Airport> EXPECTED = this.airports;
        final List<Airport> ACTUAL = this.airportsService.getAirportsByQuery(this.bounds);

        assertEquals(ACTUAL, EXPECTED);
    }

    @Test
    public void getAirportsByQuery_noneOfTheAirportsPassQuery() {
        Airport airportOne = new Airport();
        airportOne.setIata("one");
        airportOne.setLat(-50);
        airportOne.setLon(-50);

        Airport airportTwo = new Airport();
        airportTwo.setIata("two");
        airportTwo.setLat(50);
        airportTwo.setLon(50);

        this.airports.add(airportOne);
        this.airports.add(airportTwo);

        final List<Airport> ACTUAL = this.airportsService.getAirportsByQuery(this.bounds);

        assertTrue(ACTUAL.isEmpty());
    }

    @Test
    public void getAirportsByQuery_oneAirportPassQuery() {
        Airport airportOne = new Airport();
        airportOne.setIata("one");
        airportOne.setLat(0);
        airportOne.setLon(0);

        Airport airportTwo = new Airport();
        airportTwo.setIata("two");
        airportTwo.setLat(50);
        airportTwo.setLon(50);

        this.airports.add(airportOne);
        this.airports.add(airportTwo);

        final List<Airport> EXPECTED = new ArrayList<>();
        EXPECTED.add(airportOne);

        final List<Airport> ACTUAL = this.airportsService.getAirportsByQuery(this.bounds);

        assertEquals(ACTUAL, EXPECTED);
    }

    @Test
    public void getAirportReduced_noAirports() {
        Mockito.when(this.airportsRepository.findAll()).thenReturn(new ArrayList<>());

        final List<AirportDTO> ACTUAL = this.airportsService.getAirportsReduced();

        assertTrue(ACTUAL.isEmpty());
    }

    @Test
    public void getAirportReduced_oneAirport() {
        Mockito.when(this.airportsRepository.findAll())
                .thenReturn(new ArrayList<>(Arrays.asList(new Airport("AUS", "אוסטין, ארה\"ב"))));

        final List<AirportDTO> ACTUAL = this.airportsService.getAirportsReduced();
        final List<AirportDTO> EXPECTED =
                new ArrayList<>(Arrays.asList(new AirportDTO("AUS", "אוסטין, ארה\"ב")));

        assertEquals(ACTUAL, EXPECTED);
    }

    @Test
    public void getAirportReduced_unSortedAirports() {
        Mockito.when(this.airportsRepository.findAll())
                .thenReturn(
                        new ArrayList<>(
                                Arrays.asList(
                                        new Airport("LTN", "לונדון, שדה תעופה LTN"),
                                        new Airport("AUS", "אוסטין, ארה\"ב"),
                                        new Airport("HEL", "הלסינקי, פינלנד"))));

        final List<AirportDTO> EXPECTED =
                new ArrayList<>(
                        Arrays.asList(
                                new AirportDTO("LTN", "לונדון, שדה תעופה LTN"),
                                new AirportDTO("AUS", "אוסטין, ארה\"ב"),
                                new AirportDTO("HEL", "הלסינקי, פינלנד")
                        ));

        final List<AirportDTO> ACTUAL = this.airportsService.getAirportsReduced();
        assertEquals(ACTUAL, EXPECTED);
    }

    @Test
    public void getAirportReduced_sortedAirports() {
        Mockito.when(this.airportsRepository.findAll())
                .thenReturn(
                        new ArrayList<>(
                                Arrays.asList(
                                        new Airport("AUS", "אוסטין, ארה\"ב"),
                                        new Airport("HEL", "הלסינקי, פינלנד"),
                                        new Airport("LTN", "לונדון, שדה תעופה LTN"))));

        final List<AirportDTO> EXPECTED = new ArrayList<>(
                Arrays.asList(
                        new AirportDTO("AUS", "אוסטין, ארה\"ב"),
                        new AirportDTO("HEL", "הלסינקי, פינלנד"),
                        new AirportDTO("LTN", "לונדון, שדה תעופה LTN")));
        final List<AirportDTO> ACTUAL = this.airportsService.getAirportsReduced();

        assertEquals(ACTUAL, EXPECTED);
    }
}
