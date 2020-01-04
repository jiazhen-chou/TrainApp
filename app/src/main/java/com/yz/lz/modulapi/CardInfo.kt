package com.yz.lz.modulapi

import com.daohang.trainapp.db.models.SavedCardInfo
import com.daohang.trainapp.utils.enumCarType

data class CardInfo(
    //卡类型
    var cardType: CardType = CardType.UnKnown,
    //学员/教练id
    var id: String = "",
    //学员/教练姓名
    var name: String = "",
    //单日最大培训时长
    var practiceTimePerDay: Int = 0,
    //学员/教练身份证号
    var identification: String = "",
    //所属驾校
    var company: String = "",
    //有效期
    var validityPeriod: String = "",
    //车辆类型
    var carType: CarType = CarType.None,
    //科目一总学时
    var subjectOneTotalTime: Int = 0,
    //科目二总学时
    var subjectTwoTotalTime: Int = 0,
    //科目三总学时
    var subjectThreeTotalTime: Int = 0,
    //科目二总里程
    var subjectTwoTotalMiles: Int = 0,
    //科目三总里程
    var subjectThreeTotalMiles: Int = 0,
    //科目四总学时
    var subjectFourTotalMiles: Int = 0,
    /**
     * 第二十六扇区
     */
//卡片状态 0.未签到 1.已签到
    var cardState: Byte = 0,
    //当日训练时长
    var practiceTime: Int = 0,
    //签到次数
    var checkInTimes: Short = 0,
    //科目一已学学时
    var subjectOneLearnedTime: Int = 0,
    //科目二已学学时
    var subjectTwoLearnedTime: Int = 0,
    //科目二培训里程
    var subjectTwoLearnedMiles: Int = 0,
    //科目三已学学时
    var subjectThreeLearnedTime: Int = 0,
    //科目三培训里程
    var subjectThreeLearnedMiles: Int = 0,
    //科目四已学学时
    var subjectFourLearnedTime: Int = 0,
    //最后签退日期
    var lastExitDate: String = "0"
) {

    constructor(
        cardType: CardType,
        id: String,
        name: String,
        identification: String,
        company: String,
        carType: CarType,
        subjectOneTotalTime: Int,
        subjectTwoTotalTime: Int,
        subjectThreeTotalTime: Int,
        subjectTwoTotalMiles: Int,
        subjectThreeTotalMiles: Int,
        subjectFourTotalMiles: Int
    ) : this(
        cardType,
        id,
        name,
        0,
        identification,
        company,
        "",
        carType,
        subjectOneTotalTime,
        subjectTwoTotalTime,
        subjectThreeTotalTime,
        subjectTwoTotalMiles,
        subjectThreeTotalMiles,
        subjectFourTotalMiles
    )

    constructor(info: SavedCardInfo) : this(
        if (info.type == 0) CardType.StudentCard else CardType.CommonCoachCard,
        info.studentNumber,
        info.name,
        0,
        info.identification,
        info.company,
        "",
        enumCarType(info.carType),
        0,
        0,
        0,
        0,
        0,
        0
    )

    fun setCarType(byte: Byte) {
        this.carType = enumCarType(byte)
    }

    override fun toString(): String {
        return "卡类型：$cardType \nid：$id \n 姓名：$name \n身份证号：$identification \n所属驾校：$company \n科目二总学时：$subjectTwoTotalTime \n科目二总里程: " +
                "$subjectTwoTotalMiles \n科目二已学学时：$subjectTwoLearnedTime \n科目二已学里程：$subjectTwoLearnedMiles \n科目三总学时：$subjectThreeTotalTime \n" +
                "科目三总里程：$subjectThreeTotalMiles \n科目三已学学时： $subjectThreeLearnedTime \n科目三已学里程：$subjectThreeLearnedMiles \n最后一次签到日期：$lastExitDate" +
                " \n当天已学学时：$practiceTime 分钟"
    }
}