/*
 Copyright (c) 2017, Robby, Kansas State University
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

package org.sireum.intellij.logika

import java.awt.Color
import javax.swing.JComponent
import javax.swing.event.{DocumentEvent, DocumentListener}

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.util.{IconLoader, SystemInfo}
import com.intellij.ui.JBColor
import org.sireum.logika.message.CheckerKind
import org.sireum.util._

object LogikaConfigurable {
  private val logo = IconLoader.getIcon("/logika/icon/logika-logo.png")

  private val logikaKey = "org.sireum.logika."
  private val syntaxHighlightingKey = logikaKey + "highlighting"
  private val underwaveKey = logikaKey + "underwave"
  private val backgroundAnalysisKey = logikaKey + "background"
  private val idleKey = logikaKey + "idle"
  private val timeoutKey = logikaKey + "timeout"
  private val autoEnabledKey = logikaKey + "auto"
  private val checkSatKey = logikaKey + "checkSat"
  private val fileExtsKey = logikaKey + "fileExts"
  private val hintKey = logikaKey + "hint"
  private val hintUnicodeKey = logikaKey + "hintUnicode"
  private val inscribeSummoningsKey = logikaKey + "inscribeSummonings"
  private val coneInfluenceKey = logikaKey + "coneInfluence"
  private val checkerKindKey = logikaKey + "checkerKind"
  private val bitWidthKey = logikaKey + "bitWidth"
  private val loopBoundKey = logikaKey + "loopBound"
  private val recursionBoundKey = logikaKey + "recursionBound"
  private val methodContractKey = logikaKey + "methodContract"

  private[logika] var syntaxHighlighting = true
  private[logika] var underwave = true
  private[logika] var backgroundAnalysis = true
  private[logika] var idle: Natural = 1500
  private[logika] var timeout: Natural = 2000
  private[logika] var autoEnabled = false
  private[logika] var checkSat = false
  private[logika] var fileExts: ISeq[String] = ivector("sc")
  private[logika] var hint = false
  private[logika] var hintUnicode = SystemInfo.isMac
  private[logika] var inscribeSummonings = false
  private[logika] var coneInfluence = false
  private[logika] var checkerKind = CheckerKind.Forward
  private[logika] var bitWidth = 0
  private[logika] var loopBound = 10
  private[logika] var recursionBound = 10
  private[logika] var methodContract = true

  def loadConfiguration(): Unit = {
    val pc = PropertiesComponent.getInstance
    syntaxHighlighting = pc.getBoolean(syntaxHighlightingKey, syntaxHighlighting)
    underwave = pc.getBoolean(underwaveKey, underwave)
    backgroundAnalysis = pc.getBoolean(backgroundAnalysisKey, backgroundAnalysis)
    idle = pc.getInt(idleKey, idle)
    timeout = pc.getInt(timeoutKey, timeout)
    autoEnabled = pc.getBoolean(autoEnabledKey, autoEnabled)
    checkSat = pc.getBoolean(checkSatKey, checkSat)
    fileExts = Option(pc.getValue(fileExtsKey)).flatMap(parseFileExts).getOrElse(fileExts)
    hint = pc.getBoolean(hintKey, hint)
    hint = pc.getBoolean(hintUnicodeKey, hintUnicode)
    inscribeSummonings = pc.getBoolean(inscribeSummoningsKey, inscribeSummonings)
    coneInfluence = pc.getBoolean(coneInfluenceKey, coneInfluence)
    checkerKind = pc.getValue(checkerKindKey, checkerKind)
    bitWidth = pc.getInt(bitWidthKey, bitWidth)
    loopBound = pc.getInt(loopBoundKey, loopBound)
    recursionBound = pc.getInt(recursionBoundKey, recursionBound)
    methodContract = pc.getBoolean(methodContractKey, methodContract)
  }

  def saveConfiguration(): Unit = {
    val pc = PropertiesComponent.getInstance
    pc.setValue(syntaxHighlightingKey, syntaxHighlighting.toString)
    pc.setValue(underwaveKey, underwave.toString)
    pc.setValue(backgroundAnalysisKey, backgroundAnalysis.toString)
    pc.setValue(idleKey, idle.toString)
    pc.setValue(timeoutKey, timeout.toString)
    pc.setValue(autoEnabledKey, autoEnabled.toString)
    pc.setValue(checkSatKey, checkSat.toString)
    pc.setValue(fileExtsKey, fileExtsString)
    pc.setValue(hintKey, hint.toString)
    pc.setValue(hintUnicodeKey, hintUnicode.toString)
    pc.setValue(inscribeSummoningsKey, inscribeSummonings.toString)
    pc.setValue(coneInfluenceKey, coneInfluence.toString)
    pc.setValue(checkerKindKey, checkerKind)
    pc.setValue(bitWidthKey, bitWidth.toString)
    pc.setValue(loopBoundKey, loopBound.toString)
    pc.setValue(recursionBoundKey, recursionBound.toString)
    pc.setValue(methodContractKey, methodContract.toString)
  }

  def allFileExts: ISet[String] = LogikaFileType.extensions ++ fileExts

  def fileExtsString: String = fileExts.mkString("; ")

  def parseGe200(text: String): Option[Natural] =
    try {
      val n = text.toInt
      if (n < 200) None else Some(n)
    } catch {
      case _: Throwable => None
    }

  def parsePosInteger(text: String): Option[PosInteger] =
    try {
      val n = text.toInt
      if (n <= 0) None else Some(n)
    } catch {
      case _: Throwable => None
    }

  def parseFileExts(text: String): Option[ISeq[String]] = {
    var r = ivectorEmpty[String]
    for (e <- text.split(";")) {
      val ext = e.trim
      if (ext.nonEmpty && ext.forall(_.isLetterOrDigit))
        r :+= ext
      else return None
    }
    Some(r)
  }
}

import LogikaConfigurable._

final class LogikaConfigurable extends LogikaForm with Configurable {

  private var validIdle = true
  private var validTimeout = true
  private var validFileExts = true
  private var validLoopBound = true
  private var validRecursionBound = true
  private var fgColor: Color = _

  override def getDisplayName: String = "Logika"

  override def getHelpTopic: String = null

  override def isModified: Boolean =
    validIdle && validTimeout && validFileExts && validLoopBound &&
      validRecursionBound &&
      (highlightingCheckBox.isSelected != syntaxHighlighting ||
        underwaveCheckBox.isSelected != underwave ||
        backgroundCheckBox.isSelected != backgroundAnalysis ||
        idleTextField.getText != idle.toString ||
        timeoutTextField.getText != timeout.toString ||
        autoCheckBox.isSelected != autoEnabled ||
        checkSatCheckBox.isSelected != checkSat ||
        fileExtsTextField.getText != fileExtsString ||
        hintCheckBox.isSelected != hint ||
        hintUnicodeCheckBox.isSelected != hintUnicode ||
        inscribeSummoningsCheckBox.isSelected != inscribeSummonings ||
        coneInfluenceCheckBox.isSelected != coneInfluence ||
        selectedKind != checkerKind ||
        selectedBitWidth != bitWidth ||
        loopBoundTextField.getText != loopBound.toString ||
        recursionBoundTextField.getText != recursionBound.toString ||
        methodContractCheckBox.isSelected != methodContract)

  private def selectedKind: CheckerKind.Value =
    if (forwardRadioButton.isSelected) CheckerKind.Forward
    else if (backwardRadioButton.isSelected) CheckerKind.Backward
    else if (symExeRadioButton.isSelected) CheckerKind.SummarizingSymExe
    else if (unrollingSymExeRadioButton.isSelected) CheckerKind.UnrollingSymExe
    else sys.error("Unexpected checker kind.")

  private def selectedBitWidth: Int =
    if (bitsUnboundedRadioButton.isSelected) 0
    else if (bits8RadioButton.isSelected) 8
    else if (bits16RadioButton.isSelected) 16
    else if (bits32RadioButton.isSelected) 32
    else if (bits64RadioButton.isSelected) 64
    else sys.error("Unexpected bit width.")

  override def createComponent(): JComponent = {
    //devPanel.setVisible(false)
    //unrollingSymExeRadioButton.setEnabled(false)

    def updateIdle() = {
      val text = idleTextField.getText
      validIdle = parseGe200(text).nonEmpty
      idleLabel.setForeground(if (validIdle) fgColor else JBColor.red)
      idleTextField.setToolTipText(if (validIdle) "OK" else "Must be at least 200.")
    }
    def updateTimeout() = {
      val text = timeoutTextField.getText
      validTimeout = parseGe200(text).nonEmpty
      timeoutLabel.setForeground(if (validTimeout) fgColor else JBColor.red)
      timeoutTextField.setToolTipText(if (validTimeout) "OK" else "Must be at least 200.")
    }
    def updateFileExts() = {
      val text = fileExtsTextField.getText
      validFileExts = parseFileExts(text).nonEmpty
      fileExtsLabel.setForeground(if (validFileExts) fgColor else JBColor.red)
      fileExtsTextField.setToolTipText(if (validTimeout) "OK" else "Ill-formed (format: semicolon-separated file extensions of [a-zA-Z0-9]+).")
    }
    def updateLoopBound() = {
      val text = loopBoundTextField.getText
      validLoopBound = parsePosInteger(text).nonEmpty
      loopBoundLabel.setForeground(if (validLoopBound) fgColor else JBColor.red)
      loopBoundTextField.setToolTipText(if (validLoopBound) "OK" else "Must be at least 1.")
    }
    def updateRecursionBound() = {
      val text = recursionBoundTextField.getText
      validRecursionBound = parsePosInteger(text).nonEmpty
      recursionBoundLabel.setForeground(if (validRecursionBound) fgColor else JBColor.red)
      recursionBoundTextField.setToolTipText(if (validRecursionBound) "OK" else "Must be at least 1.")
    }
    def updateSymExe() = {
      val isUnrolling = unrollingSymExeRadioButton.isSelected
      val isSymExe = symExeRadioButton.isSelected || isUnrolling
      bitsLabel.setEnabled(isSymExe)
      bitsUnboundedRadioButton.setEnabled(isSymExe)
      bits8RadioButton.setEnabled(isSymExe)
      bits16RadioButton.setEnabled(isSymExe)
      bits32RadioButton.setEnabled(isSymExe)
      bits64RadioButton.setEnabled(isSymExe)
      autoCheckBox.setEnabled(!isSymExe)
      loopBoundLabel.setEnabled(isUnrolling)
      loopBoundTextField.setEnabled(isUnrolling)
      recursionBoundLabel.setEnabled(isUnrolling)
      recursionBoundTextField.setEnabled(isUnrolling)
      //methodContractCheckBox.setEnabled(isUnrolling)
    }
    def updateHintUnicode() = {
      hintUnicodeCheckBox.setEnabled(hintCheckBox.isSelected)
    }

    logoLabel.setIcon(logo)

    reset()

    fgColor = idleLabel.getForeground

    backgroundCheckBox.addActionListener(_ => {
      idleLabel.setEnabled(backgroundCheckBox.isSelected)
      idleTextField.setEnabled(backgroundCheckBox.isSelected)
    })

    idleTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = updateIdle()

      override def changedUpdate(e: DocumentEvent): Unit = updateIdle()

      override def removeUpdate(e: DocumentEvent): Unit = updateIdle()
    })

    timeoutTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = updateTimeout()

      override def changedUpdate(e: DocumentEvent): Unit = updateTimeout()

      override def removeUpdate(e: DocumentEvent): Unit = updateTimeout()
    })

    fileExtsTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = updateFileExts()

      override def changedUpdate(e: DocumentEvent): Unit = updateFileExts()

      override def removeUpdate(e: DocumentEvent): Unit = updateFileExts()
    })

    loopBoundTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = updateLoopBound()

      override def changedUpdate(e: DocumentEvent): Unit = updateLoopBound()

      override def removeUpdate(e: DocumentEvent): Unit = updateLoopBound()
    })

    recursionBoundTextField.getDocument.addDocumentListener(new DocumentListener {
      override def insertUpdate(e: DocumentEvent): Unit = updateRecursionBound()

      override def changedUpdate(e: DocumentEvent): Unit = updateRecursionBound()

      override def removeUpdate(e: DocumentEvent): Unit = updateRecursionBound()
    })

    symExeRadioButton.addChangeListener(_ => updateSymExe())
    unrollingSymExeRadioButton.addChangeListener(_ => updateSymExe())
    hintCheckBox.addChangeListener(_ => updateHintUnicode())

    updateSymExe()
    updateHintUnicode()

    logikaPanel
  }

  override def disposeUIResources(): Unit = {}

  override def apply(): Unit = {
    syntaxHighlighting = highlightingCheckBox.isSelected
    underwave = underwaveCheckBox.isSelected
    backgroundAnalysis = backgroundCheckBox.isSelected
    idle = parseGe200(idleTextField.getText).getOrElse(idle)
    timeout = parseGe200(timeoutTextField.getText).getOrElse(timeout)
    autoEnabled = autoCheckBox.isSelected
    checkSat = checkSatCheckBox.isSelected
    fileExts = parseFileExts(fileExtsTextField.getText).getOrElse(fileExts)
    hint = hintCheckBox.isSelected
    hintUnicode = hintUnicodeCheckBox.isSelected
    inscribeSummonings = inscribeSummoningsCheckBox.isSelected
    coneInfluence = coneInfluenceCheckBox.isSelected
    checkerKind = selectedKind
    bitWidth = selectedBitWidth
    loopBound = parsePosInteger(loopBoundTextField.getText).getOrElse(loopBound)
    recursionBound = parsePosInteger(recursionBoundTextField.getText).getOrElse(recursionBound)
    methodContract = methodContractCheckBox.isSelected
    saveConfiguration()
  }

  override def reset(): Unit = {
    highlightingCheckBox.setSelected(syntaxHighlighting)
    underwaveCheckBox.setSelected(underwave)
    backgroundCheckBox.setSelected(backgroundAnalysis)
    idleTextField.setText(idle.toString)
    timeoutTextField.setText(timeout.toString)
    autoCheckBox.setSelected(autoEnabled)
    checkSatCheckBox.setSelected(checkSat)
    fileExtsTextField.setText(fileExtsString)
    hintCheckBox.setSelected(hint)
    hintUnicodeCheckBox.setSelected(hintUnicode)
    inscribeSummoningsCheckBox.setSelected(inscribeSummonings)
    coneInfluenceCheckBox.setSelected(coneInfluence)
    checkerKind match {
      case CheckerKind.Forward => forwardRadioButton.setSelected(true)
      case CheckerKind.Backward => backwardRadioButton.setSelected(true)
      case CheckerKind.SummarizingSymExe => symExeRadioButton.setSelected(true)
      case CheckerKind.UnrollingSymExe => unrollingSymExeRadioButton.setSelected(true)
    }
    bitWidth match {
      case 0 => bitsUnboundedRadioButton.setSelected(true)
      case 8 => bits8RadioButton.setSelected(true)
      case 16 => bits16RadioButton.setSelected(true)
      case 32 => bits32RadioButton.setSelected(true)
      case 64 => bits64RadioButton.setSelected(true)
    }
    loopBoundTextField.setText(loopBound.toString)
    recursionBoundTextField.setText(recursionBound.toString)
    methodContractCheckBox.setSelected(methodContract)
  }
}
