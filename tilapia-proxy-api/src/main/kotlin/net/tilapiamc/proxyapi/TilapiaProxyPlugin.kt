package net.tilapiamc.proxyapi

class TilapiaProxyPlugin {

    val schemaAccess = ArrayList<String>()

    fun requireSchemaAccess(table: String) {
        schemaAccess.add(table)
    }


}