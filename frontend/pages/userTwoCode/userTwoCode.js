// pages/userTwoCode/userTwoCode.js
const app = getApp()
var util = require("../../utils/util.js");
Page({
  data: {
    index: '0',
    projectId: 'hk8700projectIdTest',
    imgUrl: '',
    result: false,
    users: [
      {
        personId: '9',
        cardNum: '2017009',
      },
      {
        personId: '10',
        cardNum: '2017010',
      },
      {
        personId: '11',
        cardNum: '2017011',
      },
      {
        personId: '12',
        cardNum: '2017012',
      },
      {
        personId: '13',
        cardNum: '2017013',
      },
      {
        personId: '14',
        cardNum: '2017014',
      },
    ]
  },
  onLoad: function (options) {

  },
  bindPickerChange: function (e) {
    this.data.index = e.detail.value
    this.setData({
      index: this.data.index,
      result: false
    })
  },
  formSubmit: function (e) {
    var that = this
    // console.log(e)
    wx.request({
      url: app.globalData.HttpsRequest + '/elinkDoorBackend/getCustomQR',
      data: {
        projectId: this.data.projectId,
        personId: e.detail.value.personId,
        cardNum: e.detail.value.cardNum
      },
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        // console.log(res.data) 
        util.towCodeOutParameter(res, that)
      },
      fail: function () {
        console.log('请求失败')
        wx.showToast({
          title: '获取失败',
        })
      }
    })
  }
})