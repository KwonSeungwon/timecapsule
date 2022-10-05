<template>
  <div class="field-holder">
    <transition-group :name="transition">
  <div class="field" v-for="(item, idx) in tutorialItem" :key="idx"
       :class="{'prev' : selectIdx > idx, 'next' : selectIdx < idx, 'none' : selectIdx - idx > 1 || selectIdx - idx < -1}"
       @click="moveField(selectIdx - 1 === idx ? 'prev' : selectIdx + 1 === idx ? 'next' : false)">
        <div class="title">
          <p>내 타임캡슐</p>
        </div>
        <div class="total-info">
          <p>안읽음 {{newCount}}</p>
          <p>전체 {{totalCount}}</p>
        </div>
        <div class="letter-holder">
          <div class="select-letter position-1">
            <button class="letter" @click="readLetter(0)"></button>
            <span>보낸사람1</span>
          </div>
          <div class="select-letter position-2">
            <button class="letter note" @click="readLetter(0)"></button>
            <span>보낸사람2</span>
          </div>
          <div class="select-letter position-3">
            <button class="letter polaroid" @click="readLetter(0)"></button>
            <span>보낸사람4</span>
          </div>
          <div class="select-letter position-4">
            <button class="letter sheepskin" @click="readLetter(0)"></button>
            <span>보낸사람5</span>
          </div>
          <div class="select-letter position-5">
            <button class="letter" @click="readLetter(0)"></button>
            <span>보낸사람3</span>
          </div>
          <div class="select-letter position-6">
            <button class="letter polaroid" @click="readLetter(0)"></button>
            <span>보낸사람6</span>
          </div>
          <button class="capsule bamboo"></button>
        </div>
        <Footer prev="홈으로" one-button :prev_back="false" v-on:footer_res="next"></Footer>
      </div>
    </transition-group>
  </div>
  <Letter v-if="openLetter"
          :letter-type="selectLetter.type"
          :contents="selectLetter.content"
          v-on:popup_res="closeLetter">
  </Letter>
</template>

<script>
import Footer from '@/components/Footer';
import Letter from "@/components/Letter";

export default {
  name: "MyCapsule",
  components : {Footer, Letter},
  data () {
    return {
      newCount : 0,
      totalCount : 0,
      transition : 'slide-next', //slide-next, slide-prev
      letters : [],
      selectIdx : 0,
      openLetter : false,
      selectLetter : {
        id : null,
        type : null,
        content : null
      },

      //임시
      tutorialItem : [1,2,3],
      letterTypes : ['letter', 'note', 'polaroid', 'sheepskin']
    }
  },
  methods : {
    readLetter(idx) {
      //TODO : 편지리스트에서 인덱스값 받아서 출력, 편지 정보에 편지지정보가있음
      this.selectLetter.id = idx;
      this.selectLetter.type = this.letterTypes[Math.floor(Math.random() * this.letterTypes.length)];
      this.selectLetter.content = '개발중...';
      this.openLetter = true;

    },
    closeLetter() {
      console.log(22);
      this.openLetter = false;
      //편지 읽음처리, disabled
    },
    next (next) {
      if (!next) {
        //TODO : 로그아웃필요
        this.$router.replace('/');
      }
    },
    moveField (type) {
      if (!type) {
        return;
      } else if (type === 'prev') {
        if (this.selectIdx) this.transition = 'slide-prev'; this.selectIdx--;
      } else if (type === 'next') {
        if (this.tutorialItem.length > this.selectIdx + 1)  this.transition = 'slide-next'; this.selectIdx++;
      }
    },
  }
}
</script>

<style scoped>
.field-holder {
  position: relative;
  /*touch-action: pan-y;*/
}
.field {
  position: absolute;
  left: 17px;
  margin: 20% auto;
  width: 341px;
  height: 652px;
  transition: all .5s;
}
.field.next {
  left: 365px;
}

.field.next.none {
  left : 500px;
}

.field.prev {
  left: -330px;
}

.field.prev.none {
  left: -500px;
}

/*.beach .field {*/
/*  background: url(../assets/images/field_bc.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

/*.mountain .field {*/
/*  background: url(../assets/images/field_mt.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

/*.desert .field {*/
/*  background: url(../assets/images/field_dt.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

/*.space .field {*/
/*  background: url(../assets/images/field_bc.png) no-repeat center transparent;*/
/*  background-size: cover;*/
/*}*/

.total-info {
  padding: 0 20px;
}

.total-info p {
  margin: 0 0 10px;
}


.letter-holder {
  position: relative;
  height: 400px;
  margin-bottom: 50px;
}

.letter-holder .capsule {
  position: absolute;
  top : calc(50% - 43px);
  left: calc(50% - 43px);
  width: 86px;
  height: 86px;
  border: 0;
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

.select-letter {
  position: absolute;
}

.select-letter.position-1 {
  top: 10px;
  left : calc(50% - 43px);
}

.select-letter.position-2 {
  top: 80px;
  right : calc(16% - 43px);
}

.select-letter.position-3 {
  bottom: 80px;
  right : calc(16% - 43px);
}

.select-letter.position-4 {
  bottom: 10px;
  left : calc(50% - 43px);
}

.select-letter.position-5 {
  bottom: 80px;
  left : calc(16% - 43px);
}

.select-letter.position-6 {
  top: 80px;
  left : calc(16% - 43px);
}

.select-letter span {
  display: inherit;
  text-align: center;
  margin-top: -4px;
}

button.letter {
  width: 86px;
  height: 86px;
  border: 0;
  background: url(../assets/images/common/letters/small/letter_small.png) no-repeat center transparent;
  background-size: cover;
}


button.letter:active,
.letter-holder .capsule:active {
  margin-top: 1px;
  margin-left: 1px;
  opacity: 0.9;
}
button.letter.note {
  background: url(../assets/images/common/letters/small/note_small.png) no-repeat center transparent;
  background-size: cover;
}

button.letter.polaroid {
  background: url(../assets/images/common/letters/small/polaroid_small.png) no-repeat center transparent;
  background-size: cover;
}
button.letter.sheepskin {
  background: url(../assets/images/common/letters/small/sheepskin_small.png) no-repeat center transparent;
  background-size: cover;
}

</style>