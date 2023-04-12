package net.tilapiamc.api.generators

import net.tilapiamc.api.generators.impl.GeneratorVoid

object Generators {

    val generators = HashMap<String, (String?) -> AbstractGenerator>()

    init {
        registerGenerator { GeneratorVoid(it) }
    }

    fun registerGenerator(generator: (String?) -> AbstractGenerator) {
        generators[generator("").name] = generator
    }

}