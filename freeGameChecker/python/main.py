import praw
import pickle
import time
import re
import smtplib,ssl
import os
import sys

pickleFile = "myPickleFile.pk"
passwordPickle = "passwordPickle.pk"
sender_email = "markelgamedeals@gmail.com"
receiver_email = "dirkdedekenmcr@gmail.com"
smtp_server = "smtp.gmail.com"
sort_order = {"Steam":0,"Epic Games":1}
path = ""#os.path.dirname(__file__) + "/"

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
        pickle.dump(0, pick)
        # pickle.dump(int(time.time()), pick)
    return


def loadUTC():
    with open(path+pickleFile, 'rb+') as pick:
        try:
            return pickle.load(pick)
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
    print(lastCheckTime)
    reddit = praw.Reddit(client_id="KrKvK25tPXyhsuYKURx4aA", client_secret="WFLyQT2FYaVau7pDTUwlKCupDF2wqQ",
                         user_agent="python:praw:gameDealsParser (by /u/dewarden)")
    reddit.read_only = True
    for submissions in reddit.subreddit("GameDeals").new(limit=100):
        if submissions.created_utc > lastCheckTime:
            # file hasn't been checked before
            game = checkSubmission(submissions)
            if game is not None:
                freeGames.append(game)
        else:
            # checked all new ones
            print("Checked all the new ones")
            return freeGames
    # stopped checking because passed limit
    print("There were new submissions that haven't been checked yet")
    return freeGames

def getCategoryBodyText(category):
    return "[" + str(category).upper() + "]\n"

def getRestBodyText(title,link):
    return title + "\nLink: https://www.reddit.com" + link + "\n"

def buildMailBody(freeGames):
    body = ""
    cat = ""
    for game in freeGames:
        body += getCategoryBodyText(game.cat)
        if not cat.__eq__(game.cat):
            cat = game.cat
            body += getCategoryBodyText(game.cat)
        body += getRestBodyText(game.title,game.reddit)
    return body
def sendGames(freeGames):
    port = 465  # For SSL
    password = ""
    with open(passwordPickle, 'rb+') as pick:
        try:
            password = pickle.load(pick)
        except EOFError:
            password = input("Please insert the password of the email markelgamedeals@gmail.com")
            with open(path+passwordPickle,'wb') as pick2:
                pickle.dump(password,pick2)

    freeGames.sort(key=lambda x: sort_order.get(x.cat,len(sort_order)))
    body = buildMailBody(freeGames)
    # Create a secure SSL context
    context = ssl.create_default_context()
    message = """\
    Subject: Free games update [GameDeals]
    Well hello there,
    There have been some changes on the GameDeals subreddit.""" + body
    print(message)

    file = open(path+"dump.txt","a+")
    file.write(body)
    file.close()


    # with smtplib.SMTP(smtp_server, port) as server:
    #     server.login(sender_email, password)
    #     server.ehlo()  # Can be omitted
    #     server.starttls(context=context)
    #     server.ehlo()  # Can be omitted
    #     server.login(sender_email, password)
    #     server.sendmail(sender_email, receiver_email, message)

def main():
    #changeStdOut()
    freeGames = getFromReddit()
    storeUTC()
    sendGames(freeGames)
    return


main()

exit(0)

# docs: https://praw.readthedocs.io/en/stable/code_overview/models/submission.html?highlight=submission#praw.models.Submission

#gmail acces, gaat ge wrs op een gegeven moment eens moeten doen vrees ik
#https://developers.google.com/gmail/api/quickstart/python