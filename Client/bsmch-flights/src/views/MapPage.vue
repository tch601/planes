<template>
  <div class="map">
    <reservations></reservations>
    <v-card
      style="height: 50vh"
      class="mx-4 mb-4 mt-10"
      id="map-card"
      v-if="showMap"
    >
      <v-container class="pt-5">
        <v-row>
          <v-col cols="4" v-if="showMapResults" class="ml-4">
            <v-card class="pa-3 text-center" height="45vh">
              <h1>{{ destination.name }}</h1>
              <h3 class="mb-4">{{ formatDate(startDate) }}</h3>
              <v-progress-circular
                class="mt-5"
                :size="50"
                indeterminate
                color="primary"
                v-if="isSearchingNow"
              ></v-progress-circular>
              <v-list
                dark
                v-else-if="flights.length > 0"
                class="animated fadeIn"
              >
                <v-list-item v-for="(flight, index) in maxFlights" :key="index">
                  <v-list-item-avatar class="ml-1">
                    <img src="@/assets/BasmachAirways-IconOnly.png" />
                  </v-list-item-avatar>
                  <v-list-item-content>
                    <v-list-item-title
                      >{{ flight.trip_time }}
                      <v-icon class="mx-2 pb-2" style="font-size: 40%"
                        >far fa-circle</v-icon
                      >
                      <template v-if="flight.flights.length > 1"
                        >{{ flight.flights.length - 1 }} עצירות</template
                      >
                      <template v-else>ללא עצירות</template>
                      <v-icon class="mx-2 pb-2" style="font-size: 40%"
                        >far fa-circle</v-icon
                      >
                      ${{ flight.price }}</v-list-item-title
                    >
                  </v-list-item-content>
                </v-list-item>
              </v-list>
              <v-alert v-else type="info" outlined>
                לא הצלחנו למצוא טיסות עבורך בתנאים אלו, נסה אולי פרמטרים אחרים
              </v-alert>
            </v-card>
          </v-col>
          <v-col>
            <destinations-map id="map"></destinations-map>
          </v-col>
        </v-row>
      </v-container>
    </v-card>
  </div>
</template>

<script>
var moment = require("moment");
import DestinationsMap from "../components/Map.vue";
import SearchBar from "../components/SearchBar.vue";
import eventbus from "@/plugins/eventbus";
import Reservations from "./Reservations.vue";
import api from "@/api/api";
moment.locale("he");

export default {
  components: {
    "destinations-map": DestinationsMap,
    "search-bar": SearchBar,
    reservations: Reservations,
  },
  data() {
    return {
      showMap: true,
      showMapResults: false,
      flights: [],
      destination: "",
      startDate: "",
      isSearchingNow: false,
    };
  },
  computed: {
    flightsWithTimesAndPrices() {
      return this.flights.map((curr) => {
        let temp = Object.assign(curr);
        temp.price = 0;

        // For each flight
        temp.flights.forEach((flight) => {
          let duration = moment.duration(
            Math.abs(
              moment(flight.takeofftime).diff(moment(flight.arrivaltime))
            )
          );
          flight.trip_time = duration.humanize();
          temp.price += flight.price;
        });

        // Overall flights (connections)
        let duration = moment.duration(
          Math.abs(
            moment(temp.flights[0].takeofftime).diff(
              moment(temp.flights[temp.flights.length - 1].arrivaltime)
            )
          )
        );
        temp.trip_time = duration.humanize();

        if (temp.flights.length > 1) {
          temp.price = Math.floor(temp.price / (1.15 * temp.flights.length));
        }

        return temp;
      });
    },
    maxFlights() {
      return this.flightsWithTimesAndPrices.slice(0, 5);
    },
  },
  mounted() {
    eventbus.$on("search", () => {
      this.showMap = false;
    });

    eventbus.$on("tempSearch", (origin, destination, startDate, stops) => {
      this.isSearchingNow = true;
      this.showMapResults = true;
      this.destination = destination;
      this.startDate = startDate;
      api
        .Flights()
        .getAllFlightsFromToOn(origin.iata, destination.iata, startDate, stops).then((res) => {
          this.flights = res;
          eventbus.$emit("flight", this.flights);
        })
        .catch((error) => {
          this.flights = "אופס, נראה שיש לנו בעיה במציאת טיסות";
        })
        .finally(() => {
          this.isSearchingNow = false;
        });
    });

    eventbus.$on("cancelTempSearch", () => {
      this.showMapResults = false;
    });
  },
  methods: {
    formatDate(date) {
      const [year, month, day] = date.split("-");
      return `${day}/${month}/${year}`;
    },
  },
};
</script>

<style scoped>
#map {
  position: relative;
  top: 0px;
  right: 0px;
  height: 890px;
  left: 0px;
  z-index: 1;
  direction: ltr;
}

.container {
  margin-top: -50px;
}
</style>
