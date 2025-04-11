import "lastfm:lastfm.ql"

provider "lastfm"

LastFmResult result = getRecentTracks("RubbaBoy")

if (result.isError()) {
    printf("Error: %s", [result.errorMessage])
    exit(1)
}

printf("Recent Tracks:\n\t%s", [result.data])
