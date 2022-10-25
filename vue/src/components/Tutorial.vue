<template>
  <div class="field-holder">
    <swiper
        :slides-per-view="3"
        :space-between="50"
        @swiper="onSwiper"
        @slideChange="onSlideChange"
    >
      <swiper-slide role="tab"> s111
      </swiper-slide>
      <swiper-slide role="tab">Slide 4</swiper-slide>
      <swiper-slide role="tab">Slide 3</swiper-slide>
      <swiper-slide role="slider">Slide 4</swiper-slide>
      <swiper-slide role="slider">Slide 5</swiper-slide>
      <swiper-slide role="slider">Slide 6</swiper-slide>
      <swiper-slide role="slider">Slide 7</swiper-slide>
      ...
    </swiper>
    <transition-group :name="transition">

    </transition-group>
  </div>
</template>

<script>
/*import Footer from '@/components/Footer';*/

// Import Swiper Vue.js components
import { Swiper, SwiperSlide } from 'swiper/vue';
// Import Swiper styles
import 'swiper/css';

export default {
  name: "cTutorial",
  components : {
  /*  Footer,*/
    Swiper,
    SwiperSlide,
  },
  data () {
    return {
      transition : 'slide-next', //slide-next, slide-prev
      tutorialItem : [1,2,3],
      selectIdx : 0,
      slidesPerView: 'auto',
      spaceBetween: 6, // swiper-slide 사이의 간격 지정
      slidesOffsetBefore: 0, // slidesOffsetBefore는 첫번째 슬라이드의 시작점에 대한 변경할 때 사용
      slidesOffsetAfter: 0, // slidesOffsetAfter는 마지막 슬라이드 시작점 + 마지막 슬라이드 너비에 해당하는 위치의 변경이 필요할 때 사용
      freeMode: true, // freeMode를 사용시 스크롤하는 느낌으로 구현 가능
      centerInsufficientSlides: true, // 컨텐츠의 수량에 따라 중앙정렬 여부를 결정함
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
  },
  setup() {
    const onSwiper = (swiper) => {
      console.log(swiper);
    };
    const onSlideChange = () => {
      console.log('slide change');
    };
    return {
      onSwiper,
      onSlideChange,
    };
  },
}
</script>
<style lang="scss" scoped>
.swiper-container {
  .swiper-wrapper {
    .swiper-slide {
      width: auto; // auto 값을 지정해야 슬라이드의 width값이 텍스트 길이 기준으로 바뀜
      min-width: 56px; // min-width를 지정하지 않을 경우 텍스트가 1개 내지는 2개가 들어갈 때 탭 모양이 상이할 수 있으므로 넣어준다.
      padding: 0px 14px;
      font-size: 14px;
      line-height: 36px;
      text-align: center;
      color: #84868c;
      border: 0;
      border-radius: 18px;
      background: #f3f4f7;
      appearance: none;
      cursor: pointer;
    }
  }
}
</style>
<style scoped>

/*.title p {*/
/*  margin: 0;*/
/*  color : white;*/
/*  font-size: 25px;*/
/*  text-shadow: 3px 0 #315fd3, 0 2px #315fd3, 2px 0 #315fd3, 0 -2px #315fd3;*/
/*}*/

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
