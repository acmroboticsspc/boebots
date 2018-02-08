VERSION 5.00
Object = "{648A5603-2C6E-101B-82B6-000000000014}#1.1#0"; "MSCOMM32.OCX"
Begin VB.Form frmMain 
   Caption         =   "PC-to-Stamp Comm"
   ClientHeight    =   2760
   ClientLeft      =   165
   ClientTop       =   735
   ClientWidth     =   4680
   Icon            =   "frmMain.frx":0000
   LinkTopic       =   "Form1"
   ScaleHeight     =   2760
   ScaleWidth      =   4680
   StartUpPosition =   3  'Windows Default
   Begin VB.HScrollBar scrValue 
      Height          =   255
      LargeChange     =   10
      Left            =   480
      Max             =   1000
      TabIndex        =   6
      Top             =   600
      Width           =   4095
   End
   Begin MSCommLib.MSComm MSComm1 
      Left            =   120
      Top             =   2040
      _ExtentX        =   1005
      _ExtentY        =   1005
      _Version        =   393216
      DTREnable       =   0   'False
      Handshaking     =   2
      InBufferSize    =   128
      OutBufferSize   =   128
      RTSEnable       =   -1  'True
   End
   Begin VB.CommandButton cmdUpload 
      Caption         =   "Upload"
      Enabled         =   0   'False
      Height          =   375
      Left            =   3480
      TabIndex        =   4
      Top             =   2280
      Width           =   1095
   End
   Begin VB.CommandButton cmdDnload 
      Caption         =   "Download"
      Enabled         =   0   'False
      Height          =   375
      Left            =   2280
      TabIndex        =   3
      Top             =   2280
      Width           =   1095
   End
   Begin VB.TextBox txtStringData 
      Height          =   285
      Left            =   480
      MaxLength       =   64
      TabIndex        =   2
      Text            =   "Text1"
      Top             =   1560
      Width           =   4095
   End
   Begin VB.OptionButton optDataType 
      Caption         =   "String"
      Height          =   255
      Index           =   1
      Left            =   120
      TabIndex        =   1
      Top             =   1200
      Width           =   855
   End
   Begin VB.OptionButton optDataType 
      Caption         =   "Number"
      Height          =   255
      Index           =   0
      Left            =   120
      TabIndex        =   0
      Top             =   240
      Width           =   855
   End
   Begin VB.Label lblValue 
      Alignment       =   1  'Right Justify
      Caption         =   "1000"
      Height          =   255
      Left            =   4080
      TabIndex        =   5
      Top             =   240
      Width           =   495
   End
   Begin VB.Line Line1 
      BorderColor     =   &H80000014&
      Index           =   1
      X1              =   0
      X2              =   4680
      Y1              =   8
      Y2              =   8
   End
   Begin VB.Line Line1 
      BorderColor     =   &H80000010&
      Index           =   0
      X1              =   0
      X2              =   4680
      Y1              =   0
      Y2              =   0
   End
   Begin VB.Menu mnuFile 
      Caption         =   "&File"
      Begin VB.Menu mnuFileExit 
         Caption         =   "E&xit"
      End
   End
   Begin VB.Menu mnuSettings 
      Caption         =   "&Settings"
      Begin VB.Menu mnuSettingsComm1 
         Caption         =   "Comm 1"
         Checked         =   -1  'True
      End
      Begin VB.Menu mnuSettingsComm2 
         Caption         =   "Comm 2"
      End
      Begin VB.Menu mnuSettingsSep1 
         Caption         =   "-"
      End
      Begin VB.Menu mnuSettingsConnect 
         Caption         =   "&Connect"
      End
   End
End
Attribute VB_Name = "frmMain"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
' ==============================================================================
'
'   File...... PC2Stamp.vbp
'   Purpose... Serial Comm with BASIC Stamp using Flow Control
'   Author.... Jon Williams
'   E-mail.... jwilliams@parallaxinc.com
'   Started... 01 AUG 2002
'   Updated... 02 AUG 2002
'
' ==============================================================================

Option Explicit

Dim txBuf As String
Dim rxBuf As String

Const CmdTxN = &HC0                     ' send number to Stamp
Const CmdRxN = &HC1                     ' get number from Stamp
Const CmdTxS = &HC2                     ' send string to Stamp
Const CmdRxS = &HC3                     ' get string from Stamp

Private Sub cmdDnload_Click()
  Dim chrPos As Integer
  
  ' stop other transfers until this one complete
  Call DisableButtons
 
  If optDataType(0).Value = True Then
    ' send number command
    MSComm1.Output = Chr$(CmdTxN)
    Call FlushTxBuf
    ' send word value; low byte first
    MSComm1.Output = Chr$(scrValue.Value Mod 256) + Chr$(scrValue.Value \ 256)
    Call FlushTxBuf
  Else
    ' send string command
    MSComm1.Output = Chr$(CmdTxS)
    Call FlushTxBuf
    ' send one character at a time
    For chrPos = 1 To Len(txtStringData.Text)
      MSComm1.Output = Mid$(txtStringData.Text, chrPos, 1)
      Call FlushTxBuf
    Next
    ' send terminating character
    MSComm1.Output = Chr$(0)
    Call FlushTxBuf
  End If
  
  ' allow transfers
  Call EnableButtons
