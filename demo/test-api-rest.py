# -*- coding: cp1252 -*-
#!/usr/bin/python

import urllib
import urllib2
import json

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


# Asks the user on which machine he wants to log on 
def askMachineUrl():
    machineUrl = input("Enter a machine Url to read from : ")  
    return machineName

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
def askChannelName():
    channelName = ""
    while channelName not in checkChannelNames():
        channelName = input("Enter a channel name to read from : ")    
    return channelName

# Ask the server for all channel names
# Return a list of all channel names
def checkChannelNames():
    # TODO
    return

# Returns the list of all messages we want on a channel
# Default value for numberMessage is 10
def fetchMessage(machineName,channelName,numberMessage=10):
    # TODO
    return

if __name__ == '__main__':
    machineUrl = askMachineUrl()
    channelName = askChannelName()
    numberMessage = askNumberMessage()
    
    l = fetchMessage(machineName,channelName,numberMessage)
    print (l.join("\n)"))
