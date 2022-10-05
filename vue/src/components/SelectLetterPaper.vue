<template>
  <div class="field">
    <div class="title">
      <p>편지지 선택</p>
    </div>
    <div class="content">
      <div class="capsule-info">
        <span>받은 사람이 설정한 캡슐</span>
        <button @click="capsuleInfo"></button>
      </div>
      <div class="letter-holder">
        <div class="letter-object" :class="{'on' : paperId === '1'}">
          <div class="selectField">
            <label for="letter1" class="letter"></label>
            <input id="letter1" type="radio" v-model="paperId" value="1">
          </div>
          <p>잘 찢은 연습장</p>
        </div>
        <div class="letter-object" :class="{'on' : paperId === '2'}">
          <div class="selectField">
            <label for="letter2" class="letter"></label>
            <input id="letter2" type="radio" v-model="paperId" value="2">
          </div>
          <p>고급스러운 양피지</p>
        </div>
        <div class="letter-object" :class="{'on' : paperId === '3'}">
          <div class="selectField">
            <label for="letter3" class="letter"></label>
            <input id="letter3" type="radio" v-model="paperId" value="3">
          </div>
          <p>평범한 편지지</p>
        </div>
        <div class="letter-object" :class="{'on' : paperId === '4'}">
          <div class="selectField">
            <label for="letter4" class="letter"></label>
            <input id="letter4" type="radio" v-model="paperId" value="4">
          </div>
          <p>편지성</p>
        </div>
      </div>
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

.capsule-info {
  margin: 0 auto;
  padding: 0 15px;
  width: 264px;
  height: 49px;
}

.beach .capsule-info {
  background: url(../assets/images/beach/input_check.png) no-repeat center transparent;
  background-size: cover;
}

.desert .capsule-info {
  background: url(../assets/images/desert/input_check.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .capsule-info {
  background: url(../assets/images/mountain/input_check.png) no-repeat center transparent;
  background-size: cover;
}

.space .capsule-info {
  background: url(../assets/images/beach/input_check.png) no-repeat center transparent;
  background-size: cover;
}

.capsule-info span {
  line-height: 49px;
}

.letter-holder {
  padding: 20px;
}

.letter-object {
  display: inline-block;
  margin: 6px;
}

.letter-object p {
  text-align: center;
}

.letter-object.on p {
  color: #FFE24A;
}

.letter-object .selectField {
  border: 0;
  width: 138px;
  height: 127px;
}

.beach .letter-object .selectField {
  background: url(../assets/images/beach/default_bg.png) no-repeat center transparent;
  background-size: cover;
}

.desert .letter-object .selectField {
  background: url(../assets/images/desert/default_bg.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .letter-object .selectField {
  background: url(../assets/images/mountain/default_bg.png) no-repeat center transparent;
  background-size: cover;
}

.space .letter-object .selectField {
  background: url(../assets/images/beach/default_bg.png) no-repeat center transparent;
  background-size: cover;
}

.beach .letter-object.on .selectField {
  background: url(../assets/images/beach/select_bg.png) no-repeat center transparent;
  background-size: cover;
}

.desert .letter-object.on .selectField {
  background: url(../assets/images/desert/select_bg.png) no-repeat center transparent;
  background-size: cover;
}

.mountain .letter-object.on .selectField {
  background: url(../assets/images/mountain/select_bg.png) no-repeat center transparent;
  background-size: cover;
}

.space .letter-object.on .selectField {
  background: url(../assets/images/beach/select_bg.png) no-repeat center transparent;
  background-size: cover;
}

</style>