package net.tilapiamc.command


abstract class AbstractCommand<T>(val name: String, val description: String, ) {
    abstract val aliases: ArrayList<String>

    abstract fun matches(commandName: String, sender: T): Boolean
    abstract fun execute(commandAlias: String, sender: T, args: Array<String>)
    abstract fun tabComplete(commandAlias: String, sender: T, args: Array<String>): Collection<String>
    abstract fun getUsageString(): String

}