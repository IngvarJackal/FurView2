import urllib2
import re
import HTMLParser
h = HTMLParser.HTMLParser()

LINK = "https://e621.net/tag_alias?aliased_to=&approved=true&forum_post=all&order=tag&page=%s&query=&user=" # % pageNum
REGEX = re.compile(r'<td><a href=".*">(.*)</a> \(\d+\)</td>')

aliases = []
for numPage in range(1, 331):
    print "Processing page", numPage
    html = urllib2.urlopen(LINK % numPage).read()
    aliases += REGEX.findall(html)

del aliases[0]

with open("../app/src/main/res/raw/aliases.txt", "w") as out:
    while len(aliases) > 1:
        a = aliases.pop()
        b = aliases.pop()
        if a != "" and b != "":
            try:
                out.write(h.unescape(a) + " -> " + h.unescape(b) + "\n")
            except Exception:
                pass
