import axios from 'axios'

const BASE_URL = 'http://localhost:8080'

export const statisticsApi = {
  getVisitorStats: () => {
    return axios.get(`${BASE_URL}/api/statistics/visitors`)
  }
} 