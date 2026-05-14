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
