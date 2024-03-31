<template>
  <v-container class="pb-0">
    <v-row justify="center">
      <v-col>
        <v-sheet :elevation="12" class="mx-auto" :class="getTheme">
          <v-radio-group
            black
            :value=" isTwoWay == true ? $t('הלוך - חזור') : $t('כיוון אחד')"
            row
            class="radioFont"
            @change="isTwoWay = !isTwoWay"
          >
            <v-radio
              :color="getColor"
              v-for="option in directionOptions"
              :key="option"
              :label="option"
              :value="option"
              :class="getTheme"
            ></v-radio>
          </v-radio-group>
          <v-row row>
            <v-col :class="getTheme" >
              <label class="title">{{ $t("מאיפה") }}</label>
              <AutoCompleteAirports
                :name="origin.name"
                :from="true"
                @airport="origin = $event"
                id="autocomplete"
              />
            </v-col>
            <v-col
              cols="1"
              style="
                text-align: center;
                margin-left: -40px;
                margin-right: -40px;
                margin-top: 38px;
              "
            >
              <v-btn :color="getSwitchColor" icon @click="swap()">
                <v-icon>fas fa-exchange-alt</v-icon>
              </v-btn>
            </v-col>
            <v-col :class="getTheme" >
              <label class="title">{{ $t("לאן") }}</label>
              <AutoCompleteAirports
                :name="destination.name"
                :from="false"
                @airport="destination = $event"
              />
            </v-col>
            <v-col style="margin-right: 1em" >
              <label class="title">{{ $t("תאריך יציאה") }}</label>
              <datepicker @date="startDate = $event" />
            </v-col>
            <v-col style="margin-right: 1em" v-if="isTwoWay" >
              <label class="title">{{ $t("תאריך חזרה") }}</label>
              <datepicker @date="endDate = $event" />
            </v-col>
            <v-col class="mt-3" style="margin-right: 1em">
              <v-row>
                <v-col cols="2"
                  ><v-icon :color="getColor" @click="hePressedIt()"
                    >fa-users</v-icon
                  ></v-col
                >
                <v-col cols="10" :class="getTheme"
                  ><TravelersChip @travelers="$emit('travelers', $event)"
                /></v-col>
              </v-row>
              <v-row class="mt-1">
                <v-col cols="2"
                  ><v-icon :color="getColor" @click="hePressedIt()"
                    >fa-exchange-alt</v-icon
                  ></v-col
                >
                <v-col cols="10"> <StopsChip @stops="stops = $event" /></v-col>
              </v-row>
            </v-col>
          </v-row>
        </v-sheet>
        <v-row justify="center">
          <v-col cols="2">
            <v-btn class="searchbtn" color="blue" @click="search()">
              <label
                v-if="!isSearchingNow"
                style="font-size: x-large; cursor: pointer"
                >{{ $t("חפש עכשיו") }}</label
              >
              <v-icon v-if="!isSearchingNow" style="padding-right: 5%"
                >fa-search</v-icon
              >
              <v-progress-circular
                v-else
                indeterminate
                color="white"
              ></v-progress-circular>
            </v-btn>
          </v-col>
        </v-row>
      </v-col>
    </v-row>

    <v-dialog v-model="popup" max-width="480">
      <v-card title="dialog">
        <div class="popup-question">
          <p>...This is not a button</p>
        </div>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<script>
import datepicker from "@/components/DatePicker";
import TravelersChip from "@/components/TravelersChip";
import StopsChip from "@/components/StopsChip";
import AutoCompleteAirports from "@/components/AutoCompleteAirports";
import eventbus from "@/plugins/eventbus";
import Swal from "sweetalert2";
import api from "@/api/api";

