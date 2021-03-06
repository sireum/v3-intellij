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

package org.sireum.intellij.logika;

import javax.swing.*;

public abstract class LogikaForm {
    protected JPanel logikaPanel;
    protected JLabel idleLabel;
    protected JTextField idleTextField;
    protected JLabel timeoutLabel;
    protected JTextField timeoutTextField;
    protected JCheckBox checkSatCheckBox;
    protected JCheckBox autoCheckBox;
    protected JLabel logoLabel;
    protected JPanel headerPanel;
    protected JLabel titleLabel;
    protected JLabel subtitleLabel;
    protected JCheckBox backgroundCheckBox;
    protected JCheckBox highlightingCheckBox;
    protected JCheckBox underwaveCheckBox;
    protected JTextField fileExtsTextField;
    protected JLabel fileExtsLabel;
    protected JCheckBox hintCheckBox;
    protected JCheckBox inscribeSummoningsCheckBox;
    protected JRadioButton forwardRadioButton;
    protected JRadioButton backwardRadioButton;
    protected JRadioButton symExeRadioButton;
    protected JLabel checkerKindLabel;
    protected JPanel devPanel;
    protected JLabel bitsLabel;
    protected JRadioButton bitsUnboundedRadioButton;
    protected JPanel checkerKindPanel;
    protected JPanel bitWidthPanel;
    protected JRadioButton bits8RadioButton;
    protected JRadioButton bits16RadioButton;
    protected JRadioButton bits32RadioButton;
    protected JRadioButton bits64RadioButton;
    protected JCheckBox coneInfluenceCheckBox;
    protected JRadioButton unrollingSymExeRadioButton;
    protected JPanel boundsPanel;
    protected JLabel loopBoundLabel;
    protected JTextField loopBoundTextField;
    protected JLabel recursionBoundLabel;
    protected JTextField recursionBoundTextField;
    protected JCheckBox methodContractCheckBox;
    protected JCheckBox hintUnicodeCheckBox;
}
