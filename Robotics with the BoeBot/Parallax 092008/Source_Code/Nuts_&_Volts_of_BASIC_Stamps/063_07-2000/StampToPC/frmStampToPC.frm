VERSION 5.00
Object = "{831FDD16-0C5C-11D2-A9FC-0000F8754DA1}#2.0#0"; "MSCOMCTL.OCX"
Object = "{648A5603-2C6E-101B-82B6-000000000014}#1.1#0"; "MSCOMM32.OCX"
Begin VB.Form frmStampToPC 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "Stamp To PC"
   ClientHeight    =   2865
   ClientLeft      =   150
   ClientTop       =   720
   ClientWidth     =   4680
   Icon            =   "frmStampToPC.frx":0000
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   ScaleHeight     =   2865
   ScaleWidth      =   4680
   StartUpPosition =   3  'Windows Default
   Begin MSComctlLib.StatusBar sbarMessage 
      Align           =   2  'Align Bottom
      Height          =   375
      Left            =   0
      TabIndex        =   8
      Top             =   2490
      Width           =   4680
      _ExtentX        =   8255
      _ExtentY        =   661
      Style           =   1
      SimpleText      =   "Stamp-To-PC message"
      _Version        =   393216
      BeginProperty Panels {8E3867A5-8586-11D1-B16A-00C0F0283628} 
         NumPanels       =   1
         BeginProperty Panel1 {8E3867AB-8586-11D1-B16A-00C0F0283628} 
         EndProperty
      EndProperty
   End
   Begin MSComctlLib.ProgressBar pbarAnalog 
      Height          =   375
      Left            =   240
      TabIndex        =   2
      Top             =   600
      Width           =   4215
      _ExtentX        =   7435
      _ExtentY        =   661
      _Version        =   393216
      BorderStyle     =   1
      Appearance      =   1
      Scrolling       =   1
   End
   Begin MSCommLib.MSComm MSComm1 
      Left            =   3960
      Top             =   2160
      _ExtentX        =   1005
      _ExtentY        =   1005
      _Version        =   393216
      DTREnable       =   0   'False
      RThreshold      =   1
   End
   Begin VB.Label lblSpanMax 
      Alignment       =   1  'Right Justify
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "100"
      Height          =   195
      Left            =   4185
      TabIndex        =   10
      Top             =   990
      Width           =   270
   End
   Begin VB.Label lblSpanMin 
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "0"
      Height          =   195
      Left            =   240
      TabIndex        =   9
      Top             =   990
      Width           =   90
   End
   Begin VB.Label lblDigitalInput 
      Alignment       =   2  'Center
      BorderStyle     =   1  'Fixed Single
      Caption         =   "Red"
      Height          =   255
      Index           =   3
      Left            =   240
      TabIndex        =   7
      Top             =   1800
      Width           =   975
   End
   Begin VB.Label lblDigitalInput 
      Alignment       =   2  'Center
      BorderStyle     =   1  'Fixed Single
      Caption         =   "Green"
      Height          =   255
      Index           =   2
      Left            =   1320
      TabIndex        =   6
      Top             =   1800
      Width           =   975
   End
   Begin VB.Label lblDigitalInput 
      Alignment       =   2  'Center
      BorderStyle     =   1  'Fixed Single
      Caption         =   "Black"
      Height          =   255
      Index           =   1
      Left            =   2400
      TabIndex        =   5
      Top             =   1800
      Width           =   975
   End
   Begin VB.Label lblDigitalInput 
      Alignment       =   2  'Center
      BorderStyle     =   1  'Fixed Single
      Caption         =   "Blue"
      Height          =   255
      Index           =   0
      Left            =   3480
      TabIndex        =   4
      Top             =   1800
      Width           =   975
   End
   Begin VB.Label Label2 
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "Digital Inputs:"
      Height          =   195
      Left            =   240
      TabIndex        =   3
      Top             =   1440
      Width           =   960
   End
   Begin VB.Label lblAnalogValue 
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "lblAnalogValue"
      Height          =   195
      Left            =   840
      TabIndex        =   1
      Top             =   240
      Width           =   1050
   End
   Begin VB.Label Label1 
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "Analog: "
      Height          =   195
      Left            =   240
      TabIndex        =   0
      Top             =   240
      Width           =   585
   End
   Begin VB.Line Line1 
      BorderColor     =   &H80000014&
      Index           =   1
      X1              =   0
      X2              =   10000
      Y1              =   15
      Y2              =   15
   End
   Begin VB.Line Line1 
      BorderColor     =   &H80000010&
      Index           =   0
      X1              =   0
      X2              =   10000
      Y1              =   0
      Y2              =   0
   End
   Begin VB.Menu mnuFile 
      Caption         =   "&File"
      Begin VB.Menu mnuFileExt 
         Caption         =   "E&xit"
      End
   End
   Begin VB.Menu mnuPort 
      Caption         =   "&Port"
      Begin VB.Menu mnuPortComX 
         Caption         =   "Com&1"
         Checked         =   -1  'True
         Index           =   1
      End
      Begin VB.Menu mnuPortComX 
         Caption         =   "Com&2"
         Index           =   2
      End
      Begin VB.Menu mnuPortComX 
         Caption         =   "Com&3"
         Index           =   3
      End
      Begin VB.Menu mnuPortComX 
         Caption         =   "Com&4"
         Index           =   4
      End
      Begin VB.Menu mnuPortSep1 
         Caption         =   "-"
      End
      Begin VB.Menu mnuPortResetStamp 
         Caption         =   "&Reset Stamp on Connection"
         Checked         =   -1  'True
      End
      Begin VB.Menu mnuPortSep2 
         Caption         =   "-"
      End
      Begin VB.Menu mnuPortConnect 
         Caption         =   "&Connect"
      End
   End
