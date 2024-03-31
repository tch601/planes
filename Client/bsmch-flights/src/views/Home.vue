<template>
  <div>
    <reservations></reservations>
    <v-container class="pt-0" v-if="showMapAndRecommendations">
      <v-row
        align="center"
        justify="space-around"
        row
        fill-height
        wrap
        class="layout"
      >
        <v-col xs6 class="pa-4">
          <v-card ripple class="card map-card" elevation="24" to="/map">
            <img src="@/assets/HomePageMap.png" style="width: 100%" />
          </v-card>
        </v-col>
        <v-col>
          <v-card>
            <v-row justify="center">
              <v-scroll-x-transition hide-on-leave group>
                <v-col
                  v-for="recommendation in recommendationInOnePage"
                  :key="recommendation.username"
                >
                  <Recommendation
                    :username="recommendation.username"
                    :img="recommendation.img"
                    :text="recommendation.content"
                    :rating="recommendation.rating"
                  ></Recommendation>
                </v-col>
              </v-scroll-x-transition>
            </v-row>

            <div v-if="recommendations.length > 6" class="text-xs-center pb-1">
              <v-pagination
                class="mb-2"
                v-model="page"
                :length="pageLength"
                circle
                light
                color="warning"
              ></v-pagination>
              <v-dialog
                v-if="currentUser.permission.id !== 3"
                v-model="dialog"
                persistent
                max-width="600px"
              >
                <template v-slot:activator="{ on, attrs }">
                  <v-btn
                    small
                    class="mx-1"
                    fab
                    dark
                    color="indigo"
                    v-bind="attrs"
                    v-on="on"
                  >
                    <v-icon dark> fas fa-plus </v-icon>
                  </v-btn>
                </template>
                <v-form v-model="valid" ref="form">
                  <v-card>
                    <v-card-title>
                      <span class="text-h5">תגובה חדשה</span>
                    </v-card-title>
                    <v-card-text>
                      <v-container>
                        <v-row>
                          <v-col cols="3">
                            <v-text-field
                              :rules="[rules.required, rules.rating]"
                              label="דירוג"
                              type="number"
                              required
                              v-model="newRecommendation.rating"
                            ></v-text-field>
                          </v-col>
                          <v-col cols="12">
                            <v-text-field
                              :rules="[rules.required, rules.content]"
                              label="תוכן תגובה"
                              required
                              v-model="newRecommendation.content"
                            ></v-text-field>
                          </v-col>
                        </v-row>
                      </v-container>
                    </v-card-text>
                    <v-card-actions>
                      <v-spacer></v-spacer>
                      <v-btn color="green darken-1" text @click="addComment">
                        הוספה
                      </v-btn>
                      <v-btn color="red darken-1" text @click="dialog = false">
                        ביטול
                      </v-btn>
                    </v-card-actions>
                  </v-card>
                </v-form>
              </v-dialog>
            </div>
          </v-card>
        </v-col>
      </v-row>
    </v-container>
  </div>
</template>

<script>
import Recommendation from '@/components/RecommendationCard';
import SearchBar from '../components/SearchBar.vue';
import Reservations from './Reservations.vue';
import eventbus from '@/plugins/eventbus';
import api from '@/api/api';
import { mapState } from 'vuex';

export default {
  name: 'home',
  components: {
    Recommendation,
    SearchBar,
    Reservations,
  },
  watch: {
    jwt() {
      this.changeCurrentUser();
    },
  },
  mounted() {
    eventbus.$on('search', (searchResults, isTwoWay) => {
      this.showMapAndRecommendations = false;
    });
  },
  async created() {
    this.getRecommendations();
    this.currentUser = await api.Users().getUser(this.jwt);
  },
  data() {
    return {
      showMapAndRecommendations: true,
      page: 1,
      recommendationsPerPage: 3,
      recommendations: [],
      dialog: false,
      newRecommendation: {},
      rules: {
        required: (value) => !!value || 'Required',
        rating: (value) => {
          const pattern = /^(?:[0-4](\.\d)?|5(\.0)?)$/;
          return pattern.test(value) || 'Invalid rating';
        },
        content: (value) => {
          const pattern = /^.{1,300}$/;
          return pattern.test(value) || 'Invalid content';
        },
      },
      valid: true,
      currentUser: {},
    };
  },
  computed: {
    ...mapState(['jwt']),
    recommendationInOnePage() {
      return this.recommendations.slice(
        (this.page - 1) * this.recommendationsPerPage,
        this.page * this.recommendationsPerPage
      );
    },
    pageLength() {
      let value = parseInt(
        this.recommendations.length / this.recommendationsPerPage
      );

      if (this.recommendations.length % this.recommendationsPerPage != 0) {
        value++;
      }

      return value;
    },
  },
  methods: {
    async addComment() {
      if (this.$refs.form.validate()) {
        this.newRecommendation.username = this.currentUser.username;
        this.newRecommendation.img = this.currentUser.image;
        await api.Recommendations().addRecommendation(this.newRecommendation);
        this.getRecommendations();
        this.dialog = false;
      }
    },
    async changeCurrentUser() {
      this.currentUser = await api.Users().getUser(this.jwt);
    },
    async getRecommendations() {
      this.recommendations = await api
        .Recommendations()
        .getAllRecommendations();
    },
  },
};
</script>

<style scoped>
.layout {
  min-height: 50vh;
}

.card {
  border-radius: 8px;
  cursor: pointer;
}

.card:hover {
  box-shadow: 0 4px 8px 0 rgba(0, 0, 0, 0.2), 0 6px 20px 0 rgba(0, 0, 0, 0.19) !important;
}

.slide-fade-enter-active {
  transition: all 1s ease;
}

.slide-fade-leave-active {
  transition: all 0s ease;
}

.slide-fade-enter,
.slide-fade-leave-to,
.slide-fade-leave-active below {
  transform: translateX(10px);
  opacity: 0;
}
</style>
