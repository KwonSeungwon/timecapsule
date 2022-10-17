<template>
  <div class="field">
    <div class="title">
      <p>타임캡슐 선택</p>
    </div>
    <div class="content">
      <div class="letter-holder">
        <div class="letter-object" :class="{'on' : capsuleId === '1'}">
          <div class="selectField">
            <input id="capsule1" type="radio" v-model="capsuleId" value="1" @click="selectCapsule(1)">
            <label for="capsule1" class="capsule bottle"></label>
          </div>
          <p>편지를 품은 병</p>
        </div>
        <div class="letter-object" :class="{'on' : capsuleId === '2'}">
          <div class="selectField">
            <input id="capsule2" type="radio" v-model="capsuleId" value="2" @click="selectCapsule(2)">
            <label for="capsule2" class="capsule bamboo"></label>
          </div>
          <p>뚜껑 달린 죽통</p>
        </div>
        <div class="letter-object" :class="{'on' : capsuleId === '3'}">
          <div class="selectField">
            <input id="capsule3" type="radio" v-model="capsuleId" value="3" @click="selectCapsule(3)">
            <label for="capsule3" class="capsule egg"></label>
          </div>
          <p>달걀형 타임캡슐</p>
        </div>
        <div class="letter-object" :class="{'on' : capsuleId === '4'}">
          <div class="selectField">
            <input id="capsule4" type="radio" v-model="capsuleId" value="4" @click="selectCapsule(4)">
            <label for="capsule4" class="capsule candybox"></label>
          </div>
          <p>속았지! 캔디통</p>
        </div>
      </div>
    </div>
    <Footer next="완료" :disable_next="capsuleId === null" v-on:footer_res="complete"></Footer>
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
import axios from 'axios';

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
      openDay : null,
      parameters : {},
      coordinates : '',
      password : ''
    }
  },
  methods : {
    selectCapsule(capsuleId) {
      this.capsuleId = capsuleId;
      this.parameters.capsuleId = capsuleId;
    },
    complete (next) {
      if (next) {
        axios.post('/api/timecapsule/user', this.parameters).then(res => {
          console.log(res);
          this.popup.open = true;
        });
      }
    },
    popup_f (close) {
      if (!close) {
        this.popup.open = false;
        this.$router.replace('/');
      }
    }
  },
  created() {
    this.parameters.coordinates = this.$route.query.coordinates;
    this.parameters.password = this.$route.query.password;
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

.capsule-info .capsule {
  float: right;
  border: 0;

  width: 37px;
  height: 37px;
  margin-top: 6px;
}

.capsule.bamboo {
  background: url(../assets/images/common/capsule/bamboo.PNG) no-repeat center transparent;
  background-size: cover;
}

.capsule.bottle {
  background: url(../assets/images/common/capsule/bottle.png) no-repeat center transparent;
  background-size: cover;
}

.capsule.candybox {
  background: url(../assets/images/common/capsule/candybox.PNG) no-repeat center transparent;
  background-size: cover;
}

.capsule.egg {
  background: url(../assets/images/common/capsule/egg.PNG) no-repeat center transparent;
  background-size: cover;
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

.selectField input[type=radio] {
  display: none;
  width: 0;
  height: 0;
}
.selectField label[for] {
  text-decoration: none;
  cursor: pointer;
  -webkit-tap-highlight-color: transparent;
}

.selectField .capsule {
  width: 90px;
  height: 90px;
  display: inline-block;
  margin: 18px 24px;
}

.letter-object .selectField {
  border: 0;
  width: 138px;
  height: 127px;
}

.letter-object.on .selectField {
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