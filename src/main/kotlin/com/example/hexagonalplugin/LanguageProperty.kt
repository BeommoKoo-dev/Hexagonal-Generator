package com.example.hexagonalplugin

import com.intellij.ide.highlighter.JavaFileType
import com.intellij.openapi.fileTypes.LanguageFileType
import org.jetbrains.kotlin.idea.KotlinFileType

enum class LanguageProperty(
    val fileExtension: String,
    val fileType: LanguageFileType,
) {
    JAVA(".java", JavaFileType.INSTANCE) {
        override fun createImplementationMessage(fileName: String): String {
            return "public class $fileName {\n}"
        }

        override fun createImplementationMessageWithInterface(fileName: String, interfaceFileName: String): String {
            return "public class $fileName implements $interfaceFileName {\n}"
        }

        override fun createInterfaceMessage(fileName: String): String {
            return "public interface $fileName {\n}"
        }
    },
    KOTLIN(".kt", KotlinFileType.INSTANCE) {
        override fun createImplementationMessage(fileName: String): String {
            return "class $fileName {\n}"
        }

        override fun createImplementationMessageWithInterface(fileName: String, interfaceFileName: String): String {
            return "class $fileName: $interfaceFileName {\n}"
        }

        override fun createInterfaceMessage(fileName: String): String {
            return "interface $fileName {\n}"
        }
    };

    abstract fun createImplementationMessage(fileName: String): String

    abstract fun createImplementationMessageWithInterface(fileName: String, interfaceFileName: String): String

    abstract fun createInterfaceMessage(fileName: String): String
}
