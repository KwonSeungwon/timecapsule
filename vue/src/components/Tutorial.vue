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
        <span class="pagination" :class="{'first' : idx === 0, 'middle' : idx > 0 && tutorialItem.length > idx, 'last' : idx === tutorialItem.length - 1 && idx !== 0}"></span>
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

.title p {
  margin: 0;
  color : white;
  font-size: 25px;
  text-shadow: 3px 0 #315fd3, 0 2px #315fd3, 2px 0 #315fd3, 0 -2px #315fd3;
}

.field-holder {
  position: relative;
}

.field {
  position: absolute;
  left: 17px;
  margin: 20% auto;
  padding: 25px 20px;
  width: 301px;
  height: 602px;
  transition: all .5s;
}

.field.next {
  left: 365px;
}

.field.next.none {
  left : 500px;
  width: 1px;
}

.field.prev {
  left: -330px;
}

.field.prev.none {
  left: -500px;
  width: 1px;
}

.pagination {
  width: 43px;
  height: 9px;
  display: block;
}

.pagination.first {
  background: url(../assets/images/pagination-first.png) no-repeat center transparent;
  background-size: cover;
}

.pagination.middle {
  background: url(../assets/images/pagination-middle.png) no-repeat center transparent;
  background-size: cover;
}

.pagination.last {
  background: url(../assets/images/pagination-last.png) no-repeat center transparent;
  background-size: cover;
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