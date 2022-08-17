<template>
  <div class="field-holder">
    <transition-group :name="transition">
      <div class="field"
           v-for="(item, idx) in tutorialItem" :key="idx"
           :class="{'prev' : selectIdx > idx, 'next' : selectIdx < idx, 'none' : selectIdx - idx > 1 || selectIdx - idx < -1}"
           @click="moveField(selectIdx - 1 === idx ? 'prev' : selectIdx + 1 === idx ? 'next' : false)">
        <div class="title">
          <p>사용법</p>
        </div>
        <div class="content">
          <p>1.{{item}}</p>
          <p>2.</p>
          <p>3.</p>
          <p>4.</p>
        </div>
        <Footer prev="닫기" one-button></Footer>
      </div>
    </transition-group>
  </div>
</template>

<script>
import Footer from '@/components/Footer';

export default {
  name: "cTutorial",
  components : {Footer},
  data () {
    return {
      transition : 'slide-next', //slide-next, slide-prev
      tutorialItem : [1,2,3],
      selectIdx : 0
    }
  },
  methods : {
    moveField (type) {
      if (!type) {
        return;
      } else if (type === 'prev') {
        this.prevIdx();
      } else if (type === 'next') {
        this.nextIdx();
      }
    },
    prevIdx () {
      if (this.selectIdx) this.transition = 'slide-prev'; this.selectIdx--;
    },
    nextIdx() {
      if (this.tutorialItem.length > this.selectIdx + 1)  this.transition = 'slide-next'; this.selectIdx++;
    }
  }
}
</script>

<style scoped>
.field-holder {
  position: relative;
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