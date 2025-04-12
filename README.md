<h1 align="center">
  Qilletni
</h1>

<p align="center">
  <b>
    <a href="https://qilletni.dev/">Website</a> •
    <a href="https://docs.qilletni.dev/">Language Docs</a> •
    <a href="https://api.qilletni.dev/">Native API Docs</a>
  </b>
</p>

<p align="center">
  <i>
    A high-performance DSL for curating music queues and playlists — declarative, composable, and music service-agnostic.
  </i>
</p>

---

## Overview

Qilletni is a **Domain-Specific Language** (DSL) designed to orchestrate and manipulate music queues, playlists, and metadata across multiple streaming platforms.

Unlike traditional API SDKs that require verbose REST wrappers, Qilletni lets you treat songs, playlists, and APIs as native constructs — with type safety, cross-provider conversion, and built-in playlist logic like weighted selection. Extension of the language is easy with a package system, and native methods that seamlessly invoke Java methods. Qilletni is a statically typed, object-oriented language with a familiar C-style syntax that leverages composition over inheritance.

> [!NOTE]
> Qilletni is currently in beta, so some documentation is still being worked on. Please make an issue if you think something should be added

---

## Key Features

### Music as a First-Class Citizen

- Native types: `song`, `album`, `collection`, `artist`, `weights`
- The `play` keyword can put songs in your queue, add it to a list, or invoke a callback
- Streamlined definitions:  
  ```
  song mySong = "Impulse" by "Harroway"
  album myAlbum = mySong.getAlbum()
  collection myMix = "My Playlist" collection by "RubbaBoy"
  
  play mySong
  ```

