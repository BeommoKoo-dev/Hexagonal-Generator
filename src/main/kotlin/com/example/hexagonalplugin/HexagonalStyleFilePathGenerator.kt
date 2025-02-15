package com.example.hexagonalplugin

class HexagonalStyleFilePathGenerator(
    private val domainName: String
) : FilePathGenerator {


    override fun generateWithSeparator(filePathProperty: FilePathProperty, separator: String): String {
        val basePath = filePathProperty.getFilePath(separator)
        return "$basePath${separator}${domainName}"
    }

}