End Sub

Private Sub cmdUpload_Click()
  Dim strLen As Integer
  
  ' make sure input buffer is clear
  rxBuf = MSComm1.Input
  
  ' stop other transfers until this one complete
  Call DisableButtons

  If optDataType(0).Value = True Then
    ' send command
    MSComm1.Output = Chr$(CmdRxN)
    Call FlushTxBuf
    ' wait for two-byte value
    Do
      DoEvents
    Loop Until (MSComm1.InBufferCount = 2)
    ' grab input buffer
    rxBuf = MSComm1.Input
    ' display it
    scrValue.Value = Asc(Mid$(rxBuf, 1, 1)) + (Asc(Mid$(rxBuf, 2, 1)) * 256)
    ' update scroller
    Call scrValue_Change
  Else
    ' clear text box
    txtStringData.Text = ""
    ' send string command
    MSComm1.Output = Chr$(CmdRxS)
    Call FlushTxBuf
    ' wait for string length
    Do
      DoEvents
    Loop Until (MSComm1.InBufferCount = 1)
    ' extract string length
    rxBuf = MSComm1.Input
    strLen = Asc(Mid$(rxBuf, 1))
    ' resend command to start upload
    MSComm1.Output = Chr$(CmdRxS)
    Call FlushTxBuf
    ' wait for string
    Do
      DoEvents
    Loop Until (MSComm1.InBufferCount = strLen)
    ' show it
    txtStringData.Text = MSComm1.Input
  End If
  
  ' allow transfers
  Call EnableButtons
End Sub

Private Sub Form_Initialize()
  ' initialize controls
  Call mnuSettingsComm1_Click
  optDataType(0).Value = True
  optDataType(1).Value = False
  Call scrValue_Change
  txtStringData.Text = ""
  
  With MSComm1
    .Settings = "9600,N,8,1"
    .DTREnable = False
    .Handshaking = comRTS                   ' use hardware flow control
    .RTSEnable = True
    .InputMode = comInputModeText
  End With
End Sub

Private Sub Form_QueryUnload(Cancel As Integer, UnloadMode As Integer)
  ' don't quit in middle of a transfer
  Cancel = Not (mnuFileExit.Enabled)
End Sub

Private Sub Form_Unload(Cancel As Integer)
  If MSComm1.PortOpen Then
    MSComm1.PortOpen = False
  End If
End Sub

Private Sub mnuFileExit_Click()
  Unload Me
End Sub

Private Sub mnuSettingsConnect_Click()
  If mnuSettingsConnect.Caption = "&Connect" Then
    ' trap connection problems
    On Error GoTo NoConnect
    MSComm1.PortOpen = True
    frmMain.Caption = "PC-to-Stamp Comm [Connected]"
    ' clear error trapping
    On Error GoTo 0
    mnuSettingsConnect.Caption = "&Disconnect"
    ' disable port selection
    mnuSettingsComm1.Enabled = False
    mnuSettingsComm2.Enabled = False
    ' enable transfer buttons
    Call EnableButtons
  Else
    MSComm1.PortOpen = False
    frmMain.Caption = "PC-to-Stamp Comm"
    mnuSettingsConnect.Caption = "&Connect"
    ' enable port selection
    mnuSettingsComm1.Enabled = True
    mnuSettingsComm2.Enabled = True
    ' disable transfer buttons
    Call DisableButtons
  End If
  Exit Sub
  
NoConnect:
  Dim response
  ' display port connection problem
  response = MsgBox("Error: Could not connect.", _
                    vbExclamation + vbOKOnly, _
                    "PC-to-Stamp Comm", "", 0)
  On Error GoTo 0
End Sub

Private Sub mnuSettingsComm1_Click()
  mnuSettingsComm1.Checked = True
  mnuSettingsComm2.Checked = False
  MSComm1.CommPort = 1
End Sub

Private Sub mnuSettingsComm2_Click()
  mnuSettingsComm1.Checked = False
  mnuSettingsComm2.Checked = True
  MSComm1.CommPort = 2
End Sub

Private Sub scrValue_Change()
  ' show value
  lblValue.Caption = Str(scrValue.Value)
End Sub

Private Sub scrValue_Scroll()
  Call scrValue_Change
End Sub

Private Sub FlushTxBuf()
  ' flush transmit buffer
  Do
    DoEvents
  Loop Until (MSComm1.OutBufferCount = 0)
End Sub

Private Sub DisableButtons()
  cmdDnload.Enabled = False
  cmdUpload.Enabled = False
  mnuFileExit.Enabled = False
End Sub

Private Sub EnableButtons()
  cmdDnload.Enabled = True
  cmdUpload.Enabled = True
  mnuFileExit.Enabled = True
End Sub
