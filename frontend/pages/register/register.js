//register.js
//获取应用实例
const app = getApp()
var util = require('../../utils/util.js');
Page({
  data: {
    userInfo: {},
    mobile: "",
    code: "",
    _code: "123456",//测试用6位数验证码
    _mobile: "13045008300",//测试用手机号
    VerifyCode: "获取验证码",
    disabled: false
  },
  onLoad: function () {
    if (app.globalData.userInfo) {
      // console.log("有缓存")
      this.setData({
        userInfo: app.globalData.userInfo,
      })
    } else {
      // console.log("没有有缓存")
      // 在没有 open-type=getUserInfo 版本的兼容处理
      wx.getUserInfo({
        success: res => {
          app.globalData.userInfo = res.userInfo
          this.setData({
            userInfo: res.userInfo,
          })
        }
      })
    }
  },
  
  // 输入时获得手机号
  mobileInputEvent: function (e) {
    this.setData({
      // replace(/\s/g, "")去除所包含的空格
      mobile: e.detail.value.replace(/\s/g, "")
    })
  },

  // 点击获取验证码判断手机号
  validateMobile: function (e) {
      var that = this
      var mobile = this.data.mobile
      if (util.isMobile(mobile)) {
        util.isSend(that)
      }
  },
  // 点击认证提交
  formSubmit: function (e) {
    // console.log('form发生了submit事件，携带数据为：', e.detail.value)
    var mobile = e.detail.value.mobile
    var code = e.detail.value.code
    var _code = this.data._code
    var that = this
    var mobile = this.data.mobile
    if (util.isMobile(mobile)) {
        if (code == _code) {
          wx.switchTab({
            url: "../open/open"
          })
        } else {
          wx.showToast({
            title: '验证码错误'
          })
        }
    }
  }
})
