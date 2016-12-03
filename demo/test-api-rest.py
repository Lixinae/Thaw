# -*- coding: cp1252 -*-
#!/usr/bin/python

import json
import requests
import socket

try:
    import urllib.request as urllib2
except ImportError:
    import urllib2

try:
    import urllib.parse as urlparse
except ImportError:
    import urlparse

# Exemple utilisation post / get en python
# import requests
# url = 'https://...'
# payload = {'key1': 'value1', 'key2': 'value2'}

# GET
# r = requests.get(url)

# GET with params in URL
# r = requests.get(url, params=payload)

# POST with form-encoded data
# r = requests.post(url, data=payload)

# POST with JSON 
# import json
# r = requests.post(url, data=json.dumps(payload))

# Response, status etc
# r.text
# r.status_code

# Get ou post avec authentification
#  r = requests.get('https://my.website.com/rest/path', auth=('myusername', 'mybasicpass'))

# Asks the user on which machine he wants to log on 
def askMachineUrl():
    print("Format must be https://[adresse]:[port]")
    machineUrl = input("Enter a machine Url to read from : ")
    
    return machineUrl

# Asks the user the number of messages he wants to see on a particular channel
def askNumberMessage():
    numberMessage = ""
    while True:
        numberMessage = input("Enter the numberMessage you want to show : ")
        if not numberMessage.isdigit():
            continue
        if int(numberMessage) > 0:
            break
    return int(numberMessage)

# Asks the user from which channel the message should be retrieved
def askChannelName(machineUrl):
    channelName = ""
    while channelName not in getChannelsList(machineUrl):
        channelName = input("Enter a channel name to read from : ")    
    return channelName


# Works
def addChannel(machineUrl, newChannelName, creatorName):
    querie = "/api/addChannel"
    url = machineUrl + querie
    payload = {'newChannelName': newChannelName, 'creatorName': creatorName}
    return doPostRequestJson(url, payload)


# Todo : Test it
def deleteChannel(machineUrl, targetChannelName, userName):
    querie = "/api/deleteChannel"
    url = machineUrl + querie
    payload = {'channelName': targetChannelName, 'userName': userName}
    return doPostRequestJson(url, payload)


# Works
def connectToChannel(machineUrl, oldChannelName, channelName, userName):
    querie = "/api/connectToChannel"
    url = machineUrl + querie
    payload = {'channelName': channelName, 'userName': userName, 'oldChannelName': oldChannelName}
    return doPostRequestJson(url, payload)


# Works
def sendMessage(machineUrl, userName, channelName, content):
    querie = "/api/sendMessage"
    url = machineUrl + querie
    payload = {'username': userName, 'channelName': channelName, 'message': content}
    return doPostRequestJson(url, payload)


# Returns the list of all messages we want on a given channel
# Default value for numberMessage is 10
# Works
def getListMessageForChannel(machineUrl, channelName, numberMessage=10):
    querie = "/api/getListMessageForChannel";
    url = machineUrl + querie
    payload = {'channelName': channelName, 'numberOfMessage': numberMessage}
    return doPostRequestJson(url, payload)

# Ask the server for all channel names
# Return a list of all channel names
# Works
def getChannelsList(machineUrl):
    querie = "/api/getListChannel"
    url = machineUrl+querie
    return doGetRequest(url)


# Works
def getListUserForChannel(machineUrl, channelName):
    querie = "/api/getListUserForChannel"
    url = machineUrl + querie
    payload = {'channelName': channelName}
    return doPostRequestJson(url, payload)


##def testSimplePost():
##    payload = { 'username' : 'mouhahahaha' , 'another':'value' }
##    url = "http://192.168.1.34:8080/api/testJson"
##    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
##    print(json.dumps(payload))
##    r = requests.post(url, data=json.dumps(payload),headers=headers)
##    print(r.text)
##    print(r.status_code)
##    print(r.json())




def doPostRequestJson(url, payload):
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    print(url)
    print(payload)
    try:
        r = requests.post(url, data=json.dumps(payload), headers=headers)
    except :
        print("Can't reach target "+url)
        return
    print(r.status_code)
    print(r.json())
    return r.json()


def doGetRequest(url):
    try:
        r = requests.get(url)
    except :
        print("Can't reach target "+url)
        return
    print(r.status_code)
    print(r.json())
    return r.json()
    
if __name__ == '__main__':
    #testSimpleGet()
    #testSimplePost()
    ip = socket.gethostbyname(socket.gethostname())
    machineUrl = "http://" + ip + ":8080"

    print("########")
    # sendMessageToServer(machineUrl)
    print("########")
    addChannel(machineUrl, 'Another', 'superUser')
    print("########")
    getChannelsList(machineUrl)
    print("########")
    connectToChannel(machineUrl, 'default', 'Another', 'superUser')
    # deleteChannel(machineUrl,'Another','superUser')
    print("########")
    sendMessage(machineUrl, 'superUser', 'Another', "Message 2")
    print("########")
    sendMessage(machineUrl, 'superUser', 'Another', "Message 3")
    print("########")
    sendMessage(machineUrl, 'superUser', 'Another', "Message 4")
    print("########")
    getListMessageForChannel(machineUrl, 'Another', numberMessage=10)
    print("########")
    getChannelsList(machineUrl)
    print("########")
    #machineUrl = askMachineUrl()
    #channelName = askChannelName()
    #numberMessage = askNumberMessage()
    
    #l = fetchMessage(machineName,channelName,numberMessage)
    #print (l.join("\n)"))


