package com.liftric.kvault

expect class KVault {
    /**
     * Saves a string value in the store.
     * @param key The key to store
     * @param value The value to store
     */
    fun set(key: String, value: String): Boolean

    /**
     * Saves an int value in the store.
     * @param key The key to store
     * @param value The value to store
     */
    fun set(key: String, value: Int): Boolean

    /**
     * Saves a long value in the store.
     * @param key The key to store
     * @param value The value to store
     */
    fun set(key: String, value: Long): Boolean

    /**
     * Saves a float value in the store.
     * @param key The key to store
     * @param value The value to store
     */
    fun set(key: String, value: Float): Boolean

    /**
     * Saves a double value in the store.
     * @param key The key to store
     * @param value The value to store
     */
    fun set(key: String, value: Double): Boolean

    /**
     * Saves a boolean value in the store.
     * @param key The key to store
     * @param value The value to store
     */
    fun set(key: String, value: Boolean): Boolean

    /**
     * Checks if object with key exists in the store.
     * @param forKey The key to query
     * @return True or false, depending on wether it is in the store or not
     */
    fun existsObject(forKey: String): Boolean

    /**
     * Returns the string value of an object in the store.
     * @param forKey The key to query
     * @return The stored string value
     */
    fun string(forKey: String): String?

    /**
     * Returns the int value of an object in the store.
     * @param forKey The key to query
     * @return The stored int value
     */
    fun int(forKey: String): Int?

    /**
     * Returns the long value of an object in the store.
     * @param forKey The key to query
     * @return The stored long value
     */
    fun long(forKey: String): Long?

    /**
     * Returns the double value of an object in the store.
     * @param forKey The key to query
     * @return The stored double lue
     */
    fun double(forKey: String): Double?

    /**
     * Returns the float value of an object in the store.
     * @param forKey The key to query
     * @return The stored float value
     */
    fun float(forKey: String): Float?

    /**
     * Returns the boolean value of an object in the keychain.
     * @param forKey The key to query
     * @return The stored boolean value
     */
    fun bool(forKey: String): Boolean?

    /**
     * Deletes object with the given key from the store.
     * @param forKey The key to query
     */
    fun deleteObject(forKey: String): Boolean

    /**
     * Deletes all objects with the service name from the store.
     */
    fun clear()
}
