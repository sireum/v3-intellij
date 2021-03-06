/*
 Copyright (c) 2016, Robby, Kansas State University
 All rights reserved.

 Redistribution and use in source and binary forms, with or without
 modification, are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this
    list of conditions and the following disclaimer.
 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.sireum.intellij

import java.awt.Color
import javax.swing.JComponent
import javax.swing.event.{DocumentEvent, DocumentListener}

import com.intellij.openapi.options.Configurable
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.ui.JBColor

import org.sireum.util._

final class SireumConfigurable extends SireumForm with Configurable {

  import SireumApplicationComponent._

  private var validSireumHome = false
  private var validVmArgs = false
  private var validEnvVars = false
  private var fgColor: Color = _

  override def getDisplayName: String = "Sireum"

  override def getHelpTopic: String = null

  override def isModified: Boolean = {
    validSireumHome && validVmArgs && validEnvVars &&
      (sireumHomeString != sireumHomeTextField.getText ||
        vmArgsString != vmArgsTextField.getText ||
        envVarsString != envVarsTextArea.getText)
  }

  override def createComponent(): JComponent = {
    def updateSireumHome(path: String): Unit = {
      validSireumHome = checkSireumDir(path,
        parseVmArgs(vmArgsTextField.getText).getOrElse(ivectorEmpty),
        parseEnvVars(envVarsTextArea.getText).getOrElse(ilinkedMapEmpty)).nonEmpty
      sireumHomeLabel.setForeground(if (validSireumHome) fgColor else JBColor.red)
      sireumHomeTextField.setToolTipText(if (validSireumHome) "OK" else sireumInvalid(path))
    }

    def updateVmArgs(text: String): Unit = {
      validVmArgs = text == "" || parseVmArgs(text).nonEmpty
      vmArgsLabel.setForeground(if (validVmArgs) fgColor else JBColor.red)
      vmArgsLabel.setToolTipText(if (validVmArgs) "OK" else "Ill-formed (format: space-separated text; each text starts with a dash '-').")
    }

    def updateEnvVars(text: String): Unit = {
      validEnvVars = text == "" || parseEnvVars(text).nonEmpty
      envVarsLabel.setForeground(if (validEnvVars) fgColor else JBColor.red)
      envVarsLabel.setToolTipText(if (validEnvVars) "OK" else "Ill-formed (format: key of [a-zA-Z_][a-zA-Z0-9_]* = value, per line).")
    }

    reset()

    fgColor = sireumHomeLabel.getForeground

    sireumHomeTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = update()

      override def changedUpdate(e: DocumentEvent): Unit = update()

      override def removeUpdate(e: DocumentEvent): Unit = update()

      def update(): Unit = updateSireumHome(sireumHomeTextField.getText)
    })

    vmArgsTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = update()

      override def changedUpdate(e: DocumentEvent): Unit = update()

      override def removeUpdate(e: DocumentEvent): Unit = update()

      def update(): Unit = updateVmArgs(vmArgsTextField.getText)
    })

    sireumHomeButton.addActionListener(e => browseSireumHome(null) match {
      case Some(p) =>
        updateSireumHome(p)
        if (validSireumHome) sireumHomeTextField.setText(p)
        else updateSireumHome(sireumHomeTextField.getText)
      case _ =>
    })

    updateSireumHome(sireumHomeString)

    envVarsTextArea.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = update()

      override def changedUpdate(e: DocumentEvent): Unit = update()

      override def removeUpdate(e: DocumentEvent): Unit = update()

      def update(): Unit = updateEnvVars(envVarsTextArea.getText.trim)
    })

    updateEnvVars(envVarsString)
    updateVmArgs(vmArgsString)

    sireumPanel
  }

  override def disposeUIResources(): Unit = {
    fgColor = null
  }

  override def apply(): Unit = {
    envVars = parseEnvVars(envVarsTextArea.getText).getOrElse(ilinkedMapEmpty)
    vmArgs = parseVmArgs(vmArgsTextField.getText).getOrElse(ivectorEmpty)
    val path = sireumHomeTextField.getText
    sireumHomeOpt = checkSireumDir(path, vmArgs, envVars)
    if (sireumHomeOpt.nonEmpty) saveConfiguration()
    else {
      Messages.showMessageDialog(null: Project, sireumInvalid(path),
        "Invalid Sireum Configuration", null)
      SireumApplicationComponent.loadConfiguration()
    }
  }

  override def reset(): Unit = {
    sireumHomeTextField.setText(sireumHomeString)
    vmArgsTextField.setText(vmArgsString)
    envVarsTextArea.setText(envVarsString)
  }
}
