// pages/open/open.js
Page({
  data: {
    returnData: [
      { doorId: "1", name: "1号楼1单元门", stateCode: "0" },
      { doorId: "2", name: "1号楼2单元门", stateCode: "0" },
      { doorId: "3", name: "1号楼3单元门", stateCode: "1" },
      { doorId: "4", name: "2号楼1单元门", stateCode: "0" },
      { doorId: "5", name: "2号楼2单元门", stateCode: "0" },
      { doorId: "6", name: "2号楼3单元门", stateCode: "0" },
    ],
    doorList: [],
    clickTime: 0,
    iTime:[]
  },
  onLoad: function (options) {
    var that = this
    var length = this.data.returnData.length
    for (var i = 0; i < length; ++i) {
      // console.log(this.data.returnData[i].stateCode)
      if (this.data.returnData[i].stateCode == 0) {
        this.data.doorList.push({
          doorId: this.data.returnData[i].doorId,
          name: this.data.returnData[i].name,
          state: false,   //状态
          disabled: false,   //开门按钮禁用
          dropDown: false    //下拉显示
        })
      } else if (this.data.returnData[i].stateCode == 1) {
        this.data.doorList.push({
          doorId: this.data.returnData[i].doorId,
          name: this.data.returnData[i].name,
          state: true,
          disabled: true,
          dropDown: false
        })
        doorStatusInitial(this, i, 2000)  //门状态初始化  
      }
      this.setData({
        doorList: this.data.doorList
      })
    }
  },
  // 点击取消时
  cancel: function (e) {
    var that = this
    var index = e.target.dataset.id - 1
    clearTimeout(this.data.iTime)
    doorStatusInitial(that, index, 500)
    invert(that, false) //禁用
    return false;
  },
  // 点击开门时
  openDoor: function (e) {
    var that = this
    open(that,e)
  },

})

// 门状态初始化，状态改为已关闭、开门按钮启用、下拉框收起
function doorStatusInitial(that, index, time) {
  setTimeout(function () {
    that.data.doorList[index].state = false
    that.data.doorList[index].disabled = false
    that.data.doorList[index].dropDown = false
    that.setData({
      doorList: that.data.doorList
    })
  }, time) //延迟时间 这里是1秒 
}

// 禁用开门按钮
function invert(that,a){
  var length = that.data.doorList.length
  for (var j = 0; j < length; ++j) {
      that.data.doorList[j].disabled = a
  }
}


function open(that,e){
  // 开门按钮禁用、打开下拉框
  var index = e.target.dataset.id - 1
  invert(that, true) //禁用
  that.data.doorList[index].dropDown = true
  that.setData({
    doorList: that.data.doorList
  })

  //延迟2秒显示成功或失败
  that.data.iTime = setTimeout(function () {
    // 随机一个0或1模拟开门成功与失败
    var a = Math.random()
    var stateCode = Math.round(a)
    console.log(stateCode)
    if (stateCode == 0) {   //模拟状态0为开门成功
      doorStatusInitial(that, index, 1500)  //门状态初始化
      wx.showToast({
        title: '开门成功',
        icon: 'success',
        duration: 1500,
        success: function () {
          //状态改为已开启、下拉框收起
          invert(that, false) //不禁用
          that.data.doorList[index].disabled = true
          that.data.doorList[index].state = true
          that.data.doorList[index].dropDown = false
          
          that.setData({
            doorList: that.data.doorList
          })
        }
      })
    } else if (stateCode == 1) {   //模拟状态1为开门失败
      wx.showModal({
        title: '开门失败',
        content: that.data.doorList[index].name + '开启失败，您可以选择上报问题或重新开门',
        cancelText: "重新开门",
        cancelColor: "#09bb07",
        confirmText: "上报问题",
        confirmColor: "#d81e06",
        success: function (res) {
          if (res.confirm) {
            // console.log("点击右侧确定-上报问题")
            doorStatusInitial(that, index, 1500)  //门状态初始化
            invert(that, false) //不禁用
            wx.navigateTo({
              url: '../upProblem/upProblem?name=' + that.data.doorList[index].name + '&problem=无法开启'
            })
          } else {
            // console.log("点击左侧取消或安卓点击蒙层-重新开门")
            // invert(that, index, false, false) //反选
            // doorStatusInitial(that, index, 500)  //
            open(that, e)
          }
        }
      })
    }
  }, 2000) //延迟时间 这里是1秒 
}