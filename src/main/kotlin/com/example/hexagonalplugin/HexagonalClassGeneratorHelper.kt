package com.example.hexagonalplugin

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiManager
import org.jetbrains.annotations.NonNls
import java.io.File


const val JAVA = "java"
const val KOTLIN = "kotlin"

class HexagonalClassGeneratorHelper(
    private val fileName: String,
    private val chosenLanguage: String,
    private val virtualFile: VirtualFile,
    private val project: Project,
    private val stringBuilder: StringBuilder
) {

    fun createHexagonalFiles() {
        // 선택한 언어에 맞는 파일 확장자 설정
        val languageProperty = when (chosenLanguage) {
            KOTLIN -> LanguageProperty.KOTLIN
            JAVA -> LanguageProperty.JAVA
            else -> throw IllegalArgumentException("Unsupported language: $chosenLanguage")
        }

        // creates components of hexagonal-architecture.
        // each methods create directory & class files.
        createAdapterInFile(languageProperty)
        createAdapterOutFile(languageProperty)
        createPortInFile(languageProperty)
        createPortOutFile(languageProperty)
        createServiceFile(languageProperty)
    }

    private fun createAdapterOutFile(languageProperty: LanguageProperty) {
        val directory = createDirectory("/adapter/out")
        val postfix = "Adapter"
        val interfaceName = "Port"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createImplementationMessageWithInterface(
                "$fileName$postfix",
                "$fileName$interfaceName"
            )
        )
    }

    private fun createAdapterInFile(
        languageProperty: LanguageProperty
    ) {
        val directory = createDirectory("/adapter/in")
        val postfix = "Controller"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createImplementationMessage("$fileName$postfix")
        )
    }

    private fun createPortInFile(languageProperty: LanguageProperty) {
        val directory = createDirectory("application/port/in")
        val postfix = "UseCase"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createInterfaceMessage("$fileName$postfix")
        )
    }

    private fun createPortOutFile(languageProperty: LanguageProperty) {
        val directory = createDirectory("application/port/out")
        val postfix = "Port"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createInterfaceMessage("$fileName$postfix")
        )
    }

    private fun createServiceFile(languageProperty: LanguageProperty) {
        val directory = createDirectory("/domain/service")
        val postfix = "Service"
        val interfaceName = "UseCase"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createImplementationMessageWithInterface(
                "$fileName$postfix",
                "$fileName$interfaceName"
            )
        )
    }

    private fun createDirectory(
        path: @NonNls String,
    ): VirtualFile {
        var directory = virtualFile.findChild(path)

        if (directory == null) {
            val segments = path.split(File.separator) // separate path by OS system separator.
            var currentPath = virtualFile.path
            var currentDirectory: VirtualFile? = virtualFile

            ApplicationManager.getApplication().runWriteAction {
                for (segment in segments) {
                    if (segment.isEmpty()) {
                        continue;
                    }
                    currentPath = if (currentPath.isEmpty()) segment else "$currentPath${File.separator}$segment"
                    // 경로에 해당하는 디렉토리가 없으면 새로 생성
                    currentDirectory = currentDirectory?.findChild(segment)
                        ?: currentDirectory?.createChildDirectory(this, segment)
                }
            }

            directory = currentDirectory // return leaf directory(finally created directory)
        }

        return directory!!
    }

    private fun createFile(
        directory: VirtualFile,
        languageProperty: LanguageProperty,
        fileName: String,
        template: String
    ) {
        val psiDirectory = PsiManager.getInstance(project).findDirectory(directory)

        if (psiDirectory != null) {
            var psiFile = psiDirectory.findFile(fileName)
            if (psiFile == null) {
                psiFile =
                    PsiFileFactory.getInstance(project)
                        .createFileFromText(fileName, languageProperty.fileType, template)

                // Add PsiFile to PsiDirectory
                WriteCommandAction.runWriteCommandAction(project) {
                    psiDirectory.add(psiFile)
                }
            }
            // if file already exists.
            else {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append("$fileName Already Exists. So We failed to generate that class.")
                } else {
                    stringBuilder.append('\n')
                        .append("$fileName Already Exists. So We failed to generate that class.")
                }
            }
        }

        var classFile = directory.findChild(fileName)

        if (classFile == null) {
            WriteCommandAction.runWriteCommandAction(project) {
                // 파일 생성
                classFile = directory.createChildData(this, fileName)
            }
        }
    }

}
