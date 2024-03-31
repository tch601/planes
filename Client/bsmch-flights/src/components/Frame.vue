<template>
  <div>
    <v-app id="inspire">
      <v-app-bar app fixed clipped-right>
        <v-app-bar-nav-icon @click.stop="mini = !mini"></v-app-bar-nav-icon>
        <v-app-bar-title>
          <img
            src="@/assets/BasmachAirways-Transparent.png"
            style="height: 55px; margin-top: 5px"
          />
        </v-app-bar-title>
        <p id="permissions">גישת {{ user.permission.name }}</p>
        <v-spacer></v-spacer>
        <v-row justify="end" align="center">
          <v-dialog v-model="dialog" max-width="600px" dark>
            <template v-slot:activator="{ on: dialog }">
              <div v-if="user.username === 'אורח'">
                <v-tooltip bottom>
                  <template v-slot:activator="{ on: tooltip }">
                    <v-btn
                      text
                      target="_blank"
                      v-on="{ ...tooltip, ...dialog }"
                    >
                      <v-icon>fas fa-user-edit</v-icon>
                    </v-btn>
                  </template>
                  <span>התחברות / הרשמה</span>
                </v-tooltip>
              </div>

              <div v-else>
                <v-tooltip bottom>
                  <template v-slot:activator="{ on: tooltip }">
                    <v-btn
                      text
                      v-on="{ ...tooltip, ...dialog }"
                      @click="sendPropsToDialog()"
                    >
                      <v-icon>fas fa-edit</v-icon>
                    </v-btn>
                  </template>
                  <span>שנה פרטים אישיים</span>
                </v-tooltip>

                <!-- <v-btn text target="_blank">
                    <v-icon>fas fa-edit</v-icon>
                  </v-btn> -->
                <v-tooltip bottom>
                  <template v-slot:activator="{ on: tooltip }">
                    <v-btn text @click="logOut()" v-on="{ ...tooltip }">
                      <v-icon>fas fa-sign-out-alt</v-icon>
                    </v-btn>
                  </template>
                  <span>התנתק</span>
                </v-tooltip>
              </div>
            </template>
            <MyDialog :key="dialogKey" :changeInfo="dialogProps.changeInfo" />
          </v-dialog>
          <v-col cols="2">
            <v-select
              label="בחר שפה"
              :items="languages"
              variant="outlined"
              v-model="currentLanguege"
              hide-details
            ></v-select>
          </v-col>
        </v-row>
      </v-app-bar>

      <v-navigation-drawer
        :mini-variant="mini"
        app
        right
        clipped
        hide-overlay
        fixed
      >
        <v-list dense>
          <v-list-item class="pa-2">
            <v-list-item-avatar v-if="user.username != 'אורח'">
              <img :src="user.image" />
            </v-list-item-avatar>
            <v-list-item-avatar v-else>
              <img src="@/assets/userProfile.jpg" />
            </v-list-item-avatar>
            <v-list-item-content>
              <v-list-item-title>{{ user.username }}</v-list-item-title>
            </v-list-item-content>

            <v-list-item-action>
              <v-btn icon @click.stop="mini = !mini">
                <v-icon>chevron_right</v-icon>
              </v-btn>
            </v-list-item-action>
          </v-list-item>
        </v-list>

        <v-divider light></v-divider>

        <v-list dense>
          <v-list-item v-for="route in notAdminRoutes" :key="route.name">
            <router-link :to="route.path">
              <v-list-item-icon>
                <v-icon
                  @click="
                    isSamePath(route.path);
                    cloudsStart();
                  "
                  >{{ route.icon }}</v-icon
                >
              </v-list-item-icon>
            </router-link>
            <v-list-item-content>
              <v-list-item-title>{{ route.title }}</v-list-item-title>
            </v-list-item-content>
          </v-list-item>
        </v-list>

        <div id="theme">
          <v-list dense>
            <v-list-item>
              <v-icon class="ml-7" @click="toggleTheme()">{{ getIcon }}</v-icon>
              <v-list-item-content>
                <v-list-item-title>עריכת נושא</v-list-item-title>
              </v-list-item-content>
            </v-list-item>
          </v-list>
        </div>
      </v-navigation-drawer>

      <v-dialog v-model="hogwarts" max-width="480">
        <v-card title="dialog">
          <div class="popup-question">
            <img src="../assets/hogwarts.png" />
          </div>
          <div class="popup-question">
            <p>!Welcome to Hogwarts</p>
          </div>
          <v-card-actions>
            <v-spacer></v-spacer>
            <div class="d-flex justify-center" style="width: 100%">
              <v-btn class="ml-3" variant="text" @click="answerHogwarts('yes')"
                >enter</v-btn
              >
              <v-btn variant="text" @click="answerHogwarts('no')">leave</v-btn>
            </div>
          </v-card-actions>
        </v-card>
      </v-dialog>

      <v-main>
        <WaveyBackground />
      </v-main>

      <v-footer app fixed>
        <span>&copy; Flights Team</span>
      </v-footer>
    </v-app>
    <Loading />
  </div>
</template>

