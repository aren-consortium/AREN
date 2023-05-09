<template>
  <div class="scroll-content" id="er404">
    <div class="title">404</div>
    <div class="subtitle">
      {{ $t('helper.404') }}
    </div>
    <div class="small">
      {{ $t('helper.redirect_home') }} {{ redirectIn }}
    </div>
  </div>
</template>

<script>
module.exports = {
  data() {
    return {
      redirectIn: 5
    }
  },
  created() {
    const interval = setInterval(() => {
      this.redirectIn--
      if (this.redirectIn == 0) {
        clearInterval(interval)
        this.$router.push("/")
      }
    }, 1000)
  }
};
</script>

<style scoped>
#er404 {
  justify-content: center;
  align-items: center;
}

#er404 .title {
  font-size: 20vh;
  font-weight: bold;
}

#er404 .subtitle {
  font-size: 20px;
}

#er404 .small {
  font-size: 12px;
}
</style>