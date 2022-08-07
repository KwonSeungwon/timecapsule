<template>
  <div class="title">
    <span @click="$router.go(0)"></span>
    <i class="capsule"></i>
  </div>
  <div class="menu">
    <div class="holder">
      <button id="tutorial" class="tutorial" @click="$router.push('/tutorial')"></button>
      <label for="tutorial"></label>
    </div>
    <div class="holder">
      <button id="my" class="my" @click="$router.push('/login')"></button>
      <label for="my"></label>
    </div>
    <div class="holder">
      <button id="send" class="send" @click="$router.push('/target')"></button>
      <label for="send"></label>
    </div>
    <div class="holder" style="margin-top: 105%">
      <img src="../assets/images/copyright.png">
    </div>
  </div>
</template>

<script>
export default {
  name: "cTitle",
  methods : {
    login () {

    }
  }
}
</script>

<style scoped>

.title,
.menu {
  display: flex;
}

.title {
  padding-top: 100px;
  position: relative;
}
.title span {
  margin: 0 auto;
  width: 250px;
  height: 86px;
  cursor: pointer;
}

.title .capsule {
  position: absolute;
  top: 55px;
  right: 55px;
  width: 80px;
  height: 80px;
}

.beach .title span {
  background: url(../assets/images/title_bc.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .title span {
  background: url(../assets/images/title_mountain.png) no-repeat center transparent;
  background-size: cover;
}

.desert .title span {
  background: url(../assets/images/title_dt.png) no-repeat center transparent;
  background-size: cover;
}

.space .title span {
  background: url(../assets/images/title_sc.png) no-repeat center transparent;
  background-size: cover;
}

.beach .title .capsule {
  background: url(../assets/images/pills.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .title .capsule {
  background: url(../assets/images/pills_mt.png) no-repeat center transparent;
  background-size: cover;
}

.desert .title .capsule {
  background: url(../assets/images/pills_dt.png) no-repeat center transparent;
  background-size: cover;
}

.space .title .capsule {
  background: url(../assets/images/pills_sc.png) no-repeat center transparent;
  background-size: cover;
}



.menu {
  margin-top: 25px;
  flex-direction: column;
}

.menu div.holder {
  position: relative;
  margin: 5px auto;
}

.holder button {
  border: 0;
  height: 25px;
}

.beach .menu .tutorial {
  width: 230px;
  background: url(../assets/images/btn_tutorial.png) no-repeat center transparent;
}

.mountain .menu .tutorial {
  width: 230px;
  background: url(../assets/images/btn_tutorial_mt.png) no-repeat center transparent;
}
.desert .menu .tutorial {
  width: 230px;
  background: url(../assets/images/btn_tutorial_dt.png) no-repeat center transparent;
}

.space .menu .tutorial {
  width: 230px;
  background: url(../assets/images/btn_tutorial_sc.png) no-repeat center transparent;
}

.beach .menu .my {
  width: 180px;
  background: url(../assets/images/btn-my.png) no-repeat center transparent;
}

.mountain .menu .my {
  width: 180px;
  background: url(../assets/images/btn_my_mt.png) no-repeat center transparent;
}

.desert .menu .my {
  width: 180px;
  background: url(../assets/images/btn_my_dt.png) no-repeat center transparent;
}

.space .menu .my {
  width: 180px;
  background: url(../assets/images/btn_my_sc.png) no-repeat center transparent;
}

.beach .menu .send {
  width: 180px;
  background: url(../assets/images/btn-send.png) no-repeat center transparent;
}

.mountain .menu .send {
  width: 180px;
  background: url(../assets/images/btn_start_mt.png) no-repeat center transparent;
}

.desert .menu .send {
  width: 180px;
  background: url(../assets/images/btn_start_dt.png) no-repeat center transparent;
}

.space .menu .send {
  width: 180px;
  background: url(../assets/images/btn_start_sc.png) no-repeat center transparent;
}

.menu label[for='my'],
.menu label[for='send'],
.menu label[for='tutorial'] {
  position: absolute;
  display: none;
  left: 0;
  width: 25px;
  height: 25px;
  margin: 0;
  background: url(../assets/images/arrow.png) no-repeat;
}

.space menu label[for='my'],
.space menu label[for='send'],
.space menu label[for='tutorial'] {
  background: url(../assets/images/arrow_sc.png) no-repeat;
}

.menu button#my:hover + label,
.menu button#send:hover + label,
.menu button#tutorial:hover + label {
  display: inline-block;
}
</style>