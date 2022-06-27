<template>
  <div class="field">
    <div>
      <label>내 좌표</label>
      <input>
    </div>
    <div>
      <label>비밀번호</label>
      <input>
    </div>
    <div>
      <label>오픈일</label>
      <select>
        <option>01.01</option>
        <option>02.28</option>
        <option>12.31</option>
      </select>
    </div>
  </div>
</template>

<script>
export default {
  name: "cJoin"
}
</script>

<style scoped>
.field {
  margin: 20% auto;
  width: 90%;
  height: 80%;
  background: url(../assets/images/field.png) no-repeat center transparent;
}
</style>