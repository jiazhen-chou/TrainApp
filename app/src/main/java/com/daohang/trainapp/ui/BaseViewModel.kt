package com.daohang.trainapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.daohang.trainapp.MyApplication
import com.daohang.trainapp.db.TrainDatabase
import com.daohang.trainapp.network.ApiFactory
import com.daohang.trainapp.network.repositories.CommonRepository
import com.daohang.trainapp.network.repositories.ProjectRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

open class BaseViewModel(application: Application) : AndroidViewModel(application){

    val database = TrainDatabase.getDataBase(application)

    val projectRepository: ProjectRepository = ProjectRepository(ApiFactory.api)

//    val commonRepository: CommonRepository = CommonRepository(ApiFactory.projectApi)

    val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.Default

    val scope = CoroutineScope(coroutineContext)
    
}