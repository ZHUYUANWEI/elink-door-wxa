//app.js
App({
  onLaunch: function () {
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)

    // 登录
    wx.login({
      success: res => {
        // 发送 res.code 到后台换取 openId, sessionKey, unionId
      }
    })
    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo

              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
  },
  globalData: {
    HttpsRequest:'http://wxa1.estos.elinkit.com.cn',
    HttpsUpLoad:'http://wxa1.estos.elinkit.com.cn',
    userInfo: null,
    items: [
      { personId: '1', checked: false, faceUrl: '', cardNo: '2017001' },
      { personId: '2', checked: false, faceUrl: '', cardNo: '2017002' },
      { personId: '3', checked: false, faceUrl: '', cardNo: '2017003' }
    ],
    demo: [
      {
        platformName: '8700平台对接演示',
        url: '',
        maxFunction: [
          {
            maxFunctionName: '二维码开门',
            url: '',
            minFunction: [
              {
                minFunctionName: '获取访客二维码',
                url: '../visitorTwoCode/visitorTwoCode'
              },
              {
                minFunctionName: '获取用户二维码',
                url: '../userTwoCode/userTwoCode'
              }
            ]
          },
          {
            maxFunctionName: '人脸识别',
            url: '',
            minFunction: [
              {
                minFunctionName: '获取人脸信息',
                url: '../getFaceInfo/getFaceInfo'
              },
              {
                minFunctionName: '更新人脸信息',
                url: '../getFaceUpdate/getFaceUpdate'
              }
            ]
          },
          {
            maxFunctionName: '可视对讲',
            url: '',
            minFunction: [
              {
                minFunctionName: '推送选项设置',
                url: '../toggleNotice/toggleNotice'
              },
              {
                minFunctionName: '查询户室设备',
                url: '../devicesOfRoom/devicesOfRoom'
              }
            ]
          }
        ]
      },
      {
        platformName: '极光推送消息演示',
        url: '',
        maxFunction: [
          {
            maxFunctionName: '注册App',
            url: '../registerApp/registerApp'
          }
        
        ]
      }
    ],
    family: [
      {
        roomName: "1期2号楼4单元202室",
        classification: [
          {
            className: "家人",
            familyInfo: [
              {
                name: "李二",
                mobile: "13145678933",
                time: "2100-12-31"
              },
              {
                name: "张三",
                mobile: "13055678944",
                time: "2100-12-31"
              }
            ]
          },
          {
            className: "租户",
            familyInfo: [
              {
                name: "李四",
                mobile: "13045678955",
                time: "2018-10-20"
              },
              {
                name: "王三",
                mobile: "13055678966",
                time: "2020-12-11"
              }
            ]
          },
          {
            className: "访客",
            familyInfo: [
              {
                name: "张三",
                mobile: "13045678977",
                time: "2017-09-22"
              },
              {
                name: "李四",
                mobile: "13055678988",
                time: "2017-09-19"
              }
            ]
          }
        ]
      },
      {
        roomName: "2期5号楼21单元101室",
        classification: [
          {
            className: "家人",
            familyInfo: [
              {
                name: "赵六",
                mobile: "13045678933",
                time: "2100-12-31"
              },
              {
                name: "李四",
                mobile: "13055678944",
                time: "2100-12-31"
              }
            ]
          },
          {
            className: "租户",
            familyInfo: [
              {
                name: "赵六小",
                mobile: "13045678955",
                time: "2100-12-31"
              },
              {
                name: "赵四",
                mobile: "13055678966",
                time: "2100-12-31"
              }
            ]
          },
          {
            className: "访客",
            familyInfo: [
              {
                name: "张大三",
                mobile: "13045678977",
                time: "2100-12-31"
              },
              {
                name: "李大四",
                mobile: "13055678988",
                time: "2100-12-31"
              }
            ]
          }
        ]
      },
      {
        roomName: "3期7号楼3单元503室",
        classification: [
          {
            className: "家人",
            familyInfo: [
              {
                name: "王五",
                mobile: "13045678933",
                time: "2100-12-31"
              },
              {
                name: "李四",
                mobile: "13055678944",
                time: "2100-12-31"
              }
            ]
          },
          {
            className: "租户",
            familyInfo: [
              {
                name: "王小五",
                mobile: "13045678955",
                time: "2100-12-31"
              },
              {
                name: "李小四",
                mobile: "13055678966",
                time: "2100-12-31"
              }
            ]
          },
          {
            className: "访客",
            familyInfo: [
              {
                name: "王大五",
                mobile: "13045678977",
                time: "2100-12-31"
              },
              {
                name: "李大四",
                mobile: "13055678988",
                time: "2100-12-31"
              }
            ]
          }
        ]
      }
    ]
  }
})