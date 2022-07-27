<template>
  <div class="field">
    <div class="title">
      <p>편지지 선택</p>
    </div>
    <div class="capsule-info">
      <span>받은 사람이 설정한 캡슐</span>
      <button></button>
    </div>
    <div class="selectField">
      <button class="letter-paper" @click="selectPaper(1)"></button>
      <span class="content">잘 찢은 연습장</span>
    </div>
    <div class="selectField">
      <button class="letter-paper" @click="selectPaper(2)"></button>
      <span class="content">고급스러운 양피지</span>
    </div>
    <div class="selectField">
      <button class="letter-paper" @click="selectPaper(3)"></button>
      <span class="content">평범한 편지지</span>
    </div>
    <div class="selectField">
      <button class="letter-paper" @click="selectPaper(4)"></button>
      <span class="content">편지성</span>
    </div>
    <Footer></Footer>
  </div>
  <Popup></Popup>
</template>

<script>
import Footer from '@/components/Footer';
import Popup from "@/components/Popup";

export default {
  name: "selectLetterPaper",
  components : {Footer, Popup},
  data () {
    return {

    }
  },
  methods : {
    selectPaper(paperId) {
      console.log(paperId);
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