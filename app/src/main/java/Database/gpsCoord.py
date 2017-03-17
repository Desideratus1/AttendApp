import requests
import json

send_url = 'http://freegeoip.net/json'
r = requests.get(send_url)
j = json.loads(r.text)
lat = j['latitude']
lon = j['longitude']

f = open("gps.txt",'w')
f.write(str(round(lat,9)) + "\n" + str(round(lon,9)))
f.close()