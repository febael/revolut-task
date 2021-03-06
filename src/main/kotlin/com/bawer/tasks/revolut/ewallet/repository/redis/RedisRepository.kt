package com.bawer.tasks.revolut.ewallet.repository.redis

import com.bawer.tasks.revolut.ewallet.repository.Repository
import com.google.gson.Gson
import redis.clients.jedis.JedisPool
import java.lang.Boolean.TRUE


abstract class RedisRepository<T, I>(
        private val objClass: Class<T>,
        private val jedisPool: JedisPool,
        private val gson: Gson,
        protected val hashName: String
) : Repository<T, I> {

    protected val jedis get() = jedisPool.resource

    protected fun serialize(obj: T): String = gson.toJson(obj)

    protected fun deserialize(string: String?): T? = gson.fromJson(string, objClass)

    protected fun T.id() = getId(this).toString()

    override fun get(id: I) = jedis.hget(hashName, id.toString())?.let { deserialize(it) }

    override fun getAll(): List<T> = jedis.hgetAll(hashName).values.map { this.deserialize(it)!! }

    /**
     * Doesn't allow update
     */
    override fun insert(obj: T) = jedis.hsetnx(hashName, obj.id(), serialize(obj))?.let { it == 1L } ?: false

    /**
     * Doesn't allow updates, a bit enigmatic implementation
     */
    override fun insertAll(vararg objs: T) = objs.mapTo(HashSet(), this::insert).run { size == 1 && contains(TRUE) }

    /**
     * Allows update:
     * 1L return code is insertion, 0L is update
     */
    override fun upsert(obj: T) = jedis.hset(hashName, obj.id(), serialize(obj))?.let { it == 1L || it == 0L } ?: false

    /**
     * Allows updates
     */
    override fun upsertAll(vararg objs: T): Boolean {
        val tempMap = objs.associate { it.id() to serialize(it) }
        jedis.hmset(hashName, tempMap)!!.let { return it == "OK" }
    }

    override fun count() = jedis.hlen(hashName)!!.toInt()

    override fun delete(id: I) = jedis.hdel(hashName, id.toString())!! == 1L

    override fun deleteAll(vararg ids: I) = jedis.del(hashName).let { true }
}