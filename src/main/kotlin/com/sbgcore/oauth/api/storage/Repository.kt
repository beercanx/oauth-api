package com.sbgcore.oauth.api.storage

interface Repository<TYPE, ID> {

    fun insert(new: TYPE)

    fun update(changed: TYPE)

    fun delete(id: ID)

    fun findById(id: ID): TYPE
}