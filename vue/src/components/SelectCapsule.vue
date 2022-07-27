<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 선택</p>
    </div>
    <div class="selectField">
      <button class="capsule" @click="selectCapsule(1)"></button>
      <span class="content">편지를 품은 병</span>
    </div>
    <div class="selectField">
      <button class="capsule" @click="selectCapsule(2)"></button>
      <span class="content">뚜껑 달린 죽통</span>
    </div>
    <div class="selectField">
      <button class="capsule" @click="selectCapsule(3)"></button>
      <span class="content">달걀형 타임캡슐</span>
    </div>
    <div class="selectField">
      <button class="capsule" @click="selectCapsule(4)"></button>
      <span class="content">속았지! 캔디통</span>
    </div>
    <Footer></Footer>
  </div>
  <Popup></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";

export default {
  name: "selectCapsule",
  components : {Footer, Popup},
  data () {
    return {

    }
  },
  methods : {
    selectCapsule(capsuleId) {
      console.log(capsuleId);
    }
  }
}
</script>

<style scoped>
.field {
  margin: 20% auto;
  width: 90%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
}
</style>