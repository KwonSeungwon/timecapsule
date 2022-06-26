<template>
<!--  <nav>-->
<!--    <router-link to="/">Home</router-link> |-->
<!--    <router-link to="/about">About</router-link>-->
<!--  </nav>-->
  <HomeView>

  </HomeView>
</template>

<style>
@font-face {
  font-family: tcf;
  src: url(./assets/font/DungGeunMo.woff) format('woff');
}
#app {
  font-family: tcf, sans-serif;
}
</style>
<script>
import HomeView from "@/views/HomeView";
export default {
  components: {HomeView}
}
</script>