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
        <div class="select-letter top left">
          <button class="capsule"></button>
          <span class="content"></span>
        </div>
        <div class="select-letter top center">
          <button class="capsule"></button>
          <span class="content"></span>
        </div>
        <div class="select-letter top right">
          <button class="capsule"></button>
          <span></span>
        </div>
        <div class="capsule">
          <button></button>
        </div>
        <div class="select-letter bottom left">
          <button class="capsule"></button>
          <span></span>
        </div>
        <div class="select-letter bottom center">
          <button class="capsule"></button>
          <span class="content"></span>
        </div>
        <div class="select-letter bottom right">
          <button class="capsule"></button>
          <span class="content"></span>
        </div>
        <Footer prev="홈으로" one-button :prev_back="false" v-on:footer_res="next"></Footer>
      </div>
      <Letter v-if="openLetterId !== null"></Letter>
    </transition-group>
  </div>
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
      openLetterId : null,
      transition : 'slide-next', //slide-next, slide-prev
      tutorialItem : [1,2,3],
      selectIdx : 0
    }
  },
  methods : {
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