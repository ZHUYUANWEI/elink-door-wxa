// pages/maxFunction/maxFunction.js
const app = getApp()
Page({
  data: {
    platformId: '',
    maxFunction: '',
    platform: [],
  },
  onLoad: function (options) {
    this.setData({
      platformId: options.platformId
    })
  },
  onShow: function () {
    this.setData({
      platform: app.globalData.demo[this.data.platformId]
    })
    wx.setNavigationBarTitle({
      title: this.data.platform.platformName
    })
    // console.log(this.data.platform)
  },
  // 跳转,传递平台索引、主功能索引
  goMinFunction: function (e) {
    // console.log(e.target.dataset.id)
    var maxFunctionId = e.target.dataset.id
    var platformId = this.data.platformId
    var url = this.data.platform.maxFunction[maxFunctionId].url //  .replace(/\s/g, "")//去除所包含的空格
    console.log(url)
    if (url) {
      // console.log("有")
    } else {
      // console.log("没有")
      url = '../minFunction/minFunction?platformId=' + platformId + '&maxFunctionId=' + maxFunctionId
    }
    wx.navigateTo({
      url: url
    })
  },
})