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
    private val filePathGenerator: FilePathGenerator,
    private val fileName: String,
    private val chosenLanguage: String,
    private val virtualFile: VirtualFile,
    private val project: Project,
    private val stringBuilder: StringBuilder
) {

    fun createHexagonalFiles() {
        stringBuilder.clear()
        // 선택한 언어에 맞는 파일 확장자 설정
        val languageProperty = when (chosenLanguage) {
            KOTLIN -> LanguageProperty.KOTLIN
            JAVA -> LanguageProperty.JAVA
            else -> throw IllegalArgumentException("Unsupported language: $chosenLanguage")
        }

        // creates components of hexagonal-architecture.
        // each methods create directory & class files.
        createAdapterOutFile(languageProperty, filePathGenerator)
        createAdapterInFile(languageProperty, filePathGenerator)
        createPortInFile(languageProperty, filePathGenerator)
        createPortOutFile(languageProperty, filePathGenerator)
        createServiceFile(languageProperty, filePathGenerator)
    }

    private fun createAdapterOutFile(languageProperty: LanguageProperty, filePathGenerator: FilePathGenerator) {
        val directory = createDirectory(filePathGenerator.generate(FilePathProperty.ADAPTER_OUT))
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
        languageProperty: LanguageProperty,
        filePathGenerator: FilePathGenerator
    ) {
        val directory = createDirectory(filePathGenerator.generate(FilePathProperty.ADAPTER_IN))
        val postfix = "Controller"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createImplementationMessage("$fileName$postfix")
        )
    }

    private fun createPortInFile(
        languageProperty: LanguageProperty,
        filePathGenerator: FilePathGenerator
    ) {
        val directory = createDirectory(filePathGenerator.generate(FilePathProperty.PORT_IN))
        val postfix = "UseCase"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createInterfaceMessage("$fileName$postfix")
        )
    }

    private fun createPortOutFile(
        languageProperty: LanguageProperty,
        filePathGenerator: FilePathGenerator
    ) {
        val directory = createDirectory(filePathGenerator.generate(FilePathProperty.PORT_OUT))
        val postfix = "Port"
        createFile(
            directory,
            languageProperty,
            fileName = "$fileName$postfix${languageProperty.fileExtension}",
            languageProperty.createInterfaceMessage("$fileName$postfix")
        )
    }

    private fun createServiceFile(
        languageProperty: LanguageProperty,
        filePathGenerator: FilePathGenerator
    ) {
        val directory = createDirectory(filePathGenerator.generate(FilePathProperty.SERVICE))
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
        val trimedFileName = fileName.trim()

        if (psiDirectory != null) {
            var psiFile = psiDirectory.findFile(trimedFileName)
            if (psiFile == null) {
                psiFile =
                    PsiFileFactory.getInstance(project)
                        .createFileFromText(trimedFileName, languageProperty.fileType, template)

                // Add PsiFile to PsiDirectory
                WriteCommandAction.runWriteCommandAction(project) {
                    psiDirectory.add(psiFile)
                }
            }
            // if file already exists.
            else {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append("$trimedFileName Already Exists. So We failed to generate that class.")
                } else {
                    stringBuilder.append('\n')
                        .append("$trimedFileName Already Exists. So We failed to generate that class.")
                }
            }
        }

        var classFile = directory.findChild(trimedFileName)

        if (classFile == null) {
            WriteCommandAction.runWriteCommandAction(project) {
                // 파일 생성
                classFile = directory.createChildData(this, trimedFileName)
            }
        }
    }

}
