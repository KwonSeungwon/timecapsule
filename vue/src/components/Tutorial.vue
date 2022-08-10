<template>
  <div class="field-holder">
    <div class="field prev" v-if="selectIdx > 0" @click="prevIdx">
    </div>
    <transition :name="transition" v-for="(item, idx) in tutorialItem" :key="idx">
      <div class="field" v-if="selectIdx === idx">
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
    </transition>
    <div class="field next" v-if="tutorialItem.length > selectIdx + 1" @click="nextIdx">
    </div>
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
    prevIdx () {
      if (this.selectIdx) this.selectIdx--; this.transition = 'slide-prev';
    },
    nextIdx() {
      if (this.tutorialItem.length > this.selectIdx + 1) this.selectIdx++; this.transition = 'slide-next'
    }
  }
}
</script>

<style scoped>
.field-holder {
  position: relative;
  touch-action: pan-y;
}

.field {
  position: absolute;
  left: 17px;
  margin: 20% auto;
  width: 341px;
  height: 652px;
}

.slide-next-leave-active,
.slide-next-enter-active,
.slide-prev-enter-active,
.slide-prev-leave-active {
  transition: 0.5s;
}
.slide-next-enter,
.slide-next-leave,
.slide-prev-leave-to {
  transform: translate(100%, 0);
}

.slide-next-leave-to,
.slide-prev-enter,
.slide-prev-leave {
  transform: translate(-100%, 0);
}

.field.next {
  left: 365px;
}

.field.prev {
  left: -330px;
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