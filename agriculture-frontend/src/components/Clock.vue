<template>
  <div class="clock">
    <div class="time">{{ currentTime }}</div>
    <div class="date">{{ currentDate }}</div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import dayjs from 'dayjs'

export default {
  name: 'Clock',
  setup() {
    const currentTime = ref('')
    const currentDate = ref('')
    let timer = null

    const updateTime = () => {
      const now = dayjs()
      currentTime.value = now.format('HH:mm:ss')
      currentDate.value = now.format('YYYY年MM月DD日 dddd')
    }

    onMounted(() => {
      updateTime()
      timer = setInterval(updateTime, 1000)
    })

    onUnmounted(() => {
      if (timer) {
        clearInterval(timer)
      }
    })

    return {
      currentTime,
      currentDate
    }
  }
}
</script>

<style scoped>
.clock {
  text-align: center;
  padding: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 8px;
  margin-bottom: 20px;
}

.time {
  font-size: 2.5em;
  font-weight: bold;
  color: #333;
}

.date {
  font-size: 1.2em;
  color: #666;
  margin-top: 5px;
}
</style> 