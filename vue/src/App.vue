<template>
  <HomeView>

  </HomeView>
</template>

<style>
button,
p,
label {
  color: white;
  font-size: 14px;
  text-shadow: 1px 1px #315fd3, -1px -1px #315fd3, 1px -1px #315fd3, -1px 1px #315fd3;
}

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
  font-size: 22px;
}

.btn {
  width: 104px;
  height: 41px;
  border: 0;
  background: url(./assets/images/field_abled.png) no-repeat center transparent;
  background-size: cover;
}

.input-field label {
  display: inline-block;
  width: 112px;
  height: 37px;
  text-align: center;
  line-height: 37px;
}

.beach .input-field label {
  background: url(./assets/images/beach/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.desert .input-field label {
  background: url(./assets/images/desert/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .input-field label {
  width: 124px;
  background: url(./assets/images/mountain/input_title.png) no-repeat center transparent;
  background-size: cover;
}

.space .input-field label {
  background: url(./assets/images/beach/input_title.png) no-repeat center transparent;
  background-size: cover;
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