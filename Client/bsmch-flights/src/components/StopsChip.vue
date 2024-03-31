<template>
  <div class="text-xs-center">
    <v-menu :close-on-content-click="false" offset-y>
      <template v-slot:activator="{ on }">
        <v-chip v-on="on" :class="getTheme">
          {{ value }}&nbsp; {{ $t('עצירות') }}&nbsp;
          <v-icon>fa-chevron-down</v-icon>
        </v-chip>
      </template>
      <v-list>
        <v-list-item v-for="(item, index) in items" :key="index">
          <div style="width: 100%">
            <v-list-item-title>{{ item.title }}</v-list-item-title>
          </div>
          <v-icon
            class="pointer"
            style="margin-right: 15%"
            @click="
              value--;
              item.amount--;
              $emit('stops', item.amount);
            "
            :color="item.minus_color"
            @mouseover="item.minus_color = 'blue'"
            @mouseleave="item.minus_color = 'blue lighten-2'"
            >fa-minus-square</v-icon
          >
          <label style="margin: 5%">{{ item.amount }}</label>
          <v-icon
            class="pointer"
            @click="
              value++;
              item.amount++;
              $emit('stops', item.amount);
            "
            :color="item.plus_color"
            @mouseover="item.plus_color = 'blue'"
            @mouseleave="item.plus_color = 'blue lighten-2'"
            >fa-plus-square</v-icon
          >
        </v-list-item>
      </v-list>
    </v-menu>
  </div>
</template>

<script>
export default {
  data() {
    return {
      value: 0,
      items: [
        {
          title: "עצירות",
          amount: 0,
          plus_color: "blue lighten-2",
          minus_color: "grey",
        },
      ],
    };
  },
  watch: {
    items: {
      handler: function (after, before) {
        before.forEach((item) => {
          if (item.amount <= 0) {
            item.minus_color = "grey";
            item.plus_color = "blue lighten-2";
          } else if (item.amount >= 3) {
            item.plus_color = "grey";
            item.minus_color = "blue lighten-2";
          } else {
            item.minus_color = "blue lighten-2";
            item.plus_color = "blue lighten-2";
          }
        });
      },
      deep: true,
    },
  },
  computed: {
    getTheme() {
      return this.$vuetify.theme.dark ? "dark" : "light";
    }
  }
};
</script>

<style scoped>
.v-chip {
  background: #C4C4C4 !important;
}
.v-chip.dark {
  background: #555 !important;
}
.pointer {
  cursor: pointer;
}

.grey--text {
  cursor: not-allowed !important;
  pointer-events: none;
}

.v-chip__content {
  cursor: pointer;
}

.v-menu__content {
  width: 15%;
  border-radius: 10px;
}

.fa-chevron-down,
.fa-chevron-up {
  transform: scale(0.6);
}
</style>
