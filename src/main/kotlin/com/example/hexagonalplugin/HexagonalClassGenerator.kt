package com.example.hexagonalplugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.popup.JBPopup
import com.intellij.openapi.ui.popup.JBPopupFactory
import com.intellij.openapi.ui.popup.JBPopupListener
import com.intellij.openapi.ui.popup.LightweightWindowEvent
import com.intellij.openapi.vfs.VirtualFile
import java.awt.BorderLayout
import java.awt.event.ActionListener
import javax.swing.*

class HexagonalClassGenerator : AnAction() {

    private val JAVA = "java"
    private val KOTLIN = "kotlin"
    private val stringBuilder: StringBuilder = StringBuilder()

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)!!
        val virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE)

        if (virtualFile == null) {
            Messages.showMessageDialog("Selected file is null", "Error", null)
            return
        }

        val popup = createSelectForProgrammingLanguagePopup(virtualFile, project)
        popup.showInFocusCenter()
    }

    private fun createHexagonalFiles(
        fileName: String, chosenLanguage: String, virtualFile: VirtualFile, project: Project
    ) {
        val hexagonalClassGeneratorHelper = HexagonalClassGeneratorHelper(
            fileName,
            chosenLanguage, virtualFile, project, stringBuilder
        )
        hexagonalClassGeneratorHelper.createHexagonalFiles()
        if (!stringBuilder.isEmpty()) {
            Messages.showErrorDialog(stringBuilder.toString(), "Info")
        }
    }

    private fun createSelectForProgrammingLanguagePopup(virtualFile: VirtualFile, project: Project): JBPopup {
        val languages = listOf(JAVA, KOTLIN)

        return JBPopupFactory.getInstance().createPopupChooserBuilder(languages)
            .setSelectionMode(ListSelectionModel.SINGLE_SELECTION)  // 단일 선택 모드
            .setItemChosenCallback { selectedItem ->
                selectedItem?.let { language ->
                    // if the programming language is selected,
                    // then enter common prefix for filename
                    // ex : input = "CreateOrder"
                    // then : output = CreateOrderUseCase.java, CreateOrderService, etc..
                    showInputPopup { fileName ->
                        createHexagonalFiles(fileName, language, virtualFile, project)
                    }
                }
            }
            .createPopup()  // 팝업 생성
    }

    // Create Input Window Popup
    private fun showInputPopup(onInputConfirmed: (String) -> Unit) {
        val panel = JPanel(BorderLayout())
        val textField = JTextField()
        val confirmButton = JButton("Create")

        panel.add(JLabel("Enter prefix for Hexagonal files:"), BorderLayout.NORTH)
        panel.add(textField, BorderLayout.CENTER)
        panel.add(confirmButton, BorderLayout.SOUTH)

        val popup = JBPopupFactory.getInstance()
            .createComponentPopupBuilder(panel, textField)
            .setTitle("Input Required")
            .setFocusable(true)
            .setRequestFocus(true)
            .setResizable(false)
            .setMovable(true)
            .createPopup()

        popup.addListener(object : JBPopupListener {
            override fun beforeShown(event: LightweightWindowEvent) {
                textField.requestFocusInWindow()
            }
        })

        val actionListener = ActionListener {
            val userInput = textField.text
            if (userInput.isNotBlank()) {
                popup.closeOk(null)
                onInputConfirmed(userInput)
            } else {
                Messages.showErrorDialog("Please enter a value!", "Error")
            }
        }

        // 버튼에 리스너 추가
        confirmButton.addActionListener(actionListener)

        // 텍스트 필드에서 엔터 키 이벤트 처리
        textField.addActionListener(actionListener)

        popup.showInFocusCenter() // 팝업 화면 중앙에 표시
    }

}
