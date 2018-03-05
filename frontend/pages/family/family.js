// pages/family/family.js
//获取应用实例
const app = getApp()
Page({
  data: {
    _index: '0',
    family: {}
  },
  onShow: function (options) {
    // 获取全局变量
    this.setData({
      family: app.globalData.family
    })
  },
  // 选择房间
  bindPickerChange: function (e) {
    this.setData({
      _index: e.detail.value,
    })
  },
  // 点击返回成员分类与成员index并删除
  deletInfo: function (e) {
    var that = this
    var _index = this.data._index
    var classId = e.target.id     //成员分类index
    var familyId = e.target.dataset.id   //成员index
    var className = this.data.family[_index].classification[classId].className
    var name = this.data.family[_index].classification[classId].familyInfo[familyId].name
    wx.showModal({
      title: '删除确认',
      content: '您确定要删除' + className + '：' + name + '吗？',
      success: function (res) {
        if (res.confirm) {
          // console.log('用户点击确定')
          that.data.family[_index].classification[classId].familyInfo.splice(familyId, 1)
          that.setData({
            family: that.data.family,
          })
          app.globalData.family = that.data.family
        } else if (res.cancel) {
          // console.log('用户点击取消')
        }
      }
    })
  },
  // 点击添加
  appInfo: function (e) {
    var _index = this.data._index
    var classId = e.target.id     //成员分类index
    wx.navigateTo({
      url: '../addFamily/addFamily?_index=' + _index + '&classId=' + classId
    })
  }

})