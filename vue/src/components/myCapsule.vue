<template>
  <div class="field-holder">
    <transition-group :name="transition">
  <div class="field" v-for="page in pages" :key="page"
       :class="{'prev' : selectIdx > page, 'next' : selectIdx < page, 'none' : selectIdx - page > 1 || selectIdx - page < -1}"
       @click="moveField(selectIdx - 1 === page ? 'prev' : selectIdx + 1 === page ? 'next' : false)">
        <div class="title">
          <p>내 타임캡슐</p>
        </div>
        <div class="total-info">
          <p>안읽음 {{newCount}}</p>
          <p>전체 {{totalCount}}</p>
        </div>
        <div class="letter-holder">
          <template v-if="letters.length">
            <template v-for="(letter, i) in letters" :key="i">
              <!-- 0 1 2 3 4 5   6 7 8 9 10 11   12 13 14 15 16 17-->
              <div v-if="(page - 1) * 6 <= i && i < page * 6"  class="select-letter" :class="'position-' + (i % 6 + 1)">
                <button class="letter" :class="letter.type" @click="readLetter(i)"></button>
                <span>{{letter.name}}</span>
              </div>
            </template>
          </template>
          <template v-else>
            <!-- 편지 없음 표시 -->
          </template>
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
      pages : 0,
      letters : [],
      selectIdx : 1,
      openLetter : false,
      selectLetter : {
        id : null,
        type : null,
        content : null
      },

      //임시
      letterTypes : ['letter', 'note', 'polaroid', 'sheepskin']
    }
  },
  methods : {
    initTestData(ea) {
      let first = ['김', '박', '이', '최', '황', '권', '조'];
      let last = ['철수', '영희', '길동', '동건', '빈', '성수', '현수', '승원'];
      let contents = [
          '안녕 반가워 다음에 또만나',
          '너를 때려 주고 싶어',
          '빌린돈 값아',
          '메세지 보내지 말아줄래',
          '이 편지는 영국에서 최초로 시작되어 일년에 한바퀴를 돌면서 받는 사람에게 행운을 주었고 지금은 당신에게로 옮겨진 이 편지는 4일 안에 당신 곁을 떠나야 합니다. 이 편지를 포함해서 7통을 행운이 필요한 사람에게 보내 주셔야 합니다. 복사를 해도 좋습니다. 혹 미신이라 하실지 모르지만 사실입니다.'
      ];

      for (let i=0, max=ea; i<max;i++) {
        this.letters.push({
          id : i,
          type : this.letterTypes[Math.floor(Math.random() * this.letterTypes.length)],
          name : this.returnRandomContent(first) + this.returnRandomContent(last),
          content : this.returnRandomContent(contents)
        });
      }

      this.pages = Math.ceil(ea / 6 );
      console.log(this.letters);
    },
    returnRandomContent (list) {
      return list[Math.floor(Math.random() * list.length)];
    },
    initLetter () {
      this.selectLetter = {
        id : null,
        type : null,
        content : null
      };
    },
    readLetter(idx) {
      //TODO : 편지리스트에서 인덱스값 받아서 출력, 편지 정보에 편지지정보가있음
      this.selectLetter = this.letters[idx];
      this.openLetter = true;
    },
    closeLetter() {
      this.initLetter();
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
        if (this.pages > this.selectIdx + 1)  this.transition = 'slide-next'; this.selectIdx++;
      }
    },
  },
  created() {
    this.initTestData(14);
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

.select-letter button.letter {
  width: 86px;
  height: 86px;
  border: 0;
}

.select-letter button.letter:active,
.letter-holder .capsule:active {
  margin-top: 1px;
  margin-left: 1px;
  opacity: 0.9;
}


</style>