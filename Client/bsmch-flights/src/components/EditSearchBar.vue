<template>
  <v-container class="pb-0">
    <v-row justify="center">
      <v-col>
        <v-sheet :elevation="12" class="mx-auto" :class="getTheme">
          <p style="font-size: 1.5em; font-weight: bold">
            {{ $t("חפש את הטיסה שברצונך לערוך") }}
          </p>
          <v-row row>
            <v-col :class="getTheme">
              <label class="title">{{ $t("מאיפה") }}</label>
              <AutoCompleteAirports
                :name="origin.name"
                :from="true"
                @airport="origin = $event"
                id="autocomplete"
              />
            </v-col>
            <v-col :class="getTheme">
              <label class="title">{{ $t("לאן") }}</label>
              <AutoCompleteAirports
                :name="destination.name"
                :from="false"
                @airport="destination = $event"
              />
            </v-col>
            <v-col>
              <label class="title">{{ $t("תאריך יציאה") }}</label>
              <datepicker @date="startDate = $event" />
            </v-col>
          </v-row>
        </v-sheet>
        <v-row justify="center">
          <v-col cols="2">
            <v-btn class="searchbtn" color="blue" @click="search()">
              <label
                v-if="!isSearchingNow"
                style="font-size: x-large; cursor: pointer"
                >חפש עכשיו</label
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
  </v-container>
</template>
<script>
import datepicker from "@/components/DatePicker";
import TravelersChip from "@/components/TravelersChip";
import StopsChip from "./StopsChip.vue";
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
      directionOptions: ["הלוך - חזור", "כיוון אחד"],
      isTwoWay: false,
      isSearchingNow: false,
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
    getTheme() {
      return this.$vuetify.theme.dark ? "dark" : "light";
    },
  },
  methods: {
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
        Swal.fire({
          type: "info",
          title: "אופס",
          text: "נראה ששכחת למלא חלק מפרטי החיפוש",
        });
      } else if (this.startDate < new Date().toISOString().split("T")[0]) {
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
  background-color: rgba(255, 255, 255, 0.8);
  color: black;
  padding: 2%;
}
.v-sheet.dark {
  background-color: rgba(0, 0, 0, 0.8);
  color: white;
}
.v-input .v-input__slot {
  border-radius: 100px !important;
  transform: scale(0.85);
  left: 12%;
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
.v-text-field >>> .v-input__slot {
  background: #c4c4c4 !important;
}
.dark > .v-text-field >>> .v-input__slot {
  background: #555 !important;
}
</style>
