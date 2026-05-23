package com.wiidesk.app.feature.shell

enum class WiPage(val label: String, val navIndex: Int? = null) {
    Scan("Escanear", 0),
    Stream("Conectado", 1),
    List("Lista", 2),
    Settings("Ajustes", 3);

    val isMain get() = true

    companion object {
        val mainPages = listOf(Scan, Stream, List, Settings)
    }
}
