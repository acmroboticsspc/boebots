VERSION 5.00
Object = "{648A5603-2C6E-101B-82B6-000000000014}#1.1#0"; "MSCOMM32.OCX"
Begin VB.Form frmPolledStamp 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "Polled Stamp-To-PC"
   ClientHeight    =   3105
   ClientLeft      =   150
   ClientTop       =   720
   ClientWidth     =   3690
   Icon            =   "frmPolledStamp.frx":0000
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   ScaleHeight     =   3105
   ScaleWidth      =   3690
   StartUpPosition =   3  'Windows Default
   Begin VB.CheckBox chkLED 
      Height          =   255
      Index           =   3
      Left            =   3240
      TabIndex        =   9
      Top             =   2040
      Width           =   255
   End
   Begin VB.CheckBox chkLED 
      Height          =   255
      Index           =   2
      Left            =   2240
      TabIndex        =   8
      Top             =   2040
      Width           =   255
   End
   Begin VB.CheckBox chkLED 
      Height          =   255
      Index           =   1
      Left            =   1240
      TabIndex        =   7
      Top             =   2040
      Width           =   255
   End
   Begin VB.CheckBox chkLED 
      Height          =   255
      Index           =   0
      Left            =   240
      TabIndex        =   6
      Top             =   2040
      Width           =   255
   End
   Begin VB.CommandButton cmdIDWrite 
      Caption         =   "Write"
      Height          =   375
      Left            =   1320
      TabIndex        =   4
      Top             =   960
      Width           =   975
   End
   Begin VB.CommandButton cmdIDRead 
      Caption         =   "Read"
      Height          =   375
      Left            =   240
      TabIndex        =   3
      Top             =   960
      Width           =   975
   End
   Begin VB.TextBox txtIDstring 
      Height          =   285
      Left            =   240
      MaxLength       =   50
      TabIndex        =   2
      Text            =   "Text1"
      Top             =   480
      Width           =   3255
   End
   Begin VB.CommandButton cmdSetLEDs 
      Caption         =   "Set"
      Height          =   375
      Left            =   240
      TabIndex        =   0
      Top             =   2520
      Width           =   975
   End
   Begin MSCommLib.MSComm MSComm1 
      Left            =   4320
      Top             =   0
      _ExtentX        =   1005
      _ExtentY        =   1005
      _Version        =   393216
      DTREnable       =   -1  'True
   End
   Begin VB.Label Label2 
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "LED Outputs:"
      Height          =   195
      Left            =   240
      TabIndex        =   5
      Top             =   1800
      Width           =   960
   End
   Begin VB.Label Label1 
      AutoSize        =   -1  'True
      BackStyle       =   0  'Transparent
      Caption         =   "Identification:"
      Height          =   195
      Left            =   240
      TabIndex        =   1
      Top             =   240
      Width           =   945
   End
   Begin VB.Line Line2 
      BorderColor     =   &H80000014&
      Index           =   3
      X1              =   240
      X2              =   3480
      Y1              =   1575
      Y2              =   1575
   End
   Begin VB.Line Line2 
      BorderColor     =   &H80000010&
      Index           =   2
      X1              =   240
      X2              =   3480
      Y1              =   1560
      Y2              =   1560
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
Attribute VB_Name = "frmPolledStamp"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
' Nuts & Volts - Stamp Applications
' August 2000 (Listing 2)

' ========================================================================
' Program... PolledStamp.VBP
' Author.... Jon Williams
' Started... 25 MAY 2000
' Updated...  2 JUL 2000
' ========================================================================


Option Explicit

Dim rxBuffer As String          ' response from Stamp
Dim roundRobin As Byte          ' polling control
Dim okayToClose As Boolean      ' okay to quit program?

Private Declare Function timeGetTime Lib "winmm.dll" () As Long

Private Sub cmdIDRead_Click()

  SendStr ("?F0")
  
End Sub

Private Sub cmdIDWrite_Click()

  SendStr ("?F1 " & Trim(txtIDstring.Text))
  
End Sub

