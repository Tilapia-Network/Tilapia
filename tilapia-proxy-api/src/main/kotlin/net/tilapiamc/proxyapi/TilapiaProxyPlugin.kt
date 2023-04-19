package net.tilapiamc.proxyapi

open class TilapiaProxyPlugin {

    val schemaAccess = ArrayList<String>()

    fun requireSchemaAccess(table: String) {
        schemaAccess.add(table)
    }


}