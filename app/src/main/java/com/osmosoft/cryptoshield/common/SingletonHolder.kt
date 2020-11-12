package com.osmosoft.cryptoshield.common

//try to create a singleton with param
//I don't know how it works
open class SingletonHolder<out T, in A>(private val constructor: (A) -> T) {

    @Volatile
    private var instance: T? = null

    //return instance
    fun getInstance(arg: A): T{
        return when{
            instance != null -> instance!!
            else -> synchronized(this){
                if(instance == null) instance = constructor(arg)
                instance!!
            }
        }
    }
}