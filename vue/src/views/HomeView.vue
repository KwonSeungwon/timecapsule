<template>
  <div class="app-wrapper">
    <div class="main-container" :class="theme">
      <router-view v-slot="{ Component }">
        <transition name="page-fade" mode="out-in">
          <component :is="Component" />
        </transition>
      </router-view>
    </div>
  </div>
</template>

<script>
export default {
  name: 'HomeView',
  data () {
    return {
      theme : null,
      themes : ['beach', 'mountain', 'desert', 'space']
    }
  },
  methods : {
    setTheme () {
      this.theme = this.themes[Math.floor(Math.random() * this.themes.length)];
    },
  },
  created() {
    this.setTheme();
  }
}
</script>

<style scoped>
.app-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #1a1a1a; /* 어두운 배경으로 픽셀 아트 강조 */
}

.main-container {
  width: 100%;
  max-width: 480px; /* 조금 더 넓은 모바일 사이즈 */

  /* ⚠️ 이 두 줄은 자식들의 높이 기준점이다. height 를 auto/indefinite 로 바꾸거나
   * 이 요소와 라우트 루트 사이에 래퍼 div 를 끼워 넣지 말 것.
   * timecapsulue.css 의 .game-field 는 max-height: calc(100% - 10vh) 로 여기에
   * 기대어 스크롤 컨테이너를 만들고, Title.vue 의 .home-container 는 height:100% 로
   * 기댄다. 기준이 indefinite 해지는 순간 % 가 auto 로 풀려서 상한이 사라지고,
   * 아래 overflow:hidden 이 콘텐츠를 조용히 잘라낸다(스크롤바가 없으니 버그로
   * 보이지도 않는다). 실측: 래퍼 하나 끼웠더니 패널이 630px → 563.8px 로 줄었다.
   * height 와 max-height 가 어긋나는 것(100vh vs 900px)도 의도된 것 —
   * .game-field 가 vh 가 아니라 % 로 재는 이유가 이거다. */
  height: 100vh;
  max-height: 900px;
  position: relative;
  overflow: hidden;
  box-shadow: 0 0 50px rgba(0,0,0,0.5);
  background-size: cover;
  background-position: center;
}

.main-container.beach { background-image: url(../assets/images/beach/beach.png); }
.main-container.mountain { background-image: url(../assets/images/mountain/mountain.png); }
.main-container.desert { background-image: url(../assets/images/desert/desert.png); }
.main-container.space { background-image: url(../assets/images/bg_sc.png); }

/* 페이지 전환 애니메이션 */
.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.3s, transform 0.3s;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(10px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-10px);
}
</style>
