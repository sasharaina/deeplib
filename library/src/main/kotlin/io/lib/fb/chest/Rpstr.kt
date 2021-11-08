package io.lib.fb.chest

import androidx.lifecycle.LiveData
import io.lib.fb.chest.persistroom.LinkDao
import io.lib.fb.chest.persistroom.model.Link
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class Rpstr(var linkDao: LinkDao) {

    val readAllData: LiveData<List<Link>> = linkDao.getAll()


    fun getAllData(): List<Link>{
        return linkDao.getAllData()
    }

    fun insert(link: Link){
        GlobalScope.launch(Dispatchers.IO){ linkDao.addLink(link) }
    }

    fun updateLink(link: Link){
        GlobalScope.launch(Dispatchers.IO) { linkDao.updateLink(link)  }
    }
}