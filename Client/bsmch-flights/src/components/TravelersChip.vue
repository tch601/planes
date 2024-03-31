<template>
  <div class="text-xs-center">
    <v-menu :close-on-content-click="false" offset-y>
      <template v-slot:activator="{ on }">
        <v-chip v-on="on" :class="getTheme">
          {{value}}&nbsp; {{ $t('נוסעים') }}&nbsp;
          <v-icon>fa-chevron-down</v-icon>
        </v-chip>
      </template>
      <v-list>
        <v-list-item v-for="(item, index) in items" :key="index">
          <div style="width: 100%">
            <v-list-item-title>{{ item.title }}</v-list-item-title>
            <v-list-item-sub-title>{{ item.subtitle }}</v-list-item-sub-title>
          </div>
          <v-icon
            class="pointer"
            style="margin-right: 15%"
            @click="
              value--;
              item.amount--;
              $emit('travelers', items);
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
              $emit('travelers', items);
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
      value: 1,
      items: [
        {
          title: "מבוגרים",
          subtitle: "",
          amount: 1,
          plus_color: "blue lighten-2",
          minus_color: "blue lighten-2",
        },
        {
          title: "ילדים",
          subtitle: "בני 2-12",
          amount: 0,
          plus_color: "blue lighten-2",
          minus_color: "grey",
        },
        {
          title: "תינוקות",
          subtitle: "במושב",
          amount: 0,
          plus_color: "blue lighten-2",
          minus_color: "grey",
        },
        {
          title: "תינוקות",
          subtitle: "ללא מושב",
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
          } else {
            item.minus_color = "blue lighten-2";
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
