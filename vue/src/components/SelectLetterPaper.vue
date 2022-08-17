<template>
  <div class="field">
    <div class="title">
      <p>편지지 선택</p>
    </div>
    <div class="capsule-info">
      <span>받은 사람이 설정한 캡슐</span>
      <button @click="capsuleInfo"></button>
    </div>
    <div class="selectField">
      <button class="letter-paper" :class="{'on' : paperId === '1'}" @click="selectPaper(1)"></button>
      <span class="content">잘 찢은 연습장</span>
    </div>
    <div class="selectField">
      <button class="letter-paper" :class="{'on' : paperId === '2'}" @click="selectPaper(2)"></button>
      <span class="content">고급스러운 양피지</span>
    </div>
    <div class="selectField">
      <button class="letter-paper" :class="{'on' : paperId === '3'}" @click="selectPaper(3)"></button>
      <span class="content">평범한 편지지</span>
    </div>
    <div class="selectField">
      <button class="letter-paper" :class="{'on' : paperId === '4'}" @click="selectPaper(4)"></button>
      <span class="content">편지성</span>
    </div>
    <Footer :disable_next="paperId === null" v-on:footer_res="next"></Footer>
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
  name: "selectLetterPaper",
  components : {Footer, Popup},
  data () {
    return {
      paperId : null,
      popup : {
        open : false,
        content : '캡슐정보'
      },
    }
  },
  methods : {
    selectPaper(paperId) {
      if (this.paperId === paperId) {
        this.paperId = null;
      } else {
        this.paperId = paperId;
      }
    },
    next (next) {
      if (next) {
        if (this.paperId) {
          this.$router.push('/target/letter/write');
        }
      }
    },
    popup_f (close) {
      if (!close) {
        this.popup.open = false;
      }
    },
    capsuleInfo () {
      //캡슐정보 바인딩
      //this.popup.content = '';
      this.popup.open = true;
    }
  }
}
</script>

<style scoped>
.field {
  margin: 20% auto;
  width: 341px;
  height: 652px;
}
.beach .field {
  background: url(../assets/images/field_bc.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .field {
  background: url(../assets/images/field_mt.png) no-repeat center transparent;
  background-size: cover;
}

.desert .field {
  background: url(../assets/images/field_dt.png) no-repeat center transparent;
  background-size: cover;
}

.space .field {
  background: url(../assets/images/field_bc.png) no-repeat center transparent;
  background-size: cover;
}
</style>