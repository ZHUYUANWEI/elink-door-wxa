// pages/problem/problem.js
Page({
  data: {
    name: "",
    problem: "",
    details: "**详细开门日志****详细开门日志****详细开门日志****详细开门日志****详细开门日志****详细开门日志****详细开门日志****详细开门日志****详细开门日志****详细开门日志**"
  },
  returnList: function () {
    wx.showToast({
      title: '上报成功',
      icon: 'success',
      duration: 1500,
      success: function () {
        setTimeout(function () {
        wx.navigateBack({
          delta: 1
        })
        }, 2000) //延迟时间 
      }
    })
  },
  onLoad: function (options) {
    var that = this
    this.setData({
      name: options.name,
      problem: options.problem
    })
    wx.setNavigationBarTitle({
      title: that.data.name + '上报问题'
    })
  }
})