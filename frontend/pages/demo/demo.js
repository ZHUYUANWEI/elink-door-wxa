// pages/demo/demo.js
const app = getApp()
Page({
  data: {
    demo: [],
  },
  onLoad: function (options) {
    this.setData({
      demo: app.globalData.demo
    })
  },

  // 跳转, 传递平台索引
  goMaxFunction: function (e) {
    // console.log(e.target.dataset.id)
    var platformId = e.target.dataset.id
    var url = this.data.demo[platformId].url.replace(/\s/g, "")//去除所包含的空格
    if (url){
      // console.log("有")
    }else{
      // console.log("没有")
      url = '../maxFunction/maxFunction?platformId=' + platformId
    }
    wx.navigateTo({
      url: url
    })
  },

})