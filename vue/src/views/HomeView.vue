<template>
  <div class="container">
    <div class="main beach">
      <router-view/>
    </div>
  </div>
</template>

<script>

export default {
  name: 'HomeView',
}
</script>
<style scoped>
.container {
  display: flex;
}
.main {
  margin: auto;
  width: 375px;
  height: 100vh;
  max-height: 812px;
}

.main.beach {
  background: url(../assets/images/beach.svg) no-repeat center;
  background-size: cover;
}
</style>
