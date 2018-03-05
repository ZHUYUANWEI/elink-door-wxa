const formatTime = date => {
  const year = date.getFullYear()
  const month = date.getMonth() + 1
  const day = date.getDate()
  const hour = date.getHours()
  const minute = date.getMinutes()
  const second = date.getSeconds()

  return [year, month, day].map(formatNumber).join('/') + ' ' + [hour, minute, second].map(formatNumber).join(':')
}

const formatNumber = n => {
  n = n.toString()
  return n[1] ? n : '0' + n
}

// 正则表达判断手机号
function isMobile(mobile) {
  // replace(/\s/g, "")去除所包含的空格
  var mobile = mobile.replace(/\s/g, "")
  if (mobile.length != 11) {
    wx.showToast({
      title: '手机长度错误',
      icon: 'success',
      duration: 1500
    })
    return false
  }
  var myreg = /^(((13[0-9]{1})|(14[0-9]{1})|(15[0-9]{1})|(18[0-9]{1})|(17[0-9]{1}))+\d{8})$/;
  if (!myreg.test(mobile)) {
    wx.showToast({
      title: '手机格式错误',
      icon: 'success',
      duration: 1500
    })
    return false
  }
  return true
}

// 短信是否发送
function isSend(that) {
  var mobile = that.data.mobile
  var _mobile = that.data._mobile
  if (mobile == _mobile) {
    wx.showToast({
      title: '短信已发送',
      icon: 'success',
      duration: 1500
    })
    //验证码倒计时（演示使用）
    var total_micro_second = 60 * 1000
    count_down(that, total_micro_second);
  } else {
    wx.showModal({
      title: '提示',
      content: '    您的手机号未在物业留存，请到物业留存或由户主添加成员后登录。',
      showCancel: false,
      success: function (res) {
        if (res.confirm) {
          // console.log('用户点击确定')
        }
      }
    })
  }
}
/* 毫秒级倒计时 */
function count_down(that, total_micro_second) {
  if (total_micro_second <= 0) {
    that.setData({
      VerifyCode: "重新发送",
      disabled: false
    });
    // timeout则跳出递归
    return;
  }
  // 渲染倒计时时钟
  that.setData({
    disabled: true,
    VerifyCode: date_format(total_micro_second) + " 秒"
  });
  setTimeout(function () {
    // 放在最后--
    total_micro_second -= 10;
    count_down(that, total_micro_second);
  }, 10)
}
// 时间格式化输出，如03:25:19 86。每10ms都会调用一次
function date_format(micro_second) {
  // 秒数
  var second = Math.floor(micro_second / 1000);
  // 小时位
  var hr = Math.floor(second / 3600);
  // 分钟位
  var min = fill_zero_prefix(Math.floor((second - hr * 3600) / 60));
  // 秒位
  var sec = fill_zero_prefix((second - hr * 3600 - min * 60));// equal to => var sec = second % 60;
  // 毫秒位，保留2位
  var micro_sec = fill_zero_prefix(Math.floor((micro_second % 1000) / 10));
  return sec;
}
// 位数不足补零
function fill_zero_prefix(num) {
  return num < 10 ? "0" + num : num
}

// 获取当前时间，格式YYYY-MM-DD
function getNowFormatDate() {
  var date = new Date();
  var seperator1 = "-";
  var year = date.getFullYear();
  var month = date.getMonth() + 1;
  var strDate = date.getDate();
  if (month >= 1 && month <= 9) {
    month = "0" + month;
  }
  if (strDate >= 0 && strDate <= 9) {
    strDate = "0" + strDate;
  }
  var currentdate = year + seperator1 + month + seperator1 + strDate;
  return currentdate;
}
// 获取当前时间，格式hh:mm
function getNowFormatHour() {
  var date = new Date();
  var seperator2 = ":";
  var hours = date.getHours();
  var minutes = date.getMinutes();
  if (hours >= 0 && hours <= 9) {
    hours = "0" + hours;
  }
  if (minutes >= 0 && minutes <= 9) {
    minutes = "0" + minutes;
  }
  var currentdate = hours + seperator2 + minutes;
  return currentdate;
}

// 判断中文名字
function IsChinese(str) {
  var han = /^[\u4e00-\u9fa5]+$/;
  // console.log(str.length)
  if (str == '') {
    wx.showToast({
      title: '请输入姓名',
    })
    return false;
  };
  if (str.length > 4) {
    wx.showToast({
      title: '名字过长',
    })
    return false;
  };
  if (!han.test(str)) {
    wx.showToast({
      title: '不是中文',
    })
    return false;
  };
  return true;
};

// 判断开始时间不能小于结束时间
function checkDate(startDate, endDate, startHour, endHour) {
  if (startDate.length > 0 && endDate.length > 0) {
    var startTmp = startDate.split("-");
    var endTmp = endDate.split("-");
    var startHo = startHour.split(":");
    var endHo = endHour.split(":");
    var sd = new Date(startTmp[0], startTmp[1], startTmp[2], startHo[0], startHo[1]);
    var ed = new Date(endTmp[0], endTmp[1], endTmp[2], endHo[0], endHo[1], 59);
    if (sd.getTime() > ed.getTime()) {
      // console.log("开始日期不能大于结束日期");
      return false;
    }
  }
  // console.log("开始日期小于结束日期");
  return true;
}

// 二维码输出参数
function towCodeOutParameter(res, that) {
  console.log(res)
  var errCode = res.data.errCode
  // 成功
  if (errCode == 0) {
    that.setData({
      imgUrl: res.data.result.qrUrl,
      result: true
    })
  } else {     // 失败
  console.log('返回错误失败')
    wx.showToast({
      title: '获取失败',
    })
  }
}
      


module.exports = {
  formatTime: formatTime,
  isMobile: isMobile,
  getNowFormatDate: getNowFormatDate,
  getNowFormatHour: getNowFormatHour,
  IsChinese: IsChinese,
  isSend: isSend,
  checkDate: checkDate,
  towCodeOutParameter: towCodeOutParameter
}
