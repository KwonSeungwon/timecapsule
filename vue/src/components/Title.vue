<template>
  <div class="title">
    <span @click="reloadPage"></span>
  </div>
  <div class="menu">
    <div class="holder">
      <button id="tutorial" class="tutorial" @click="movePage(false)"></button>
      <label for="tutorial"></label>
    </div>
    <div class="holder">
      <button id="my" class="my" @click="$router.push('/login')"></button>
      <label for="my"></label>
    </div>
    <div class="holder">
      <button id="send" class="send" @click="movePage(true)"></button>
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

    },
    reloadPage () {
      alert('홈으로 돌아가기 구현');
    }
  }
}
</script>

<style scoped>
.title {
  padding-top: 100px;
}
.title span {
  margin: 0 auto;
  width: 250px;
  height: 96px;
  cursor: pointer;
  background: url(../assets/images/title2.png) no-repeat center transparent;
  background-size: cover;
}


.title .capsule {
  background: url(../assets/images/pills.png) no-repeat center transparent;
  background-size: cover;
}

.title,
.menu {
  display: flex;
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
.menu .tutorial {
  width: 230px;
  background: url(../assets/images/btn_tutorial.png) no-repeat center transparent;
}
.svg .menu .tutorial {
  width: 230px;
  background: url(../assets/images/btn_tutorial.svg) no-repeat center transparent;
}

.menu .my {
  width: 180px;
  background: url(../assets/images/btn-my.png) no-repeat center transparent;
}
.menu .send {
  width: 180px;
  background: url(../assets/images/btn-send.png) no-repeat center transparent;
}

.svg .menu .start {
  width: 180px;
  background: url(../assets/images/btn_start.svg) no-repeat center transparent;
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
.menu button#my:hover + label,
.menu button#send:hover + label,
.menu button#tutorial:hover + label {
  display: inline-block;
}
</style>