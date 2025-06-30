<template>
  <div class="chart-container">
    <h3>全国主要城市实时天气</h3>
    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else>
      <div class="chart-wrapper">
        <Line
          :data="chartData"
          :options="chartOptions"
        />
      </div>
      <div class="weather-details">
        <div v-for="(city, index) in weatherDetails" :key="index" class="city-weather">
          <p>{{ city.name }}: {{ city.weather }}, {{ city.temperature }}°C</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { Line } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
} from 'chart.js'
import axios from 'axios'
import { AMAP_KEY } from '../config/api'

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend
)

// 主要城市的高德地图城市编码
const CITIES = [
  { name: '北京', adcode: '110000' },
  { name: '上海', adcode: '310000' },
  { name: '广州', adcode: '440100' },
  { name: '深圳', adcode: '440300' },
  { name: '成都', adcode: '510100' },
  { name: '杭州', adcode: '330100' },
  { name: '武汉', adcode: '420100' },
  { name: '西安', adcode: '610100' }
]

export default {
  name: 'TemperatureChart',
  components: { Line },
  setup() {
    const loading = ref(false)
    const error = ref('')
    const weatherDetails = ref([])
    const chartData = ref({
      labels: [],
      datasets: [{
        label: '实时温度 (°C)',
        backgroundColor: 'rgba(75, 192, 192, 0.2)',
        borderColor: 'rgb(75, 192, 192)',
        data: []
      }]
    })

    const chartOptions = {
      responsive: true,
      maintainAspectRatio: false,
      scales: {
        y: {
          beginAtZero: false,
          title: {
            display: true,
            text: '温度 (°C)'
          }
        }
      },
      plugins: {
        tooltip: {
          callbacks: {
            label: function(context) {
              const cityIndex = context.dataIndex
              const city = weatherDetails.value[cityIndex]
              return [
                `温度: ${context.parsed.y}°C`,
                `天气: ${city?.weather || '未知'}`,
                `湿度: ${city?.humidity || '未知'}%`,
                `风向: ${city?.winddirection || '未知'}`,
                `风力: ${city?.windpower || '未知'}`
              ]
            }
          }
        }
      }
    }

    let updateTimer = null

    const fetchWeatherData = async () => {
      loading.value = true
      error.value = ''
      try {
        const weatherData = []
        const labels = []
        const details = []

        // 使用Promise.all并发请求所有城市的天气数据
        const results = await Promise.all(CITIES.map(city => 
          axios.get('https://restapi.amap.com/v3/weather/weatherInfo', {
            params: {
              key: AMAP_KEY,
              city: city.adcode,
              extensions: 'base'
            }
          })
        ))

        results.forEach((response, index) => {
          const city = CITIES[index]
          console.log(`城市: ${city.name}, 返回数据:`, response.data) // 添加调试日志
          
          if (response.data.status === '1') {
            if (response.data.lives && response.data.lives.length > 0) {
              const live = response.data.lives[0]
              weatherData.push(parseFloat(live.temperature))
              labels.push(city.name)
              details.push({
                name: city.name,
                temperature: live.temperature,
                weather: live.weather,
                winddirection: live.winddirection,
                windpower: live.windpower,
                humidity: live.humidity
              })
            } else {
              console.warn(`${city.name} 没有返回天气数据`)
            }
          } else {
            console.warn(`${city.name} 请求失败, 状态码: ${response.data.status}, 信息: ${response.data.info}`)
          }
        })

        if (weatherData.length === 0) {
          error.value = '暂无天气数据'
          return
        }

        // 更新图表数据
        chartData.value.labels = labels
        chartData.value.datasets[0].data = weatherData
        weatherDetails.value = details

        console.log('处理后的天气数据:', {
          labels: labels,
          temperatures: weatherData,
          details: details
        })
      } catch (err) {
        console.error('获取天气数据失败:', err)
        error.value = '获取天气数据失败，请稍后重试'
      } finally {
        loading.value = false
      }
    }

    onMounted(() => {
      fetchWeatherData()
      // 每10分钟更新一次数据
      updateTimer = setInterval(fetchWeatherData, 10 * 60 * 1000)
    })

    onUnmounted(() => {
      if (updateTimer) {
        clearInterval(updateTimer)
      }
    })

    return {
      chartData,
      chartOptions,
      loading,
      error,
      weatherDetails
    }
  }
}
</script>

<style scoped>
.chart-container {
  background: rgba(255, 255, 255, 0.9);
  padding: 20px;
  border-radius: 8px;
  width: 100%;
  max-height: 600px;
  overflow-y: auto;
}

.chart-wrapper {
  position: relative;
  height: 300px;
  width: 100%;
}

h3 {
  text-align: center;
  margin-bottom: 20px;
  color: #333;
}

.loading, .error {
  text-align: center;
  padding: 20px;
  font-size: 16px;
}

.error {
  color: #ff4444;
}

.weather-details {
  margin-top: 20px;
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
  gap: 10px;
  padding: 10px;
}

.city-weather {
  background: rgba(75, 192, 192, 0.1);
  padding: 10px;
  border-radius: 4px;
  text-align: center;
}

.city-weather p {
  margin: 0;
  color: #333;
  font-size: 14px;
}
</style> 