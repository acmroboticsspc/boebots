# Project BoeBot
A project led by @oconnellamethyst to create a quirky army of BoeBots to take over the third floor of Saint Paul College

### News
ovalbot.bs2 in the Great Program folder can be used to drive the robot in a wonky rectangle. Shikataganai.

### What is Makerspace & ACM?
Makerspace and ACM are two clubs on the Saint Paul College Campus which work together on many things. The ACM club is Saint Paul College's Association of Computing Machinery, and this club is focused on cool computer science things. Makerspace is a space where people make things, and is overall a friendly bunch of engineers. You can find us in our [Slack Channel](spstem.slack.com) or in our [Facebook Group](https://www.facebook.com/groups/spcrobots/)

### How do I work with this repository?
As an aspiring developer, you may not be familiar with GitHub and Git, or doing things with stuff. That's okay, here's a crash course in it! If you ever need any help with any of this, drop me a line in the Gitter chat or in the Facebook Group

First, you'll need a computer. You may be thinking, "Yes, @oconnellamethyst, of course I need a computer to code, but I'm on a computer right now!" But when I say that, I mean, you need a computer that you are actually comfortable developing code in. When I first started coding, I started with a Windows 7 laptop that was a pain to get anything of value to run on and had the battery life of a early 2000s cheap cell phone, it wrecked my ability to understand anything of substance in computer programming. Then that computer died and I bought a new-used [XUbuntu](https://xubuntu.org/tour/) Laptop from [Free Geek Twin Cities](http://freegeektwincities.org/) for $170, promptly installed [Ubuntu (the Long-Term Stable Release)](https://www.ubuntu.com/download/desktop) over XUbuntu just because I was getting super weird glitches when trying to install Google Chrome, and promptly realized that I had just accidentally improved my coding abilities by 200% just by getting a laptop that I didn't have to fight to code anything on. In coding, such as in life, you need tools that will work with you, not against you. I highly recommend the laptops sold by [Free Geek Twin Cities](http://freegeektwincities.org/), as not only are they recycling laptops and keeping them from going to waste, but also, they're just really good, but honestly, just get something that you aren't fighting. Coding is a battle all it's own without having to fight your computer. With that said, these Boebots are built to work on an old Windows computer, and I had to do some fighting to get the serial port to work on Ubuntu, and I haven't really documented how I did it much, so take the above with a grain of salt.

Second, you need to get Git on your computer. [Installation instructions here](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git). Notice how easy it is to install Git if you followed my advice and got a laptop from Free Geek Twin Cities, and installed Ubuntu over XUbuntu, you just open up the Terminal and type a few lines!

Now you've got to do Github things. If you haven't created a Github account, go do that now, and make sure to verify your email. See that button in the top right corner that says Fork? Press it. Now you are making your own copy of this Repository so that you don't accidentally eat it or something IDK. Now you want a copy of your copy of this repository on your computer so that you can edit code things. Open up your terminal in Ubuntu, or the BASH Terminal emulator that comes with Git on Windows, or whatever magic you Mac folks do, and basically get a Git copy of your fork, which is a copy of this repository. On my Ubuntu, this looks something like this.

```
computerusername@Computername:~$ mkdir folderthatIwantcodestuffsin
computerusername@Computername:~$ cd folderthatIwantcodestuffsin/
computerusername@Computername:~/folderthatIwantcodestuffsin$ git clone https://github.com/githubuser/boebots.git
Cloning into 'boebots'...
remote: Counting objects: 34, done.
remote: Compressing objects: 100% (33/33), done.
remote: Total 34 (delta 1), reused 30 (delta 0), pack-reused 0
Unpacking objects: 100% (34/34), done.
```

I made a folder that I wanted my codestuffs in using ```mkdir``` which is short for make directory, it makes you a nice folder. I then went inside of that folder using ```cd``` which is short for change directory, it lets you go inside of that folder you just created. What if I forget what the folder is called? You might ask? That's where you use the ```ls``` command. It's short for list, and it lists all the files and directories that are in whereever you are, it's super useful, and looks something like this. Also, note, if you try to use a keyboard shortcut to copy and paste in the terminal, it wont work. You have to either do Shift+Ctrl+C or just right-click.

```
computerusername@Computername:~/folderthatIwantcodestuffsin$ ls
boebots
```

See, when I git cloned my fork onto my computer in folderthatIwantcodestuffsin, it created a folder called boebots, a special folder that works with git. I want in that folder, but I'm a lazy typer, so because there is only one folder, I can type ```cd b``` and then press the tab key, and since boebots is the only folder that starts with an b, it will autocomplete. Nifty right?

