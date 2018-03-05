// pages/getFaceUpdate/getFaceUpdate.js
const app = getApp()
var util = require("../../utils/util.js");
Page({
  data: {
    index: '0',
    projectId: 'hk8700projectIdTest',
    personIds: [],
    items: [],
    tempFilePaths: [],
    disabled: true
  },
  onLoad: function (options) {
    var that = this
    this.setData({
      items: app.globalData.items,
    })
    // 获取personIds[]
    var personId = parseInt(this.data.items[0].personId)
    this.data.personIds.push(personId)
    console.log(this.data.personIds)
    // 获取人脸信息公共方法
    getFaceInfo(that, function () {
      if (that.data.list[0].faceUrl) {
        that.data.items[that.data.index].faceUrl = that.data.list[0].faceUrl
      }
      that.setData({
        items: that.data.items,
      })
    })
  },
  // 选择用户ID
  bindPickerChange: function (e) {
    var that = this
    this.data.index = e.detail.value
    this.data.items[e.detail.value].faceUrl = ''
    this.setData({
      index: this.data.index,
      items: this.data.items,
      personIds: [],
      disabled: true
    })
    // console.log(this.data.index)
    // 获取personIds[]
    var personId = parseInt(this.data.items[this.data.index].personId)
    this.data.personIds.push(personId)
    console.log(this.data.personIds)
    // 获取人脸信息公共方法
    getFaceInfo(that, function () {
      if (that.data.list.length > 0) {
        that.data.items[that.data.index].faceUrl = that.data.list[0].faceUrl
      }
      that.setData({
        items: that.data.items,
      })
    })
  },
  // 预览图片
  previewImage: function (e) {
    // console.log(e)
    var current = [e.currentTarget.dataset.src];
    // console.log(current)
    wx.previewImage({
      current: current, // 当前显示图片的http链接
      urls: current, // 需要预览的图片http链接列表
    })
  },
  // 选择图片
  addImg: function () {
    var that = this;
    wx.chooseImage({
      count: 1, // 默认9
      sizeType: ['original', 'compressed'], // 指定是原图还是压缩图
      sourceType: ['album', 'camera'], // 指定来源是相册还是相机
      success: function (res) {
        // console.log(res)
        var size = res.tempFiles[0].size / 1024 / 1024
        console.log(size + "M")
        // 返回选定照片的本地文件路径列表，tempFilePath可以作为img标签的src属性显示图片
        var tempFilePaths = res.tempFilePaths
        var path = tempFilePaths[0];
        // console.log(path)
        //如果包含有"/"号 从最后一个"/"号+1的位置开始截取字符串
        if (path.indexOf(".") > 0) {
          var imgtype = path.substring(path.lastIndexOf(".") + 1, path.length).toLowerCase();
          console.log(imgtype)
        }
        if (imgtype == "jpeg" || imgtype == "jpg") {
          // console.log("图片格式正确！")
          if (size < 1) {
            that.data.items[that.data.index].faceUrl = tempFilePaths[0]
            that.setData({
              items: that.data.items,
              tempFilePaths: tempFilePaths,
              disabled: false
            })
            app.globalData.items = that.data.items
          } else {
            console.log("图片要小于1M,")
            wx.showToast({
              title: '图片要小于1M',
            })
          }
        } else {
          console.log("图片格式不正确,")
          wx.showToast({
            title: '图片格式不正确',
          })
        }

      }
    })
  },
  uploadFile: function () {
    var that = this
    this.setData({
      disabled: true
    })
    wx.uploadFile({
      url: app.globalData.HttpsUpLoad + '/elinkDoorBackend/getFaceUpdate',
      filePath: this.data.tempFilePaths[0],
      name: 'file',
      formData: {
        'projectId': this.data.projectId,
        'personId': this.data.items[this.data.index].personId,
        'cardNo': this.data.items[this.data.index].cardNo,
        'filePath': this.data.tempFilePaths[0]
      },
      fail: function (res) {
        console.log('提交失败')
        wx.showToast({
          title: '提交失败'
        })
      },
      success: function (res) {
        console.log(res)
        var data = JSON.parse(res.data)
        // console.log(data)
        var errCode = data.errCode
        // 成功
        if (errCode == 0) {
          // 获取人脸信息公共方法
          getFaceInfo(that, function () {
            if (that.data.list.length > 0) {
              that.data.items[that.data.index].faceUrl = that.data.list[0].faceUrl
            }
            that.setData({
              items: that.data.items,
            })
          })
          wx.showToast({
            title: "提交成功",
          })
        }
        // 失败
        if (errCode > 0) {
          var errMsg = data.errMsg
          wx.showToast({
            title: errMsg
          })
        }
      }
    })
  }
})




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
          title: '失败'
        })
      }
    }
  })
} 