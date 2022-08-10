<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 선택</p>
    </div>
    <div class="selectField">
      <button class="capsule"
              :class="{'on' : capsuleId === 1}"
              @click="selectCapsule(1)"></button>
      <span class="content">편지를 품은 병</span>
    </div>
    <div class="selectField">
      <button class="capsule"
              :class="{'on' : capsuleId === 2}"
              @click="selectCapsule(2)"></button>
      <span class="content">뚜껑 달린 죽통</span>
    </div>
    <div class="selectField">
      <button class="capsule"
              :class="{'on' : capsuleId === 3}"
              @click="selectCapsule(3)"></button>
      <span class="content">달걀형 타임캡슐</span>
    </div>
    <div class="selectField">
      <button class="capsule"
              :class="{'on' : capsuleId === 4}"
              @click="selectCapsule(4)"></button>
      <span class="content">속았지! 캔디통</span>
    </div>
    <Footer next="완료"
            :disable_next="capsuleId === null"
            v-on:footer_res="complete"></Footer>
  </div>
  <Popup v-if="popup.open"
         one-button
         cancel="확인"
         :contents="popup.content"
         v-on:popup_res="popup_f"></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";

export default {
  name: "selectCapsule",
  components : {Footer, Popup},
  data () {
    return {
      popup : {
        open : false,
        content : `타임캡슐이 만들어 졌어요.\n${this.openDay}일까지 이 캡슐로 편지를 받아볼 수 있어요.`
      },
      capsuleId : null,
      openDay : null
    }
  },
  methods : {
    selectCapsule(capsuleId) {
      this.capsuleId = capsuleId;
    },
    complete (next) {
      if (next) {
        this.popup.open = true;
      }
    },
    popup_f (close) {
      if (!close) {
        this.popup.open = false;
        this.$router.replace('/');
      }
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