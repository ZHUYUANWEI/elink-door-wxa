// pages/getFaceInfo/getFaceInfo.js
const app = getApp()
var util = require("../../utils/util.js");
Page({
  data: {
    projectId: 'hk8700projectIdTest',
    items: [],
    personIds: [],
    list: [],
    checkedIndex: []
  },
  onLoad: function (options) {
    this.setData({
      items: app.globalData.items
    })
  },

  // 选取改变时
  checkboxChange: function (e) {

    this.setData({
      checkedIndex: e.detail.value,
      // items: this.data.items
    })
    console.log(this.data.checkedIndex)
  },

  // 点击获取
  getFace: function (e) {
    var that = this
    // 每次提交checked先置空全部设为false，再将选中的设为true
    for (var j1 = 0; j1 < this.data.items.length; j1++) {
      this.data.items[j1].checked = false
    }
    // 将选取的设为true
    for (var i1 = 0; i1 < this.data.checkedIndex.length; i1++) {
      var i1checked = this.data.checkedIndex[i1]
      this.data.items[i1checked].checked = true
    }
    //每次获取前将list/personIds清空
    this.setData({
      list: [],
      personIds: [],
      items: this.data.items
    })
    // 获取personIds[]
    for (var i = 0; i < this.data.checkedIndex.length; i++) {
      var personId = parseInt(this.data.items[this.data.checkedIndex[i]].personId)
      this.data.personIds.push(personId)
    }
    console.log(this.data.personIds)
    
    getFaceInfo(that, function () {  // 获取人脸信息公共方法
      for (var c = 0; c < that.data.items.length; c++) {
        for (var d = 0; d < that.data.list.length; d++) {
          if (that.data.items[c].personId == that.data.list[d].personId) {
            that.data.items[c].faceUrl = that.data.list[d].faceUrl
            that.data.list.splice(d, 1)
          }
        }
      }
      that.setData({
        items: that.data.items
      })
    })   

  },

  // 清除信息
  resetFace: function () {
    for (var i = 0; i < this.data.items.length; i++) {
      this.data.items[i].checked = false
    }
    this.setData({
      list: [],
      checkedIndex: [],
      items: this.data.items
    })
    // console.log(this.data.list)
  }
})


// 排序函数
function sortNumber(a, b) {
  return a - b
}
// 获取人脸信息
function getFaceInfo(that, callback) {
  wx.request({
    url: app.globalData.HttpsRequest + '/elinkDoorBackend/getFaceInfo',
    data: {
      projectId: that.data.projectId,
      personIds: that.data.personIds
    },
    header: {
      'content-type': 'application/json' // 默认值
    },
    fail: function () {
      wx.showToast({
        title: '请求失败'
      })
    },
    success: function (res) {
      console.log(res)
      // console.log(res.data.result)
      var errCode = res.data.errCode
      var errMsg = res.data.errMsg
      // 成功
      if (errCode == 0) {
        console.log(res.data.result)
        that.setData({
          list: res.data.result
        })

        callback()
      } else { // 失败
        wx.showToast({
          title: '获取失败'
        })
      }
    }
  })
} 