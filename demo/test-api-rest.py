# -*- coding: cp1252 -*-
#!/usr/bin/python

import urllib

import json
import requests

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

# Ask the server for all channel names
# Return a list of all channel names
def getChannelsList(machineUrl):
    # TODO
    #url = "http://192.168.1.34:8080/api/getListChannel"
    querie = "/api/getListChannel"
    url = machineUrl+querie
    print(url)
    try:
        r = requests.get(url)
    except :
        print("Can't reach target "+url)
        return
    print(r.status_code)
    print(r.json())
    return r.json()

# Returns the list of all messages we want on a channel
# Default value for numberMessage is 10
def fetchMessage(machineName,channelName,numberMessage=10):
    # TODO
    return

# This function is only to test how the REST API works
def testSimpleGet():
    url = "http://192.168.1.34:8080/api/test"
    #payload = { 'username' : 'mouhahahaha' }
    r = requests.get(url)
    print(r.text)
    print(r.status_code)
    print(r.json())
    print("##########")
    url = "http://192.168.1.34:8080/api/testParam/"+"yooooooow"
    r = requests.get(url)
    print(r.text)
    print(r.status_code)
    print(r.json())

def testSimplePost():
    payload = { 'username' : 'mouhahahaha' , 'another':'value' }
    url = "http://192.168.1.34:8080/api/testJson"
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    print(json.dumps(payload))
    r = requests.post(url, data=json.dumps(payload),headers=headers)
    print(r.text)
    print(r.status_code)
    print(r.json())

def sendMessageToServer(machineUrl):
    querie = "/api/sendMessage"
    url = machineUrl+querie
    payload = { 'username' : 'Narex' , 'channelName':'monSuperChan1' , 'message':'Super message de test qui dechire' }
    headers = {'Content-type': 'application/json', 'Accept': 'text/plain'}
    try:
        r = requests.post(url,data=json.dumps(payload),headers=headers)
    except :
        print("Can't reach target "+url)
        return
    print(r.status_code)
    print(r.json())
    return r.json()
    
if __name__ == '__main__':
    #testSimpleGet()
    #testSimplePost()
    machineUrl = "http://192.168.1.34:8080"
    #listTest = getChannelsList("http://192.168.1.34:8080")
    #print(listTest)
    sendMessageToServer(machineUrl)
    
    #machineUrl = askMachineUrl()
    #channelName = askChannelName()
    #numberMessage = askNumberMessage()
    
    #l = fetchMessage(machineName,channelName,numberMessage)
    #print (l.join("\n)"))


