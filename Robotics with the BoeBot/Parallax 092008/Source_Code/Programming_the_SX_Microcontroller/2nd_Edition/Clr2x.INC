; Clr2x.inc
;
; Clear all data memory

  clr       fsr

:ClearData
  sb      fsr.4
    setb  fsr.3
  clr     ind
  ijnz    fsr, :ClearData
