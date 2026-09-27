<template>
  <router-view></router-view>
</template>

<script setup>
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background-color: #F8F9FE;
  color: #2D3436;
}

#app {
  min-height: 100vh;
}

.app-feedback-box {
  width: min(420px, calc(100vw - 32px));
  border-radius: 8px;
  padding: 8px 8px 4px;
}

.app-feedback-box .el-message-box__title,
.app-feedback-box .el-message-box__message {
  color: #1A2623;
}

.app-feedback-box .el-message-box__message {
  line-height: 1.8;
}

.app-feedback-box .el-message-box__btns {
  padding-top: 8px;
}

.app-feedback-box .el-button {
  border-radius: 8px;
}
</style>
