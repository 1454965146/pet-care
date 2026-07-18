<script>
export default {
  onLaunch() {
    console.log('App Launch')
    this.preloadECharts()
  },
  
  preloadECharts() {
    if (typeof window !== 'undefined' && window.echarts) {
      return
    }
    const script = document.createElement('script')
    script.src = 'https://cdn.jsdelivr.net/npm/echarts@5.4.3/dist/echarts.min.js'
    script.onload = () => {
      console.log('ECharts preloaded')
    }
    script.onerror = () => {
      console.error('ECharts preload failed')
    }
    document.head.appendChild(script)
  }
}
</script>
<style>
page {
  background-color: #f5f6fa;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}
</style>
