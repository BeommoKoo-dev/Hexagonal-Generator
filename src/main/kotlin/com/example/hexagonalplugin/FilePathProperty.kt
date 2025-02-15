package com.example.hexagonalplugin

enum class FilePathProperty(
    private val path: String
) {
    ADAPTER_IN("/adapter/in"),
    ADAPTER_OUT("/adapter/out"),
    PORT_IN("/application/port/in"),
    PORT_OUT("/application/port/out"),
    SERVICE("/domain/service");

    fun getFilePath(separator: String): String {
        return this.path.replace("/", separator)
    }
}
