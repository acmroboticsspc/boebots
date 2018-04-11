amethyst@Callisto:~$ ls
99-usbftdi.rules  ptcsetup.log
boebots           Public
circuita2.ods     runsikulix
csci1523          safe-not-sorry
Desktop           sikulistuff
Documents         SikuliX-1.1.2-SetupLog.txt
Downloads         sikulix.jar
examples.desktop  sikulixsetup-1.1.2.jar
image.png         snap
LambdaSchool      StCloud
LEGO Creations    Templates
math2082          the-entire-midwest-right-now_c_1321843.jpg
Music             #untitled_1.sch#
octave            Untitled Document 1
OpenAI            Videos
Pictures          VirtualBox VMs
ptcsetup.bak      workspace
amethyst@Callisto:~$ cd Documents
amethyst@Callisto:~/Documents$ ls
1523 InClass Laboratory - Functions Lab05A.pdf  poop.png
disability_notification_card_508.pdf            swiggy.png
egypt.png                                       trangle.xcf
egypt.xcf                                       Win10_1709_English_x64.iso
environments
amethyst@Callisto:~/Documents$ mkdir CodeStuffs
amethyst@Callisto:~/Documents$ cd CodeStuffs/
amethyst@Callisto:~/Documents/CodeStuffs$ git clone https://github.com/Nateboy97/boebots.git
Cloning into 'boebots'...
remote: Counting objects: 2244, done.
remote: Total 2244 (delta 0), reused 0 (delta 0), pack-reused 2244
Receiving objects: 100% (2244/2244), 387.12 MiB | 6.83 MiB/s, done.
Resolving deltas: 100% (301/301), done.
Checking out files: 100% (1843/1843), done.
amethyst@Callisto:~/Documents/CodeStuffs$ ls
boebots
amethyst@Callisto:~/Documents/CodeStuffs$ cd boebots/
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ ls
GettingStampToWorkWithWine.txt  README.command
GreatProgram                    Robotics with the BoeBot
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ cd ..
amethyst@Callisto:~/Documents/CodeStuffs$ cd boebots/
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ git remote -v
origin	https://github.com/Nateboy97/boebots.git (fetch)
origin	https://github.com/Nateboy97/boebots.git (push)
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ git remote add upstream https://github.com/acmroboticsspc/boebots.git
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ git remote -v
origin	https://github.com/Nateboy97/boebots.git (fetch)
origin	https://github.com/Nateboy97/boebots.git (push)
upstream	https://github.com/acmroboticsspc/boebots.git (fetch)
upstream	https://github.com/acmroboticsspc/boebots.git (push)
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ git fetch upstream
From https://github.com/acmroboticsspc/boebots
 * [new branch]      master     -> upstream/master
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ git merge upstream/master
Already up to date.
amethyst@Callisto:~/Documents/CodeStuffs/boebots$ 

