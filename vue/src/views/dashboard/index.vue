<template>
  <div class="dashboard-container">
    <div class="dashboard-text">昵称:{{ name }}</div>
  </div>
</template>
<script>
export default {
  name: 'Dashboard',
  mounted: function() {},
  data() {
    return {
      name: '',
    }
  }
}

</script>
<style rel="stylesheet/scss" lang="scss" scoped>
.dashboard {
  &-container {
    margin: 30px;
  }
  &-text {
    font-size: 30px;
    line-height: 46px;
  }
}

</style>
