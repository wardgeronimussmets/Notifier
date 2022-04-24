import praw
import pickle
import time
import re
import smtplib,ssl
import os
import sys

pickleFile = "myPickleFile.pk"
sort_order = {"Steam":0,"Epic Games":1}
path = os.getcwd() + "/"

class freeGame:
    def __init__(self, category, title, redditLink):
        self.cat = category
        self.title = title
        self.reddit = redditLink

def changeStdOut():
    sys.stderr = open(path+'logFile.log','w+')
    return


def storeUTC():
    with open(path+pickleFile, 'wb+') as pick:
        pickle.dump(int(time.time()-1), pick)
        pick.close()
    return


def loadUTC():
    with open(path+pickleFile, 'rb+') as pick:
        try:
            time = pickle.load(pick)
            pick.close()
            storeUTC()
            return time
        except EOFError:
            return 0


def containsException(target):
    if re.search("Free Weekend", target, re.IGNORECASE):
        # steam free weekend trigger -> not important
        return True
    control = "free"
    match = target.lower().find(control)
    if (match == -1) or (match-1)<0 or (match+len(control))>(len(target)-1):
        return False
    else:
        if target[match-1].isalpha() or target[match+len(control)].isalpha():
            #the character is a letter thus meaning that free is used in a word like freedom
            return True
        return False


def checkSubmission(submission):
    # get category
    s = str(submission.title)
    if s.__contains__("Free") or s.__contains__("FREE") or s.__contains__("free") or s.__contains__("100%"):
        if containsException(s):
            return None
        category = s[s.find("[") + len("["):s.rfind("]")]
        target = "[" + str(category) + "] "
        title = str.replace(submission.title, target, "")
        return freeGame(category, title, submission.permalink)
    else:
        return None


def getFromReddit():
    freeGames = []
    lastCheckTime = loadUTC()
    wereChanges = False
    reddit = praw.Reddit(client_id="KrKvK25tPXyhsuYKURx4aA", client_secret="WFLyQT2FYaVau7pDTUwlKCupDF2wqQ",
                         user_agent="python:praw:gameDealsParser (by /u/dewarden)")
    reddit.read_only = True
    for submissions in reddit.subreddit("GameDeals").new(limit=100):
        if submissions.created_utc > lastCheckTime:
            # file hasn't been checked before
            game = checkSubmission(submissions)
            if game is not None:
                freeGames.append(game)
                wereChanges = True
        else:
            # checked all new ones
            if wereChanges:
                print("Checked all the new ones")
            else:
                print("No new games were found")
            return freeGames
    # stopped checking because passed limit
    print("There were new submissions that haven't been checked yet")
    return freeGames


def sendGames(freeGames):
    freeGames.sort(key=lambda x: sort_order.get(x.cat,len(sort_order)))
    file = open(path + "dump.txt", "a+")
    for free_game in freeGames:
        file.write(free_game.cat+"\n")
        file.write(free_game.title+"\n")
        file.write("https://www.reddit.com"+free_game.reddit+"\n")
        print("found: " + free_game.cat + "" + free_game.title + "https://www.reddit.com"+free_game.reddit+"\n")
    file.close()

def main():
    changeStdOut()
    freeGames = getFromReddit()
    sendGames(freeGames)
    return


main()
time.sleep(0.5)#make sure that the dump.txt is properly closed
print("Python is exiting...")
exit(0)

# docs: https://praw.readthedocs.io/en/stable/code_overview/models/submission.html?highlight=submission#praw.models.Submission

#gmail acces, gaat ge wrs op een gegeven moment eens moeten doen vrees ik
#https://developers.google.com/gmail/api/quickstart/python