export default {
  name: "SearchBar",
  components: {
    datepicker,
    TravelersChip,
    StopsChip,
    AutoCompleteAirports,
  },
  data() {
    return {
      origin: { name: "דבלין, אירלנד", iata: "DUB" },
      destination: { name: "" },
      startDate: new Date().toISOString().substring(0, 10),
      endDate: new Date().toISOString().substring(0, 10),
      stops: 0,
      searchResults: "",
      isTwoWay: true,
      isSearchingNow: false,
      popup: false,
    };
  },
  watch: {
    origin() {
      eventbus.$emit("origin", this.origin);
      this.mapResults();
    },
    destination() {
      eventbus.$emit("destination", this.destination);
      this.mapResults();
    },
    startDate() {
      this.mapResults();
    },
    endDate() {
      this.mapResults();
    },
    stops() {
      this.mapResults();
    },
  },
  mounted() {
    eventbus.$on("selectDestination", (wantedDestination) => {
      this.destination = {
        iata: wantedDestination.iata,
        name: wantedDestination.name,
      };
    });
  },
  computed: {
    directionOptions() {
      return [this.$t("הלוך - חזור"), this.$t("כיוון אחד")];
    },
    getTheme() {
      return this.$vuetify.theme.dark ? "dark" : "light";
    },
    getColor() {
      return this.$vuetify.theme.dark ? "white" : "#555";
    },
    getSwitchColor() {
      return this.$vuetify.theme.dark ? "blue" : "blue darken-4";
    },
  },
  methods: {
    hePressedIt() {
      this.popup = true;
    },
    swap() {
      const temp = this.origin;
      this.origin = this.destination;
      this.destination = temp;
    },
    search() {
      this.getDepartingFlights();
    },
    mapResults() {
      if (this.destination.name != "") {
        eventbus.$emit(
          "tempSearch",
          this.origin,
          this.destination,
          this.startDate,
          this.stops
        );
      } else {
        eventbus.$emit("cancelTempSearch");
      }
    },
    getDepartingFlights() {
      this.searchResults = {};
      if (this.destination.name == "") {
        if (this.isTwoWay) {
          Swal.fire({
            type: "info",
            title: "אופס",
            text: "נראה ששכחת למלא חלק מפרטי החיפוש",
          });
        } else {
          this.getOneWayFlightsWithoutDestination().then(() => {
            this.getReturningFlights();
          });
        }
      } else if (
        this.startDate < new Date().toISOString().split("T")[0] ||
        (this.isTwoWay && this.endDate < this.startDate)
      ) {
        Swal.fire({
          type: "error",
          title: "אופס",
          text: "נראה שהתאריכים שהזנת לא אפשריים",
        });
      } else {
        eventbus.$emit("startloading");
        api
          .Flights()
          .getAllFlightsFromToOn(
            this.origin.iata,
            this.destination.iata,
            this.startDate,
            this.stops
          )
          .then((res) => {
            this.searchResults.departingFlights = res;
            this.getReturningFlights();
          })
          .catch((error) => {
            this.searchResults.flightsTo =
              "אופס, נראה שיש לנו בעיה במציאת טיסות";
          });
      }
    },
    async getOneWayFlightsWithoutDestination() {
      eventbus.$emit("startloading");

      const { data: flights } = await api
        .Flights()
        .getAllFlightsFromOn(this.origin.iata, this.startDate);

      const formattedFlights = flights.map((flight) => {
        const formattedFlight = {};
        formattedFlight.destination = { ...flight.arrivalAirport };
        formattedFlight.origin = { ...flight.takeoffAirport };
        formattedFlight.flights = [];
        formattedFlight.flights.push(flight);

        return formattedFlight;
      });

      this.searchResults.departingFlights = formattedFlights;
    },
    async getReturningFlights() {
      if (this.isTwoWay) {
        try {
          await api
            .Flights()
            .getAllFlightsFromToOn(
              this.destination.iata,
              this.origin.iata,
              this.endDate,
              this.stops
            )
            .then((res) => {
              this.searchResults.returningFlights = res;
            });
        } catch (err) {
          this.searchResults.returningFlights =
            "אופס, נראה שיש לנו בעיה במציאת טיסות";
        }
      } else {
        this.searchResults.returningFlights = [];
      }

      eventbus.$emit("stoploading");
      eventbus.$emit("search", this.searchResults, this.isTwoWay);
    },
  },
};
</script>

<style scoped>
.v-sheet {
  border-radius: 10px;
  padding: 2%;
  background-color: rgba(255, 255, 255, 0.8);
  color: black;
}
.v-sheet.dark {
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
}
.v-radio.dark >>> label {
  color: white !important;
}
.v-input .v-input__slot {
  border-radius: 100px !important;
  transform: scale(0.85);
  left: 12%;
}

.v-text-field >>> .v-input__slot {
  background: #c4c4c4 !important;
}
.dark > .v-text-field >>> .v-input__slot {
  background: #555 !important;
}

.searchbtn {
  border-radius: 100px;
  bottom: 25px;
  transform: scale(1.1);
  cursor: pointer;
}

.radioFont >>> label {
  color: black;
  font-weight: bold;
  font-size: x-large;
}

.v-input--checkbox {
  margin-top: -25px;
  margin-right: 1%;
}

.title {
  font-family: "Assistant" !important;
}
.popup-question {
  padding-top: 10px;
  width: 100%;
  text-align: center;
  font-size: 2em;
}
</style>
