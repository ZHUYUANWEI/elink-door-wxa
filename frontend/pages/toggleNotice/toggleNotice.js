// pages/toggleNotice/toggleNotice.js
Page({
  data: {
    res: '',
    tokenIndex: '0',
    projectIdIndex: '0',
    tokens: [
      // { token: '080c697c835b85c4c9c4fe99478316dd', disabled: false },
      // { token: '521c4c75cea5a8578bd0f29ef499a102', disabled: false },
      // { token: 'c4a5a84a0f36cd41865b86502b17b369', disabled: false },
      { token: '3c8c45647154cd25137ac925cad32f8c', disabled: false },
      { token: '6f2e8a2b80d8b6ff0e6d4c8bd5eea93c', disabled: false },
      { token: 'b3d341923fb49feeff8cdffcefdd74ef', disabled: false },
      { token: '068520f4f8e5e23ce08576f1bf07414f', disabled: false },
      { token: 'ab497170444232660b5423533db73d0b', disabled: false },
      { token: '其它', disabled: true },           //其它必须放在最后
    ],
    projectIds: [
      { projectId: 'hk8700projectIdTest', disabled: false },
      { projectId: '791a92f601c02eb48febf55f55897940', disabled: false },
      { projectId: '其它', disabled: true },        //其它必须放在最后
    ],
  },
  onLoad: function (options) {

  },

  //选择token
  bindTokenChange: function (e) {
    // console.log(e.detail.value)
    this.setData({
      tokenIndex: e.detail.value
    })
  },

  //选择projectId
  bindProjectIdChange: function (e) {
    // console.log(e.detail.value)
    this.setData({
      projectIdIndex: e.detail.value
    })
  },

  formSubmit: function (e) {
    var that = this
    // console.log(e.detail.value)
    // console.log(e.detail.target.dataset.acceptnotice)
    var tokenLength = this.data.tokens.length - 1
    var projectIdLength = this.data.projectIds.length - 1
    var token = this.data.tokens[e.detail.value.tokena].token
    var projectId = this.data.projectIds[e.detail.value.projectIda].projectId
    //根据数组长度判断选中的只不是最后一个（其它）
    if (e.detail.value.tokena == tokenLength) {
      token = e.detail.value.tokenb
    }
    if (e.detail.value.projectIda == projectIdLength) {
      projectId = e.detail.value.projectIdb
    }
    var roomInfo = e.detail.value.roomInfo
    var registrationId = e.detail.value.registrationId
    var acceptNotice = e.detail.target.dataset.acceptnotice
    // console.log('token', token)
    // console.log('projectId', projectId)
    // console.log('roomInfo', roomInfo)
    // console.log('registrationId', registrationId)
    // console.log('acceptNotice', acceptNotice)
    if (acceptNotice) {
      // console.log('有')
      //打开、关闭
      wx.request({
        url: 'http://wxa1.estos.elinkit.com.cn/elinkDoorBackend/whetherReceiveNotice',
        data: {
          token: token,
          projectId: projectId,
          roomInfo: roomInfo,
          registrationId: registrationId,
          acceptNotice: acceptNotice
        },
        header: {
          'content-type': 'application/json' // 默认值
        },
        success: function (res) {
          console.log(res.data)
          var errCode = res.data.errCode
          var r = JSON.stringify(res.data)
          that.setData({
            res: '返回信息:' + r
          })
          // 成功
          if (errCode == 0) {
            console.log('成功')
            wx.showToast({
              title: '成功',
            })
          } else {     // 失败
            console.log('失败')
            wx.showToast({
              title: '失败',
            })
          }
        },
        fail: function () {
          console.log('请求失败')
        }
      })
    } else {
      // console.log('没有')
      //查询
      wx.request({
        url: 'http://wxa1.estos.elinkit.com.cn/elinkDoorBackend/selectNoticeflag',
        data: {
          token: token,
          projectId: projectId,
          roomInfo: roomInfo,
          registrationId: registrationId
        },
        header: {
          'content-type': 'application/json' // 默认值
        },
        success: function (res) {
          console.log(res.data)
          var errCode = res.data.errCode
          var r = JSON.stringify(res.data)
          that.setData({
            res: '返回信息:' + r
          })
          // 成功
          if (errCode == 0) {
            console.log('获取成功')
            wx.showToast({
              title: '获取成功',
            })
          } else {     // 失败
            console.log('获取失败')
            wx.showToast({
              title: '获取失败',
            })
          }
        },
        fail: function () {
          console.log('请求失败')
        }
      })
    }
  }

})