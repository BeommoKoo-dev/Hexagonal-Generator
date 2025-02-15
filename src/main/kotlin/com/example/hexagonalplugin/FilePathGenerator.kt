package com.example.hexagonalplugin

import java.io.File

interface FilePathGenerator {
    fun generate(filePathProperty: FilePathProperty): String {
        return generateWithSeparator(filePathProperty, File.separator)
    }

    fun generateWithSeparator(filePathProperty: FilePathProperty, separator: String): String
}
