import sqlite3

aliases = []
with open("aliases.txt") as infile:
    for line in infile.readlines():
        aliases.append([x.decode('utf-8') for x in line.split(" -> ")])

aliases = [(x[0], x[1][:-1]) for x in aliases]

import os
if not os.path.exists("../app/src/main/res/raw/"):
    os.makedirs("../app/src/main/res/raw/")

conn = sqlite3.connect('../app/src/main/res/raw/furry_db')

cur = conn.cursor()
cur.execute("drop table if exists aliases;\n")
cur.execute("drop table if exists android_metadata;\n")
cur.execute("create table aliases (_id integer primary key autoincrement, a text, b text);\n")

cur.executemany("insert into aliases (a, b) values (?, ?);", aliases)

cur.execute('CREATE TABLE "android_metadata" ("locale" TEXT DEFAULT "en_US");')
cur.execute('INSERT INTO "android_metadata" VALUES ("en_US");')
conn.commit()
