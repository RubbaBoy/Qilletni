import "!std/core.ql"

song mySong = "I Am A Fault" by "156/Silence"
print(mySong)

print("Song info:")
print(mySong.getUrl())
print(mySong.getId())
print(mySong.getArtist())
print(mySong.getArtistId())
print(mySong.getTitle())
print(mySong.getDuration())