[Native Types →](https://qilletni.dev/language/types/built_in_types/)

### Intelligent Weighted Playlists

- Weighted selection by percentage or multiplier

- Nested, shuffled, sequential, and function-backed weights

- Custom orchestration logic per playlist, like:

  ```
  weights myWeights =
      | 25% "Track A" by "Artist"   // Play 25% of every song picked from weightedCollection
      | 5x  "Track B" by "Artist"   // When shuffling, play 5x more than normal
      | 10% "Inner Playlist" collection by "username"    // 10% of every song, play a song from "Inner Playlist"
      |~ 15% someFunctionReturningSong()    // 15% of every song, pick one from this method. Dont do this twice in a row 
  
  collection weightedCollection = "My Playlist" collection by "username" weights[myWeights]
  play weightedCollection limit[5]
  ```

[Weights →](https://qilletni.dev/language/types/built_in_types/#weights)

### Platform-Agnostic Playback

- Seamlessly switch service providers mid-execution

- Automatic cross-platform conversion for songs and other music types, only when needed

- Example:

  ```
  provider "lastfm"
  
  song top = getTopTracks("adam").data[0]  // Get your top song from Last.Fm
  
  provider "spotify"
  
  play top  // Auto-converted & queued on your Spotify account
  ```

### Familiar Syntax

- C-style syntax, with some modern additions
- Object-oriented with [Entities →](https://qilletni.dev/language/types/entities/)
- Example:

```
fun generateUniqueRecommendations(alreadyRecommended) {
    Recommender recommender = new Recommender()
            ..seedTracks = ["Truth Serum" by "Gutter King",
                            "Spiral" by "Feyn Entity"]
            ..targetEnergy = 1.0
            ..targetPopularity = 10

    song[] recs = recommender.recommend(100)
    Stack recommendations = new Stack()

    for (rec : recs) {
        if (!alreadyRecommended.containsArtist(rec.getArtist())) {
            recommendations.push(rec)
        }
    }
    
    print("Generated %d unique recommendations".format([recommendations.size()]))
    return recommendations
}
```

[Introduction →](https://qilletni.dev/language/introduction/)

### Java + HTTP + Database Interop

- Bind native Java methods to Qilletni functions [Native Bindings →](https://qilletni.dev/native_binding/introduction/)
- Native `http` and `json` libraries for API integrations
- `postgres` support with simplified SQL output parsing

------

## Supported Packages

Qilletni has modular, versioned packages. Some key official packages include:

| Package                                                  | Description                                                |
| -------------------------------------------------------- | ---------------------------------------------------------- |
| [`spotify`](https://qilletni.dev/docs/library/spotify)   | Spotify control, search, queueing, playlist management     |
| [`lastfm`](https://qilletni.dev/docs/library/lastfm)     | Recent tracks, top artists, listening history              |
| [`http`](https://qilletni.dev/docs/library/http)         | Basic and advanced HTTP requests                           |
| [`json`](https://qilletni.dev/docs/library/json)         | Read/write JSON objects and arrays                         |
| [`postgres`](https://qilletni.dev/docs/library/postgres) | Run queries, bind results to Qilletni objects              |
| [`metadata`](https://qilletni.dev/docs/library/metadata) | Access and modify song metadata tags, stored in a database |

More packages are in development and will be distributed via **QPM**, the Qilletni Package Manager (coming soon).

------

## Installation

### 1. Start Postgres (for caching)

```bash
docker run -d \
  --name qilletni-db \
  -p 5435:5432 \
  -e POSTGRES_USER=qilletni \
  -e POSTGRES_PASSWORD=pass \
  -e POSTGRES_DB=qilletni \
  -v ~/.qilletni/cache:/var/lib/postgresql/data \
  postgres
```

### 2. Install Qilletni (local or Docker)

**Local (requires Java 22):**

```bash
curl https://raw.githubusercontent.com/RubbaBoy/QilletniToolchain/refs/heads/master/scripts/install.sh | bash
source ~/.bashrc
qilletni run your_script.ql
```

**Docker:**

```bash
docker run --rm \
  --network host \
  -v qilletni-docker:/root \
  -v "$(pwd)":/data \
  ghcr.io/rubbaboy/qilletni:latest \
  run your_script.ql
```

See [Getting Started →](https://qilletni.dev/quickstart/getting_started/)

------

## Examples

Below are a couple quick examples. A lot more can be found on the Examples page of the docs [here](https://qilletni.dev/examples/).

### Build and Play a Weighted Playlist

```qilletni
// Assign songs to weights — "Track A" gets played 50% of the time
weights myMix =
    | 50% "Track A" by "Artist One"
    | 25% "Track B" by "Artist Two"
    | 5x  "Track C" by "Artist Three"

// Use a Spotify playlist with those weights and shuffle order
collection source = "My Spotify Playlist" collection by "username" weights[myMix] order[shuffle]

// Play 20 songs based on weights
play source limit[20]
```

See [Built-in Types →](https://qilletni.dev/language/types/built_in_types/)

### Cross-Platform Top Songs

```qilletni
import "lastfm:lastfm.ql"
import "spotify:playlist_tools.ql"

provider "lastfm"  // All music data is in Last.Fm

Page page = new Page()
                ..page = 1
                ..count = 100

LastFmResult result = getTopTracks("RubbaBoy", "3month", page)

if (result.isError()) {
    printf("Error: %s", [result.errorMessage])
    exit(1)
}

for (track : result.wrappedData.getValue()) {  // Print out how many plays per song
    printf("%s\t plays  %s", [track.playCount, track.track])
}

provider "spotify"  // All music data is converted to Spotify when needed

collection newPlaylist = createPlaylist("Top Song Playlist")  // Create a Spotify playlist
addToPlaylist(newPlaylist, result.data)  // result.data is a song[]

print("Created a playlist with %s songs".format([result.data.size()]))
```

See [Service Providers →](https://qilletni.dev/language/service_providers/)

------

## Project Structure

Qilletni supports both apps and libraries.

```bash
# Initialize app
qilletni init my_project/

# Or library
qilletni init -t library my_library/
```

Each project includes:

```
my_project/
└── qilletni-src/
    ├── my_project.ql
    └── qilletni_info.yml
```

See [Project Structure →](https://qilletni.dev/project_structure/)

## Related Repositories

Qilletni is broken into a handful of repositories. The standard library and Spotify service provider are in the main repo here, but additional functionality is separated.

[QilletniToolchain](https://github.com/RubbaBoy/QilletniToolchain): The CLI and wrapper for the implementation. This is where the language is packaged and released.

[QilletniDocgen](https://github.com/RubbaBoy/QilletniDocgen): The generator for language docs, hosted at: https://docs.qilletni.dev/

[QilletniDocs](https://github.com/RubbaBoy/QilletniDocs): The markdown docs of the main website, using mkdocs.
