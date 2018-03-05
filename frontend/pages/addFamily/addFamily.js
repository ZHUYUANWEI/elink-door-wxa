// pages/add/add.js
const app = getApp()
var util = require('../../utils/util.js');
Page({
  data: {
    family: {},
    _index: '',
    classId: '',
    className: '',
    time: '',
    items: [
      { time: '2100-12-31', value: '永久' },
    ],
    disabled: false,
    mobile: ''
  },
  onLoad: function (options) {
    var _index = options._index
    var classId = options.classId
    var className = app.globalData.family[_index].classification[classId].className
    var time = util.getNowFormatDate()
    // 获取全局变量
    this.setData({
      _index: _index,
      time: time,
      classId: classId,
      className: className,
      family: app.globalData.family
    })
  },
  // 时间选择器
  bindDateChange: function (e) {
    this.setData({
      time: e.detail.value
    })
  },
  // 勾选永久
  checkboxChange: function (e) {
    var length = e.detail.value.length
    if (length == 1) {
      this.data.time = '2100-12-31'
      this.data.disabled = !this.data.disabled
    } else {
      this.data.time = util.getNowFormatDate()
      this.data.disabled = !this.data.disabled
    }
    // console.log(this.data.time)
    this.setData({
      time: this.data.time,
      disabled: this.data.disabled
    })
  },
  formSubmit: function (e) {
    // console.log(e)
    var that = this
    var _index = this.data._index
    var classId = this.data.classId
    var name = e.detail.value.name
    var mobile = e.detail.value.mobile.replace(/\s/g, "")
    var time = e.detail.value.time
    // console.log(time)
    if (util.IsChinese(name) && util.isMobile(mobile)) {
      wx.showToast({
        title: '添加成功',
      })
      that.data.family[_index].classification[classId].familyInfo.push({
        name: name,
        mobile: mobile,
        time: time
      })
      this.setData({
        _index: _index,
        classId: classId,
        name: name,
        mobile: mobile,
        time: time,
        family: this.data.family
      })
      // console.log(that.data.family[_index].classification[classId].familyInfo)
      app.globalData.family = that.data.family
      wx.navigateBack({
        delta: 1
      })
    }
    return false
  }


})

