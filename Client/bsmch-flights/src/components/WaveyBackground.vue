<template>
  <div class="containerWave">
    <svg viewBox="0 0 500 500" preserveAspectRatio="xMinYMin meet">
      <path
        d="M521 221C364 121 164 321-4 221L-4 507 521 507Z"
        style="stroke: white; stroke-width: 2px; fill: rgb(66, 66, 66, 0.95)"
      ></path>
    </svg>
      <transition name="fade"><router-view /></transition>
  </div>
</template>

<style scoped>
svg {
  display: inline-block;
  position: absolute;
  top: 0;
  left: 0;
}

.containerWave {
  width: 100%;
  height: 100%;
  vertical-align: middle;
  overflow: hidden;
  position: relative;
  background-image: url("../assets/background.jpg");
  background-size: 100vw 100vh;
  background-position: top;
}

.fade-enter-active, .fade-leave-active {
  transition-property: opacity;
  transition-duration: .7s;
}

.fade-enter-active {
  transition-delay: .7s;
}

.fade-enter, .fade-leave-active {
  opacity: 0
}
</style>
