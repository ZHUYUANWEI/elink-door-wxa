// pages/minFunction/minFunction.js
const app = getApp()
Page({
  data: {
    platformId: '',
    maxFunctionId: '',
    minFunctionId: '',
    maxFunction: []
  },
  onLoad: function (options) {
    this.setData({
      platformId: options.platformId,
      maxFunctionId: options.maxFunctionId,
    })
  },
  onShow: function () {
    this.setData({
      maxFunction: app.globalData.demo[this.data.platformId].maxFunction[this.data.maxFunctionId]
    })
    wx.setNavigationBarTitle({
      title: this.data.maxFunction.maxFunctionName
    })
    // console.log(this.data.platform)
  },
  selectItem: function (e) {
    // console.log(e.target.dataset.id)
    // console.log(this.data.maxFunction.minFunction[e.target.dataset.id].url)
    wx.navigateTo({
      url: this.data.maxFunction.minFunction[e.target.dataset.id].url
    })
  }
})