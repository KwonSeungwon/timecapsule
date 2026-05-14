<template>
  <div class="home-container">
    <div class="title">
      <span @click="$router.go(0)"></span>
      <i class="capsule"></i>
    </div>
    <div class="menu">
      <div class="holder">
        <button id="send" class="send" @click="$router.push('/write')"></button>
        <label for="send"></label>
      </div>
      <div class="holder">
        <button id="my" class="my" @click="$router.push('/find')"></button>
        <label for="my"></label>
      </div>
      <div class="holder">
        <button id="tutorial" class="tutorial" @click="$router.push('/tutorial')"></button>
        <label for="tutorial"></label>
      </div>
    </div>
    <div class="footer-copyright">
      <img src="../assets/images/copyright.png">
    </div>
  </div>
</template>

<script>
export default {
  name: "cTitle"
}
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  justify-content: space-between;
  padding-bottom: 20px;
}

.title,
.menu {
  display: flex;
}

.title {
  padding-top: 80px;
  position: relative;
  justify-content: center;
}
.title span {
  width: 250px;
  height: 86px;
  cursor: pointer;
}

.title .capsule {
  position: absolute;
  top: 35px;
  right: 20%;
  width: 80px;
  height: 80px;
}

/* Biome-specific assets */
.beach .title span { background: url(../assets/images/title_bc.png) no-repeat center; background-size: contain; }
.mountain .title span { background: url(../assets/images/title_mountain.png) no-repeat center; background-size: contain; }
.desert .title span { background: url(../assets/images/title_dt.png) no-repeat center; background-size: contain; }
.space .title span { background: url(../assets/images/title_sc.png) no-repeat center; background-size: contain; }

.beach .title .capsule { background: url(../assets/images/pills.png) no-repeat center; background-size: contain; }
.mountain .title .capsule { background: url(../assets/images/pills_mt.png) no-repeat center; background-size: contain; }
.desert .title .capsule { background: url(../assets/images/pills_dt.png) no-repeat center; background-size: contain; }
.space .title .capsule { background: url(../assets/images/pills_sc.png) no-repeat center; background-size: contain; }

.menu {
  margin-top: 40px;
  flex-direction: column;
  align-items: center;
}

.menu div.holder {
  position: relative;
  margin: 15px auto;
}

.holder button {
  border: 0;
  height: 40px;
  cursor: pointer;
  background-size: contain;
  background-repeat: no-repeat;
  transition: transform 0.1s;
}

.holder button:active {
  transform: scale(0.95);
}

.beach .menu .tutorial { width: 200px; background-image: url(../assets/images/btn_tutorial.png); }
.mountain .menu .tutorial { width: 200px; background-image: url(../assets/images/btn_tutorial_mt.png); }
.desert .menu .tutorial { width: 200px; background-image: url(../assets/images/btn_tutorial_dt.png); }
.space .menu .tutorial { width: 200px; background-image: url(../assets/images/btn_tutorial_sc.png); }

.beach .menu .my { width: 180px; background-image: url(../assets/images/btn-my.png); }
.mountain .menu .my { width: 180px; background-image: url(../assets/images/btn_my_mt.png); }
.desert .menu .my { width: 180px; background-image: url(../assets/images/btn_my_dt.png); }
.space .menu .my { width: 180px; background-image: url(../assets/images/btn_my_sc.png); }

.beach .menu .send { width: 180px; background-image: url(../assets/images/btn-send.png); }
.mountain .menu .send { width: 180px; background-image: url(../assets/images/btn_start_mt.png); }
.desert .menu .send { width: 180px; background-image: url(../assets/images/btn_start_dt.png); }
.space .menu .send { width: 180px; background-image: url(../assets/images/btn_start_sc.png); }

.menu label {
  position: absolute;
  display: none;
  left: -35px;
  top: 8px;
  width: 25px;
  height: 25px;
  background: url(../assets/images/arrow.png) no-repeat center;
  background-size: contain;
}

.menu div.holder:hover label {
  display: block;
}

.footer-copyright {
  text-align: center;
  width: 100%;
}

.footer-copyright img {
  width: 150px;
}

@media (max-height: 700px) {
  .title { padding-top: 40px; }
  .menu { margin-top: 20px; }
  .menu div.holder { margin: 10px auto; }
}
</style>