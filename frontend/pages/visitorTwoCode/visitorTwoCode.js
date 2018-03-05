// pages/visitortwocode/visitortwocode.js
const app = getApp()
var util = require('../../utils/util.js');
Page({
  data: {
    projectId: 'hk8700projectIdTest',
    index: '0',
    start: '', //来访、结束起始时间
    startDate: '', //来访日期
    startHour: '',  //来访小时
    endDate: '',   //结束日期
    endHour: '',   //结束小时
    startTime: '',
    endTime: '',
    imgUrl: '',
    result: false,
    visitors: [
      {
        personId: '1',
        cardNo: '2017001',
      },
      {
        personId: '2',
        cardNo: '2017002',
      },
      {
        personId: '3',
        cardNo: '2017003',
      }
    ]
  },
  onLoad: function (options) {
    var startDate = util.getNowFormatDate()
    var startHour = util.getNowFormatHour()
    var start = startDate
    var endDate = startDate
    var endHour = startHour
    // console.log(startHour)
    this.setData({
      startDate: startDate,
      startHour: startHour,
      endDate: endDate,
      endHour: endHour,
      start: start
    })
    this.setData({
      startTime: this.data.startDate + ' ' + this.data.startHour,
      endTime: this.data.endDate + ' ' + this.data.endHour
    })
  },
  // 选择用户ID
  bindPickerChange: function (e) {
    // console.log(e.detail.value)
    this.data.index = e.detail.value
    this.setData({
      index: this.data.index,
      result: false
    })
    //  初始化置空
    var startDate = util.getNowFormatDate()
    var startHour = util.getNowFormatHour()
    var start = startDate
    var endDate = startDate
    var endHour = startHour
    this.setData({
      visitors: this.data.visitors,
      startDate: startDate,
      startHour: startHour,
      endDate: endDate,
      endHour: endHour,
      start: start
    })
    this.setData({
      startTime: this.data.startDate + ' ' + this.data.startHour,
      endTime: this.data.endDate + ' ' + this.data.endHour
    })
  },
  // 来访时间日期
  bindStartDateChange: function (e) {
    this.setData({
      startDate: e.detail.value
    })
    if (util.checkDate(this.data.startDate, this.data.endDate, this.data.startHour, this.data.endHour)) {
      //  console.log("开始日期小于结束日期");
    } else {
      this.setData({
        endDate: e.detail.value,
      })
      //  console.log("开始日期不能大于结束日期");
    }
    this.setData({
      startTime: this.data.startDate + ' ' + this.data.startHour,
      endTime: this.data.endDate + ' ' + this.data.endHour
    })
  },
  // 来访时间小时
  bindStartHourChange: function (e) {
    this.setData({
      startHour: e.detail.value
    })
    if (util.checkDate(this.data.startDate, this.data.endDate, this.data.startHour, this.data.endHour)) {

    } else {
      this.setData({
        endHour: e.detail.value,
      })
    }
    this.setData({
      startTime: this.data.startDate + ' ' + this.data.startHour,
      endTime: this.data.endDate + ' ' + this.data.endHour
    })
  },
  // 结束时间日期小时
  bindEedDateChange: function (e) {
    this.setData({
      endDate: e.detail.value
    })
    if (util.checkDate(this.data.startDate, this.data.endDate, this.data.startHour, this.data.endHour)) {

    } else {
      this.setData({
        endDate: this.data.startDate,
        endHour: this.data.startHour
      })
    }
    this.setData({
      startTime: this.data.startDate + ' ' + this.data.startHour,
      endTime: this.data.endDate + ' ' + this.data.endHour
    })
  },
  // 结束时间小时
  bindEedHourChange: function (e) {
    this.setData({
      endHour: e.detail.value
    })
    if (util.checkDate(this.data.startDate, this.data.endDate, this.data.startHour, this.data.endHour)) {

    } else {
      this.setData({
        endHour: this.data.startHour
      })
    }
    this.setData({
      startTime: this.data.startDate + ' ' + this.data.startHour,
      endTime: this.data.endDate + ' ' + this.data.endHour
    })
  },
  // 提交
  formSubmit: function (e) {
    var that = this
    // console.log(e)
    // 判断是否为空
    if (e.detail.value.period == '') {
      wx.showToast({
        title: '有效期为空',
      })
      return
    } else {
      var firstLetter = e.detail.value.period.substring(0, 1)
      if (firstLetter == 'h' || firstLetter == 'm') {
        var other = e.detail.value.period.substring(1)
        var otherNum = Number(other)
        if (otherNum % 1 != 0 || other == "" || other <= 0) {
          wx.showToast({
            title: '有效期错误',
          })
          return
        }
        else{
          var period = e.detail.value.period
        }
      } else {
        wx.showToast({
          title: '有效期错误',
        })
        return
      }
    }

    if (e.detail.value.unlockCount == '') {
      wx.showToast({
        title: '开锁次数为空',
      })
      return
    } else {
      var other = e.detail.value.unlockCount
      var otherNum = Number(other)
      if (otherNum % 1 != 0 || other == "" || other <= 0) {
        wx.showToast({
          title: '开锁次数错误',
        })
        return
      }
      else {
        var unlockCount = e.detail.value.unlockCount
      }
    }

    if (e.detail.value.visitorName == '') {
      wx.showToast({
        title: '姓名为空',
      })
      return
    } else {
      var visitorName = e.detail.value.visitorName
    }

    if (e.detail.value.phone == '') {
      wx.showToast({
        title: '手机号为空',
      })
      return
    } else {
      if (util.isMobile(e.detail.value.phone)) {
        var phone = e.detail.value.phone
      } else {
        return
      }
    }
    



    wx.request({
      url: app.globalData.HttpsRequest + '/elinkDoorBackend/getGuestQR',
      data: {
        projectId: this.data.projectId,
        personId: this.data.visitors[e.detail.value.personId].personId,
        cardNo: e.detail.value.cardNo,
        period: period,
        unlockCount: unlockCount,
        visitorName: visitorName,
        phone: phone,
        startTime: this.data.startTime,
        endTime: this.data.endTime,
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
