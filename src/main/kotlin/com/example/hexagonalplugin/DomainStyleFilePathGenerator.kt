package com.example.hexagonalplugin

class DomainStyleFilePathGenerator : FilePathGenerator {

    override fun generateWithSeparator(filePathProperty: FilePathProperty, separator: String): String {
        val basePath = filePathProperty.getFilePath(separator)
        return basePath
    }
    
}
