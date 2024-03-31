<template>
  <div class="reservations">
    <v-container v-show="showSearchBar" class="pb-0">
      <v-row column class="searchBar" :dir="direction">
        <SearchBar @travelers="travelers = $event"></SearchBar>
      </v-row>
    </v-container>
    <v-container class="pt-0">
      <v-row v-if="this.showError === true" justify="center">
        <v-alert type="info" outlined
          >לא הצלחנו למצוא טיסות עבורך בתנאים אלו, נסה שוב</v-alert
        >
      </v-row>
      <v-row row v-if="this.showResults === true">
        <v-col mb6 offset-mb3>
          <v-stepper v-model="e1">
            <v-stepper-header v-if="isTwoWay">
              <template v-for="(step, index) in stepperSteps.twoWay">
                <v-stepper-step
                  id="steps"
                  :key="step"
                  :complete="e1 > index + 1"
                  :step="index + 1"
                >
                  {{ step }}</v-stepper-step
                >
                <v-divider :key="index"></v-divider>
              </template>
            </v-stepper-header>

            <v-stepper-header v-else>
              <template v-for="(step, index) in stepperSteps.oneWay">
                <v-stepper-step
                  :key="index"
                  :complete="e1 > index + 1"
                  :step="index + 1"
                >
                  {{ step }}</v-stepper-step
                >
                <v-divider :key="step"></v-divider>
              </template>
            </v-stepper-header>

            <v-stepper-items>
              <v-stepper-content step="1">
                <v-row justify="space-between" align="center">
                  <v-col cols="4">
                    <strong>טיסות ההלוך שמצאנו עבורך:</strong>
                  </v-col>
                  <v-col cols="2">
                    <v-select
                      v-model="sortDepartingProperty"
                      :items="items"
                      label="בחר מיון לפי"
                    >
                      <template v-slot:item="{ item }">
                        <v-list-item-content
                          :style="{ background: itemBackground }"
                        >
                          {{ item }}
                        </v-list-item-content>
                      </template>
                    </v-select>
                  </v-col>
                </v-row>
                <SearchResults
                  class="mt-3"
                  v-if="
                    this.flightItems.departingFlights.length > 0 &&
                    computedTravelers.length > 0
                  "
                  :results="departingFlightsWithTimesAndPrices"
                  :titles="titles"
                  :propertyToSortBy="sortDepartingProperty"
                  @row-chosen="chooseLeavingFlight($event)"
                >
                  <template v-slot:row-action>בחר טיסת הלוך</template>
                </SearchResults>
                <v-alert v-else type="info" outlined
                  >לא הצלחנו למצוא טיסות עבורך בתנאים אלו, נסה אולי פרמטרים
                  אחרים</v-alert
                >
              </v-stepper-content>

              <v-stepper-content v-if="isTwoWay" step="2">
                <v-row align="center">
                  <v-col cols="4">
                    <strong>טיסות החזור שמצאנו עבורך:</strong>
                  </v-col>
                  <v-spacer></v-spacer>
                  <v-col cols="2">
                    <v-select
                      v-model="sortReturningProperty"
                      :items="items"
                      label="בחר מיון לפי"
                    >
                      <template v-slot:item="{ item }">
                        <v-list-item-content
                          :style="{ background: itemBackground }"
                        >
                          {{ item }}
                        </v-list-item-content>
                      </template>
                    </v-select>
                  </v-col>
                  <v-col cols="2">
                    <v-btn
                      outlined
                      rounded
                      @click="
                        () => {
                          goBack();
                          removeFlight();
                        }
                      "
                      >חזור לטיסות הלוך</v-btn
                    >
                  </v-col>
                </v-row>
                <SearchResults
                  class="mt-3"
                  v-if="flightItems.returningFlights.length > 0"
                  :results="returningFlightsWithTimesAndPrices"
                  :titles="titles"
                  :propertyToSortBy="sortReturningProperty"
                  @row-chosen="chooseReturningFlight($event)"
                >
                  <template v-slot:row-action>בחר טיסת חזור</template>
                </SearchResults>
                <v-alert v-else type="info" outlined
                  >לא הצלחנו למצוא טיסות עבורך בתנאים אלו, נסה אולי פרמטרים
                  אחרים
                </v-alert>
              </v-stepper-content>

              <v-stepper-content :step="stepperSteps.summeryStepNum">
                <v-card class="mb-5">
                  <v-row align="center" justify="space-around" row fill-height>
                    <v-card
                      class="mx-auto my-5 text-xs-center"
                      color="#26c6da"
                      dark
                      max-width="400"
                      v-for="flight in selectedFlights"
                      :key="flight.data.id"
                    >
                      <v-card-title>
                        <v-icon
                          v-if="flight.desc === 'הלוך'"
                          large
                          left
                          style="transform: scaleX(-1)"
                          >fa-plane-departure</v-icon
                        >
                        <v-icon
                          v-else-if="flight.desc === 'חזור'"
                          large
                          left
                          style="transform: scaleX(-1)"
                          >fa-plane-arrival</v-icon
                        >
                        <span class="title font-weight-light mx-3"
                          >טיסת {{ flight.desc }}</span
                        >
                      </v-card-title>

                      <v-card-text>
                        <strong class="headline font-weight-bold">
                          <template v-for="innerFlight in flight.data.flights">
                            {{ innerFlight.takeoffAirport.iata }}
                            <v-icon :key="innerFlight.id"
                              >fa-long-arrow-alt-right</v-icon
                            >
                            {{ innerFlight.arrivalAirport.iata }}
                            <br :key="innerFlight.arrivalAirport.iata" />
                          </template>
                        </strong>
                        {{
                          flight.data.flights[0].takeofftime.getDate() +
                          "/" +
                          (flight.data.flights[
                            flight.data.flights.length - 1
                          ].takeofftime.getMonth() +
                            1) +
                          "/" +
                          flight.data.flights[
                            flight.data.flights.length - 1
                          ].takeofftime.getFullYear()
                        }}
                      </v-card-text>

                      <v-card-actions>
                        <v-list-item class="grow">
                          <v-list-item-avatar color="grey darken-3">
                            <img
                              class="elevation-10"
                              src="@/assets/BasmachAirways-IconOnly.png"
                            />
                          </v-list-item-avatar>
                          <v-spacer></v-spacer>
                          ${{ flight.data.price }}
                        </v-list-item>
                      </v-card-actions>
                    </v-card>
                  </v-row>
                </v-card>

                <v-btn outlined rounded @click="goBackSummery()">חזור</v-btn>
                <v-btn rounded color="primary" @click="e1++">
                  המשך לפרטי הנוסעים
                  <v-icon class="mr-2">fa-arrow-left</v-icon>
                </v-btn>
              </v-stepper-content>

              <v-stepper-content :step="stepperSteps.summeryStepNum + 1">
                <v-card class="mb-5 pa-4">
                  <v-row align="start" justify="start">
                    <v-row class="mt-3 mr-3">
                      <v-alert
                        type="info"
                        color="#26c6da"
                        outlined
                        style="
                          background-color: rgb(255, 255, 255);
                          font-size: large;
                        "
                        class="font-weight-bold"
                      >
                        &nbsp; מלאו את פרטי הנוסע באנגלית בלבד כפי שמופיע בדרכון
                        הנוסע
                        <a @click.stop="passportDialog = true">לחצו לדוגמא</a>
                      </v-alert>
                    </v-row>
                    <v-row
                      class="mt-3 mr-3 mb-3"
                      xs12
                      v-for="(travaler, index) in computedTravelers"
                      :key="index"
                    >
                      <PassengerDetails
                        ref="passangerDetails"
                        :passengerType="travaler.title"
                        :subtitle="travaler.subtitle"
                      />
                    </v-row>
                  </v-row>
                </v-card>

                <v-btn outlined rounded @click="goBack()">חזור</v-btn>
                <v-btn rounded color="primary" @click="goToPayment()">
                  המשך לתשלום
                  <v-icon class="mr-2">fa-arrow-left</v-icon>
                </v-btn>

                <v-dialog v-model="passportDialog" max-width="440">
                  <v-card class="font-weight-bold">
                    <v-card-title
                      class="blue--text text--darken-2"
                      style="font-size: xx-large"
                    >
                      דרכון לדוגמא
                      <v-spacer></v-spacer>
                      <v-btn
                        icon
                        text="text"
                        color="primary"
                        @click="passportDialog = false"
                      >
                        <v-icon>close</v-icon>
                      </v-btn>
                    </v-card-title>
                    <v-img
                      src="https://www.issta.co.il/Resources/Shared/css/images/darkon.jpg"
                    ></v-img>
                  </v-card>
                </v-dialog>
              </v-stepper-content>

              <v-stepper-content :step="stepperSteps.summeryStepNum + 2">
                <v-card class="pa-4 mb-2">
                  <v-row align="start" justify="space-around" row wrap>
                    <v-col cols="5">
                      <v-row>
                        <v-col>
                          <v-alert
                            type="info"
                            class="mb-2 font-weight-bold"
                            style="font-size: x-large"
                            icon="far fa-credit-card"
                            >&nbsp; סך הכל לתשלום: {{ payment }} $</v-alert
                          >
                        </v-col>
                      </v-row>
                      <v-row>
                        <v-col class="mt-3" v-if="user.username == 'אורח'">
                          <v-alert type="error" class="mb-2 text-xs-center">
                            <div class="title font-weight-bold mr-1">
                              שמנו לב שאתה לא רשום
                            </div>
                            <div class="mr-1">
                              משתמשים רשומים לא צריכים למלא פרטי אשראי ויכולים
                              לעקוב אחרי ההזמנות שלהם
                            </div>
                          </v-alert>
                        </v-col>
                      </v-row>
                    </v-col>

                    <v-col cols="5" v-if="!isNewCard">
                      <v-alert
                        type="warning"
                        outlined
                        class="mb-5 font-weight-bold"
                        >הכרטיס איתו נרשמת עומד לשלם על הטיסה</v-alert
                      >
                      <v-btn outlined rounded @click="isNewCard = true"
                        >שלם עם כרטיס אחר</v-btn
                      >
                    </v-col>

                    <v-col cols="5" v-else>
                      <v-form
                        v-model="creditValidation"
                        ref="form"
                        class="mx-5"
                      >
                        <v-card color="grey darken-4" class="pa-3 mb-4">
                          <v-row>
                            <v-text-field
                              label="מספר אשראי"
                              solo
                              style="direction: ltr"
                              type="tel"
                              name="number"
                              v-model="cardDetail.number"
                              :rules="[rules.required, rules.minCreditNum]"
                              minlength="8"
                            ></v-text-field>
                          </v-row>
                          <v-row>
                            <v-text-field
                              label="שם בעל הכרטיס"
                              solo
                              style="direction: rtl"
                              type="text"
                              v-model="cardDetail.name"
                              :rules="[rules.required, rules.name]"
                            ></v-text-field>
                          </v-row>
                          <v-row>
                            <v-col>
                              <v-text-field
                                label="MM YY"
                                solo
                                type="text"
                                name="expiry"
                                v-model="cardDetail.expiry"
                                maxlength="4"
                                :rules="[rules.required, rules.expiry]"
                                style="direction: ltr"
                                reverse
                              ></v-text-field>
                            </v-col>
                            <v-col>
                              <v-text-field
                                label="CVC"
                                solo
                                type="text"
                                name="cvc"
                                v-model="cardDetail.cvc"
                                @focus="back = true"
                                @blur="back = false"
                                :rules="[rules.required, rules.cvc]"
                                reverse
                              ></v-text-field>
                            </v-col>
                          </v-row>
                          <div class="card-wrapper" style="direction: ltr">
                            <card
                              ref="cardRef"
                              v-model="cardDetail"
                              :toInvertCVC="back"
                            ></card>
                          </div>
                        </v-card>
                        <v-btn outlined rounded @click="isNewCard = false"
                          >חזור</v-btn
                        >
                      </v-form>
                      <!-- <CreditCard></CreditCard> -->
                    </v-col>
                  </v-row>
                </v-card>

                <v-btn outlined rounded @click="goBackPayment()">חזור</v-btn>
                <v-btn color="success" rounded @click="completeOrder()"
                  >סיים הזמנה</v-btn
                >
              </v-stepper-content>
            </v-stepper-items>
          </v-stepper>
        </v-col>
      </v-row>

      <v-dialog persistent v-model="dialog3" max-width="700px">
        <v-card style="font-size: x-large" light>
          <v-card-title>
            <strong>
              <span class="ml-2">מספר הזמנה:</span>
              <span>{{ this.confirmationCode }}</span>
            </strong>
            <v-spacer></v-spacer>
          </v-card-title>
          <v-card-text style="direction: ltr">
            <v-row id="codes" justify="end" align="center">
              <v-col
                class="mb-4 text-xs-left"
                v-for="ticketCode in ticketCodes"
                :key="ticketCode.code"
              >
                <strong>
                  <span class="ml-4">{{ ticketCode.name }}</span>
                </strong>
                <br />
                <span class="ml-4">{{ ticketCode.origin }}</span>
                <v-icon class="mx-2">fa-arrow-right</v-icon>
                <span>{{ ticketCode.destination }}</span>
                <br />
                <barcode :code="ticketCode.code" />
              </v-col>
            </v-row>
          </v-card-text>
          <v-card-actions>
            <router-link to="/"
              ><v-btn color="primary" text @click="finishTransaction()"
                >סיימתי</v-btn
              ></router-link
            >

            <v-btn
              color="primary"
              text
              type="button"
              onclick="printJS({
                        printable: 'codes',
                        type: 'html',
                        targetStyles: ['*'],
                      })"
              >הדפס כרטיסים</v-btn
            >
          </v-card-actions>
        </v-card>
      </v-dialog>
    </v-container>
  </div>