Private Sub cmdSetLEDs_Click()

  Dim x As Integer
  Dim ledStr As String
  
  ledStr = ""                       ' clear status string
  For x = 0 To 3                    ' build binary string of status
    If chkLED(x).Value = 1 Then
      ledStr = ledStr & "1"
    Else
      ledStr = ledStr & "0"
    End If
  Next
  
  SendStr ("?B1 " & ledStr)         ' send command and status

End Sub

Private Sub Form_Load()

  Dim x As Integer

  ' setup form
  Me.Left = (Screen.Width - Me.Width) / 2
  Me.Top = (Screen.Height - Me.Height) / 2
  Me.Caption = App.Title
  
  ' clear ID text
  txtIDstring.Text = ""
  cmdIDRead.Enabled = False
  cmdIDWrite.Enabled = False
  
  ' uncheck LEDs
  For x = 0 To 3
    chkLED(x).Value = 0
  Next
  cmdSetLEDs.Enabled = False
  
  ' setup comm object
  With MSComm1
    .CommPort = 1
    .Settings = "9600,N,8,1"            ' setup for DEBUG
    .DTREnable = mnuPortResetStamp.Checked
    .RThreshold = 1                     ' process one char at a time
    .InputLen = 1                       ' grab one char at a time
    .InputMode = comInputModeText       ' input will be strings
    .SThreshold = 0                     ' don't wait to send
  End With
  
  okayToClose = True
 
End Sub

Private Sub Form_QueryUnload(Cancel As Integer, UnloadMode As Integer)

  Cancel = Not (okayToClose)
  
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

  If okayToClose And (Not (MSComm1.PortOpen)) Then
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
    ' enable form controls
    cmdIDRead.Enabled = True
    cmdIDWrite.Enabled = True
    cmdSetLEDs.Enabled = True
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
    ' disable form controls
    cmdIDRead.Enabled = False
    cmdIDWrite.Enabled = False
    cmdSetLEDs.Enabled = False
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

Private Sub ProcessBuffer(ByVal buffer As String)

  Dim leadChar As String
  Dim delimPos As Integer
  Dim label As String
  Dim param As String
  
  ' get leading character
  leadChar = Mid(buffer, 1, 1)
  
  If leadChar = "?" Then
    ' echoed query - ignore
  Else
    ' process the response
    delimPos = InStr(1, buffer, "=")
    If delimPos > 0 Then
      ' extract label and parameter
      label = UCase(Trim(Mid(buffer, 1, delimPos - 1)))
      param = Trim(Mid(buffer, delimPos + 1))
      ' process known responses
      Select Case label
        Case "ID"
          txtIDstring.Text = param
        
        Case "DS1620"
          ' process raw temperature
        
        Case "TEMPC"
          ' display celcius temp
        
        Case "TEMPF"
          ' display fahrenheit temp
            
        Case "STATUS"
          ' confirm LED status
        
        Case Else
          ' unknown label
        
      End Select
    Else
      ' buffer has no delimiter
      ' (error message)
    End If
  End If

End Sub

Public Sub Delay(milliseconds As Single)
  Dim timeOut As Single
  
  timeOut = milliseconds + timeGetTime()
  Do Until timeGetTime() >= timeOut
    DoEvents
  Loop
End Sub

Private Sub SendStr(ByVal txBuf As String)

  Dim x As Integer

  ' can't quit while transmitting
  okayToClose = False
  
  For x = 1 To Len(txBuf)
    MSComm1.Output = Mid(txBuf, x, 1)
    ' give Stamp time to receive and process the character
    Delay (5)
  Next
  ' add CR to end of command
  MSComm1.Output = Chr(13)
  
  okayToClose = True
  
End Sub

Private Function Dec2Bin(ByVal decValue As Long) As String

  Dim tmpBin As String
  Dim testBit As Long
  
  tmpBin = ""
  testBit = 1
  
  Do
    If (testBit And decValue) > 0 Then
      tmpBin = "1" & tmpBin
      decValue = decValue - testBit
    Else
      tmpBin = "0" & tmpBin
    End If
    testBit = testBit * 2
  Loop While (decValue > 0)
  
  Dec2Bin = tmpBin

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

