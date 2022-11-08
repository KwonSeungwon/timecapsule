<template>
  <div class="field-holder">
    <swiper class="swiper" :slides-per-view="3" :space-between="50" @swiper="onSwiper" @slideChange="onSlideChange" >
      <!--첫번째 슬라이드-->
      <swiper-slide role="tab">
        <div class="field">
          <div class="title" style="padding: 4px;">
            <p style="padding: 4px; font-size: 30px;">사용법</p>
            <p style="padding: 4px; font-size: 15px;">타임캡슐 만들기 (MY)</p>
          </div>
          <div class="content" style="font-weight: normal;">
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">1.     좌표를 새로 생성하고</p>
             <div style="float: left"> <img src="../assets/images/passwordBox.png"></div><div><p style="color: #522a2a; text-shadow: none; ">를 입력해주세요</p></div>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">2.     캡슐을 열어 편지지를 읽을 날짜를 <br>입력해주세요.</p>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">3.     원하는 타임캡슐을 선택해 주세요.</p>
          </div>

          <span class="paginationFirst">

          </span>
          <Footer style="margin-top:10px;" prev="닫기" one-button></Footer>
        </div>
      </swiper-slide>
      <!-- 두번째 슬라이드 -->
      <swiper-slide role="tab">
        <div class="field">
          <div class="title" style="padding: 4px;">
            <p style="padding: 4px; font-size: 30px;">사용법</p>
            <p style="padding: 4px; font-size: 15px;">편지보내기 (Send)</p>
          </div>
          <div class="content" style="font-weight: normal;">
            <p style="color: #522a2a; text-shadow: none;">1.     <img src="../assets/images/receiver.png">의 타임캡슐 주소(url)와<br>
              보내는 사람을 입력해주세요</p>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">2.     원하는 편지지를 선택해 주세요</p>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">3.     선택한 편지지에 내용을 써주세요</p>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">4.     완료를 클릭해 작성한 편지를 전송해주세요</p>
          </div>
          <span class="paginationMiddle">

          </span>
          <Footer style="margin-top:10px;" prev="닫기" one-button></Footer>
        </div>
      </swiper-slide>
      <!-- 세번째 슬라이드 -->
      <swiper-slide role="tab">
        <div class="field">
          <div class="title" style="padding: 4px;">
            <p style="padding: 4px; font-size: 30px;">사용법</p>
            <p style="padding: 4px; font-size: 15px;">타입캡슐 확인하기 (MY)</p>
          </div>
          <div class="content" style="font-weight: normal;">
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">1.     기존에 생성한 내 좌표와 비밀번호를 입력해주세요<br>
              비밀번호를 입력해주세요</p>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">2.     타입캡슐로 들어온 편지지를 클릭해 내용을 <br>확인해주세요.</p>
            <p style="color: #522a2a; text-shadow: none; margin-top: 30px;">3.     편지는 한 화면에 최대 6개까지 노출되며, 화면을 좌우로 스와이프하여 이전/다음 6개의 편지를 확인할수 있어요.</p>
          </div>
          <span class="paginationLast">
          </span>
          <Footer style="margin-top:10px;" prev="닫기" one-button></Footer>
        </div>
      </swiper-slide>
      <swiper-slide role="slider"></swiper-slide>
      <swiper-slide role="slider"></swiper-slide>
      <swiper-slide role="slider"></swiper-slide>
      <swiper-slide role="slider"></swiper-slide>
      <swiper-slide role="slider"></swiper-slide>

      ...
    </swiper>
    <transition-group :name="transition">

    </transition-group>
  </div>
</template>

<script>
import Footer from '@/components/Footer';

import { Swiper, SwiperSlide } from 'swiper/vue';
// Import Swiper styles
import 'swiper/css';
import 'swiper/css/effect-fade';

export default {
  name: "cTutorial",
  components : {
    Footer,
    Swiper,
    SwiperSlide,
  },
  data () {
    return {
      transition : 'slide-next', //slide-next, slide-prev
      tutorialItem : [1,2,3],
      selectIdx : 0,
      slidesPerView: 'auto',
      spaceBetween: 10, // swiper-slide 사이의 간격 지정
      slidesOffsetBefore: 0, // slidesOffsetBefore는 첫번째 슬라이드의 시작점에 대한 변경할 때 사용
      slidesOffsetAfter: 7, // slidesOffsetAfter는 마지막 슬라이드 시작점 + 마지막 슬라이드 너비에 해당하는 위치의 변경이 필요할 때 사용
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
/*.swiper-container {
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
}*/
.swiper {

  height: 830px;
  width: 100%;

  .swiper-slide {
    width: auto;
    min-width: 297.6667px;
    margin-right: 50px;
    display: flex;
    justify-content: center;
    align-items: center;
    font-weight: bold;
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
  top: 10px;
  margin: 20% auto;
  padding: 25px 20px;
  width: 301px;
  height: 68%;
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

/*.pagination {
  width: 43px;
  height: 9px;
  display: block;
}*/

.paginationFirst {
  margin-left: 43%;
  width: 43px;
  height: 9px;
  display: block;
  background: url(../assets/images/pagination-first.png) no-repeat center transparent;
  background-size: cover;
}

.paginationMiddle {
  margin-left: 43%;
  width: 43px;
  height: 9px;
  display: block;
  background: url(../assets/images/pagination-middle.png) no-repeat center transparent;
  background-size: cover;
}

.paginationLast {
  margin-left: 43%;
  width: 43px;
  height: 9px;
  display: block;
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