End
Attribute VB_Name = "frmStampToPC"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
' Nuts & Volts - Stamp Applications
' July 2000 (Listing 2)

' ========================================================================
' Program... StampToPC.vbp
' Author.... Jon Williams
' Started... 26 MAY 2000
' Updated... 26 MAY 2000
' ========================================================================

Option Explicit

Dim showData As Boolean                 ' okay to show incoming data
Dim rxBuffer As String                  ' buffer for incoming characters
Dim multiplier As Single                ' analog multiplier

Private Sub Form_Load()

  ' center form
  Me.Left = (Screen.Width - Me.Width) / 2
  Me.Top = (Screen.Height - Me.Height) / 2
  Me.Caption = App.Title
  
  ' setup comm object
  With MSComm1
    .CommPort = 1
    .Settings = "9600,N,8,1"            ' setup for DEBUG
    .DTREnable = mnuPortResetStamp.Checked
    .RThreshold = 1                     ' process one char at a time
    .InputLen = 1
    .InputMode = comInputModeText       ' input will be strings
  End With
  
  multiplier = 1#                       ' analog multiplier
  SetSpan ("0,100")                     ' set span of progress bar
  ClearForm
  showData = False                      ' wait for reset
  
End Sub

Private Sub Form_Unload(Cancel As Integer)

  If MSComm1.PortOpen Then MSComm1.PortOpen = False
  
End Sub

Private Sub mnuFileExt_Click()

  Unload Me
  
End Sub

Private Sub mnuPortComX_Click(Index As Integer)

  ' deselect last port
  mnuPortComX(MSComm1.CommPort).Checked = False
  ' select new
  MSComm1.CommPort = Index
  mnuPortComX(Index).Checked = True
  
End Sub

Private Sub mnuPortConnect_Click()

  Dim x As Byte

  If Not (MSComm1.PortOpen) Then
    ' open the port
    On Error GoTo PortError
    MSComm1.PortOpen = True
    ' update the title bar
    Me.Caption = App.Title & " [Connected]"
    ' update port menu
    For x = 1 To 4
      mnuPortComX(x).Enabled = False
    Next
    mnuPortConnect.Caption = "&Disconnect"
  Else
    ' close the port
    MSComm1.PortOpen = False
    ' update the title bar
    Me.Caption = App.Title
    ' update port menu
    For x = 1 To 4
      mnuPortComX(x).Enabled = True
    Next
    mnuPortConnect.Caption = "&Connect"
  End If
  Exit Sub
  
PortError:
  MsgBox "Could not open Com" & Trim(Str(MSComm1.CommPort)) & ". " & _
         vbCr & "Please select another port.", _
         vbExclamation + vbOKOnly, App.Title
         
  On Error GoTo 0
  
End Sub

Private Sub mnuPortResetStamp_Click()

  mnuPortResetStamp.Checked = Not (mnuPortResetStamp.Checked)
  MSComm1.DTREnable = mnuPortResetStamp.Checked

