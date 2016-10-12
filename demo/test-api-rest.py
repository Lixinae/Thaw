
# Asks the user where
def askMachineName():
    machineName = input("Enter a machine name to read from : ")  
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
    machineName = askMachineName()
    channelName = askChannelName()
    numberMessage = askNumberMessage()
    
    l = fetchMessage(machineName,channelName,numberMessage)
    print (l.join("\n)")       
