amethyst@Callisto:~$ git clone https://github.com/acmroboticsspc/boebots.git
Cloning into 'boebots'...
warning: You appear to have cloned an empty repository.
amethyst@Callisto:~$ cd
amethyst@Callisto:~$ ls
ASCIIName.bs2     FirstProgram.bs2          ptcsetup.bak    #untitled_1.sch#
boebots           FirstProgramYourTurn.bs2  ptcsetup.log    Videos
Desktop           LEGO Creations            Public          VirtualBox VMs
Documents         LitAnaly5.docx_0.odt      safe-not-sorry  workspace
Downloads         Music                     snap
examples.desktop  Pictures                  Templates
amethyst@Callisto:~$ cd boebots
amethyst@Callisto:~/boebots$ ls
ASCIIName.bs2  FirstProgram.bs2  FirstProgramYourTurn.bs2  HighLowLed.bs2
amethyst@Callisto:~/boebots$ git add .
amethyst@Callisto:~/boebots$ git commit -m "Yo, programs mate!"
[master (root-commit) e025edd] Yo, programs mate!
 4 files changed, 47 insertions(+)
 create mode 100644 ASCIIName.bs2
 create mode 100644 FirstProgram.bs2
 create mode 100644 FirstProgramYourTurn.bs2
 create mode 100644 HighLowLed.bs2
amethyst@Callisto:~/boebots$ git push
Username for 'https://github.com': oconnellamethyst
Password for 'https://oconnellamethyst@github.com': 
Counting objects: 6, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (6/6), done.
Writing objects: 100% (6/6), 916 bytes | 916.00 KiB/s, done.
Total 6 (delta 1), reused 0 (delta 0)
remote: Resolving deltas: 100% (1/1), done.
To https://github.com/acmroboticsspc/boebots.git
 * [new branch]      master -> master