<script>
import { mapState, mapActions } from 'vuex';
import MyDialog from '@/components/MyDialog';
import eventbus from '@/plugins/eventbus';
import WaveyBackground from '@/components/WaveyBackground';
import Loading from '@/components/Loading';
import Axios from '../api/api';
import Swal from 'sweetalert2';

export default {
  watch: {
    jwt() {
      this.initUser();
    },
    currentLanguege(newLanguege) {
      this.$i18n.locale = this.languagesKeys[`${newLanguege}`];
    },
  },
  components: {
    MyDialog,
    WaveyBackground,
    Loading,
  },
  created() {
    eventbus.$on('closedialog', (reRender) => {
      this.dialog = false;
      if (reRender) {
        this.dialogKey = Math.random();
      }
    });
    this.initUser();
    window.addEventListener("input", (event) => {
      if (event.target.value === "נס ציונה") {
        Swal.fire({
          type: "error",
          title: "אופס",
          text: "מצטערים המיקום שחיפשת אינו קיים",
        });
      } else if (event.target.value === "פתח תקווה") {
        Swal.fire({
          type: "info",

          title: "מאוד אמיץ מצידך",
        });
      } else if (event.target.value === "adam the king") {
        this.$confetti.start();
      } else {
        if (event.target.value === "fire boy water girl") {
          window
            .open(
              "https://www.friv.com/z/games/fireboyandwatergirlforest/game.html",
              "_blank"
            )
            .focus();
        } else if (event.target.value === "hogwarts") {
          this.hogwarts = true;
        }
        this.$confetti.stop();
      }
    });
  },
  data: () => ({
    dialogKey: Math.random(),
    dialog: false,
    drawer: true,
    mini: true,
    dialogProps: {},
    items: [
      { title: 'הזמנת טיסות', icon: 'flight', route: 'reservations' },
      { title: 'צפייה במפה', icon: 'fa-map-marked-alt', route: 'map' },
    ],
    user: {},
    hogwarts: false,
    languages: ['עברית', 'English'],
    languagesKeys: { עברית: 'HE', English: 'EN' },
    currentLanguege: 'עברית',
  }),
  computed: {
    ...mapState(["jwt"]),
    guestRoutes() {
      if (this.user.username === "אורח") {
        return [...this.$router.options.routes].filter(
          (route) => route.path !== '/history'
        );
      }
      return this.$router.options.routes;
    },
    notAdminRoutes() {
      if (this.user.permissions.name !== "מנהל") {
        return [...this.guestRoutes].filter((route) => route.path !== "/edit");
      }

      return this.guestRoutes;
    },
    getIcon() {
      return this.$vuetify.theme.dark ? "fas fa-sun" : "fas fa-moon";
    },
  },
  methods: {
    sendPropsToDialog() {
      this.dialogProps = {
        changeInfo: true,
      };
    },
    toggleTheme() {
      this.$vuetify.theme.dark = !this.$vuetify.theme.dark;
    },
    answerHogwarts(answer) {
      if (answer === "yes") {
        window.open("https://www.wizardingworld.com/", "_blank").focus();
      } else {
        Swal.fire({
          type: "info",
          title: "Bye bye",
          text: "Come visit us some time ok?",
        });
      }

      this.hogwarts = false;
    },
    cloudsStart() {
      this.$confetti.start({
        particles: [
          {
            type: "image",
            url: "https://www.nicepng.com/png/full/410-4103159_single-sparkle-png.png",
          },
        ],
        defaultSize: 10,
        defaultDropRate: 10,
        windSpeedMax: 1,
        particlesPerFrame: 2,
      });

      const TIMEOUT = 1500;

      setTimeout(() => {
        this.$confetti.stop();
      }, TIMEOUT);
    },
    logOut() {
      this.$store.dispatch("defaultJWT");
      this.$router.push("/");
      location.reload();
    },
    clickRouter(newRoute) {
      if (this.$route.path === newRoute) {
        this.$router.go(newRoute);
      }

      this.$router.push(newRoute);
    },
    async initUser() {
      this.user = await Axios.Users().getUser(this.jwt);
      if (this.user.id === -1) {
        Swal.fire({
          type: 'error',
          title: 'אופס',
          text: 'נראה שצריך שתתחבר שוב',
        });
      }
    },
    isSamePath(newRoute) {
      if (this.$route.path === newRoute) {
        this.$router.go(newRoute);
      }
    },
    guestInterface() {
      if (user.username === 'אורח') {
        for (
          routeIndex = 0;
          routeIndex < this.$router.options.routes.length;
          routeIndex++
        ) {
          if (route.path === '/history') {
            this.$router.options.routes.splice(routeIndex, 1);
          }
        }
      }
      return this.$router.options.routes;
    },
  },
};
</script>

<style scoped>
.v-app-bar {
  padding-right: 0px !important;
}

.v-footer {
  padding-right: 2%;
}

#permissions {
  margin: 0px 10px;
  text-align: center;
  font-size: 1.75em;
}
#theme {
  position: absolute;
  bottom: 0;
}
.popup-question {
  padding-top: 10px;
  width: 100%;
  text-align: center;
  font-size: 2em;
}
.title {
  font-family: "Assistant" !important;
}
</style>
