from twython import Twython, TwythonError, TwythonRateLimitError, TwythonAuthError
import time
import json
import sys

appkey= <your_app_key>
appsecret = <your_app_secret>

accesstoken = <your_access_token>
accesstokensecret = <your_token_secret>

twitter = Twython(appkey, appsecret, accesstoken, accesstokensecret)

##########Task-1: Crawl a user's timeline
try: 
	usertimeline = twitter.get_user_timeline(screen_name='googleresearch', count=500)
	for i in range(0,len(usertimeline)):
		print usertimeline[i]['text']

except TwythonError as e:
		print e


##########Task-2: Crawl the results of a search query

#try: 
#	alltweets = twitter.search(q='election2016', count=2)
#	print alltweets['statuses'][0]['text']
#	print alltweets['statuses'][1]['text']
##	usertimeline = twitter.get_user_timeline(screen_name='googleresearch', count=500)

#except TwythonError as e:
#		print e

