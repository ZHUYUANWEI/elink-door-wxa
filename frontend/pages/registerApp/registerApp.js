// pages/registerApp/registerApp.js
Page({
  data: {
    index: '0',
    res: '',
    app: [
      // { appName: 'com.ceshi', token: '080c697c835b85c4c9c4fe99478316dd', disabled: 'false' },
      // { appName: 'APP2222', token: '521c4c75cea5a8578bd0f29ef499a102', disabled: 'false' },
      // { appName: 'APP3333', token: 'c4a5a84a0f36cd41865b86502b17b369', disabled: 'false' },
      { appName: 'com.ceshi', token: '3c8c45647154cd25137ac925cad32f8c', disabled: 'false' },
      { appName: 'APP2', token: '6f2e8a2b80d8b6ff0e6d4c8bd5eea93c', disabled: 'false' },
      { appName: 'APP3', token: 'b3d341923fb49feeff8cdffcefdd74ef', disabled: 'false' },
      { appName: 'APP4', token: '068520f4f8e5e23ce08576f1bf07414f', disabled: 'false' },
      { appName: 'APP5', token: 'ab497170444232660b5423533db73d0b', disabled: 'false' },
      { appName: '其它', token: '', disabled: '' },
    ],

  },
  onLoad: function (options) {

  },
  //选择
  bindPickerChange: function (e) {
    // console.log('picker发送选择改变，携带值为', e.detail.value)
    this.setData({
      index: e.detail.value
    })
  },

  //提交
  formSubmit: function (e) {
    var that = this
    // console.log(e.detail.value)
    // if (e.detail.value.token == '' || e.detail.value.jPushAppId == '' || e.detail.value.jPushMasterSecret == '') {
    //   wx.showToast({
    //     title: '有参数为空',
    //   })
    //   return
    // }

    wx.request({
      url: 'http://wxa1.estos.elinkit.com.cn/elinkDoorBackend/viAppRegistered',
      data: {
        token: e.detail.value.token,
        jPushAppId: e.detail.value.jPushAppId,
        jPushMasterSecret: e.detail.value.jPushMasterSecret,
      },
      header: {
        'content-type': 'application/json' // 默认值
      },
      success: function (res) {
        console.log(res)
        var errCode = res.data.errCode
        var r = JSON.stringify(res.data)
        that.setData({
          res: '返回信息:'+r
        })
        // 成功
        if (errCode == 0) {
          console.log('注册成功')
          wx.showToast({
            title: '注册成功',
          })
        } else {     // 失败
          console.log('注册失败')
          wx.showToast({
            title: '注册失败',
          })
        }
      },
      fail: function () {
        console.log('请求失败')
        wx.showToast({
          title: '请求失败',
        })
      }
    })
  }
})