</template>

<script>
var moment = require("moment");
import SearchBar from "@/components/SearchBar";
import CreditCard from "@/components/RegisterComponents/CreditCard";
import SearchResults from "@/components/SearchResults";
import PassengerDetails from "@/components/PassengerDetails";
import card from "@/plugins/vue-credit-card/components/Card";
import eventbus from "@/plugins/eventbus";
import Swal from "sweetalert2";
import { setTimeout } from "timers";
import api from "@/api/api";
import print from "print-js";
import Barcode from "../components/Barcode.vue";
import { mapState } from "vuex";

moment.locale("he");

let defaultProps = {
  number: "",
  name: "",
  expiry: "",
  cvc: "",
};

export default {
  name: "reservations",
  components: {
    SearchBar,
    SearchResults,
    card,
    PassengerDetails,
    Barcode,
    CreditCard,
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
      sortDepartingProperty: null,
      sortReturningProperty: null,
      items: ["משך טיול", "מחיר"],
      itemBackground: "lightblue",
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
    planeIcon() {
      return fa - plane - departure;
    },
  },
  methods: {
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
    removeFlight() {
      this.selectedFlights.pop();
    },
    goBack() {
      this.e1--;
    },
    goBackPayment() {
      this.isNewCard = false;
      this.goBack();
    },
    goBackSummery() {
      this.showSearchBar = true;
      this.goBack();
      this.removeFlight();
    },
    goToPayment() {
      let valid = true;
      for (let index = 0; index < this.$refs.passangerDetails.length; index++) {
        this.$refs.passangerDetails[index].$refs.form.validate();

        if (!this.$refs.passangerDetails[index].valid) {
          valid = false;
        }
      }
      if (valid) {
        this.e1++;
      }
    },
    finishTransaction() {
      this.dialog3 = false;
      window.location.replace("/history");
    },
    chooseLeavingFlight(item) {
      this.e1 = 2;
      this.selectedFlights.push({
        desc: "הלוך",
        data: item,
      });

      if (!this.isTwoWay) {
        this.showSearchBar = false;
      }
    },
    chooseReturningFlight(item) {
      this.e1 = 3;
      this.showSearchBar = false;
      const returning = "חזור";
      const returningFlightIndex = this.selectedFlights.findIndex(
        (flight) => flight.desc === returning
      );
      if (returningFlightIndex !== -1) {
        this.selectedFlights = this.selectedFlights.splice(
          returningFlightIndex - 1
        );
      }

      this.selectedFlights.push({
        desc: returning,
        data: item,
      });
    },
    async completeOrder() {
      if (!this.isNewCard || this.$refs.form.validate()) {
        this.ticketCodes = [];
        let reservations = [];
        this.confirmationCode = Math.floor(100000 + Math.random() * 900000);

        // For each passenger
        for (let passengerIndex in this.$refs.passangerDetails) {
          // For each direction
          for (let flightsDirectionIndex in this.selectedFlights) {
            // For each flight
            for (let flightIndex in this.selectedFlights[flightsDirectionIndex]
              .data.flights) {
              const ticketCode = this.generateConfirmationCode();
              const [day, month, year] =
                this.$refs.passangerDetails[
                  passengerIndex
                ].computedDateFormatted.split("/");

              let reservation = {
                userId: this.user.id,
                flightId:
                  this.selectedFlights[flightsDirectionIndex].data.flights[
                    flightIndex
                  ].id,
                passportId:
                  this.$refs.passangerDetails[passengerIndex].passport,
                orderConfirmation: this.confirmationCode,
                payment: this.payment,
                ticketCode: ticketCode,
                passengerName: this.$refs.passangerDetails[passengerIndex].name,
                passengerSex:
                  this.$refs.passangerDetails[passengerIndex].sex == "זכר"
                    ? "M"
                    : "F",
                passengerBirthDate: `${year}-${month}-${day}`,
                isSeatTaken:
                  this.$refs.passangerDetails[passengerIndex].isSeatTaken,
              };

              reservations.push(reservation);

              let ticket = {
                code: ticketCode,
                name: this.$refs.passangerDetails[passengerIndex].name,
                origin:
                  this.selectedFlights[flightsDirectionIndex].data.flights[
                    flightIndex
                  ].takeoffAirport.iata,
                destination:
                  this.selectedFlights[flightsDirectionIndex].data.flights[
                    flightIndex
                  ].arrivalAirport.iata,
              };

              this.ticketCodes.push(ticket);
            }
          }
        }

        eventbus.$emit("startloading");
        try {
          await api.Reservations().addReservation(reservations);
          setTimeout(() => {
            eventbus.$emit("stoploading");
            this.dialog3 = true;
          }, 1000);
        } catch (error) {
          eventbus.$emit("stoploading");
          Swal.fire({
            type: "error",
            title: "מצטערים, משהו לא עבד",
            showConfirmButton: false,
            timer: 2000,
          });
        }
      }
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
