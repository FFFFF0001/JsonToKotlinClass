package wu.seal.jsontokotlin.ui

import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.InputValidator
import com.intellij.openapi.ui.Messages
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.components.JBLabel
import com.intellij.util.ui.JBDimension
import com.intellij.util.ui.JBEmptyBorder
import wu.seal.jsontokotlin.utils.addComponentIntoVerticalBoxAlignmentLeft
import java.awt.BorderLayout
import java.awt.event.ActionEvent
import javax.swing.*
import javax.swing.event.DocumentEvent

/**
 * Dialog widget relative
 * Created by Seal.wu on 2017/9/21.
 */


class MyInputValidator : InputValidator {

    lateinit var classNameField: JTextField
    override fun checkInput(inputString: String): Boolean {
        try {
            val classNameLegal = classNameField.text.trim().isNotBlank()
            val jsonElement = JsonParser().parse(inputString)

            return (jsonElement.isJsonObject || jsonElement.isJsonArray) && classNameLegal
        } catch (e: JsonSyntaxException) {
            return false
        }

    }

    override fun canClose(inputString: String): Boolean {
        return true
    }
}

val myInputValidator = MyInputValidator()

/**
 * Json input Dialog
 */
class JsonInputDialog(private val classsName: String, project: Project) : Messages.InputDialog(project, "Please input the class name and JSON String for generating Kotlin data class", "Make Kotlin Data Class", Messages.getInformationIcon(), "", myInputValidator) {

    private lateinit var classNameInput: JTextField

    init {
        setOKButtonText("Make")
        classNameInput.text = classsName
    }

    override fun createMessagePanel(): javax.swing.JPanel {
        val messagePanel = javax.swing.JPanel(BorderLayout())
        if (myMessage != null) {
            val textComponent = createTextComponent()
            messagePanel.add(textComponent, BorderLayout.NORTH)
        }
        myField = createTextFieldComponent()


        val classNameInputContainer = createLinearLayoutVertical()
        val classNameTitle = JBLabel("Class Name: ")
        classNameTitle.border = JBEmptyBorder(5, 0, 5, 0)
        classNameInputContainer.addComponentIntoVerticalBoxAlignmentLeft(classNameTitle)
        classNameInput = JTextField()
        classNameInput.preferredSize = JBDimension(400, 40)
        myInputValidator.classNameField = classNameInput

        classNameInput.document.addDocumentListener(object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent?) {
                okAction.isEnabled = myInputValidator.checkInput(myField.text)
            }
        })

        classNameInputContainer.addComponentIntoVerticalBoxAlignmentLeft(classNameInput)
        classNameInputContainer.preferredSize = JBDimension(500, 56)


        val createScrollableTextComponent = createMyScrollableTextComponent()
        val jsonInputContainer = createLinearLayoutVertical()
        val jsonTitle = JBLabel("JSON Text:")
        jsonTitle.border = JBEmptyBorder(5, 0, 5, 0)
        jsonInputContainer.addComponentIntoVerticalBoxAlignmentLeft(jsonTitle)
        jsonInputContainer.addComponentIntoVerticalBoxAlignmentLeft(createScrollableTextComponent)


        val centerContainer = JPanel()
        val centerBoxLayout = BoxLayout(centerContainer, BoxLayout.PAGE_AXIS)
        centerContainer.layout = centerBoxLayout
        centerContainer.addComponentIntoVerticalBoxAlignmentLeft(classNameInputContainer)
        centerContainer.addComponentIntoVerticalBoxAlignmentLeft(jsonInputContainer)
        messagePanel.add(centerContainer, BorderLayout.CENTER)
        val settingButton = JButton("Settings")
        settingButton.horizontalAlignment = SwingConstants.CENTER
        settingButton.addActionListener(object : AbstractAction() {
            override fun actionPerformed(e: ActionEvent) {
                SettingsDialog(false).show()
            }
        })
        val settingContainer = JPanel()
        val boxLayout = javax.swing.BoxLayout(settingContainer, BoxLayout.LINE_AXIS)
        settingContainer.layout = boxLayout
        settingContainer.add(settingButton)
        messagePanel.add(settingContainer, BorderLayout.SOUTH)

        return messagePanel
    }

    override fun createTextFieldComponent(): javax.swing.text.JTextComponent {
        val jTextArea = javax.swing.JTextArea(15, 100)
        jTextArea.minimumSize = JBDimension(800, 450)
        jTextArea.maximumSize = JBDimension(1000, 700)
        jTextArea.lineWrap = true
        jTextArea.wrapStyleWord = true
        jTextArea.autoscrolls = true
        return jTextArea
    }


    protected fun createMyScrollableTextComponent(): javax.swing.JComponent {
        return com.intellij.ui.components.JBScrollPane(myField)
    }

    fun getClassName(): String {
        if (exitCode == 0) {
            return classNameInput.text.trim()
        }
        return ""
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        if (classNameInput.text?.isEmpty() ?: true) {
            return classNameInput
        } else {
            return myField
        }
    }
}


fun createLinearLayoutVertical(): JPanel {
    val container = JPanel()
    val boxLayout = BoxLayout(container, BoxLayout.PAGE_AXIS)
    container.layout = boxLayout
    return container
}