End Sub

Private Sub MSComm1_OnComm()

  Dim newChar As String
  
  Select Case MSComm1.CommEvent
    Case comEvReceive
      newChar = MSComm1.Input
      If newChar = Chr(13) Then
        ProcessBuffer (rxBuffer)
        rxBuffer = ""
      Else
        rxBuffer = rxBuffer & newChar
      End If
      
    ' process other events here
    
  End Select
  
End Sub

Private Sub ProcessBuffer(ByVal strBuffer As String)

  Dim leadChar As String
  Dim param As String
  
  ' get leading character
  leadChar = Mid(strBuffer, 1, 1)
  
  Select Case leadChar
    Case "!"
      ' command string
      DoCommand (strBuffer)
    Case "%"
      ' binary data
      param = Trim(Mid(strBuffer, 2))
      If showData Then ShowDigital (Bin2Dec(param))
    Case Else
      If IsNumeric(strBuffer) Then
        ' buffer has analog data
        If showData Then ShowAnalog (CLng(strBuffer))
      Else
        ' buffer contains message
        sbarMessage.SimpleText = Trim(strBuffer)
      End If
  End Select
   
End Sub

Private Function DoCommand(ByVal theCommand As String)

  Dim delimPos As Integer
  Dim cmd As String
  Dim param As String
  
  ' remove any leading or trailing spaces
  theCommand = Trim(theCommand)
  
  delimPos = InStr(1, theCommand, " ")
  If delimPos = 0 Then
    ' no parameter(s)
    cmd = UCase(theCommand)
  Else
    ' command has parameter(s)
    ' - get command
    cmd = UCase(Mid(theCommand, 1, delimPos - 1))
    ' extract parameters from command string
    param = Mid(theCommand, delimPos + 1)
  End If
  
  ' process the command
  Select Case cmd
    Case "!RSET"
      ClearForm
      showData = True
    Case "!CLRM"
      If showData Then sbarMessage.SimpleText = ""
    Case "!USRS"
      If showData Then sbarMessage.SimpleText = param
    Case "!AMIN"
      pbarAnalog.Min = CLng(param)
    Case "!AMAX"
      pbarAnalog.Max = CLng(param)
    Case "!AMUL"
      multiplier = CSng(param)
    Case "!SPAN"
      SetSpan (param)
  End Select

End Function

Private Function Bin2Dec(ByVal binValue As String) As Long

  Dim temp As Long
  Dim binLen As Integer
  Dim x As Integer
  
  temp = 0
  binLen = Len(binValue)
  For x = 1 To binLen
    ' add bit value if "1"
    If Mid(binValue, x, 1) = "1" Then
      temp = temp + 2 ^ (binLen - x)
    End If
  Next
  
  Bin2Dec = temp

End Function

Private Sub SetSpan(ByVal span As String)

  Dim comma As Integer
  
  comma = InStr(1, span, ",")
  If comma = 0 Then Exit Sub            ' improper format - exit
  
  ' update progress bar
  pbarAnalog.Min = CLng(Mid(span, 1, comma - 1))
  pbarAnalog.Max = CLng(Mid(span, comma + 1))
  
  ' update legends
  lblSpanMin.Caption = Str(pbarAnalog.Min)
  lblSpanMax.Caption = Str(pbarAnalog.Max)
  
End Sub

Private Sub ShowAnalog(ByVal aValue As Long)

  aValue = CLng(CSng(aValue) * multiplier)

  ' show value
  lblAnalogValue.Caption = Trim(Str(aValue))
  
  ' check limits and show on progress bar
  If aValue > pbarAnalog.Max Then aValue = pbarAnalog.Max
  If aValue < pbarAnalog.Min Then aValue = pbarAnalog.Min
  pbarAnalog.Value = aValue
  
End Sub

Private Sub ShowDigital(ByVal digValue As Long)

  Dim mask As Long
  Dim led As Byte
    
  For led = 0 To 3
    If (digValue And (2 ^ led)) > 0 Then
      ' channel off - extinguish
      lblDigitalInput(led).BackColor = &H8000000F
    Else
      ' channel on - light
      lblDigitalInput(led).BackColor = vbGreen
    End If
  Next

End Sub

Private Sub ClearForm()

  ShowAnalog (0)
  ShowDigital (&HFFFF)                  ' all off (active low)
  sbarMessage.SimpleText = ""
  
End Sub
