package com.daohang.trainapp.db

import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.db.models.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

object DaoHelper {

    private val executor: ExecutorService = Executors.newCachedThreadPool()

    val database = TrainDatabase.getDataBase(MyApplication.instance)

    private fun execute(command: () -> Unit) = executor.execute(command)

    /**
     * 清空数据库
     */
    fun deleteAll() {
        TrainPreference.delete()
        Message.delete()
        Student.delete()
        Coach.delete()
        Preference.deletePreference()
        Location.delete()
        Record.delete()
        Register.delete()
        Photo.delete()
        History.delete()
    }

    object TrainPreference{

        fun insert(model: TrainPreferenceModel) = execute { database.trainPreferenceDao().insertTrainPreference(model) }

        fun getTrainPreferenceLiveData() = database.trainPreferenceDao().getTrainPreferenceLiveData()

        fun getTrainPreference() = database.trainPreferenceDao().getTrainPreference()

        fun delete() = execute { database.trainPreferenceDao().deleteTrainPreference() }
    }

    object Message{
        fun insertMessage(model: MessageModel) = execute {
            database.messageDao().insertMessage(model)
        }

        fun getCount() = database.messageDao().getCount()

        fun getLatestMessage() = database.messageDao().getLatestMessage()

        fun getMessageById(id: Int) = database.messageDao().getMessageById(id)

        fun getAllMessage() = database.messageDao().getAllMessage()

        fun updateMessageSendState(id: Int) = execute { database.messageDao().updateMessageSendState(id) }

        fun updateMessageSendStateBySequence(sequenceNumber: Int) = execute { database.messageDao().updateMessageSendStateBySequence(sequenceNumber) }

        fun delete() = execute { database.messageDao().delete() }
    }

    object Student{
        fun insertStudentLogin(model: StudentStateModel) = execute { database.studentStateDao().insertStudentLogin(model) }

        fun delete(id: Int) = execute { database.studentStateDao().delete(id) }

        fun findStudentUnUploaded() = database.studentStateDao().findStudentUnUploaded()

        fun delete() = execute { database.studentStateDao().delete() }
    }

    object Coach{

        fun insertCoachLogin(model: CoachStateModel) = execute { database.coachStateDao().insertCoachLogin(model) }

        fun delete(id: Int) = execute { database.coachStateDao().delete(id) }

        fun findCoachUnUploaded() = database.coachStateDao().findCoachUnUploaded()

        fun delete() = execute { database.coachStateDao().delete() }
    }

    object Preference{

        fun getPreferenceLiveData() = database.preferenceDao().getPreferenceLiveData()

        fun getPreferenceByIdLiveData(id: Int) = database.preferenceDao().getPreferenceByIdLiveData(id)

        fun deletePreference() = execute { database.preferenceDao().deletePreference() }

        fun insertPreference(preferenceModel: PreferenceModel) = execute { database.preferenceDao().insertPreference(preferenceModel) }

        fun getPreferenceCountLiveData() = database.preferenceDao().getPreferenceCountLiveData()

        fun getPreference() = database.preferenceDao().getPreference()

        fun getPreferenceById(id: Int) = database.preferenceDao().getPreferenceById(id)

        fun getPreferenceCount() = database.preferenceDao().getPreferenceCount()
    }


    object Location{

        fun getLocations() = database.locationDao().getLocations()

        fun insertLocation(locationModel: LocationModel) = execute { database.locationDao().insertLocation(locationModel) }

        fun delete() = execute { database.locationDao().deleteAll() }
    }

    object Record{

        fun getRecord(recordNumber: String) = database.recordDao().getRecord(recordNumber)

        fun getBlindAreaRecords() = database.recordDao().getBlindAreaRecords()

        fun insertRecord(recordModel: RecordModel) = execute { database.recordDao().insertRecord(recordModel) }

        fun delete() = execute { database.recordDao().deleteAllRecords() }

    }

    object Register{

        fun insert(model: RegisterModel) = execute { database.registerDao().insert(model) }

        fun getRegisterInfo() = database.registerDao().getRegisterInfo()

        fun getCount() = database.registerDao().getCount()

        fun delete() = execute { database.registerDao().delete() }
    }

    object Photo{

        fun insert(model: PictureInitModel) = execute { database.pictureInitDao().insert(model) }

        fun getInfoLiveData() = database.pictureInitDao().getLatestPictureInfoLiveData()

        fun getInfo() = database.pictureInitDao().getLatestPictureInfo()

        fun update(id: ByteArray) = execute { database.pictureInitDao().updateStatus(id) }

        fun getInfoById(id: ByteArray) = database.pictureInitDao().getPictureInfoById(id)

        fun delete() = execute { database.pictureInitDao().delete() }
    }

    object History{

        fun insert(model: SavedCardInfo) = execute { database.savedCardInfoDao().insert(model) }

        fun delete() = execute { database.savedCardInfoDao().delete() }

        fun getStudentInfo() = database.savedCardInfoDao().getStudentInfo()

        fun getCoachInfo() = database.savedCardInfoDao().getCoachInfo()

        fun getUnCompleteInfo() = database.savedCardInfoDao().getUnCopmpleteInfo()

        fun getUnCompleteCount() = database.savedCardInfoDao().getUpCompleteCount()
    }
}