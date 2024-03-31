<template>
  <div class="reservations">
    <v-container v-show="showSearchBar" class="pb-0">
      <v-row column class="searchBar" :dir="direction">
        <EditSearchBar @travelers="travelers = $event"></EditSearchBar>
      </v-row>
    </v-container>
    <v-container class="pt-0">
      <v-row v-if="this.showError === true" justify="center">
        <v-alert type="info" outlined
          >לא הצלחנו למצוא טיסות עבורך בתנאים אלו, נסה שוב</v-alert
        >
      </v-row>
      <v-row row v-show="this.showResults === true">
        <v-col mb6 offset-mb3>
          <v-stepper v-model="e1">
            <v-stepper-items>
              <v-stepper-content step="1">
                <strong>טיסות ההלוך שמצאנו עבורך:</strong>
                <EditSearchResults
                  v-for="flights in departingFlightsWithTimesAndPrices"
                  :key="flights"
                  class="mt-3"
                  :flight="flights.flights"
                  :results="departingFlightsWithTimesAndPrices"
                  :titles="titles"
                  @row-chosen="chooseFlight($event)"
                >
                  <template v-slot:row-action>ערוך</template>
                </EditSearchResults>

                >
              </v-stepper-content>
            </v-stepper-items>
          </v-stepper>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
var moment = require("moment");
import EditSearchBar from "@/components/EditSearchBar";
import EditSearchResults from "@/components/EditSearchResults";
import PassengerDetails from "@/components/PassengerDetails";

import eventbus from "@/plugins/eventbus";

import api from "@/api/api";
import { mapState } from "vuex";

moment.locale("he");
let defaultProps = {
  number: "",
  name: "",
  expiry: "",
  cvc: "",
};
export default {
  name: "edit",
  components: {
    EditSearchBar,
    EditSearchResults,
    PassengerDetails,
  },
  mounted() {
    eventbus.$on("search", (searchResults, isTwoWay) => {
      this.init(isTwoWay);
      if (
        searchResults.departingFlights.length == 0 &&
        searchResults.returningFlights.length == 0
      ) {
        this.showResults = false;
        this.showError = true;
      } else {
        this.showResults = true;
        this.showError = false;
      }

      this.flightItems = searchResults;
    });

    this.initUser().then(() => {
      if (this.user.username == "אורח") {
        this.isNewCard = true;
      }
    });
  },
  data() {
    return {
      user: {},
      showSearchBar: true,
      selectedFlights: [],
      ticketCodes: [],
      e1: 0,
      showResults: false,
      showError: false,
      isTwoWay: false,
      dialog3: false,
      flightItems: {
        departingFlights: [],
        returningFlights: [],
      },
      titles: [
        { realName: "trip_time", displayName: "משך הטיול" },
        { realName: "stops", displayName: "מספר עצירות" },
        { realName: "price", displayName: "מחיר" },
      ],
      stepperSteps: {
        oneWay: ["טיסות הלוך", "סיכום הזמנה", "פרטי הנוסעים", "תשלום"],
        twoWay: [
          "טיסות הלוך",
          "טיסות חזור",
          "סיכום הזמנה",
          "פרטי הנוסעים",
          "תשלום",
        ],
        summeryStepNum: null,
      },
      cardDetail: defaultProps,
      confirmationCode: "",
      back: false,
      creditValidation: true,
      isNewCard: false,
      passportDialog: false,
      travelers: [
        {
          title: "מבוגרים",
          subtitle: "",
          amount: 1,
        },
      ],
      today: new Date(),
      someday: new Date(),
      rules: {
        required: (value) => !!value || "חובה למלא שדה זה",
        cvc: (value) =>
          (value.length == 3 && /^[0-9]*$/.test(value)) || "CVC לא תקין",
        expiry: (value) => {
          const [month, year] = value.split(" / ");
          this.someday.setFullYear("20" + year, month, 1);

          return this.someday >= this.today || "תאריך תפוגה לא חוקי";
        },
        name: (value) => {
          const pattern = /^[א-ת ]+$/i;
          return pattern.test(value) || "שם לא חוקי";
        },
        minCreditNum: (value) => value.length >= 8 || "מספר לא תקין",
      },
    };
  },
  computed: {
    ...mapState(["jwt"]),
    direction() {
      return this.$i18n.locale === "EN" ? "ltr" : "rtl";
    },
    departingFlightsWithTimesAndPrices() {
      return this.flightItems.departingFlights.map((curr) => {
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
    returningFlightsWithTimesAndPrices() {
      return this.flightItems.returningFlights.map((curr) => {
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
    payment() {
      return (
        this.computedTravelers.length *
        this.selectedFlights.reduce((total, currFlight) => {
          return total + currFlight.data.price;
        }, 0)
      );
    },
    computedTravelers() {
      let travelersArray = [];
      this.travelers.forEach((travelGroup) => {
        let title = "תינוק";
        if (travelGroup.title == "מבוגרים") {
          title = "מבוגר";
        } else if (travelGroup.title == "ילדים") {
          title = "ילד";
        }

        for (let index = 0; index < travelGroup.amount; index++) {
          let traveler = {
            title,
            subtitle: travelGroup.subtitle,
          };
          travelersArray.push(traveler);
        }
      });

      return travelersArray;
    },
  },
  methods: {
    chooseFlight(reservation) {
      this.ticketCodesArray =
        this.reservationsByConfirmationCode["c" + reservation.code].tickets;
      this.dialog = true;
    },
    init(isTwoWay) {
      this.e1 = 1;
      this.selectedFlights = [];
      this.isTwoWay = isTwoWay;
      isTwoWay
        ? (this.stepperSteps.summeryStepNum = 3)
        : (this.stepperSteps.summeryStepNum = 2);
    },
    generateConfirmationCode() {
      const creditcard = this.isNewCard
        ? this.cardDetail.number.replace(/\s/g, "") + this.cardDetail.cvc
        : this.user.creditcard;

      return (creditcard.substring(0, 3) + Date.now() * Math.random())
        .toString()
        .substring(0, 11);
    },

    async initUser() {
      this.user = await api.Users().getUser(this.jwt);
    },
  },
};
</script>

<style scoped>
.v-expansion-panels__container {
  border-radius: 10px;
  background-color: rgba(255, 255, 255, 0.5);
  margin-bottom: 10px;
}

.v-application--is-rtl .v-list__tile__content {
  padding: 0 10px 0 10px !important;
  text-align: center;
}

.theme--dark.v-list {
  background: none;
}

.item-title {
  font-weight: bold !important;
  font-size: 1.1em;
}
</style>
