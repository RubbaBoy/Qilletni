import "!std:core.ql"

collection rocMetal = "Rochester Metal" collection by "rubbaboy"

print("id = " + rocMetal.getId())
print("name = " + rocMetal.getName())
print("track count = " + rocMetal.getTrackCount())

User creator = rocMetal.getCreator()
print("creator id = " + creator.getId())
print("creator name = " + creator.getName())

Map map = new Map()
map.put(1, "Hello")

print(map.get(1))
