<template>
  <HomeView />
</template>

<script>
import HomeView from "@/views/HomeView";
import './assets/css/timecapsulue.css';

export default {
  components: {HomeView}
}
</script>

<style>
@font-face {
  font-family: 'tcf';
  src: url('./assets/font/DungGeunMo.woff') format('woff');
}

body {
  margin: 0;
  padding: 0;
  background-color: #000;
  font-family: 'tcf', sans-serif;
  -webkit-font-smoothing: none;
  -moz-osx-font-smoothing: none;
  image-rendering: pixelated;
}

#app {
  width: 100%;
  height: 100vh;
}

/* Global Transition */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}
</style>