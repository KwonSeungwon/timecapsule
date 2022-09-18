<template>
  <HomeView>

  </HomeView>
</template>

<style>
.title {
  height: 15%;
}
.content {
  height: 70%;
}
.footer {
  height: 15%;
  text-align: center;
}
.footer button.btn:not(:first-child) {
  margin-left: 10px;
}
.title p {
  margin: 0;
  padding: 30px;
  color: white;
  text-shadow: 1px 1px #315fd3, -1px -1px #315fd3, 1px -1px #315fd3, -1px 1px #315fd3;
  font-size: 20px;
}
@font-face {
  font-family: tcf;
  src: url(./assets/font/DungGeunMo.woff) format('woff');
}
#app {
  font-family: tcf, sans-serif;
  overflow-x: hidden;
}
</style>
<script>
import HomeView from "@/views/HomeView";
export default {
  components: {HomeView}
}
</script>