```
computerusername@Computername:~/folderthatIwantcodestuffsin$ cd boebots/
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ 
```

Now here's a quandry, what happens if say, @oconnellamethyst updates everything in this repository, and you want your folder on your computer to stay up to date with my shenanagins, so that you can add your code. That's pretty easy, you just need to tell git that this is the upstream. Before you do this, you usually want to make sure you don't already have an upstream, like so.

```
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git remote -v
origin	https://github.com/githubuser/boebots.git (fetch)
origin	https://github.com/githubuser/boebots.git (push)
```

You're good. You've only got your stuff in there. Now, since you are on this page, and you are reading these instructions, you should go click that green button on this page to get the url. Then, in your terminal, type.

```
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git remote add upstream https://github.com/NorthScript/boebots.git
```

Now if you do ```git remote -v``` again, you'll find...

```
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git remote -v
origin	https://github.com/githubuser/boebots.git (fetch)
origin	https://github.com/githubuser/boebots.git (push)
upstream	https://github.com/NorthScript/boebots.git (fetch)
upstream	https://github.com/NorthScript/boebots.git (push)
```

Note that, typing the same command twice, it's hard on your wrists. You don't want carpal tunnel. Luckily, the terminal doesn't either, so if you've already used a command in the terminal, and you want to use the same command again, you can use the up arrow key until you get to the command and then press enter. Nifty eh?

Now you've just got to keep yours up to date with my shenanagins, 

```
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git fetch upstream
remote: Counting objects: 3, done.
remote: Compressing objects: 100% (2/2), done.
remote: Total 3 (delta 1), reused 3 (delta 1), pack-reused 0
Unpacking objects: 100% (3/3), done.
From https://github.com/NorthScript/boebots
 * [new branch]      master     -> upstream/master
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git checkout master
Already on 'master'
Your branch is up to date with 'origin/master'.
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git merge upstream/master
Updating 5f46644..530c7ee
Fast-forward
 README.md | 77 +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++--
 1 file changed, 75 insertions(+), 2 deletions(-)
```

See, now you're up to date with my shenanagins, but how do you keep me up to date on your shenanagins? First, you'll want to make changes to the code using the editor of your choice. For this particular project, I'm just using the Basic Stamp Editor and WINE because it also runs the boebots, but like, there are lots of cooler ones. You can go old school and use Vi, you can go new school and use Visual Studio Code. Anything really. Choosing an editor can be intimidating, but it's really the same as picking a computer. Choose something that helps, not hinders, your coding process. You will probably change editors over time as you get more experience, or in employment when your boss tells you to use a certain one. That's part of the learning process, the important part is that you code something in something!

So I make a change to ovalbot.bs2 in the GreatProgram folder using whatever, and I want to send it to senpai @oconnellamethyst, and the world really. First, you should keep up with my shenanagins to make sure that I haven't already done the thing, see above. Then, I want to update my own fork!

```
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git add GreatProgram/ovalbot.bs2
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git commit -m "This is where I describe briefly the changes I made to trips.txt"
[master d08e589] This is where I describe briefly the changes I made to trips.txt
 1 file changed, 2 insertions(+), 2 deletions(-)
computerusername@Computername:~/folderthatIwantcodestuffsin/boebots$ git push
Username for 'https://github.com': githubuser
Password for 'https://githubuser@github.com':
Counting objects: 7, done.
Delta compression using up to 4 threads.
Compressing objects: 100% (7/7), done.
Writing objects: 100% (7/7), 3.49 KiB | 3.49 MiB/s, done.
Total 7 (delta 4), reused 0 (delta 0)
remote: Resolving deltas: 100% (4/4), completed with 3 local objects.
To https://github.com/githubuser/boebots.git
   5f46644..d08e589  master -> master
```

Then I want to navigate to my fork on Github in my web browser, and submit a pull request, which is basically just, @oconnellamethyst senpai! Please add my code! So navigate to your fork, probably found at https://github.com/githubuser/boebots with githubuser replaced with your username, or also found by going to your profile, and then to your repositories, and then finding this repository. Then push the New pull request button, and then press Create pull request, and then thoroughly describe the changes made to the code in the comments, and then press create pull request. If your code is good, senpai will notice you! And that is how you do things. What we are specifically doing 'round these parts is creating robots that will conquer the third floor of Saint Paul College. Neato eh? The folder labeled "Robotics with the Boe Bot" is a whole bucket of learning materials that you can use.
